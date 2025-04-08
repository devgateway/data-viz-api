package org.devgateway.viz.commons.domain;

import jakarta.persistence.Entity;
import org.devgateway.viz.commons.domain.Category;
import org.devgateway.viz.commons.domain.LocaleText;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Entity
public class LatLong extends Category {
    private Double latitude;
    private Double longitude;

    @Override
    public String getCode() {
        return generateLocationCode(latitude, longitude);
    }

    public LatLong() {
    }

    public static String generateLocationCode(double latitude, double longitude) {
        String locationStr = String.format("%.8f,%.8f", latitude, longitude);

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(locationStr.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes).substring(0, 12); // Shorten the hash to 12 characters
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }



    public LatLong(Double latitude, Double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.setValue(this.latitude + "," + this.longitude);
        this.setCode(generateLocationCode(latitude, longitude));
    }



    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
