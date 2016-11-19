package com.sbt.com.sbt.factory;

import com.sbt.imdg.interfaces.IMassOperations;
import com.sbt.imdg.interfaces.IPreparation;
import com.sbt.imdg.interfaces.ITransactionEngine;

import java.util.Date;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public class ServiceFactory {

    public static IMassOperations getMassOperations(){
        return new IMassOperations(){
            @Override
            public String runCalculation(Date date) {
                return null;
            }

            @Override
            public void rollbackCalculation(String calculationUid) {

            }
        };
    }

    public static IPreparation getPreparation(){
        return new IPreparation(){
            @Override
            public void prepareData() {

            }
        };
    }

    public static ITransactionEngine getTransactionEngine(){
        return new ITransactionEngine(){
            @Override
            public void makeTransaction(String transactionUID) {

            }

            @Override
            public String makeAuthorization() {
                return null;
            }
        };
    }


}
