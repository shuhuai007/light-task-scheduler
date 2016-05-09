package com.github.ltsopensource.startup.tasktracker.test;

import com.github.ltsopensource.core.domain.Action;
import com.github.ltsopensource.core.json.JSON;
import com.github.ltsopensource.tasktracker.Result;
import com.github.ltsopensource.tasktracker.runner.JobContext;
import com.github.ltsopensource.tasktracker.runner.JobRunner;

import java.util.Date;

/**
 * @author Robert HG (254963746@qq.com) on 9/12/15.
 */
public class TestSleep2MinutesJobRunner implements JobRunner {

    @Override
    public Result run(JobContext jobContext) throws Throwable {
        System.out.println("...:" + new Date());
        System.out.println("...zhoujie test");
        System.out.println(JSON.toJSONString(jobContext));

        Thread.sleep(1000 * 60 * 2);

        return new Result(Action.EXECUTE_SUCCESS);
    }
}
