package org.ulpgc;

import java.util.stream.IntStream;

public class MatrixMultiplicationParallelStreams {
    private int size;
    private double[][] a;
    private double[][] b;
    private double[][] result;
    private int nThreads;

    public MatrixMultiplicationParallelStreams(double[][] a, double[][] b, int nThreads) {
        this.a = a;
        this.b = b;
        this.result = new double[a.length][a[0].length];
        this.size = a.length;
        this.nThreads = nThreads;
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", String.valueOf(nThreads));
    }

    public void multiply(){
        IntStream.range(0, size).parallel().forEach(i -> {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        });
    }

    public int getUsedThreads(){
        return nThreads;
    }
}
