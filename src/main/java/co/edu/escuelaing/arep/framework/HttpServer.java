package co.edu.escuelaing.arep.framework;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HttpServer {
    private final int port;
    private final ExecutorService executorService;
    private volatile boolean running;
    private ServerSocket serverSocket;
    private final StaticFileHandler staticFileHandler;

    public HttpServer(int port, int nThreads) {
        this.port = port;
        this.executorService = Executors.newFixedThreadPool(nThreads);
        this.running = false;
        this.staticFileHandler = new StaticFileHandler();
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName("0.0.0.0"));
            running = true;
            System.out.println("Servidor iniciado en puerto " + port);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    executorService.execute(new RequestHandler(clientSocket, staticFileHandler));
                } catch (Exception e) {
                    if (running) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            executorService.shutdown();
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
            System.out.println("Servidor detenido correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            executorService.shutdownNow();
        }
    }

    public void addEndpoint(String path, HttpEndpoint endpoint) {
        RequestHandler.addEndpoint(path, endpoint);
    }
}
