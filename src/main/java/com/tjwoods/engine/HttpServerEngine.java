package com.tjwoods.engine;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.tjwoods.gui.LogWindow;
import com.tjwoods.model.RouteConfig;
import com.tjwoods.model.ServerConfig;
import com.tjwoods.util.JsonUtil;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.Map;

public class HttpServerEngine {
    private HttpServer server;
    private ServerConfig config;
    private boolean running;
    private LogWindow logWindow;

    public HttpServerEngine(ServerConfig config) {
        this.config = config;
        this.running = false;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String contentType, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType);
        byte[] responseBytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }

    public void start() throws IOException {
        if (running) {
            throw new IllegalStateException("Server is already running");
        }

        server = HttpServer.create(new InetSocketAddress(config.getPort()), 0);

        // 为每个路由创建处理器
        for (RouteConfig route : config.getRoutes()) {
            server.createContext(route.getPath(), new RouteHandler(route));
        }

        // 默认根路径处理器
        server.createContext("/", new DefaultHandler());

        server.setExecutor(null); // 使用默认执行器
        server.start();
        running = true;
    }

    public void setLogWindow(LogWindow logWindow) {
        this.logWindow = logWindow;
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            running = false;
        }
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return config.getPort();
    }

    private class RouteHandler implements HttpHandler {
        private final RouteConfig routeConfig;

        public RouteHandler(RouteConfig routeConfig) {
            this.routeConfig = routeConfig;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            // 记录请求
            if (logWindow != null) {
                StringBuilder requestHeaders = new StringBuilder();
                for (Map.Entry<String, java.util.List<String>> header : exchange.getRequestHeaders().entrySet()) {
                    requestHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
                }

                String requestBody = readRequestBody(exchange);
                logWindow.logRequest(requestMethod, requestPath, requestHeaders.toString(), requestBody);
            }

            // 检查方法是否匹配
            if (!routeConfig.getMethod().equalsIgnoreCase(requestMethod)) {
                String responseBody = "Method Not Allowed";
                sendResponse(exchange, 405, "text/plain", responseBody);

                // 记录响应
                if (logWindow != null) {
                    logWindow.logResponse(405, "Content-Type: text/plain", responseBody);
                }
                return;
            }

            // 设置响应头
            if (routeConfig.getHeaders() != null) {
                for (Map.Entry<String, String> header : routeConfig.getHeaders().entrySet()) {
                    exchange.getResponseHeaders().set(header.getKey(), header.getValue());
                }
            }

            // 设置响应内容类型
            if (routeConfig.getContentType() != null) {
                exchange.getResponseHeaders().set("Content-Type", routeConfig.getContentType());
            }

            // 发送响应 - 如果是 JSON 类型，使用紧凑格式
            String responseBody = routeConfig.getResponseBody() != null ? routeConfig.getResponseBody() : "";
            if (routeConfig.getContentType() != null && routeConfig.getContentType().contains("application/json")) {
                responseBody = JsonUtil.compact(responseBody);
            }

            // 记录响应头
            StringBuilder responseHeaders = new StringBuilder();
            responseHeaders.append("Content-Type: ").append(routeConfig.getContentType()).append("\n");
            if (routeConfig.getHeaders() != null) {
                for (Map.Entry<String, String> header : routeConfig.getHeaders().entrySet()) {
                    responseHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
                }
            }

            sendResponse(exchange, routeConfig.getStatusCode(), routeConfig.getContentType(), responseBody);

            // 记录响应
            if (logWindow != null) {
                logWindow.logResponse(routeConfig.getStatusCode(), responseHeaders.toString(), responseBody);
            }
        }

        private String readRequestBody(HttpExchange exchange) throws IOException {
            InputStream is = exchange.getRequestBody();
            if (is == null) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
            }
            return sb.toString().trim();
        }
    }

    private class DefaultHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            String requestPath = exchange.getRequestURI().getPath();

            // 记录请求
            if (logWindow != null) {
                StringBuilder requestHeaders = new StringBuilder();
                for (Map.Entry<String, java.util.List<String>> header : exchange.getRequestHeaders().entrySet()) {
                    requestHeaders.append(header.getKey()).append(": ").append(header.getValue()).append("\n");
                }

                String requestBody = "";
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        requestBody += line + "\n";
                    }
                }
                logWindow.logRequest(requestMethod, requestPath, requestHeaders.toString(), requestBody.trim());
            }

            String response = "Moker HTTP Server\nServer is running!";
            sendResponse(exchange, 200, "text/plain", response);

            // 记录响应
            if (logWindow != null) {
                logWindow.logResponse(200, "Content-Type: text/plain", response);
            }
        }
    }
}
