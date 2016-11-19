package com.sbt.com.sbt.benchmarks;

import com.sbt.com.sbt.factory.ServiceFactory;
import com.sbt.imdg.interfaces.IMassOperations;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public class BenchmarkTask1 extends Benchmark {

    private static long now = new Date().getTime();
    private static IMassOperations service = ServiceFactory.getMassOperations();

    private static List<String> completeOperations = new ArrayList<>();

    private static AtomicLong totalCalculations = new AtomicLong();
    private static AtomicLong totalRollbacks = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {

        if( "TIME".equals(TEST_MODE) ) {

            runTimeBenchmark(new Action() {
                @Override
                public void run() {
                    if (isWarmupPeriodComplete()) {
                        if (totalOperations % 5 == 0) {
                            rollbackRandomCalculation();
                        } else {
                            runCalculationWithRandomDate();
                        }
                    } else {
                        runCalculationWithRandomDate();
                    }
                }
            });
        } else {
            runCountBenchmark(new Action() {
                @Override
                public void run() {
                    runCalculationWithRandomDate();
                }
            }, ITERATIONS);

            runCountBenchmark(new Action() {
                @Override
                public void run() {
                    rollbackRandomCalculation();
                }
            }, ITERATIONS/5);
        }

        System.out.println("Total calculations count: " + totalCalculations.get() + " Total rollbacks=" + totalRollbacks.get());
    }

    private static void runCalculationWithRandomDate(){
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        long randomTimestamp = localRandom.nextLong(now, Long.MAX_VALUE);
        String calculationUID = service.runCalculation(new Date(randomTimestamp));
        synchronized (completeOperations) {
            completeOperations.add(calculationUID);
        }
        totalCalculations.incrementAndGet();
    }

    private static void rollbackRandomCalculation(){
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        String calculationUID;
        synchronized (completeOperations) {
            int randomIndex = localRandom.nextInt(completeOperations.size());
            calculationUID = completeOperations.remove(randomIndex);
        }
        service.rollbackCalculation(calculationUID);

        totalRollbacks.incrementAndGet();
    }
}
