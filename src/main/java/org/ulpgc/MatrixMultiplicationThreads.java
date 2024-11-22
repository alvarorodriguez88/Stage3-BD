package org.ulpgc;

public class MatrixMultiplicationThreads {

    private int size;
    private double[][] a;
    private double[][] b;
    private double[][] result;
    private Thread[] threads;

    public MatrixMultiplicationThreads(double[][] a, double[][] b, int nThreads) {
        this.a = a;
        this.b = b;
        this.result = new double[a.length][a[0].length];
        this.size = a.length;
        this.threads = new Thread[Math.min(nThreads, a.length)];
    }

    public void multiply() {
        for (int i = 0; i < threads.length; i++) {
            final int row = i;
            threads[i] = new Thread(() -> multiplyRow(row));
            threads[i].start();
        }

        for (int i = 0; i < size; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void multiplyRow(int row) {
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                result[row][j] += a[row][k] * b[k][j];
            }
        }
    }

    public int getUsedThreads(){
        return threads.length;
    }
}
