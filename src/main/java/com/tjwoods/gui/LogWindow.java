package com.tjwoods.gui;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogWindow extends JPanel {
    private JTextPane logPane;
    private StyledDocument doc;
    private Style requestStyle;
    private Style responseStyle;
    private Style separatorStyle;
    private SimpleDateFormat dateFormat;

    public LogWindow() {
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        setBackground(new Color(255, 255, 255));
        setPreferredSize(new Dimension(800, 400));

        logPane = new JTextPane();
        logPane.setEditable(false);
        logPane.setFont(new Font("Consolas", Font.PLAIN, 12));

        doc = logPane.getStyledDocument();

        // 创建样式
        StyleContext styleContext = new StyleContext();

        requestStyle = styleContext.addStyle("Request", null);
        StyleConstants.setForeground(requestStyle, new Color(41, 128, 185));
        StyleConstants.setBold(requestStyle, true);

        responseStyle = styleContext.addStyle("Response", null);
        StyleConstants.setForeground(responseStyle, new Color(39, 174, 96));
        StyleConstants.setBold(responseStyle, true);

        separatorStyle = styleContext.addStyle("Separator", null);
        StyleConstants.setForeground(separatorStyle, new Color(189, 195, 199));
        StyleConstants.setBold(separatorStyle, true);

        Style normalStyle = styleContext.addStyle("Normal", null);
        StyleConstants.setForeground(normalStyle, new Color(51, 51, 51));

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        JScrollPane scrollPane = new JScrollPane(logPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        add(scrollPane, BorderLayout.CENTER);

        // 清空按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 255, 255));
        JButton clearButton = new JButton("清空日志");
        clearButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        clearButton.setBackground(new Color(149, 165, 166));
        clearButton.setForeground(Color.WHITE);
        clearButton.setFocusPainted(false);
        clearButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        clearButton.addActionListener(e -> {
            try {
                doc.remove(0, doc.getLength());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
        buttonPanel.add(clearButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void logRequest(String method, String path, String requestHeaders, String requestBody) {
        try {
            // 时间戳
            String timestamp = dateFormat.format(new Date());
            doc.insertString(doc.getLength(),
                    "\n[" + timestamp + "] " + method + " " + path + "\n", separatorStyle);

            doc.insertString(doc.getLength(), "=== 请求 ===\n", requestStyle);

            if (requestHeaders != null && !requestHeaders.isEmpty()) {
                doc.insertString(doc.getLength(), "请求头:\n", separatorStyle);
                doc.insertString(doc.getLength(), requestHeaders + "\n", null);
            }

            if (requestBody != null && !requestBody.isEmpty()) {
                doc.insertString(doc.getLength(), "请求内容:\n", separatorStyle);
                doc.insertString(doc.getLength(), requestBody + "\n", null);
            }

            doc.insertString(doc.getLength(), "\n", null);

            // 自动滚动到底部
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void logResponse(int statusCode, String responseHeaders, String responseBody) {
        try {
            doc.insertString(doc.getLength(), "=== 响应 ===\n", responseStyle);
            doc.insertString(doc.getLength(), "状态码: " + statusCode + "\n", null);

            if (responseHeaders != null && !responseHeaders.isEmpty()) {
                doc.insertString(doc.getLength(), "响应头:\n", separatorStyle);
                doc.insertString(doc.getLength(), responseHeaders + "\n", null);
            }

            if (responseBody != null && !responseBody.isEmpty()) {
                doc.insertString(doc.getLength(), "响应内容:\n", separatorStyle);
                doc.insertString(doc.getLength(), responseBody + "\n", null);
            }

            doc.insertString(doc.getLength(), "─────────────────────────────────────\n", separatorStyle);

            // 自动滚动到底部
            logPane.setCaretPosition(doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void clearLog() {
        try {
            doc.remove(0, doc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
