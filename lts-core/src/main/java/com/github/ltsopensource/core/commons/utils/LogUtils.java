package com.github.ltsopensource.core.commons.utils;

import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.logger.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Utils for logging.
 */
public class LogUtils {

    public static final String METHOD_NAME_RETURN_MAP_KEY = "method_name";
    public static final String CLASS_NAME_RETURN_MAP_KEY = "class_name";

    public static Map<String, String> logMethod(Logger logger, Level level, String keyMessage) {
        Map<String, String> resultMap = new HashMap<String, String>();
        final String className = getClassName();
        final String methodName = getMethodName();
        if (level.equals(Level.DEBUG)) {
            logger.debug(keyMessage + " " + className +", method:" + methodName);
        } else if (level.equals(Level.INFO)) {
            logger.info(keyMessage + " " + className + ", method:" + methodName);
        }
        resultMap.put(METHOD_NAME_RETURN_MAP_KEY, methodName);
        resultMap.put(CLASS_NAME_RETURN_MAP_KEY, className);
        return resultMap;
    }

    private static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

    private static String getClassName() {
        return Thread.currentThread().getStackTrace()[3].getClassName();
    }

}
