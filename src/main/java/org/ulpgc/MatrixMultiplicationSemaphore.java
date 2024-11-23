package org.ulpgc;

import java.util.concurrent.Semaphore;

public class MatrixMultiplicationSemaphore {

    private double[][] a;
    private double[][] b;
    private double[][] result;
    private Semaphore semaphore;

    public MatrixMultiplicationSemaphore(double[][] a, double[][] b, int maxThreads) {
        this.a = a;
        this.b = b;
        this.result = new double[a.length][b[0].length];
        this.semaphore = new Semaphore(maxThreads);
    }

    public void multiply() {
        Thread[] threads = new Thread[result.length];

        for (int row = 0; row < result.length; row++) {
            final int currentRow = row;
            threads[row] = new Thread(() -> {
                try {
                    semaphore.acquire();
                    for (int col = 0; col < b[0].length; col++) {
                        double sum = 0;
                        for (int k = 0; k < a[0].length; k++) {
                            sum += a[currentRow][k] * b[k][col];
                        }
                        result[currentRow][col] = sum;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            });
            threads[row].start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public double[][] getResult() {
        return result;
    }
}
