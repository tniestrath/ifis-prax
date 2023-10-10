package com.analysetool.util;

import java.util.List;

public class MathHelper {


    //Variance = Varianz
    //Mean = Mittelwert
    //StandartDeviation = Standartabweichung
    //AbsoluteDeviation = echte Abweichung


    // Für float
    public static float getMean(List<Float> data) {
        float sum = 0.0f;
        for(float a : data)
            sum += a;
        return sum/data.size();
    }

    public static float getVariance(List<Float> data) {
        float mean = getMean(data);
        float temp = 0;
        for(float a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    public static float getStandardDeviation(List<Float> data) {
        return (float) Math.sqrt(getVariance(data));
    }

    public static float getAbsoluteDeviation(List<Float> data) {
        float mean = getMean(data);
        float sum = 0;
        for(float a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }

    // Für double
    public static double getMeanDouble(List<Double> data) {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.size();
    }

    public static double getVarianceDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double temp = 0;
        for(double a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    public static double getStandardDeviationDouble(List<Double> data) {
        return Math.sqrt(getVarianceDouble(data));
    }

    public static double getAbsoluteDeviationDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double sum = 0;
        for(double a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }

    // Für int
    public static double getMeanInt(List<Integer> data) {
        double sum = 0.0;
        for(int a : data)
            sum += a;
        return sum/data.size();
    }

    public static double getVarianceInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double temp = 0;
        for(int a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    public static double getStandardDeviationInt(List<Integer> data) {
        return Math.sqrt(getVarianceInt(data));
    }

    public static double getAbsoluteDeviationInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double sum = 0;
        for(int a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }
}
