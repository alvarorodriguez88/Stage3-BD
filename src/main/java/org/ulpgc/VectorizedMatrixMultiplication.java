package org.ulpgc;

public class VectorizedMatrixMultiplication {

    public double[][] execute(double[][] matrixA, double[][] matrixB) {

        // Resultant matrix of the appropriate size
        double [][] result = new double[matrixA.length][matrixB[0].length];

        // Perform vectorized matrix multiplication
        for (int i = 0; i < matrixA.length; i++) {
            for (int j = 0; j < matrixB[0].length; j++) {
                result[i][j] = vectorizedDotProduct(matrixA[i], getColumn(matrixB, j));
            }
        }
        return result;
    }

    /**
     * Helper method to compute the dot product of two vectors
     * This method performs a "vectorized-like" operation on two arrays (vectors)
     *
     * @param row the row vector from matrix A
     * @param column the column vector from matrix B
     * @return the dot product of row and column vectors
     */
    private static int vectorizedDotProduct(double[] row, double[] column) {
        int sum = 0;

        for (int k = 0; k < row.length; k++) {
            sum += row[k] * column[k];
        }
        return sum;
    }

    /**
     * Helper method to extract a column from a matrix as a vector
     *
     * @param matrix the matrix to extract the column from
     * @param colIndex the index of the column to extract
     * @return the column as a vector
     */
    private static double[] getColumn(double[][] matrix, int colIndex) {
        double[] column = new double[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            column[i] = matrix[i][colIndex];
        }
        return column;
    }

    /**
     * Helper method to print a matrix
     *
     * @param matrix the matrix to print
     */
    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Helper method to convert an array to a string for printing
     *
     * @param array the array to convert to a string
     * @return the string representation of the array
     */
    private static String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if (i < array.length - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}

