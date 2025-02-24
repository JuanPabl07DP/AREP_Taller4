package co.edu.escuelaing.arep.framework;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler {
    private static final String[] POSSIBLE_PATHS = {
            "/app/public",  // Ruta en Docker
            "src/main/resources/public",  // Ruta en desarrollo local
            "./src/main/resources/public",
            "target/classes/public"
    };

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>();

    static {
        CONTENT_TYPES.put(".html", "text/html");
        CONTENT_TYPES.put(".css", "text/css");
        CONTENT_TYPES.put(".js", "application/javascript");
        CONTENT_TYPES.put(".json", "application/json");
        CONTENT_TYPES.put(".png", "image/png");
        CONTENT_TYPES.put(".jpg", "image/jpeg");
        CONTENT_TYPES.put(".jpeg", "image/jpeg");
        CONTENT_TYPES.put(".gif", "image/gif");
    }

    public void handleRequest(String path, OutputStream out) throws IOException {
        System.out.println("Ruta solicitada: " + path);

        Path filePath = findFile(path);

        if (filePath == null) {
            throw new FileNotFoundException("Archivo no encontrado: " + path);
        }

        System.out.println("Ruta completa del archivo: " + filePath.toString());
        System.out.println("Existe el archivo: " + Files.exists(filePath));
        System.out.println("Es un archivo: " + Files.isRegularFile(filePath));

        try {
            byte[] fileContent = Files.readAllBytes(filePath);
            String contentType = getContentType(filePath.toString());

            String response = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n" +
                    "\r\n";

            out.write(response.getBytes());
            out.write(fileContent);
            out.flush();
        } catch (IOException e) {
            System.err.println("Error al servir archivo: " + e.getMessage());
            e.printStackTrace();

            String response = "HTTP/1.1 404 Not Found\r\n" +
                    "Content-Type: text/plain\r\n" +
                    "Content-Length: 24\r\n" +
                    "\r\n" +
                    "Archivo no encontrado: " + path;
            out.write(response.getBytes());
            out.flush();
        }
    }

    private Path findFile(String path) {
        for (String basePath : POSSIBLE_PATHS) {
            Path filePath;
            if ("/".equals(path) || path.equals("")) {
                filePath = Paths.get(basePath, "index.html");
            } else {
                path = path.startsWith("/") ? path.substring(1) : path;
                filePath = Paths.get(basePath, path);
            }

            System.out.println("Probando ruta: " + filePath.toString());

            if (Files.exists(filePath)) {
                return filePath;
            }
        }
        return null;
    }

    private String getContentType(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf('.'));
        return CONTENT_TYPES.getOrDefault(extension, "text/plain");
    }
}