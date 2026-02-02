package com.tjwoods.model;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    private int port;
    private List<RouteConfig> routes;

    public ServerConfig() {
        this.port = 8080;
        this.routes = new ArrayList<>();
    }

    public ServerConfig(int port) {
        this();
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public List<RouteConfig> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteConfig> routes) {
        this.routes = routes;
    }

    public void addRoute(RouteConfig route) throws IllegalArgumentException {
        // 检查路由是否已存在（HTTP 方法和路径联合唯一）
        for (RouteConfig existingRoute : routes) {
            if (existingRoute.getMethod().equalsIgnoreCase(route.getMethod()) &&
                    existingRoute.getPath().equals(route.getPath())) {
                throw new IllegalArgumentException(
                        "路由已存在: " + route.getMethod() + " " + route.getPath()
                );
            }
        }
        this.routes.add(route);
    }

    public void removeRoute(RouteConfig route) {
        this.routes.remove(route);
    }
}
