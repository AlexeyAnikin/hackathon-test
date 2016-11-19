package com.sbt.imdg.interfaces;

import java.util.Date;

/**
 * @author AlexAnikin
 * @date 19.11.16
 */
public interface IMassOperations {

    /**
     * Запуск массового вычисления
     *
     * @param date - Дата вычисления
     * @return UID операции
     */
    String runCalculation( Date date );


    /**
     * Отмена массовой операции
     *
     * @param calculationUid - UID операции
     */
    void rollbackCalculation( String calculationUid );

}
