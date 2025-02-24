package co.edu.escuelaing.arep.service;

import co.edu.escuelaing.arep.model.Conversiones;
import co.edu.escuelaing.arep.model.ConversionRequest;

public class ConversionService {
    private final ConsultaConvertidor consultaConvertidor;
    private final CacheService cacheService;

    public ConversionService() {
        this.consultaConvertidor = new ConsultaConvertidor();
        this.cacheService = new CacheService();
    }

    public double convertir(ConversionRequest request) throws Exception {
        String cacheKey = cacheService.generateKey(
                request.sourceCurrency().toString(),
                request.targetCurrency().toString()
        );

        Conversiones conversion = cacheService.get(cacheKey);

        if (conversion == null) {
            conversion = consultaConvertidor.tipoDeCambio(
                    request.sourceCurrency().toString(),
                    request.targetCurrency().toString()
            );
            cacheService.put(cacheKey, conversion);
        }

        return conversion.convert(request.amount());
    }

    public Conversiones obtenerTasaDeCambio(String deMoneda, String aMoneda) throws Exception {
        String cacheKey = cacheService.generateKey(deMoneda, aMoneda);

        Conversiones conversion = cacheService.get(cacheKey);
        if (conversion == null) {
            conversion = consultaConvertidor.tipoDeCambio(deMoneda, aMoneda);
            cacheService.put(cacheKey, conversion);
        }

        return conversion;
    }
}