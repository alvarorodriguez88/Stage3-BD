package org.ulpgc;

public class MatrixMultiplicationSynchronizedBlocks {

    private double[][] a;
    private double[][] b;
    private double[][] result;
    private int nThreads;

    public MatrixMultiplicationSynchronizedBlocks(double[][] a, double[][] b, int nThreads) {
        this.a = a;
        this.b = b;
        this.result = new double[a.length][b[0].length];
        this.nThreads = nThreads;
    }

    public void multiply() {
        Thread[] threads = new Thread[nThreads];
        int rowsPerThread = a.length / nThreads;

        for (int i = 0; i < nThreads; i++) {
            final int startRow = i * rowsPerThread;
            final int endRow = (i == nThreads - 1) ? a.length : startRow + rowsPerThread;

            threads[i] = new Thread(() -> {
                for (int row = startRow; row < endRow; row++) {
                    for (int col = 0; col < b[0].length; col++) {
                        double sum = 0;
                        for (int k = 0; k < a[0].length; k++) {
                            sum += a[row][k] * b[k][col];
                        }
                        synchronized (this) {
                            result[row][col] = sum;
                        }
                    }
                }
            });
            threads[i].start();
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

    public int getUsedThreads() {
        return nThreads;
    }
}
