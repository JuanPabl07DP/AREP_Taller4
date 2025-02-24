package co.edu.escuelaing.arep.framework;

import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String path;
    private final Map<String, String> headers;
    private String body;

    public HttpRequest(String method, String path, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}