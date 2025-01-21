package com.sgi.bootcoin.domain.shared;

import java.util.Random;

/**
 * Utility class for defining constants and helper methods used throughout the application.
 * This class includes static constants and a method to generate unique card numbers.
 */
public class Constants {

    public static final String KAFKA_MESSAGE_RECEIVING = "Receiving message topic: {}";
    public static final String OPERATION_SUCCESS = "Operation performed successfully {}";
    public static final String OPERATION_FAILED = "It was not processed correctly for reason: {}";
    public static final String BOOTCOIN = "Bootcoin";


    public static String generateRandomId() {
        Random random = new Random();
        long number = (long) (random.nextDouble() * 1_000_000_000_000L);
        return String.format("%08d", number);
    }
}
