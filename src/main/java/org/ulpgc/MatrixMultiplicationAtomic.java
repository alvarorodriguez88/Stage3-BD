package org.ulpgc;

import java.util.concurrent.atomic.AtomicInteger;

public class MatrixMultiplicationAtomic {
    private int size;
    private double[][] a;
    private double[][] b;
    private double[][] result;

    public MatrixMultiplicationAtomic(double[][] a, double[][] b) {
        this.a = a;
        this.b = b;
        this.size = a.length;
        this.result = new double[size][size];
    }

    public void multiply(){
        AtomicInteger row = new AtomicInteger(0);
        AtomicInteger col = new AtomicInteger(0);

        while (row.get() < size) {
            int r = row.getAndIncrement();
            if (r < size) {
                for (int c = 0; c < size; c++) {
                    for (int k = 0; k < size; k++) {
                        result[r][c] += a[r][k] * b[k][c];
                    }
                }
            }
        }
    }

}
