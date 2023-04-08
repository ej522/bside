package com.example.beside.util;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log4j2Test {
    private static Logger logger = LoggerFactory.getLogger(Log4j2Test.class);
    @Test
    public static void main(String[] args) {
        logger.info("log_into");
        logger.warn("log_warn");
        logger.debug("log_debug");
        logger.error("log_error");
        logger.trace("log_trace");

    }
}
