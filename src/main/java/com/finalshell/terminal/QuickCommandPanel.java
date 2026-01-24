package com.finalshell.terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Quick Command Panel - Panel for quick command execution
 * 
 * Based on analysis of FinalShell 3.8.3
 */
public class QuickCommandPanel extends JPanel implements QuickCommandManager.QuickCommandListener {
    
    private static final Logger logger = LoggerFactory.getLogger(QuickCommandPanel.class);
    
    private final QuickCommandManager commandManager;
    private final CommandExecutor executor;
    
    private JList<QuickCommand> commandList;
    private DefaultListModel<QuickCommand> listModel;
    private JTextField searchField;
    private JButton addBtn;
    private JButton editBtn;
    private JButton deleteBtn;
    private JButton executeBtn;
    
    public QuickCommandPanel(CommandExecutor executor) {
        this.executor = executor;
        this.commandManager = QuickCommandManager.getInstance();
        
        initComponents();
        initLayout();
        initListeners();
        loadCommands();
        
        commandManager.addListener(this);
    }
    
    private void initComponents() {
        listModel = new DefaultListModel<>();
        commandList = new JList<>(listModel);
        commandList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commandList.setCellRenderer(new CommandCellRenderer());
        
        searchField = new JTextField(15);
        searchField.putClientProperty("JTextField.placeholderText", "搜索命令...");
        
        addBtn = new JButton("添加");
        editBtn = new JButton("编辑");
        deleteBtn = new JButton("删除");
        executeBtn = new JButton("执行");
        
        editBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        executeBtn.setEnabled(false);
    }
    
    private void initLayout() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Top: Search
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.add(new JLabel("快捷命令"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        add(searchPanel, BorderLayout.NORTH);
        
        // Center: Command list
        JScrollPane scrollPane = new JScrollPane(commandList);
        scrollPane.setPreferredSize(new Dimension(200, 300));
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonPanel.add(addBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(executeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void initListeners() {
        // Search
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filterCommands(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filterCommands(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filterCommands(); }
        });
        
        // Selection
        commandList.addListSelectionListener(e -> {
            boolean selected = commandList.getSelectedValue() != null;
            editBtn.setEnabled(selected);
            deleteBtn.setEnabled(selected);
            executeBtn.setEnabled(selected);
        });
        
        // Double-click to execute
        commandList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    executeSelected();
                }
            }
        });
        
        // Buttons
        addBtn.addActionListener(e -> addCommand());
        editBtn.addActionListener(e -> editCommand());
        deleteBtn.addActionListener(e -> deleteCommand());
        executeBtn.addActionListener(e -> executeSelected());
    }
    
    private void loadCommands() {
        listModel.clear();
        for (QuickCommand cmd : commandManager.getCommands()) {
            listModel.addElement(cmd);
        }
    }
    
    private void filterCommands() {
        String keyword = searchField.getText().trim();
        listModel.clear();
        
        List<QuickCommand> commands = keyword.isEmpty() 
            ? commandManager.getCommands() 
            : commandManager.searchCommands(keyword);
        
        for (QuickCommand cmd : commands) {
            listModel.addElement(cmd);
        }
    }
    
    private void addCommand() {
        QuickCommandDialog dialog = new QuickCommandDialog(
            SwingUtilities.getWindowAncestor(this), null);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            commandManager.addCommand(dialog.getResult());
            commandManager.saveCommands();
        }
    }
    
    private void editCommand() {
        QuickCommand selected = commandList.getSelectedValue();
        if (selected == null) return;
        
        QuickCommandDialog dialog = new QuickCommandDialog(
            SwingUtilities.getWindowAncestor(this), selected);
        dialog.setVisible(true);
        
        if (dialog.isConfirmed()) {
            commandManager.updateCommand(dialog.getResult());
            commandManager.saveCommands();
        }
    }
    
    private void deleteCommand() {
        QuickCommand selected = commandList.getSelectedValue();
        if (selected == null) return;
        
        int result = JOptionPane.showConfirmDialog(this,
            "确定删除命令 \"" + selected.getName() + "\"?",
            "确认删除", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            commandManager.removeCommand(selected);
            commandManager.saveCommands();
        }
    }
    
    private void executeSelected() {
        QuickCommand selected = commandList.getSelectedValue();
        if (selected != null && executor != null) {
            executor.executeCommand(selected.getCommand(), selected.isSendEnter());
            logger.debug("Executed quick command: {}", selected.getName());
            
            // Show brief tooltip notification
            showExecutionTooltip(selected.getName());
        }
    }
    
    private void showExecutionTooltip(String commandName) {
        JToolTip tip = new JToolTip();
        tip.setTipText("已执行: " + commandName);
        
        Popup popup = PopupFactory.getSharedInstance().getPopup(
            this, tip, 
            getLocationOnScreen().x + 10, 
            getLocationOnScreen().y + getHeight() - 30);
        popup.show();
        
        Timer timer = new Timer(1500, e -> popup.hide());
        timer.setRepeats(false);
        timer.start();
    }
    
    @Override
    public void onCommandsChanged() {
        SwingUtilities.invokeLater(this::filterCommands);
    }
    
    /**
     * Command cell renderer
     */
    private static class CommandCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof QuickCommand) {
                QuickCommand cmd = (QuickCommand) value;
                setText("<html><b>" + cmd.getName() + "</b><br><small>" + 
                    cmd.getCommand() + "</small></html>");
            }
            
            return this;
        }
    }
    
    /**
     * Command executor interface
     */
    public interface CommandExecutor {
        void executeCommand(String command, boolean sendEnter);
    }
}
