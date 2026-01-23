package com.finalshell.control;

import com.finalshell.App;

/**
 * 检查线程
 * 后台线程持续检查控制客户端状态
 */
public class CheckThread implements Runnable {
    
    private volatile boolean running = true;
    
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            ControlClient controlClient = App.getInstance().getControlClient();
            if (controlClient == null || !controlClient.isConnected()) {
                continue;
            }
            controlClient.checkStatus();
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public boolean isRunning() {
        return running;
    }
}
