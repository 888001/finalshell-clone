package com.finalshell.ui.browser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Swing/JavaFX混合WebView组件
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SwingFXWebView extends JPanel {
    
    private volatile String currentUrl;
    private WebViewListener listener;
    private JEditorPane editorPane;
    private SwingWorker<String, Void> worker;
    private final List<String> history = new ArrayList<>();
    private int historyIndex = -1;

    private boolean fxMode;
    private JComponent fxPanel;
    private volatile Object fxWebView;
    private volatile Object fxEngine;
    private volatile boolean pendingPushHistory;
    private volatile String pendingUrl;
    private volatile String fxLastError;

    private volatile String userAgent = "FinalShell EmbeddedBrowser/1.0";

    private final AtomicLong navSeq = new AtomicLong();
    private volatile long pendingNavSeq;
    
    public SwingFXWebView() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        this.fxMode = initFxIfPossible();

        if (this.fxMode && this.fxPanel != null) {
            add(this.fxPanel, BorderLayout.CENTER);
        } else {
            editorPane = new JEditorPane();
            editorPane.setEditable(false);
            editorPane.setContentType("text/html");
            add(new JScrollPane(editorPane), BorderLayout.CENTER);
        }
    }
    
    public void loadUrl(String url) {
        loadUrlInternal(url, true);
    }
    
    public void loadContent(String content) {
        stop();
        currentUrl = "about:blank";
        if (listener != null) {
            listener.onLoadStart(currentUrl);
        }
        if (fxMode && fxEngine != null) {
            fxLoadContent(content == null ? "" : content);
            return;
        }

        editorPane.setText(content == null ? "" : content);
        editorPane.setCaretPosition(0);
        fireTitleChangedFromHtml(content);
        if (listener != null) {
            listener.onLoadFinished(currentUrl);
        }
    }
    
    public String getCurrentUrl() {
        return currentUrl;
    }
    
    public void setListener(WebViewListener listener) {
        this.listener = listener;
    }
    
    public void goBack() {
        if (historyIndex > 0) {
            historyIndex--;
            String url = history.get(historyIndex);
            loadUrlInternal(url, false);
        }
    }
    
    public void goForward() {
        if (historyIndex >= 0 && historyIndex < history.size() - 1) {
            historyIndex++;
            String url = history.get(historyIndex);
            loadUrlInternal(url, false);
        }
    }

    public boolean canGoBack() {
        return historyIndex > 0;
    }

    public boolean canGoForward() {
        return historyIndex >= 0 && historyIndex < history.size() - 1;
    }
    
    public void reload() {
        if (currentUrl != null) {
            loadUrlInternal(currentUrl, false);
        }
    }
    
    public void stop() {
        if (fxMode && fxEngine != null) {
            fxStopLoading();
            return;
        }

        if (worker != null) {
            worker.cancel(true);
            worker = null;
        }
    }
    
    public Object executeScript(String script) {
        if (fxMode && fxEngine != null) {
            return fxExecuteScript(script);
        }
        return null;
    }

    public void setUserAgent(String userAgent) {
        String ua = userAgent == null ? "" : userAgent.trim();
        if (ua.isEmpty()) {
            ua = "FinalShell EmbeddedBrowser/1.0";
        }
        this.userAgent = ua;

        Object engine = this.fxEngine;
        if (this.fxMode && engine != null) {
            String finalUa = ua;
            fxRunLater(() -> {
                try {
                    Method m = engine.getClass().getMethod("setUserAgent", String.class);
                    m.invoke(engine, finalUa);
                } catch (Throwable ignored) {
                }
            });
        }
    }

    private void loadUrlInternal(String url, boolean pushHistory) {
        if (url == null || url.trim().isEmpty()) {
            return;
        }

        String target = url.trim();
        if (!target.startsWith("http://") && !target.startsWith("https://")) {
            target = "http://" + target;
        }

        stop();
        currentUrl = target;
        if (listener != null) {
            listener.onLoadStart(target);
        }

        if (fxMode && fxEngine != null) {
            this.pendingUrl = target;
            this.pendingPushHistory = pushHistory;
            this.pendingNavSeq = navSeq.incrementAndGet();
            fxLoadUrl(target);
            return;
        }

        final String finalTarget = target;
        final String finalUa = this.userAgent;
        worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpURLConnection conn = (HttpURLConnection) new URL(finalTarget).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("User-Agent", finalUa);

                try (InputStream in = conn.getInputStream();
                     BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (isCancelled()) {
                            break;
                        }
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                } finally {
                    conn.disconnect();
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) {
                    return;
                }
                try {
                    String content = get();
                    editorPane.setText(content);
                    editorPane.setCaretPosition(0);
                    fireTitleChangedFromHtml(content);
                    if (pushHistory) {
                        historyIndex++;
                        while (history.size() > historyIndex) {
                            history.remove(history.size() - 1);
                        }
                        history.add(finalTarget);
                    }
                    if (listener != null) {
                        listener.onLoadFinished(finalTarget);
                    }
                } catch (Exception e) {
                    editorPane.setText("<html><body><h2>加载失败</h2><p>" +
                        escapeHtml(e.getMessage()) + "</p></body></html>");
                    if (listener != null) {
                        listener.onLoadError(finalTarget, String.valueOf(e.getMessage()));
                    }
                }
            }
        };

        worker.execute();
    }

    private static String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    private boolean initFxIfPossible() {
        try {
            Class<?> jfxPanelCls = Class.forName("javafx.embed.swing.JFXPanel");
            Object panel = jfxPanelCls.getConstructor().newInstance();
            if (!(panel instanceof JComponent)) {
                return false;
            }
            this.fxPanel = (JComponent) panel;

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<Throwable> err = new AtomicReference<>();
            fxRunLater(() -> {
                try {
                    Object engine = createFxWebEngine(panel);
                    this.fxEngine = engine;
                    attachFxListeners(engine);
                } catch (Throwable t) {
                    err.set(t);
                } finally {
                    latch.countDown();
                }
            });

            boolean ok = latch.await(5, TimeUnit.SECONDS);
            if (!ok || err.get() != null || this.fxEngine == null) {
                this.fxEngine = null;
                this.fxPanel = null;
                return false;
            }
            return true;
        } catch (Throwable ignored) {
            this.fxEngine = null;
            this.fxPanel = null;
            return false;
        }
    }

    private void fxRunLater(Runnable r) {
        try {
            Class<?> platformCls = Class.forName("javafx.application.Platform");
            Method runLater = platformCls.getMethod("runLater", Runnable.class);
            runLater.invoke(null, r);
        } catch (Throwable t) {
            SwingUtilities.invokeLater(r);
        }
    }

    private boolean isFxApplicationThread() {
        try {
            Class<?> platformCls = Class.forName("javafx.application.Platform");
            Method m = platformCls.getMethod("isFxApplicationThread");
            Object r = m.invoke(null);
            return Boolean.TRUE.equals(r);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private Object createFxWebEngine(Object jfxPanel) throws Exception {
        Class<?> webViewCls = Class.forName("javafx.scene.web.WebView");
        Object webView = webViewCls.getConstructor().newInstance();
        Method getEngine = webViewCls.getMethod("getEngine");
        Object engine = getEngine.invoke(webView);

        this.fxWebView = webView;

        try {
            Method setContextMenuEnabled = webViewCls.getMethod("setContextMenuEnabled", boolean.class);
            setContextMenuEnabled.invoke(webView, false);
        } catch (Throwable ignored) {
        }

        try {
            Method setUserAgent = engine.getClass().getMethod("setUserAgent", String.class);
            setUserAgent.invoke(engine, this.userAgent);
        } catch (Throwable ignored) {
        }

        Class<?> parentCls = Class.forName("javafx.scene.Parent");
        Class<?> sceneCls = Class.forName("javafx.scene.Scene");
        Constructor<?> sceneCtor = sceneCls.getConstructor(parentCls);
        Object scene = sceneCtor.newInstance(webView);

        Class<?> jfxPanelCls = Class.forName("javafx.embed.swing.JFXPanel");
        Method setScene = jfxPanelCls.getMethod("setScene", sceneCls);
        setScene.invoke(jfxPanel, scene);
        return engine;
    }

    private void attachFxListeners(Object engine) {
        try {
            Class<?> changeListenerCls = Class.forName("javafx.beans.value.ChangeListener");
            Object titleProp = engine.getClass().getMethod("titleProperty").invoke(engine);
            Object titleListener = java.lang.reflect.Proxy.newProxyInstance(
                changeListenerCls.getClassLoader(),
                new Class[]{changeListenerCls},
                (proxy, method, args) -> {
                    if ("changed".equals(method.getName()) && args != null && args.length >= 3) {
                        Object newVal = args[2];
                        if (newVal != null) {
                            String title = String.valueOf(newVal);
                            SwingUtilities.invokeLater(() -> {
                                if (listener != null) {
                                    listener.onTitleChanged(title);
                                }
                            });
                        }
                    }
                    return null;
                }
            );
            titleProp.getClass().getMethod("addListener", changeListenerCls).invoke(titleProp, titleListener);

            Object locationProp = engine.getClass().getMethod("locationProperty").invoke(engine);
            Object locationListener = java.lang.reflect.Proxy.newProxyInstance(
                changeListenerCls.getClassLoader(),
                new Class[]{changeListenerCls},
                (proxy, method, args) -> {
                    if ("changed".equals(method.getName()) && args != null && args.length >= 3) {
                        Object newVal = args[2];
                        if (newVal != null) {
                            String loc = String.valueOf(newVal);
                            currentUrl = loc;
                            SwingUtilities.invokeLater(() -> {
                                WebViewListener l = listener;
                                if (l instanceof ExtendedWebViewListener) {
                                    ((ExtendedWebViewListener) l).onLocationChanged(loc);
                                }
                            });
                            if (pendingUrl == null) {
                                pendingUrl = loc;
                                pendingPushHistory = true;
                                pendingNavSeq = navSeq.incrementAndGet();
                                SwingUtilities.invokeLater(() -> {
                                    if (listener != null) {
                                        listener.onLoadStart(loc);
                                    }
                                });
                            } else {
                                pendingUrl = loc;
                            }
                        }
                    }
                    return null;
                }
            );
            locationProp.getClass().getMethod("addListener", changeListenerCls).invoke(locationProp, locationListener);

            Object loadWorker = engine.getClass().getMethod("getLoadWorker").invoke(engine);

            try {
                Object progProp = loadWorker.getClass().getMethod("progressProperty").invoke(loadWorker);
                Object progListener = java.lang.reflect.Proxy.newProxyInstance(
                    changeListenerCls.getClassLoader(),
                    new Class[]{changeListenerCls},
                    (proxy, method, args) -> {
                        if ("changed".equals(method.getName()) && args != null && args.length >= 3) {
                            Object v = args[2];
                            double p = -1D;
                            if (v instanceof Number) {
                                p = ((Number) v).doubleValue();
                            } else if (v != null) {
                                try {
                                    p = Double.parseDouble(String.valueOf(v));
                                } catch (Exception ignored) {
                                }
                            }
                            double finalP = p;
                            SwingUtilities.invokeLater(() -> {
                                WebViewListener l = listener;
                                if (l instanceof ExtendedWebViewListener) {
                                    ((ExtendedWebViewListener) l).onProgressChanged(finalP);
                                }
                            });
                        }
                        return null;
                    }
                );
                progProp.getClass().getMethod("addListener", changeListenerCls).invoke(progProp, progListener);
            } catch (Throwable ignored) {
            }

            try {
                Object excProp = loadWorker.getClass().getMethod("exceptionProperty").invoke(loadWorker);
                Object excListener = java.lang.reflect.Proxy.newProxyInstance(
                    changeListenerCls.getClassLoader(),
                    new Class[]{changeListenerCls},
                    (proxy, method, args) -> {
                        if ("changed".equals(method.getName()) && args != null && args.length >= 3) {
                            Object ex = args[2];
                            fxLastError = ex == null ? null : String.valueOf(ex);
                        }
                        return null;
                    }
                );
                excProp.getClass().getMethod("addListener", changeListenerCls).invoke(excProp, excListener);
            } catch (Throwable ignored) {
            }

            Object stateProp = loadWorker.getClass().getMethod("stateProperty").invoke(loadWorker);
            Object stateListener = java.lang.reflect.Proxy.newProxyInstance(
                changeListenerCls.getClassLoader(),
                new Class[]{changeListenerCls},
                (proxy, method, args) -> {
                    if ("changed".equals(method.getName()) && args != null && args.length >= 3) {
                        Object newVal = args[2];
                        if (newVal != null) {
                            String state = String.valueOf(newVal);
                            if (state.endsWith("SUCCEEDED")) {
                                String url = pendingUrl != null ? pendingUrl : currentUrl;
                                boolean push = pendingPushHistory;
                                long seq = pendingNavSeq;
                                SwingUtilities.invokeLater(() -> {
                                    if (push && url != null) {
                                        historyIndex++;
                                        while (history.size() > historyIndex) {
                                            history.remove(history.size() - 1);
                                        }
                                        history.add(url);
                                    }
                                    if (listener != null && url != null) {
                                        listener.onLoadFinished(url);
                                    }
                                    clearPendingIfSeq(seq);
                                });
                            } else if (state.endsWith("FAILED")) {
                                final String url = pendingUrl != null ? pendingUrl : currentUrl;
                                String tempErrMsg = "load " + state;
                                final long seq = pendingNavSeq;
                                try {
                                    Object ex = loadWorker.getClass().getMethod("getException").invoke(loadWorker);
                                    if (ex != null) {
                                        try {
                                            Object m = ex.getClass().getMethod("getMessage").invoke(ex);
                                            if (m != null) {
                                                tempErrMsg = String.valueOf(m);
                                            } else {
                                                tempErrMsg = String.valueOf(ex);
                                            }
                                        } catch (Throwable ignored) {
                                            tempErrMsg = String.valueOf(ex);
                                        }
                                    } else if (fxLastError != null && !fxLastError.isEmpty()) {
                                        tempErrMsg = fxLastError;
                                    }
                                } catch (Throwable ignored) {
                                }
                                final String errMsg = tempErrMsg;
                                SwingUtilities.invokeLater(() -> {
                                    if (listener != null && url != null) {
                                        listener.onLoadError(url, errMsg);
                                    }
                                    clearPendingIfSeq(seq);
                                });
                            } else if (state.endsWith("CANCELLED")) {
                                final String url = pendingUrl != null ? pendingUrl : currentUrl;
                                final long seq = pendingNavSeq;
                                final String loadState = state;
                                SwingUtilities.invokeLater(() -> {
                                    if (listener != null && url != null) {
                                        listener.onLoadError(url, "load " + loadState);
                                    }
                                    clearPendingIfSeq(seq);
                                });
                            }
                        }
                    }
                    return null;
                }
            );
            stateProp.getClass().getMethod("addListener", changeListenerCls).invoke(stateProp, stateListener);
        } catch (Throwable ignored) {
        }

        try {
            Class<?> callbackCls = Class.forName("javafx.util.Callback");
            Method setCreatePopupHandler = engine.getClass().getMethod("setCreatePopupHandler", callbackCls);
            Object popupHandler = java.lang.reflect.Proxy.newProxyInstance(
                callbackCls.getClassLoader(),
                new Class[]{callbackCls},
                (proxy, method, args) -> {
                    if ("call".equals(method.getName())) {
                        return null;
                    }
                    return null;
                }
            );
            setCreatePopupHandler.invoke(engine, popupHandler);
        } catch (Throwable ignored) {
        }

        try {
            Class<?> eventHandlerCls = Class.forName("javafx.event.EventHandler");
            Method setOnStatusChanged = engine.getClass().getMethod("setOnStatusChanged", eventHandlerCls);
            Object statusHandler = java.lang.reflect.Proxy.newProxyInstance(
                eventHandlerCls.getClassLoader(),
                new Class[]{eventHandlerCls},
                (proxy, method, args) -> {
                    if ("handle".equals(method.getName()) && args != null && args.length >= 1) {
                        Object evt = args[0];
                        Object data = null;
                        try {
                            data = evt.getClass().getMethod("getData").invoke(evt);
                        } catch (Throwable ignored) {
                        }
                        String msg = data == null ? "" : String.valueOf(data);
                        SwingUtilities.invokeLater(() -> {
                            WebViewListener l = listener;
                            if (l instanceof ExtendedWebViewListener) {
                                ((ExtendedWebViewListener) l).onStatusChanged(msg);
                            }
                        });
                    }
                    return null;
                }
            );
            setOnStatusChanged.invoke(engine, statusHandler);
        } catch (Throwable ignored) {
        }

        try {
            Class<?> eventHandlerCls = Class.forName("javafx.event.EventHandler");
            Method setOnError = engine.getClass().getMethod("setOnError", eventHandlerCls);
            Object errorHandler = java.lang.reflect.Proxy.newProxyInstance(
                eventHandlerCls.getClassLoader(),
                new Class[]{eventHandlerCls},
                (proxy, method, args) -> {
                    if ("handle".equals(method.getName()) && args != null && args.length >= 1) {
                        Object evt = args[0];
                        String msg = "";
                        try {
                            Object m = evt.getClass().getMethod("getMessage").invoke(evt);
                            if (m != null) {
                                msg = String.valueOf(m);
                            }
                        } catch (Throwable ignored) {
                        }
                        if (msg == null || msg.isEmpty()) {
                            msg = String.valueOf(evt);
                        }
                        fxLastError = msg;
                    }
                    return null;
                }
            );
            setOnError.invoke(engine, errorHandler);
        } catch (Throwable ignored) {
        }

        attachFxDialogHandlers(engine);
    }

    private void clearPendingIfSeq(long seq) {
        if (pendingNavSeq != seq) {
            return;
        }
        pendingUrl = null;
        pendingPushHistory = false;
        fxLastError = null;
    }

    private void attachFxDialogHandlers(Object engine) {
        try {
            Class<?> eventHandlerCls = Class.forName("javafx.event.EventHandler");
            Method setOnAlert = engine.getClass().getMethod("setOnAlert", eventHandlerCls);
            Object alertHandler = java.lang.reflect.Proxy.newProxyInstance(
                eventHandlerCls.getClassLoader(),
                new Class[]{eventHandlerCls},
                (proxy, method, args) -> {
                    if ("handle".equals(method.getName()) && args != null && args.length >= 1) {
                        Object evt = args[0];
                        Object data = null;
                        try {
                            data = evt.getClass().getMethod("getData").invoke(evt);
                        } catch (Throwable ignored) {
                        }
                        String msg = data == null ? "" : String.valueOf(data);
                        SwingUtilities.invokeLater(() -> showAlert(msg));
                    }
                    return null;
                }
            );
            setOnAlert.invoke(engine, alertHandler);
        } catch (Throwable ignored) {
        }

        try {
            Class<?> callbackCls = Class.forName("javafx.util.Callback");
            Method setConfirmHandler = engine.getClass().getMethod("setConfirmHandler", callbackCls);
            Object confirmHandler = java.lang.reflect.Proxy.newProxyInstance(
                callbackCls.getClassLoader(),
                new Class[]{callbackCls},
                (proxy, method, args) -> {
                    if ("call".equals(method.getName()) && args != null && args.length >= 1) {
                        String msg = args[0] == null ? "" : String.valueOf(args[0]);
                        Boolean result = callOnEdtAndWait(() -> showConfirm(msg));
                        return Boolean.TRUE.equals(result);
                    }
                    return Boolean.FALSE;
                }
            );
            setConfirmHandler.invoke(engine, confirmHandler);
        } catch (Throwable ignored) {
        }

        try {
            Class<?> callbackCls = Class.forName("javafx.util.Callback");
            Method setPromptHandler = engine.getClass().getMethod("setPromptHandler", callbackCls);
            Object promptHandler = java.lang.reflect.Proxy.newProxyInstance(
                callbackCls.getClassLoader(),
                new Class[]{callbackCls},
                (proxy, method, args) -> {
                    if ("call".equals(method.getName()) && args != null && args.length >= 1) {
                        Object pd = args[0];
                        String msg = "";
                        String def = "";
                        if (pd != null) {
                            try {
                                Object m = pd.getClass().getMethod("getMessage").invoke(pd);
                                if (m != null) msg = String.valueOf(m);
                            } catch (Throwable ignored) {
                            }
                            try {
                                Object d = pd.getClass().getMethod("getDefaultValue").invoke(pd);
                                if (d != null) def = String.valueOf(d);
                            } catch (Throwable ignored) {
                            }
                        }
                        final String finalMsg = msg;
                        final String finalDef = def;
                        String result = callOnEdtAndWait(() -> showPrompt(finalMsg, finalDef));
                        return result;
                    }
                    return null;
                }
            );
            setPromptHandler.invoke(engine, promptHandler);
        } catch (Throwable ignored) {
        }
    }

    private <T> T callOnEdtAndWait(Callable<T> task) {
        if (task == null) {
            return null;
        }
        if (SwingUtilities.isEventDispatchThread()) {
            try {
                return task.call();
            } catch (Exception e) {
                return null;
            }
        }
        AtomicReference<T> result = new AtomicReference<>();
        AtomicReference<Exception> err = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        SwingUtilities.invokeLater(() -> {
            try {
                result.set(task.call());
            } catch (Exception e) {
                err.set(e);
            } finally {
                latch.countDown();
            }
        });
        try {
            latch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (err.get() != null) {
            return null;
        }
        return result.get();
    }

    protected void showAlert(String message) {
        try {
            JOptionPane.showMessageDialog(this, message, "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ignored) {
        }
    }

    protected boolean showConfirm(String message) {
        try {
            int r = JOptionPane.showConfirmDialog(this, message, "确认", JOptionPane.YES_NO_OPTION);
            return r == JOptionPane.YES_OPTION;
        } catch (Exception ignored) {
            return false;
        }
    }

    protected String showPrompt(String message, String defaultValue) {
        try {
            return JOptionPane.showInputDialog(this, message, defaultValue);
        } catch (Exception ignored) {
            return null;
        }
    }

    private void fxLoadUrl(String url) {
        Object engine = this.fxEngine;
        if (engine == null) {
            return;
        }
        fxRunLater(() -> {
            try {
                Method load = engine.getClass().getMethod("load", String.class);
                load.invoke(engine, url);
            } catch (Throwable ignored) {
            }
        });
    }

    private void fxLoadContent(String content) {
        Object engine = this.fxEngine;
        if (engine == null) {
            return;
        }
        this.pendingUrl = currentUrl;
        this.pendingPushHistory = false;
        this.pendingNavSeq = navSeq.incrementAndGet();
        fxRunLater(() -> {
            try {
                Method loadContent = engine.getClass().getMethod("loadContent", String.class);
                loadContent.invoke(engine, content);
            } catch (Throwable ignored) {
            }
        });
    }

    private void fxStopLoading() {
        Object engine = this.fxEngine;
        if (engine == null) {
            return;
        }
        fxRunLater(() -> {
            try {
                Object loadWorker = engine.getClass().getMethod("getLoadWorker").invoke(engine);
                Method cancel = loadWorker.getClass().getMethod("cancel");
                cancel.invoke(loadWorker);
            } catch (Throwable ignored) {
            }
        });
    }

    private Object fxExecuteScript(String script) {
        Object engine = this.fxEngine;
        if (engine == null || script == null) {
            return null;
        }

        if (isFxApplicationThread()) {
            try {
                Method exec = engine.getClass().getMethod("executeScript", String.class);
                return exec.invoke(engine, script);
            } catch (Throwable ignored) {
                return null;
            }
        }

        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Object> result = new AtomicReference<>();
        fxRunLater(() -> {
            try {
                Method exec = engine.getClass().getMethod("executeScript", String.class);
                result.set(exec.invoke(engine, script));
            } catch (Throwable ignored) {
            } finally {
                latch.countDown();
            }
        });

        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return result.get();
    }

    private void fireTitleChangedFromHtml(String html) {
        if (listener == null || html == null) {
            return;
        }
        String title = extractTitle(html);
        if (title == null || title.isEmpty()) {
            return;
        }
        try {
            listener.onTitleChanged(title);
        } catch (Exception ignored) {
        }
    }

    private static String extractTitle(String html) {
        if (html == null) {
            return null;
        }
        Pattern p = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(html);
        if (!m.find()) {
            return null;
        }
        String t = m.group(1);
        if (t == null) {
            return null;
        }
        return t.replaceAll("\\s+", " ").trim();
    }
    
    public interface WebViewListener {
        void onLoadStart(String url);
        void onLoadFinished(String url);
        void onLoadError(String url, String error);
        void onTitleChanged(String title);
    }

    public interface ExtendedWebViewListener extends WebViewListener {
        void onLocationChanged(String url);
        void onProgressChanged(double progress);
        void onStatusChanged(String status);
    }
}
