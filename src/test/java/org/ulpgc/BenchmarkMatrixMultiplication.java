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
        @Param({"10", "100", "500", "1000", "2000", "3000"})
        private int size;
        double[][] a;
        double[][] b;
        List<Long> memoryUsages;

        @Setup
        public void setup() {
            a = initializeMatrix(size);
            b = initializeMatrix(size);
            memoryUsages = new ArrayList<>();
        }

        public double calculateAverage(List<Long> values) {
            long sum = 0;
            for (long value : values) {
                sum += value;
            }
            return values.isEmpty() ? 0 : (double) sum / values.size();
        }

        @TearDown(Level.Trial)
        public void printResults() {
            double avgMemoryUsage = calculateAverage(memoryUsages);

            System.out.println("------ Benchmark Results ------");
            System.out.println("Matrix Size: " + size);
            System.out.println("Average Memory Used: " + avgMemoryUsage + " MB");
            System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
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

        NaiveMatrixMultiplication.multiply(operands.a, operands.b);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;
        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void vectorizedMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        VectorizedMatrixMultiplication.multiply(operands.a,operands.b);

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;
        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void atomicMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        MatrixMultiplicationAtomic atomic = new MatrixMultiplicationAtomic(operands.a, operands.b);
        atomic.multiply();

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;
        operands.memoryUsages.add(usedMemory);
    }

    private static long getMemory(Runtime runtime) {
        return (runtime.totalMemory() / 1024 / 1024) - (runtime.freeMemory() / 1024 / 1024);
    }
}
