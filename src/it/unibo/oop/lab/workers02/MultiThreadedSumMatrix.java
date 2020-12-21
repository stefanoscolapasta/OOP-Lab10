package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of the calculation.
 * 
 */
public final class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * 
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedSumMatrix(final int nthread) {
        super();
        this.nthread = nthread;
    }

    private static class Worker extends Thread {
        private final double[][] matrix;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] matrix, final int startpos, final int nelem) {
            super();
            this.matrix = matrix;
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            /**
            *   Sum all elements from the rows of the matrix from startpos till endPosition(=startpos+size) because
            *   sometimes lines can't be divided equally between threads the "i < matrix[0].length" has to be added
            */
            for (int i = startpos; i < matrix[0].length && i < startpos + nelem; i++) {
                for(final double d : this.matrix[i]) {
                    this.res += d;
                }     
            }
        }
        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            System.out.println("Thread N: " + this.getId() + " returns: " + this.res);
            return this.res;
        }
    }

    @Override
    public double sum(final double[][] matrix) {
        //I divide the matrix by rows through the threads

        final int size = matrix[0].length % nthread + matrix[0].length / nthread;
        // TODO Auto-generated method stub
        
        final List<Worker> workers = new ArrayList<>(nthread);
        System.out.println("\n\nNumber of threads created: "
                            + (int)Math.ceil((double)matrix[0].length/size)
                            + "\n"
                            + "------\n"
                            + "Threads requested originally: "
                            + nthread
                            + "\n"
                            );
        for (int start = 0; start < matrix[0].length; start += size) {
            workers.add(new Worker(matrix, start, size));
        }
        
        for (final Worker w: workers) {
            w.start();
        }
        
        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                
                sum += w.getResult();
                
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        
        /*
         * Return the sum
         */
        return sum;

    }
}
