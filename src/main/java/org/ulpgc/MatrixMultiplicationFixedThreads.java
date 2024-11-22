package org.ulpgc;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MatrixMultiplicationFixedThreads {
    private int size;
    private double[][] a;
    private double[][] b;
    private double[][] result;
    private ExecutorService executor;
    private int nThreads;

    public MatrixMultiplicationFixedThreads(double[][] a, double[][] b, int nThreads) {
        this.a = a;
        this.b = b;
        this.size = a.length;
        this.result = new double[size][size];
        this.nThreads = nThreads;
        this.executor = Executors.newFixedThreadPool(nThreads);
    }

    public void multiply() {
        for (int i = 0; i < size; i++) {
            final int row = i;
            executor.submit(() -> multiplyRow(row));
        }

        executor.shutdown();

        try {
            executor.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void multiplyRow(int row) {
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                result[row][j] += a[row][k] * b[k][j];
            }
        }
    }

    public int getThreadsUsed() {
        return nThreads;
    }
}
