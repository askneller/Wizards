package com.example.wizards;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class Util {

    private static final Logger logger = LogUtils.getLogger();

    public static void printStackTrace(int lines) {
        logger.info("Here");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.out.println("\nTrace");
        int len = lines;
        if (stackTrace.length < len)
            len = stackTrace.length;
        for (int i = 0; i < len; i++) {
            System.out.println(stackTrace[i]);
        }
        System.out.println("\n");
    }
}
