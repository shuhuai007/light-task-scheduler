package com.github.ltsopensource.core.commons.utils;

import com.github.ltsopensource.core.constant.Level;
import com.github.ltsopensource.core.logger.Logger;
import com.github.ltsopensource.core.logger.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

/**
 * Unit tests for {@link LogUtils}.
 */
public class LogUtilsTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogUtilsTest.class);

    @Test
    public void logMethodEnterTest() {
        Map<String, String> result = LogUtils.logMethod(LOGGER, Level.DEBUG, "enter");
        Assert.assertEquals("com.github.ltsopensource.core.commons.utils.LogUtilsTest",
                result.get(LogUtils.CLASS_NAME_RETURN_MAP_KEY));
        Assert.assertEquals("logMethodEnterTest", result.get(LogUtils.METHOD_NAME_RETURN_MAP_KEY));
    }
}
