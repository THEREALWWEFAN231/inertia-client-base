package com.inertiaclient.base.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtils {

    public static double round(double value, int places) {
        return MathUtils.round(value, places, RoundingMode.HALF_UP);
    }

    public static double round(double value, int places, RoundingMode roundingMode) {
        return MathUtils.roundBD(value, places, roundingMode).doubleValue();
    }

    public static BigDecimal roundBD(double value, int places) {
        return MathUtils.roundBD(value, places, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundBD(double value, int places, RoundingMode roundingMode) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(places, roundingMode);
        return bigDecimal;
    }

}
