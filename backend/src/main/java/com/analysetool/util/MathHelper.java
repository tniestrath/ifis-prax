package com.analysetool.util;

import java.util.*;

public class MathHelper {


    //Variance = Varianz
    //Mean = Mittelwert
    //StandartDeviation = Standartabweichung
    //AbsoluteDeviation = echte Abweichung
    //rest siehe doc

    // Für float
    /**
     * Berechnet den Mittelwert einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Der Mittelwert der Liste.
     */
    public static float getMeanFloat(List<Float> data) {
        float sum = 0.0f;
        for(float a : data)
            sum += a;
        return sum/data.size();
    }
    /**
     * Berechnet den Median einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Der Median der Liste.
     */
    public static float getMedianFloat(List<Float> data) {
        Collections.sort(data);
        int middle = data.size()/2;
        if (data.size() % 2 == 1) {
            return data.get(middle);
        } else {
            return (data.get(middle - 1) + data.get(middle)) / 2.0f;
        }
    }
    /**
     * Berechnet die Varianz einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Der Median der Liste.
     */
    public static float getVariance(List<Float> data) {
        float mean = getMeanFloat(data);
        float temp = 0;
        for(float a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    /**
     * Berechnet die Spannweite einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Die Spannweite der Liste.
     */
    public static float getRangeFloat(List<Float> data) {
        return Collections.max(data) - Collections.min(data);
    }

    /**
     * Berechnet die Standardabweichung einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static float getStandardDeviationFloat(List<Float> data) {
        return (float) Math.sqrt(getVariance(data));
    }
    /**
     * Berechnet die echte Abweichung einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static float getAbsoluteDeviationFloat(List<Float> data) {
        float mean = getMeanFloat(data);
        float sum = 0;
        for(float a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }
    /**
     * Berechnet den Modalwert einer Liste von float-Werten.
     * @param data Eine Liste von float-Werten.
     * @return Der Modalwert der Liste.
     */
    public static float getModeFloat(List<Float> data) {
        Map<Float, Integer> freqMap = new HashMap<>();
        for(float num : data) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
        Map.Entry<Float, Integer> maxEntry = null;
        for(Map.Entry<Float, Integer> entry : freqMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }

    /**
     * Berechnet das untere Quartil (Q1) einer sortierten Liste von Float-Werten.
     * @param data Eine sortierte Liste von Float-Werten.
     * @return Das untere Quartil der Liste.
     */
    public static double getLowerQuartileFloat(List<Float> data) {
        Collections.sort(data);
        int n = data.size();
        if (n % 4 == 0) {
            return (data.get(n/4 - 1) + data.get(n/4)) / 2.0;
        } else {
            return data.get(n/4);
        }
    }

    /**
     * Berechnet das obere Quartil (Q3) einer sortierten Liste von Float-Werten.
     * @param data Eine sortierte Liste von Float-Werten.
     * @return Das obere Quartil der Liste.
     */
    public static double getUpperQuartileFloat(List<Float> data) {
        Collections.sort(data);
        int n = data.size();
        if (3*n % 4 == 0) {
            return (data.get(3*n/4 - 1) + data.get(3*n/4)) / 2.0;
        } else {
            return data.get(3*n/4);
        }
    }

    /**
     * Berechnet den Interquartilbereich (IQR) einer Liste von Float-Werten.
     * Der IQR ist ein Maß für die statistische Streuung und wird als Differenz zwischen dem oberen und dem unteren Quartil berechnet.
     * @param data Eine Liste von Float-Werten.
     * @return Der Interquartilbereich der Liste.
     */
    public static double getInterquartileRangeFloat(List<Float> data) {
        return getUpperQuartileFloat(data) - getLowerQuartileFloat(data);
    }

    /**
     * Ermittelt die Ausreißer in einer Liste von Float-Werten.
     * Ein Ausreißer ist ein Wert, der weit von den anderen Werten in einem Datensatz entfernt ist.
     * @param data Eine Liste von Float-Werten.
     * @return Eine Liste von Ausreißern.
     */
    public static List<Float> getOutliersFloat(List<Float> data) {
        double q1 = getLowerQuartileFloat(data);
        double q3 = getUpperQuartileFloat(data);
        double iqr = getInterquartileRangeFloat(data);
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        List<Float> outliers = new ArrayList<>();
        for (float value : data) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        return outliers;
    }

    /**
     * Berechnet den Skewness-Wert (Schiefe) einer Liste von Float-Werten.
     * Ein positiver Wert zeigt eine Verteilung an, die nach rechts geneigt ist, während ein negativer Wert eine Verteilung anzeigt, die nach links geneigt ist.
     * @param data Eine Liste von Float-Werten.
     * @return Der Skewness-Wert der Liste.
     */
    public static double getSkewnessFloat(List<Float> data) {
        double mean = getMeanFloat(data);
        double sd = getStandardDeviationFloat(data);
        double skewness = 0.0;

        for (float value : data) {
            skewness += Math.pow(value - mean, 3);
        }

        skewness /= data.size();
        skewness /= Math.pow(sd, 3);
        return skewness;
    }

    /**
     * Berechnet den Kurtosis-Wert (Wölbung) einer Liste von Float-Werten.
     * Kurtosis beschreibt die "Spitzheit" einer Verteilung.
     * @param data Eine Liste von Float-Werten.
     * @return Der Kurtosis-Wert der Liste.
     */
    public static double getKurtosisFloat(List<Float> data) {
        double mean = getMeanFloat(data);
        double sd = getStandardDeviationFloat(data);
        double kurtosis = 0.0;

        for (float value : data) {
            kurtosis += Math.pow(value - mean, 4);
        }

        kurtosis /= data.size();
        kurtosis /= Math.pow(sd, 4);
        kurtosis -= 3;  // Adjust for excess kurtosis
        return kurtosis;
    }




    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Für double
    /**
     * Berechnet den Mittelwert einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Der Mittelwert der Liste.
     */
    public static double getMeanDouble(List<Double> data) {
        double sum = 0.0;
        for(double a : data)
            sum += a;
        return sum/data.size();
    }

    /**
     * Berechnet den Median einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Der Median der Liste.
     */
    public static double getMedianDouble(List<Double> data) {
        Collections.sort(data);
        int middle = data.size()/2;
        if (data.size() % 2 == 1) {
            return data.get(middle);
        } else {
            return (data.get(middle - 1) + data.get(middle)) / 2.0;
        }
    }
    /**
     * Berechnet den Modalwert einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Der Modalwert der Liste.
     */
    public static double getModeDouble(List<Double> data) {
        Map<Double, Integer> freqMap = new HashMap<>();
        for(double num : data) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
        Map.Entry<Double, Integer> maxEntry = null;
        for(Map.Entry<Double, Integer> entry : freqMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }
    /**
     * Berechnet die Varianz einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Der Modalwert der Liste.
     */
    public static double getVarianceDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double temp = 0;
        for(double a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }
    /**
     * Berechnet die Spannweite einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Die Spannweite der Liste.
     */
    public static double getRangeDouble(List<Double> data) {
        return Collections.max(data) - Collections.min(data);
    }

    /**
     * Berechnet die Standardabweichung einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getStandardDeviationDouble(List<Double> data) {
        return Math.sqrt(getVarianceDouble(data));
    }
    /**
     * Berechnet die echte Abweichung einer Liste von double-Werten.
     * @param data Eine Liste von double-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getAbsoluteDeviationDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double sum = 0;
        for(double a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }

    /**
     * Berechnet das untere Quartil (Q1) einer sortierten Liste von Double-Werten.
     * @param data Eine sortierte Liste von Double-Werten.
     * @return Das untere Quartil der Liste.
     */
    public static double getLowerQuartileDouble(List<Double> data) {
        Collections.sort(data);
        int n = data.size();
        if (n % 4 == 0) {
            return (data.get(n/4 - 1) + data.get(n/4)) / 2.0;
        } else {
            return data.get(n/4);
        }
    }

    /**
     * Berechnet das obere Quartil (Q3) einer sortierten Liste von Double-Werten.
     * @param data Eine sortierte Liste von Double-Werten.
     * @return Das obere Quartil der Liste.
     */
    public static double getUpperQuartileDouble(List<Double> data) {
        Collections.sort(data);
        int n = data.size();
        if (3*n % 4 == 0) {
            return (data.get(3*n/4 - 1) + data.get(3*n/4)) / 2.0;
        } else {
            return data.get(3*n/4);
        }
    }

    /**
     * Berechnet den Interquartilbereich (IQR) einer Liste von Double-Werten.
     * @param data Eine Liste von Double-Werten.
     * @return Der Interquartilbereich der Liste.
     */
    public static double getInterquartileRangeDouble(List<Double> data) {
        return getUpperQuartileDouble(data) - getLowerQuartileDouble(data);
    }

    /**
     * Ermittelt die Ausreißer in einer Liste von Double-Werten.
     * @param data Eine Liste von Double-Werten.
     * @return Eine Liste von Ausreißern.
     */
    public static List<Double> getOutliersDouble(List<Double> data) {
        double q1 = getLowerQuartileDouble(data);
        double q3 = getUpperQuartileDouble(data);
        double iqr = getInterquartileRangeDouble(data);
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        List<Double> outliers = new ArrayList<>();
        for (double value : data) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        return outliers;
    }

    /**
     * Berechnet den Skewness-Wert (Schiefe) einer Liste von Double-Werten.
     * @param data Eine Liste von Double-Werten.
     * @return Der Skewness-Wert der Liste.
     */
    public static double getSkewnessDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double sd = getStandardDeviationDouble(data);
        double skewness = 0.0;

        for (double value : data) {
            skewness += Math.pow(value - mean, 3);
        }

        skewness /= data.size();
        skewness /= Math.pow(sd, 3);
        return skewness;
    }

    /**
     * Berechnet den Kurtosis-Wert (Wölbung) einer Liste von Double-Werten.
     * @param data Eine Liste von Double-Werten.
     * @return Der Kurtosis-Wert der Liste.
     */
    public static double getKurtosisDouble(List<Double> data) {
        double mean = getMeanDouble(data);
        double sd = getStandardDeviationDouble(data);
        double kurtosis = 0.0;

        for (double value : data) {
            kurtosis += Math.pow(value - mean, 4);
        }

        kurtosis /= data.size();
        kurtosis /= Math.pow(sd, 4);
        kurtosis -= 3;  // Adjust for excess kurtosis
        return kurtosis;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // Für int
    /**
     * Berechnet den Mittelwert einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Mittelwert der Liste.
     */
    public static double getMeanInt(List<Integer> data) {
        double sum = 0.0;
        for(int a : data)
            sum += a;
        return sum/data.size();
    }
    /**
     * Berechnet den Median einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Median der Liste.
     */
    public static double getMedianInt(List<Integer> data) {
        Collections.sort(data);
        int middle = data.size() / 2;
        if (data.size() % 2 == 1) {
            return data.get(middle);
        } else {
            return (data.get(middle - 1) + data.get(middle)) / 2.0;
        }
    }/**
     * Berechnet die Varianz einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getVarianceInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double temp = 0;
        for(int a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    /**
     * Berechnet den Modalwert einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Modalwert der Liste.
     */
    public static int getModeInt(List<Integer> data) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        for(int num : data) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
        Map.Entry<Integer, Integer> maxEntry = null;
        for(Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }

    /**
     * Berechnet die Spannweite einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Die Spannweite der Liste.
     */
    public static int getRangeInt(List<Integer> data) {
        return Collections.max(data) - Collections.min(data);
    }

    /**
     * Berechnet die Standardabweichung einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getStandardDeviationInt(List<Integer> data) {
        return Math.sqrt(getVarianceInt(data));
    }
    /**
     * Berechnet die echte Abweichung einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getAbsoluteDeviationInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double sum = 0;
        for(int a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }

    /**
     * Berechnet das untere Quartil (Q1) einer sortierten Liste von Integer-Werten.
     * @param data Eine sortierte Liste von Integer-Werten.
     * @return Das untere Quartil der Liste.
     */
    public static double getLowerQuartileInt(List<Integer> data) {
        Collections.sort(data);
        int n = data.size();
        if (n % 4 == 0) {
            return (data.get(n / 4 - 1) + data.get(n / 4)) / 2.0;
        } else {
            return data.get(n / 4);
        }
    }

    /**
     * Berechnet das obere Quartil (Q3) einer sortierten Liste von Integer-Werten.
     * @param data Eine sortierte Liste von Integer-Werten.
     * @return Das obere Quartil der Liste.
     */
    public static double getUpperQuartileInt(List<Integer> data) {
        Collections.sort(data);
        int n = data.size();
        if (3 * n % 4 == 0) {
            return (data.get(3 * n / 4 - 1) + data.get(3 * n / 4)) / 2.0;
        } else {
            return data.get(3 * n / 4);
        }
    }

    /**
     * Berechnet den Interquartilbereich (IQR) einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Interquartilbereich der Liste.
     */
    public static double getInterquartileRangeInt(List<Integer> data) {
        return getUpperQuartileInt(data) - getLowerQuartileInt(data);
    }

    /**
     * Ermittelt die Ausreißer in einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Eine Liste von Ausreißern.
     */
    public static List<Integer> getOutliersInt(List<Integer> data) {
        double q1 = getLowerQuartileInt(data);
        double q3 = getUpperQuartileInt(data);
        double iqr = getInterquartileRangeInt(data);
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        List<Integer> outliers = new ArrayList<>();
        for (int value : data) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        return outliers;
    }

    /**
     * Berechnet den Skewness-Wert (Schiefe) einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Skewness-Wert der Liste.
     */
    public static double getSkewnessInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double sd = getStandardDeviationInt(data);
        double skewness = 0.0;

        for (int value : data) {
            skewness += Math.pow(value - mean, 3);
        }

        skewness /= data.size();
        skewness /= Math.pow(sd, 3);
        return skewness;
    }

    /**
     * Berechnet den Kurtosis-Wert (Wölbung) einer Liste von Integer-Werten.
     * @param data Eine Liste von Integer-Werten.
     * @return Der Kurtosis-Wert der Liste.
     */
    public static double getKurtosisInt(List<Integer> data) {
        double mean = getMeanInt(data);
        double sd = getStandardDeviationInt(data);
        double kurtosis = 0.0;

        for (int value : data) {
            kurtosis += Math.pow(value - mean, 4);
        }

        kurtosis /= data.size();
        kurtosis /= Math.pow(sd, 4);
        kurtosis -= 3;  // Adjust for excess kurtosis
        return kurtosis;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Long
    /**
     * Berechnet den Mittelwert einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Der Mittelwert der Liste.
     */
    public static double getMeanLong(List<Long> data) {
        double sum = 0.0;
        for(long a : data)
            sum += a;
        return sum/data.size();
    }

    /**
     * Berechnet den Median einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Der Median der Liste.
     */
    public static double getMedianLong(List<Long> data) {
        Collections.sort(data);
        int middle = data.size() / 2;
        if (data.size() % 2 == 1) {
            return data.get(middle);
        } else {
            return (data.get(middle - 1) + data.get(middle)) / 2.0;
        }
    }

    /**
     * Berechnet die Varianz einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Die Varianz der Liste.
     */
    public static double getVarianceLong(List<Long> data) {
        double mean = getMeanLong(data);
        double temp = 0;
        for(long a : data)
            temp += (a-mean) * (a-mean);
        return temp/data.size();
    }

    /**
     * Berechnet den Modalwert einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Der Modalwert der Liste.
     */
    public static long getModeLong(List<Long> data) {
        Map<Long, Integer> freqMap = new HashMap<>();
        for(long num : data) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }
        Map.Entry<Long, Integer> maxEntry = null;
        for(Map.Entry<Long, Integer> entry : freqMap.entrySet()) {
            if (maxEntry == null || entry.getValue() > maxEntry.getValue()) {
                maxEntry = entry;
            }
        }
        return maxEntry.getKey();
    }

    /**
     * Berechnet die Spannweite einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Die Spannweite der Liste.
     */
    public static long getRangeLong(List<Long> data) {
        return Collections.max(data) - Collections.min(data);
    }

    /**
     * Berechnet die Standardabweichung einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Die Standardabweichung der Liste.
     */
    public static double getStandardDeviationLong(List<Long> data) {
        return Math.sqrt(getVarianceLong(data));
    }

    /**
     * Berechnet die echte Abweichung einer Liste von Long-Werten.
     * @param data Eine Liste von Long-Werten.
     * @return Die echte Abweichung der Liste.
     */
    public static double getAbsoluteDeviationLong(List<Long> data) {
        double mean = getMeanLong(data);
        double sum = 0;
        for(long a : data)
            sum += Math.abs(a - mean);
        return sum/data.size();
    }
    /**
     * Berechnet das untere Quartil (Q1) einer sortierten Liste von Long-Werten.
     * @param data Eine sortierte Liste von Long-Werten.
     * @return Das untere Quartil der Liste.
     */
    public static double getLowerQuartileLong(List<Long> data) {
        Collections.sort(data);
        int n = data.size();
        if (n % 4 == 0) {
            return (data.get(n/4 - 1) + data.get(n/4)) / 2.0;
        } else {
            return data.get(n/4);
        }
    }

    /**
     * Berechnet das obere Quartil (Q3) einer sortierten Liste von Long-Werten.
     * @param data Eine sortierte Liste von Long-Werten.
     * @return Das obere Quartil der Liste.
     */
    public static double getUpperQuartileLong(List<Long> data) {
        Collections.sort(data);
        int n = data.size();
        if (3*n % 4 == 0) {
            return (data.get(3*n/4 - 1) + data.get(3*n/4)) / 2.0;
        } else {
            return data.get(3*n/4);
        }
    }

    /**
     * Berechnet den Interquartilbereich (IQR) einer Liste von Long-Werten.
     * Der IQR ist ein Maß für die statistische Streuung und wird als Differenz zwischen dem oberen und dem unteren Quartil berechnet.
     * @param data Eine Liste von Long-Werten.
     * @return Der Interquartilbereich der Liste.
     */
    public static double getInterquartileRangeLong(List<Long> data) {
        return getUpperQuartileLong(data) - getLowerQuartileLong(data);
    }

    /**
     * Ermittelt die Ausreißer in einer Liste von Long-Werten.
     * Ein Ausreißer ist ein Wert, der weit von den anderen Werten in einem Datensatz entfernt ist.
     * @param data Eine Liste von Long-Werten.
     * @return Eine Liste von Ausreißern.
     */
    public static List<Long> getOutliersLong(List<Long> data) {
        double q1 = getLowerQuartileLong(data);
        double q3 = getUpperQuartileLong(data);
        double iqr = getInterquartileRangeLong(data);
        double lowerBound = q1 - 1.5 * iqr;
        double upperBound = q3 + 1.5 * iqr;

        List<Long> outliers = new ArrayList<>();
        for (long value : data) {
            if (value < lowerBound || value > upperBound) {
                outliers.add(value);
            }
        }
        return outliers;
    }

    /**
     * Berechnet den Skewness-Wert (Schiefe) einer Liste von Long-Werten.
     * Ein positiver Wert zeigt eine Verteilung an, die nach rechts geneigt ist, während ein negativer Wert eine Verteilung anzeigt, die nach links geneigt ist.
     * @param data Eine Liste von Long-Werten.
     * @return Der Skewness-Wert der Liste.
     */
    public static double getSkewnessLong(List<Long> data) {
        double mean = getMeanLong(data);
        double sd = getStandardDeviationLong(data);
        double skewness = 0.0;

        for (long value : data) {
            skewness += Math.pow(value - mean, 3);
        }

        skewness /= data.size();
        skewness /= Math.pow(sd, 3);
        return skewness;
    }

    /**
     * Berechnet den Kurtosis-Wert (Wölbung) einer Liste von Long-Werten.
     * Kurtosis beschreibt die "Spitzheit" einer Verteilung.
     * @param data Eine Liste von Long-Werten.
     * @return Der Kurtosis-Wert der Liste.
     */
    public static double getKurtosisLong(List<Long> data) {
        double mean = getMeanLong(data);
        double sd = getStandardDeviationLong(data);
        double kurtosis = 0.0;

        for (long value : data) {
            kurtosis += Math.pow(value - mean, 4);
        }

        kurtosis /= data.size();
        kurtosis /= Math.pow(sd, 4);
        kurtosis -= 3;  // Adjust for excess kurtosis
        return kurtosis;
    }

}
