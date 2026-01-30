package com.finalshell.ui.browser;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Swing/JavaFX混合WebView组件
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class SwingFXWebView extends JPanel {
    
    private String currentUrl;
    private WebViewListener listener;
    private JEditorPane editorPane;
    private SwingWorker<String, Void> worker;
    private final List<String> history = new ArrayList<>();
    private int historyIndex = -1;
    
    public SwingFXWebView() {
        setLayout(new BorderLayout());
        initComponents();
    }
    
    private void initComponents() {
        editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");
        add(new JScrollPane(editorPane), BorderLayout.CENTER);
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
        editorPane.setText(content == null ? "" : content);
        editorPane.setCaretPosition(0);
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
    
    public void reload() {
        if (currentUrl != null) {
            loadUrlInternal(currentUrl, false);
        }
    }
    
    public void stop() {
        if (worker != null) {
            worker.cancel(true);
            worker = null;
        }
    }
    
    public Object executeScript(String script) {
        return null;
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

        final String finalTarget = target;
        worker = new SwingWorker<>() {
            @Override
            protected String doInBackground() throws Exception {
                HttpURLConnection conn = (HttpURLConnection) new URL(finalTarget).openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(30000);
                conn.setRequestProperty("User-Agent", "FinalShell EmbeddedBrowser/1.0");

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
    
    public interface WebViewListener {
        void onLoadStart(String url);
        void onLoadFinished(String url);
        void onLoadError(String url, String error);
        void onTitleChanged(String title);
    }
}
