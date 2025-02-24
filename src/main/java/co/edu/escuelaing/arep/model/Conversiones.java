package co.edu.escuelaing.arep.model;

import com.google.gson.annotations.SerializedName;

public record Conversiones(
        @SerializedName("result") String result,
        @SerializedName("documentation") String documentation,
        @SerializedName("terms_of_use") String termsOfUse,
        @SerializedName("time_last_update_unix") Long timeLastUpdateUnix,
        @SerializedName("time_last_update_utc") String timeLastUpdateUtc,
        @SerializedName("time_next_update_unix") Long timeNextUpdateUnix,
        @SerializedName("time_next_update_utc") String timeNextUpdateUtc,
        @SerializedName("base_code") String baseCode,
        @SerializedName("target_code") String targetCode,
        @SerializedName("conversion_rate") Double conversionRate
) {
    public Conversiones {
        if (conversionRate == null) {
            throw new IllegalArgumentException("La tasa de conversión no puede ser nula");
        }
    }

    public double convert(double amount) {
        return amount * conversionRate;
    }
}