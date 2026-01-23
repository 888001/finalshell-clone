package com.finalshell.network;

import javax.swing.*;
import java.awt.*;

/**
 * 网络连接详情面板
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class NetDetailPanel extends JPanel {
    
    private JLabel protocolLabel;
    private JLabel localLabel;
    private JLabel remoteLabel;
    private JLabel stateLabel;
    private JLabel pidLabel;
    private JLabel processLabel;
    private JLabel rxLabel;
    private JLabel txLabel;
    
    public NetDetailPanel() {
        initUI();
    }
    
    private void initUI() {
        setLayout(new GridLayout(4, 2, 10, 5));
        setBorder(BorderFactory.createTitledBorder("连接详情"));
        
        add(new JLabel("协议:"));
        protocolLabel = new JLabel("-");
        add(protocolLabel);
        
        add(new JLabel("本地地址:"));
        localLabel = new JLabel("-");
        add(localLabel);
        
        add(new JLabel("远程地址:"));
        remoteLabel = new JLabel("-");
        add(remoteLabel);
        
        add(new JLabel("状态:"));
        stateLabel = new JLabel("-");
        add(stateLabel);
    }
    
    public void showDetail(NetRow row) {
        if (row == null) {
            clear();
            return;
        }
        
        protocolLabel.setText(row.getProtocol());
        localLabel.setText(row.getLocalEndpoint());
        remoteLabel.setText(row.getRemoteEndpoint());
        stateLabel.setText(row.getState());
    }
    
    public void clear() {
        protocolLabel.setText("-");
        localLabel.setText("-");
        remoteLabel.setText("-");
        stateLabel.setText("-");
    }
}
