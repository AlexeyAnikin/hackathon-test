package com.sbt.imdg.interfaces;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public interface ITransactionEngine {

    void makeTransaction( String transactionUID );
    String makeAuthorization();

}
