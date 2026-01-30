package com.finalshell.control;

import java.awt.Frame;

/**
 * 专业版介绍对话框
 */
public class ProIntroDialog extends com.finalshell.ui.dialog.ProIntroDialog {
    private static final long serialVersionUID = 2036878767831364221L;
    
    public ProIntroDialog() {
        this((Frame) null);
    }

    public ProIntroDialog(Frame owner) {
        super(owner);
    }
}
