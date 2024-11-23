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

public class BenchmarkParallelMethods {

    @State(Scope.Thread)
    public static class Operands {
        @Param({"10", "100", "500", "1000", "2000", "3000"})
        private int size;

        @Param({"1", "2", "4", "8", "16", "32", "64", "128", "256"})
        private int nThreads;

        private double[][] a;
        private double[][] b;
        private List<Long> memoryUsages;
        private List<Integer> parallelThreadsUsed;

        @Setup
        public void setup() {
            a = initializeMatrix(size);
            b = initializeMatrix(size);
            memoryUsages = new ArrayList<>();
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
            double avgParallelThreads = calculateAverageThreads(parallelThreadsUsed);
            double avgMemoryUsage = calculateAverage(memoryUsages);

            System.out.println("------ Benchmark Results ------");
            System.out.println("Matrix Size: " + size);
            System.out.println("Average Memory Used: " + avgMemoryUsage + " MB");
            System.out.println("Available Processors: " + Runtime.getRuntime().availableProcessors());
            System.out.println("Average Threads: " + avgParallelThreads);
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
    public void executorServiceParallelization(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();

        long beforeMemory = getMemory(runtime);

        MatrixMultiplicationFixedThreads parallel = new MatrixMultiplicationFixedThreads(operands.a, operands.b, operands.nThreads);
        parallel.multiply();

        int threadsUsed = parallel.getThreadsUsed();
        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.parallelThreadsUsed.add(threadsUsed);
        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void parallelStreamMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        MatrixMultiplicationParallelStreams parallelStreams = new MatrixMultiplicationParallelStreams(operands.a, operands.b, operands.nThreads);
        parallelStreams.multiply();

        int threadsUsed = parallelStreams.getUsedThreads();
        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.parallelThreadsUsed.add(threadsUsed);
        operands.memoryUsages.add(usedMemory);

    }

    @Benchmark
    public void synchronizedBlocksMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        MatrixMultiplicationSynchronizedBlocks synchronizedBlocks = new MatrixMultiplicationSynchronizedBlocks(operands.a, operands.b, operands.nThreads);
        synchronizedBlocks.multiply();

        int threadsUsed = synchronizedBlocks.getUsedThreads();
        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.parallelThreadsUsed.add(threadsUsed);
        operands.memoryUsages.add(usedMemory);
    }

    @Benchmark
    public void semaphoreMatrixMultiplication(Operands operands) {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long beforeMemory = getMemory(runtime);

        MatrixMultiplicationSemaphore semaphore = new MatrixMultiplicationSemaphore(operands.a, operands.b, operands.nThreads);
        semaphore.multiply();

        long afterMemory = getMemory(runtime);
        long usedMemory = afterMemory - beforeMemory;

        operands.memoryUsages.add(usedMemory);
    }

    private static long getMemory(Runtime runtime) {
        return (runtime.totalMemory() / 1024 / 1024) - (runtime.freeMemory() / 1024 / 1024);
    }
}
