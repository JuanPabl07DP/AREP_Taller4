package co.edu.escuelaing.arep.framework;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements Runnable {
    private final Socket clientSocket;
    private final StaticFileHandler staticFileHandler;
    private static final Map<String, HttpEndpoint> endpoints = new HashMap<>();

    public RequestHandler(Socket socket, StaticFileHandler staticFileHandler) {
        this.clientSocket = socket;
        this.staticFileHandler = staticFileHandler;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            System.out.println("\n=== Incoming Request ===");
            System.out.println("Request Line: " + requestLine);

            if (requestLine == null) {
                System.out.println("Request line is null, returning");
                return;
            }

            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            System.out.println("Method: " + method);
            System.out.println("Full Path: " + path);

            // Leer headers
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            System.out.println("\nHeaders:");
            while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                System.out.println(headerLine);
                String[] headerParts = headerLine.split(": ");
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            String basePath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
            System.out.println("\nBase Path: " + basePath);

            if (endpoints.containsKey(basePath)) {
                System.out.println("Handling endpoint: " + basePath);
                HttpRequest request = new HttpRequest(method, path, headers);
                HttpResponse response = new HttpResponse();
                endpoints.get(basePath).handle(request, response);
                sendResponse(out, response);
            } else {
                System.out.println("Attempting to handle static file: " + basePath);
                staticFileHandler.handleRequest(basePath, out);
            }

        } catch (Exception e) {
            System.err.println("Error handling request:");
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    private void sendResponse(OutputStream out, HttpResponse response) throws IOException {
        String headerString = "HTTP/1.1 " + response.getStatus() + " " + getStatusText(response.getStatus()) + "\r\n" +
                "Content-Type: " + response.getContentType() + "\r\n" +
                "Content-Length: " + response.getBody().length() + "\r\n" +
                "\r\n";

        out.write(headerString.getBytes());
        out.write(response.getBody().getBytes());
        out.flush();
    }

    private String getStatusText(int status) {
        return switch (status) {
            case 200 -> "OK";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Unknown";
        };
    }

    public static void addEndpoint(String path, HttpEndpoint endpoint) {
        endpoints.put(path, endpoint);
    }
}