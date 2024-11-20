package org.ulpgc;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

public class MatrixMultiplicationThreads2 {
    private static final int size = 1024;
    private static final double[][] a = new double[size][size];
    private static final double[][] b = new double[size][size];
    private static final double[][] result = new double[size][size];

    public static void main(String[] args) {
        // Get system information
        printSystemInfo();

        initializeMatrix(a);
        initializeMatrix(b);

        long startTime = System.currentTimeMillis();

        // Create and launch threads for each row of the matrix
        Thread[] threads = new Thread[size];
        for (int i = 0; i < size; i++) {
            final int row = i;
            threads[i] = new Thread(() -> multiplyRow(row));
            threads[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < size; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Execution time with Threads: " + (endTime - startTime) + " ms");
    }

    private static void multiplyRow(int row) {
        // Print the current thread name
        System.out.println("Executing thread: " + Thread.currentThread().getName());
        for (int j = 0; j < size; j++) {
            for (int k = 0; k < size; k++) {
                result[row][j] += a[row][k] * b[k][j];
            }
        }
    }

    private static void initializeMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = Math.random();
            }
        }
    }

    // Method to print system information
    private static void printSystemInfo() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        Runtime runtime = Runtime.getRuntime();

        System.out.println("System Information:");
        System.out.println("Operating System: " + osBean.getName() + " " + osBean.getVersion());
        System.out.println("Architecture: " + osBean.getArch());
        System.out.println("Available Processors: " + osBean.getAvailableProcessors());
        System.out.println("Total Memory (MB): " + (runtime.totalMemory() / 1024 / 1024));
        System.out.println("Free Memory (MB): " + (runtime.freeMemory() / 1024 / 1024));
        System.out.println("Active Thread Count: " + Thread.activeCount());
    }
}
