package com.tjwoods.gui;

import com.tjwoods.engine.HttpServerEngine;
import com.tjwoods.model.RouteConfig;
import com.tjwoods.model.ServerConfig;
import com.tjwoods.util.JsonUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {
    private HttpServerEngine serverEngine;
    private ServerConfig serverConfig;
    private LogWindow logWindow;
    private JDialog logDialog;

    private JTextField portField;
    private JButton startButton;
    private JButton stopButton;
    private JButton logButton;
    private JLabel statusLabel;
    private JTable routesTable;
    private DefaultTableModel tableModel;

    public MainFrame() {
        serverConfig = new ServerConfig();
        logWindow = new LogWindow();
        initUI();
    }

    private void initUI() {
        setTitle("Moker - HTTP Server Builder");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 设置窗口图标
        setIconImage(createRocketIcon());

        // 主面板
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 242, 245));

        // 顶部配置面板
        JPanel configPanel = createConfigPanel();
        mainPanel.add(configPanel, BorderLayout.NORTH);

        // 中间路由列表
        JPanel routesPanel = createRoutesPanel();
        mainPanel.add(routesPanel, BorderLayout.CENTER);

        // 底部按钮面板
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // 将主面板添加到窗格
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createConfigPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        "服务器配置",
                        0,
                        0,
                        new Font("Microsoft YaHei", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(new Color(255, 255, 255));

        JLabel portLabel = new JLabel("端口:");
        portLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        portLabel.setForeground(new Color(51, 51, 51));
        panel.add(portLabel);

        portField = new JTextField("8080", 10);
        portField.setFont(new Font("Arial", Font.PLAIN, 13));
        portField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(portField);

        statusLabel = new JLabel("状态: 已停止");
        statusLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        statusLabel.setForeground(new Color(235, 77, 75));
        panel.add(statusLabel);

        // 添加日志按钮 - 使用 Unicode 符号而不是 emoji
        logButton = new JButton("📝 日志");
        logButton.setFont(new Font("Segoe UI Symbol, Arial Unicode MS, Microsoft YaHei", Font.PLAIN, 12));
        logButton.setBackground(new Color(241, 196, 15));
        logButton.setForeground(Color.WHITE);
        logButton.setFocusPainted(false);
        logButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        logButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logButton.addActionListener(e -> toggleLogWindow());
        panel.add(logButton);

        return panel;
    }

    private JPanel createRoutesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                        "路由配置",
                        0,
                        0,
                        new Font("Microsoft YaHei", Font.BOLD, 14)
                ),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        panel.setBackground(new Color(255, 255, 255));

        // 表格
        String[] columnNames = {"方法", "路径", "状态码", "内容类型"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        routesTable = new JTable(tableModel);
        routesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        routesTable.getTableHeader().setReorderingAllowed(false);

        // 表格样式
        routesTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        routesTable.setRowHeight(28);
        routesTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        routesTable.getTableHeader().setBackground(new Color(243, 246, 249));
        routesTable.getTableHeader().setForeground(new Color(51, 51, 51));
        routesTable.setSelectionBackground(new Color(52, 152, 219));
        routesTable.setSelectionForeground(Color.WHITE);
        routesTable.setGridColor(new Color(236, 240, 241));

        JScrollPane scrollPane = new JScrollPane(routesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        scrollPane.setPreferredSize(new Dimension(800, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        // 路由操作按钮
        JPanel routeButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        routeButtons.setBackground(new Color(255, 255, 255));

        JButton addRouteButton = createStyledButton("添加路由", new Color(52, 152, 219));
        JButton editRouteButton = createStyledButton("编辑路由", new Color(155, 89, 182));
        JButton deleteRouteButton = createStyledButton("删除路由", new Color(231, 76, 60));

        addRouteButton.addActionListener(this::onAddRoute);
        editRouteButton.addActionListener(this::onEditRoute);
        deleteRouteButton.addActionListener(this::onDeleteRoute);

        routeButtons.add(addRouteButton);
        routeButtons.add(editRouteButton);
        routeButtons.add(deleteRouteButton);
        panel.add(routeButtons, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 20, 8, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
        return button;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBackground(new Color(240, 242, 245));

        startButton = createStyledButton("启动服务器", new Color(46, 204, 113));
        stopButton = createStyledButton("停止服务器", new Color(231, 76, 60));
        stopButton.setEnabled(false);

        startButton.addActionListener(this::onStartServer);
        stopButton.addActionListener(this::onStopServer);

        panel.add(startButton);
        panel.add(stopButton);

        return panel;
    }

    private void onAddRoute(ActionEvent e) {
        RouteConfig route = showRouteDialog(null);
        if (route != null) {
            try {
                serverConfig.addRoute(route);
                updateRoutesTable();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onEditRoute(ActionEvent e) {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择要编辑的路由", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        RouteConfig route = serverConfig.getRoutes().get(selectedRow);
        RouteConfig updatedRoute = showRouteDialog(route);
        if (updatedRoute != null) {
            try {
                // 如果方法和路径没有变化，直接替换当前路由
                if (updatedRoute.getMethod().equals(route.getMethod()) &&
                        updatedRoute.getPath().equals(route.getPath())) {
                    serverConfig.getRoutes().set(selectedRow, updatedRoute);
                } else {
                    // 如果方法和路径变化了，先删除旧的，再添加新的
                    serverConfig.removeRoute(route);
                    serverConfig.addRoute(updatedRoute);
                }
                updateRoutesTable();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onDeleteRoute(ActionEvent e) {
        int selectedRow = routesTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "请选择要删除的路由", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "确定要删除选中的路由吗？",
                "确认", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            serverConfig.getRoutes().remove(selectedRow);
            updateRoutesTable();
        }
    }

    private RouteConfig showRouteDialog(RouteConfig existingRoute) {
        JDialog dialog = new JDialog(this, existingRoute == null ? "添加路由" : "编辑路由", true);
        dialog.setSize(900, 850);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(new Color(240, 242, 245));

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(new Color(255, 255, 255));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 方法
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel methodLabel = new JLabel("HTTP 方法:");
        methodLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        methodLabel.setForeground(new Color(51, 51, 51));
        panel.add(methodLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        String[] methods = {"GET", "POST", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS"};
        JComboBox<String> methodCombo = new JComboBox<>(methods);
        methodCombo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        if (existingRoute != null) {
            methodCombo.setSelectedItem(existingRoute.getMethod());
        }
        panel.add(methodCombo, gbc);

        // 路径
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        JLabel pathLabel = new JLabel("路径:");
        pathLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        pathLabel.setForeground(new Color(51, 51, 51));
        panel.add(pathLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField pathField = new JTextField(existingRoute != null ? existingRoute.getPath() : "/api/test", 25);
        pathField.setFont(new Font("Arial", Font.PLAIN, 13));
        pathField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(pathField, gbc);

        // 状态码
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        JLabel statusCodeLabel = new JLabel("状态码:");
        statusCodeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        statusCodeLabel.setForeground(new Color(51, 51, 51));
        panel.add(statusCodeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField statusCodeField = new JTextField(existingRoute != null ?
                String.valueOf(existingRoute.getStatusCode()) : "200", 25);
        statusCodeField.setFont(new Font("Arial", Font.PLAIN, 13));
        statusCodeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(statusCodeField, gbc);

        // 内容类型
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        JLabel contentTypeLabel = new JLabel("内容类型:");
        contentTypeLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        contentTypeLabel.setForeground(new Color(51, 51, 51));
        panel.add(contentTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        JTextField contentTypeField = new JTextField(existingRoute != null ?
                existingRoute.getContentType() : "application/json", 25);
        contentTypeField.setFont(new Font("Arial", Font.PLAIN, 13));
        contentTypeField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        panel.add(contentTypeField, gbc);

        // 响应头设置
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel headersLabel = new JLabel("响应头:");
        headersLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        headersLabel.setForeground(new Color(51, 51, 51));
        panel.add(headersLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 1;
        gbc.weighty = 0.5;
        gbc.fill = GridBagConstraints.BOTH;

        // 创建响应头表格（参考 Postman 形式）
        String[] headerColumns = {"Key", "Value", ""};
        DefaultTableModel headerTableModel = new DefaultTableModel(headerColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column < 2;
            }
        };

        // 添加现有响应头
        System.out.println("===== 加载响应头 =====");
        if (existingRoute != null && existingRoute.getHeaders() != null) {
            System.out.println("加载的响应头数量: " + existingRoute.getHeaders().size());
            for (Map.Entry<String, String> entry : existingRoute.getHeaders().entrySet()) {
                System.out.println("  -> 添加: " + entry.getKey() + " = " + entry.getValue());
                headerTableModel.addRow(new Object[]{entry.getKey(), entry.getValue(), "删除"});
            }
        } else {
            System.out.println("没有加载响应头: existingRoute=" + existingRoute);
            if (existingRoute != null) {
                System.out.println("existingRoute.getHeaders()=" + existingRoute.getHeaders());
            }
        }
        System.out.println("==================");

        // 如果没有现有响应头，添加一个空行
        if (headerTableModel.getRowCount() == 0) {
            headerTableModel.addRow(new Object[]{"", "", "删除"});
        }

        JTable headersTable = new JTable(headerTableModel);
        headersTable.setRowHeight(30);
        headersTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        headersTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        headersTable.getTableHeader().setBackground(new Color(243, 246, 249));
        headersTable.getTableHeader().setForeground(new Color(51, 51, 51));

        // 设置列宽
        headersTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        headersTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        headersTable.getColumnModel().getColumn(2).setPreferredWidth(80);

        // 为删除按钮列设置渲染器和编辑器
        headersTable.getColumnModel().getColumn(2).setCellRenderer(new ButtonRenderer());
        headersTable.getColumnModel().getColumn(2).setCellEditor(new ButtonEditor(headersTable, headerTableModel));

        // 添加表格选择监听器，用于删除操作
        headersTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = headersTable.columnAtPoint(e.getPoint());
                if (col == 2) { // 点击删除按钮列
                    int row = headersTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        if (headerTableModel.getRowCount() > 1) {
                            headerTableModel.removeRow(row);
                        } else {
                            // 如果只剩一行，清空内容
                            headerTableModel.setValueAt("", 0, 0);
                            headerTableModel.setValueAt("", 0, 1);
                        }
                    }
                }
            }
        });

        JScrollPane headersScrollPane = new JScrollPane(headersTable);
        headersScrollPane.setPreferredSize(new Dimension(400, 500));
        headersScrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199), 1));
        panel.add(headersScrollPane, gbc);

        // 添加响应头按钮
        JPanel headerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        headerButtonPanel.setBackground(new Color(255, 255, 255));

        JButton addHeaderButton = new JButton("+ 添加响应头");
        addHeaderButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        addHeaderButton.setBackground(new Color(46, 204, 113));
        addHeaderButton.setForeground(Color.WHITE);
        addHeaderButton.setFocusPainted(false);
        addHeaderButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        addHeaderButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addHeaderButton.addActionListener(e -> {
            headerTableModel.addRow(new Object[]{"", "", "删除"});
        });

        headerButtonPanel.add(addHeaderButton);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(headerButtonPanel, gbc);

        // 响应内容
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0;
        JLabel responseLabel = new JLabel("响应内容:");
        responseLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        responseLabel.setForeground(new Color(51, 51, 51));
        panel.add(responseLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;

        String initialResponse = existingRoute != null ? existingRoute.getResponseBody() :
                "{\"message\": \"Hello World\"}";
        JTextArea responseArea = new JTextArea(initialResponse, 12, 25);
        responseArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        responseArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
        ));
        // 自动格式化 JSON
        if (JsonUtil.isValidJson(initialResponse)) {
            responseArea.setText(JsonUtil.prettify(initialResponse));
        }

        JScrollPane scrollPane = new JScrollPane(responseArea);
        panel.add(scrollPane, gbc);

        // JSON 格式化按钮
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel jsonButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        jsonButtonPanel.setBackground(new Color(255, 255, 255));

        JButton formatJsonButton = new JButton("格式化 JSON");
        formatJsonButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        formatJsonButton.setBackground(new Color(52, 152, 219));
        formatJsonButton.setForeground(Color.WHITE);
        formatJsonButton.setFocusPainted(false);
        formatJsonButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        formatJsonButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton compactJsonButton = new JButton("紧凑化 JSON");
        compactJsonButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        compactJsonButton.setBackground(new Color(155, 89, 182));
        compactJsonButton.setForeground(Color.WHITE);
        compactJsonButton.setFocusPainted(false);
        compactJsonButton.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        compactJsonButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        formatJsonButton.addActionListener(evt -> {
            String text = responseArea.getText();
            String formatted = JsonUtil.prettify(text);
            if (!formatted.equals(text)) {
                responseArea.setText(formatted);
            } else {
                JOptionPane.showMessageDialog(dialog, "无法格式化：不是有效的 JSON 或已经格式化", "提示", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        compactJsonButton.addActionListener(evt -> {
            String text = responseArea.getText();
            String compacted = JsonUtil.compact(text);
            if (!compacted.equals(text)) {
                responseArea.setText(compacted);
            }
        });

        jsonButtonPanel.add(formatJsonButton);
        jsonButtonPanel.add(compactJsonButton);
        panel.add(jsonButtonPanel, gbc);

        // 按钮面板
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.setBackground(new Color(255, 255, 255));

        JButton okButton = new JButton("确定");
        okButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        okButton.setBackground(new Color(46, 204, 113));
        okButton.setForeground(Color.WHITE);
        okButton.setFocusPainted(false);
        okButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JButton cancelButton = new JButton("取消");
        cancelButton.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(8, 25, 8, 25));
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        final RouteConfig[] result = {null};

        okButton.addActionListener(evt -> {
            try {
                // 停止表格的编辑状态，确保数据已经提交到模型
                if (headersTable.isEditing()) {
                    headersTable.getCellEditor().stopCellEditing();
                }
                // 强制表格失去焦点，确保编辑器完全停止
                headersTable.transferFocus();

                String method = (String) methodCombo.getSelectedItem();
                String path = pathField.getText().trim();
                int statusCode = Integer.parseInt(statusCodeField.getText().trim());
                String contentType = contentTypeField.getText().trim();
                String response = responseArea.getText();

                if (path.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "路径不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // 从表格中解析响应头
                Map<String, String> headers = new HashMap<>();
                System.out.println("===== 保存响应头 =====");
                System.out.println("表格行数: " + headerTableModel.getRowCount());
                for (int i = 0; i < headerTableModel.getRowCount(); i++) {
                    String key = (String) headerTableModel.getValueAt(i, 0);
                    String value = (String) headerTableModel.getValueAt(i, 1);
                    System.out.println("第 " + i + " 行: key='" + key + "', value='" + value + "'");
                    if (key != null && value != null && !key.trim().isEmpty() && !value.trim().isEmpty()) {
                        String trimmedKey = key.trim();
                        String trimmedValue = value.trim();
                        headers.put(trimmedKey, trimmedValue);
                        System.out.println("  -> 添加: " + trimmedKey + " = " + trimmedValue);
                    } else {
                        System.out.println("  -> 跳过: key 或 value 为空");
                    }
                }
                System.out.println("最终保存的响应头数量: " + headers.size());
                System.out.println("保存的响应头: " + headers);
                System.out.println("==================");

                RouteConfig route = new RouteConfig();
                route.setMethod(method);
                route.setPath(path);
                route.setStatusCode(statusCode);
                route.setContentType(contentType);
                route.setResponseBody(response);
                // 始终设置响应头，即使是空的也要设置为 null
                if (!headers.isEmpty()) {
                    route.setHeaders(headers);
                } else {
                    route.setHeaders(null);
                }

                result[0] = route;
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "状态码必须是数字", "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(evt -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);

        return result[0];
    }

    private void onStartServer(ActionEvent e) {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            serverConfig.setPort(port);

            serverEngine = new HttpServerEngine(serverConfig);
            serverEngine.setLogWindow(logWindow);
            serverEngine.start();

            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            portField.setEnabled(false);
            statusLabel.setText("状态: 运行中 (端口 " + port + ")");
            statusLabel.setForeground(new Color(0, 128, 0));

            JOptionPane.showMessageDialog(this, "服务器启动成功！\n端口: " + port,
                    "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "请输入有效的端口号", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "启动服务器失败: " + ex.getMessage(),
                    "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onStopServer(ActionEvent e) {
        if (serverEngine != null) {
            serverEngine.stop();
            serverEngine = null;
        }

        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        portField.setEnabled(true);
        statusLabel.setText("状态: 已停止");
        statusLabel.setForeground(Color.RED);
    }

    private void updateRoutesTable() {
        tableModel.setRowCount(0);
        for (RouteConfig route : serverConfig.getRoutes()) {
            Object[] row = {
                    route.getMethod(),
                    route.getPath(),
                    route.getStatusCode(),
                    route.getContentType()
            };
            tableModel.addRow(row);
        }
    }

    private void toggleLogWindow() {
        if (logDialog == null) {
            // 创建日志对话框
            logDialog = new JDialog(this, "请求日志", false);
            logDialog.setSize(850, 550);
            logDialog.setLocationRelativeTo(this);
            logDialog.getContentPane().setLayout(new BorderLayout());
            logDialog.getContentPane().add(logWindow, BorderLayout.CENTER);

            // 对话框关闭时更新按钮文本
            logDialog.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e) {
                    logButton.setText("📝 日志");
                }
            });
        }

        if (logDialog.isVisible()) {
            logDialog.setVisible(false);
            logButton.setText("📝 日志");
        } else {
            logDialog.setVisible(true);
            logButton.setText("📝 日志 (已展开)");
        }
    }

    /**
     * 创建火箭图标
     */
    private Image createRocketIcon() {
        // 创建一个 64x64 的图像
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 启用抗锯齿
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(52, 152, 219),
                64, 64, new Color(155, 89, 182));
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, 64, 64, 12, 12);

        // 绘制火箭形状（简化版）
        g2d.setColor(new Color(255, 255, 255));
        g2d.setFont(new Font("Segoe UI Emoji, Arial Unicode MS", Font.PLAIN, 32));
        FontMetrics fm = g2d.getFontMetrics();
        String rocket = "🚀";
        int x = (64 - fm.stringWidth(rocket)) / 2;
        int y = (64 + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(rocket, x, y);

        // 添加阴影效果
        g2d.setColor(new Color(0, 0, 0, 50));
        g2d.fillRect(4, 60, 56, 4);

        g2d.dispose();
        return image;
    }

    /**
     * 按钮渲染器
     */
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
            setBackground(new Color(231, 76, 60));
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            return this;
        }
    }

    /**
     * 按钮编辑器
     */
    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private DefaultTableModel model;
        private int currentRow;

        public ButtonEditor(JTable table, DefaultTableModel model) {
            super(new JCheckBox());
            this.table = table;
            this.model = model;
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
            button.setBackground(new Color(231, 76, 60));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.addActionListener(e -> {
                // 直接执行删除逻辑
                if (model.getRowCount() > 1) {
                    model.removeRow(currentRow);
                } else {
                    // 如果只剩一行，清空内容
                    model.setValueAt("", 0, 0);
                    model.setValueAt("", 0, 1);
                }
                fireEditingStopped();
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            currentRow = row;
            return button;
        }

        public Object getCellEditorValue() {
            return label;
        }
    }
}
