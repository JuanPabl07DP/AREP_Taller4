package co.edu.escuelaing.arep.model;

public enum Currency {
    USD("Dólar estadounidense"),
    EUR("Euro"),
    COP("Peso colombiano"),
    ARS("Peso argentino"),
    BRL("Real brasileño");

    private final String description;

    Currency(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static Currency fromCode(String code) {
        try {
            return Currency.valueOf(code.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Moneda no soportada: " + code);
        }
    }
}