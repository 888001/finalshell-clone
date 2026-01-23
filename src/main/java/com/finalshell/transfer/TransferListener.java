package com.finalshell.transfer;

/**
 * 传输监听器接口
 * 
 * Based on analysis of FinalShell 3.8.3
 * Reference: FtpTransfer_Event_DeepAnalysis.md
 */
public interface TransferListener {
    
    void onTransferStart(TransEvent event);
    
    void onTransferProgress(TransEvent event);
    
    void onTransferComplete(TransEvent event);
    
    void onTransferError(TransEvent event);
    
    void onTransferCancelled(TransEvent event);
    
    /**
     * 适配器类
     */
    class Adapter implements TransferListener {
        @Override
        public void onTransferStart(TransEvent event) {}
        
        @Override
        public void onTransferProgress(TransEvent event) {}
        
        @Override
        public void onTransferComplete(TransEvent event) {}
        
        @Override
        public void onTransferError(TransEvent event) {}
        
        @Override
        public void onTransferCancelled(TransEvent event) {}
    }
}
