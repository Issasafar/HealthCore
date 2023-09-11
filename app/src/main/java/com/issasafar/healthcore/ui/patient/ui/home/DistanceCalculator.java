package com.issasafar.healthcore.ui.patient.ui.home;

public class DistanceCalculator {
    private static final int EARTH_RADIUS = 6371;

    public static double calculateDistanceInMeters(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians((lat2 - lat1));
        double dLong = Math.toRadians((lon2 - lon1));
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        double a = semiSin(dLat) +
                Math.cos(lat1) *
                        Math.cos((lat2)) *
                        semiSin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        // 1000 for converting Kilometers to meters
        return EARTH_RADIUS * c * 1000;
    }

    public static double semiSin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
