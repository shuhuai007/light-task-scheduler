package com.github.ltsopensource.client.utils;

import com.github.ltsopensource.client.LTSClient;
import com.github.ltsopensource.client.jdl.JDLObject;
import com.github.ltsopensource.core.domain.LTSTask;
import com.github.ltsopensource.core.json.JSON;

/**
 * Parser of JDL.
 */
public class JDLParser {

    public static boolean verifyJDL(String jdl) {
        // TODO(zj): to be implemented
        try {
            JDLObject jdlObject = JSON.parse(jdl, JDLObject.class);
//            System.out.println(jdlObject.getEngine());
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    /**
     * Parse the jdl, and generate the {@link LTSTask}.
     *
     * @param jdl jdl string
     * @param taskId generated from task database table
     * @return lts task object
     */
    public static LTSTask generateLTSTask(String jdl, String taskId) {
        JDLObject jdlObject = parse(jdl);
        LTSTask ltsTask = new LTSTask();

        return ltsTask;
    }

    public static JDLObject parse(String jdl) {
        JDLObject jdlObject = JSON.parse(jdl, JDLObject.class);
        return jdlObject;
    }
}
