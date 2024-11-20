package org.ulpgc;

import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.MILLISECONDS)
@Fork(1)

public class BenchmarkMatrixMultiplication {

    @State(Scope.Thread)
    public static class Operands {
        @Param({"10", "100", "500", "1000", "2000"})
        private int size;
        private double[][] a;
        private double[][] b;
        private List<Long> naiveTimes;
        private List<Long> parallelTimes;
        private List<Long> memoryUsages;
        private List<Integer> naiveThreadsUsed;
        private List<Integer> parallelThreadsUsed;

        @Setup
        public void setup() {
            a = initializeMatrix(size);
            b = initializeMatrix(size);
            naiveTimes = new ArrayList<>();
            parallelTimes = new ArrayList<>();
            memoryUsages = new ArrayList<>();
            naiveThreadsUsed = new ArrayList<>();
            parallelThreadsUsed = new ArrayList<>();
        }

        public double calculateAverage(List<Long> values) {
            long sum = 0;
            for (long value : values) {
                sum += value;
            }
            return values.isEmpty() ? 0 : (double) sum / values.size();
        }

        public double calculateAverageThreads(List<Integer> values) {
            int sum = 0;
            for (int value : values) {
                sum += value;
            }
            return values.isEmpty() ? 0 : (double) sum / values.size();
        }

        @TearDown(Level.Trial)
        public void printResults() {
            double avgNaiveTime = calculateAverage(naiveTimes);
            double avgParallelTime = calculateAverage(parallelTimes);
            double avgNaiveThreads = calculateAverageThreads(naiveThreadsUsed);
            double avgParallelThreads = calculateAverageThreads(parallelThreadsUsed);
            double speedup = avgNaiveTime / avgParallelTime;
            double efficiency = speedup / avgParallelThreads;
            double avgMemoryUsage = calculateAverage(memoryUsages);

            System.out.println("------ Benchmark Results ------");
            System.out.println("Matrix Size: " + size);
            System.out.println("Average Memory Used: " + avgMemoryUsage + " MB");
            System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
            System.out.println("Active Thread Count: " + Thread.activeCount());
            System.out.println("--------------------------------");
        }

        public double[][] initializeMatrix(int size) {
            double[][] matrix = new double[size][size];
            Random random = new Random();
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    matrix[i][j] = random.nextDouble();
                }
            }
            return matrix;
        }
    }

    @Benchmark
    public void naiveMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long beforeMemory = getMemory(runtime);
        // Capturar número de hilos iniciales
        //int initialThreads = Thread.activeCount();

        NaiveMatrixMultiplication.multiply(operands.a, operands.b);

        // Capturar número de hilos finales
        int finalThreads = Thread.activeCount();
        //int threadsUsed = finalThreads - initialThreads;

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
        //operands.parallelThreadsUsed.add(threadsUsed);
    }

    @Benchmark
    public void parallelThreadsMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long beforeMemory = getMemory(runtime);
        // Capturar número de hilos iniciales
        //int initialThreads = Thread.activeCount();

        MatrixMultiplicationThreads parallel = new MatrixMultiplicationThreads(operands.a, operands.b);
        parallel.multiply();

        // Capturar número de hilos finales
        int finalThreads = Thread.activeCount();
        //int threadsUsed = finalThreads - initialThreads;

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
        //operands.parallelThreadsUsed.add(threadsUsed);
    }

    private static long getMemory(Runtime runtime) {
        return (runtime.totalMemory() / 1024 / 1024) - (runtime.freeMemory() / 1024 / 1024);
    }
}
