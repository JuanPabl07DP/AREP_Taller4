package co.edu.escuelaing.arep;

import co.edu.escuelaing.arep.framework.HttpServer;
import co.edu.escuelaing.arep.model.Conversiones;
import co.edu.escuelaing.arep.service.ConsultaConvertidor;
import com.google.gson.JsonObject;

public class CurrencyConverterApp {
    private static final int DEFAULT_PORT = 8080;
    private static final int THREAD_POOL_SIZE = 10;

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", String.valueOf(DEFAULT_PORT)));

        HttpServer server = new HttpServer(port, THREAD_POOL_SIZE);

        server.addEndpoint("/api/convertir", (request, response) -> {
            try {
                String queryString = request.getPath().split("\\?")[1];
                String[] params = queryString.split("&");

                String origen = "";
                String destino = "";
                double monto = 0.0;

                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        switch (keyValue[0]) {
                            case "origen" -> origen = keyValue[1].toUpperCase();
                            case "destino" -> destino = keyValue[1].toUpperCase();
                            case "monto" -> monto = Double.parseDouble(keyValue[1]);
                        }
                    }
                }

                if (origen.isEmpty() || destino.isEmpty() || monto <= 0) {
                    throw new IllegalArgumentException("Parámetros inválidos");
                }

                ConsultaConvertidor convertidor = new ConsultaConvertidor();
                Conversiones conversion = convertidor.tipoDeCambio(origen, destino);
                double resultado = conversion.convert(monto);

                JsonObject jsonResponse = new JsonObject();
                jsonResponse.addProperty("resultado", resultado);
                jsonResponse.addProperty("monedaOrigen", origen);
                jsonResponse.addProperty("monedaDestino", destino);
                jsonResponse.addProperty("montoOriginal", monto);
                jsonResponse.addProperty("tasaCambio", conversion.conversionRate());

                response.setContentType("application/json");
                response.setBody(jsonResponse.toString());

            } catch (Exception e) {
                e.printStackTrace(); // Para ver el error en la consola
                response.setStatus(500);
                JsonObject error = new JsonObject();
                error.addProperty("error", "Error en la conversión: " + e.getMessage());
                response.setBody(error.toString());
            }
        });

        System.out.println("Iniciando servidor en puerto " + port);
        server.start();
    }
}