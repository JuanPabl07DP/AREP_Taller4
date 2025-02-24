package co.edu.escuelaing.arep.framework;

@FunctionalInterface
public interface HttpEndpoint {
    void handle(HttpRequest request, HttpResponse response);
}