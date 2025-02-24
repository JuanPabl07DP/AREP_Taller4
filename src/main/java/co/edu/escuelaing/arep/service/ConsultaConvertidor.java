package co.edu.escuelaing.arep.service;

import co.edu.escuelaing.arep.model.Conversiones;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsultaConvertidor {
    private static final String API_KEY = "57e10440419df6a14a7266c8";
    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private final HttpClient client;
    private final Gson gson;

    public ConsultaConvertidor() {
        this.client = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    public Conversiones tipoDeCambio(String deMoneda, String aMoneda) throws IOException, InterruptedException {
        String url = BASE_URL + API_KEY + "/pair/" + deMoneda + "/" + aMoneda;
        URI direccion = URI.create(url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(direccion)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new IOException("Error en la API. Código de estado: " + response.statusCode());
        }

        System.out.println("Respuesta de la API: " + response.body());

        Conversiones conversion = gson.fromJson(response.body(), Conversiones.class);

        if (conversion == null || conversion.conversionRate() == null) {
            throw new RuntimeException("La respuesta de la API no tiene el formato esperado");
        }

        return conversion;
    }
}