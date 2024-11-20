package org.ulpgc;

public class MatrixMultiplicationThreads {

    private int size;
    private double[][] a;
    private double[][] b;
    private double[][] result;;

    public MatrixMultiplicationThreads(double[][] a, double[][] b) {
        this.a = a;
        this.b = b;
        this.result = new double[a.length][a[0].length];
        this.size = a.length;
    }

    public void multiply() {
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
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
}
