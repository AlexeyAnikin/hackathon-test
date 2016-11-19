package com.sbt.com.sbt.benchmarks;

import com.sbt.com.sbt.factory.ServiceFactory;
import org.jsr166.LongAdder8;

import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public abstract class Benchmark {

    public static final int THREADS = System.getProperty("THREADS") != null ? Integer.parseInt(System.getProperty("THREADS")) : 8;
    public static final int WARM_UP_PERIOD = System.getProperty("WARM_UP_PERIOD") != null ? Integer.parseInt(System.getProperty("WARM_UP_PERIOD")) : 20;
    public static final boolean WARM_UP_REQUIRED = System.getProperty("WARM_UP_PERIOD") == null;

    public static final String TEST_MODE = System.getProperty("TEST_MODE");

    public static final long DURATION = Long.parseLong(System.getProperty("DURATION"));
    public static final int ITERATIONS = Integer.parseInt(System.getProperty("TEST_MODE"));

    protected static long totalOperations = 0;
    protected static int periods = 0;

    protected static void runTimeBenchmark(final Action action) throws InterruptedException {
        ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
        final CyclicBarrier cyclicBarrier = new CyclicBarrier(THREADS);

        final AtomicBoolean interrupt = new AtomicBoolean(false);
        final LongAdder8 operationCount = new LongAdder8();

        System.err.println("Preparation started");
        ServiceFactory.getPreparation().prepareData();
        System.err.println("Preparation completed");

        for (int i = 0; i < THREADS; i++) {
            threadPool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }

                    while (!interrupt.get()) {
                        action.run();
                        operationCount.increment();
                    }
                }
            });
        }

        long stopTime = System.currentTimeMillis() + DURATION * 1000;
        long max = Long.MIN_VALUE, min = Long.MAX_VALUE;

        while (System.currentTimeMillis() < stopTime) {
            Thread.sleep(1000);
            long sum = operationCount.sumThenReset();
            periods++;
            if (isWarmupPeriodComplete()) {
                totalOperations += sum;

                max = Math.max(max, sum);
                min = Math.min(min, sum);

                System.out.println("Operation count: " + sum + " min=" + min + " max=" + max + " avg=" + totalOperations / (periods - WARM_UP_PERIOD));
            }
        }

        interrupt.set(true);

        threadPool.shutdown();
        System.out.println("Benchmark complete");
    }

    protected static void runCountBenchmark(final Action action, int iterationsCount ) throws InterruptedException {

        System.err.println("Preparation started");
        ServiceFactory.getPreparation().prepareData();
        System.err.println("Preparation completed");

        long benchmarkStart = System.currentTimeMillis();
        for (int i = 0; i < iterationsCount; i++) {
            action.run();
        }
        long benchmarkEnd = System.currentTimeMillis();

        System.out.println("Benchmark complete");
        System.out.println("Avg operation time : " + ( benchmarkEnd - benchmarkStart )/iterationsCount);
    }


    protected static boolean isWarmupPeriodComplete() {
        return WARM_UP_REQUIRED && periods > WARM_UP_PERIOD;
    }

}
