package co.edu.escuelaing.arep.model;

public record ConversionRequest(
        Currency sourceCurrency,
        Currency targetCurrency,
        double amount
) {

    public ConversionRequest {
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a 0");
        }
        if (sourceCurrency == targetCurrency) {
            throw new IllegalArgumentException("Las monedas de origen y destino deben ser diferentes");
        }
    }

    public static ConversionRequest of(String sourceCode, String targetCode, double amount) {
        return new ConversionRequest(
                Currency.fromCode(sourceCode),
                Currency.fromCode(targetCode),
                amount
        );
    }
}