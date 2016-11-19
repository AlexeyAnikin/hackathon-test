package com.sbt.com.sbt.benchmarks;

import com.sbt.com.sbt.factory.ServiceFactory;
import com.sbt.imdg.interfaces.ITransactionEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public class BenchmarkTask2 extends Benchmark {

    private static ITransactionEngine service = ServiceFactory.getTransactionEngine();

    private static List<String> transactions = new ArrayList<>();

    private static AtomicLong totalAuthorizations = new AtomicLong();
    private static AtomicLong totalTransactions = new AtomicLong();

    public static void main(String[] args) throws InterruptedException {

        runTimeBenchmark(new Action() {
            @Override
            public void run() {
                ThreadLocalRandom localRandom = ThreadLocalRandom.current();

                if (isWarmupPeriodComplete()) {
                    if( totalOperations % 2 == 0 ){
                        int randomCount = localRandom.nextInt(100);

                        for( int i = 0; i < randomCount; i++ ) {
                            makeRandomAuthorization();
                        }
                    } else {
                        int randomPercent = localRandom.nextInt(0, 100);
                        int count = transactions.size() / 100 * randomPercent;
                        for( int i = 0; i < count; i++ ) {
                            makeRandomTransaction();
                        }
                    }
                } else {
                    if( totalOperations % 5 == 0 ){
                        makeRandomAuthorization();
                    } else {
                        makeRandomTransaction();
                    }
                }
            }
        });

        System.out.println("Total authorizations count: " + totalAuthorizations.get() + " Total transactions=" + totalTransactions.get());
    }

    private static void makeRandomAuthorization(){
        String transactionUID = service.makeAuthorization();
        synchronized (transactions) {
            transactions.add(transactionUID);
        }

        totalTransactions.incrementAndGet();
    }

    private static void makeRandomTransaction(){
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();

        String transactionUID;

        synchronized (transactions) {
            int randomIndex = localRandom.nextInt(transactions.size());
            transactionUID = transactions.remove(randomIndex);
        }
        service.makeTransaction(transactionUID);

        totalTransactions.incrementAndGet();
    }
}
