package com.tjwoods.model;

import java.util.Map;

public class RouteConfig {
    private String method;
    private String path;
    private String responseBody;
    private int statusCode;
    private Map<String, String> headers;
    private String contentType;

    public RouteConfig() {
        this.statusCode = 200;
        this.contentType = "application/json";
    }

    public RouteConfig(String method, String path, String responseBody) {
        this();
        this.method = method;
        this.path = path;
        this.responseBody = responseBody;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RouteConfig that = (RouteConfig) obj;
        return method.equalsIgnoreCase(that.method) &&
                path.equals(that.path);
    }

    @Override
    public int hashCode() {
        int result = method.toLowerCase().hashCode();
        result = 31 * result + path.hashCode();
        return result;
    }
}
