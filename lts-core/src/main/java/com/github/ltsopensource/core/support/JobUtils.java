package com.github.ltsopensource.core.support;

import com.github.ltsopensource.biz.logger.domain.JobLogPo;
import com.github.ltsopensource.core.commons.utils.BeanUtils;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import com.github.ltsopensource.core.constant.Constants;
import com.github.ltsopensource.core.constant.JobInfoConstants;
import com.github.ltsopensource.core.constant.JobNodeType;
import com.github.ltsopensource.core.domain.BizLog;
import com.github.ltsopensource.core.domain.Job;
import com.github.ltsopensource.core.domain.JobType;
import com.github.ltsopensource.core.support.bean.BeanCopier;
import com.github.ltsopensource.core.support.bean.BeanCopierFactory;
import com.github.ltsopensource.core.support.bean.PropConverter;
import com.github.ltsopensource.queue.domain.JobPo;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Robert HG (254963746@qq.com) on 3/26/16.
 */
public class JobUtils {

    private static final BeanCopier<Job, Job> JOB_BEAN_COPIER;
    private static final BeanCopier<JobPo, JobPo> JOB_PO_BEAN_COPIER;

    private static final BeanCopier<JobLogPo, BizLog> JOB_LOG_PO_2_BIZ_LOG_BEAN_COPIER;
    private static final BeanCopier<BizLog, JobLogPo> BIZ_LOG_2_JOB_LOG_PO_BEAN_COPIER;

    static {
        JOB_PO_BEAN_COPIER = generateJobPo2JobPo();

        JOB_BEAN_COPIER = generateJob2Job();

        JOB_LOG_PO_2_BIZ_LOG_BEAN_COPIER = generateJobLogPo2BizLog();

        BIZ_LOG_2_JOB_LOG_PO_BEAN_COPIER = generateBizLog2JobLogPo();
    }

    private static BeanCopier<BizLog, JobLogPo> generateBizLog2JobLogPo() {
        Map<String, PropConverter<?, ?>> jobLogPoConverterMap = new ConcurrentHashMap<String, PropConverter<?, ?>>(1);
        jobLogPoConverterMap.put("extParams", new PropConverter<BizLog, Map<String, String>>() {
            @Override
            public Map<String, String> convert(BizLog bizLog) {
                return BeanUtils.copyMap(bizLog.getExtParams());
            }
        });
        jobLogPoConverterMap.put("internalExtParams", new PropConverter<BizLog, Map<String, String>>() {
            @Override
            public Map<String, String> convert(BizLog bizLog) {
                return BeanUtils.copyMap(bizLog.getInternalExtParams());
            }
        });
        return BeanCopierFactory.createCopier(BizLog.class, JobLogPo.class,
                jobLogPoConverterMap);
    }

    private static BeanCopier<JobPo, JobPo> generateJobPo2JobPo() {
        Map<String, PropConverter<?, ?>> jobPoConverterMap = new ConcurrentHashMap<String, PropConverter<?, ?>>(1);
        // 目前只有这个 extParams和 internalExtParams 不是基本类型, 为了不采用流的方式, 从而提升性能
        jobPoConverterMap.put("extParams", new PropConverter<JobPo, Map<String, String>>() {
            @Override
            public Map<String, String> convert(JobPo jobPo) {
                return BeanUtils.copyMap(jobPo.getExtParams());
            }
        });
        jobPoConverterMap.put("internalExtParams", new PropConverter<JobPo, Map<String, String>>() {
            @Override
            public Map<String, String> convert(JobPo jobPo) {
                return BeanUtils.copyMap(jobPo.getInternalExtParams());
            }
        });
        return BeanCopierFactory.createCopier(JobPo.class, JobPo.class, jobPoConverterMap);
    }

    private static BeanCopier<Job, Job> generateJob2Job() {
        Map<String, PropConverter<?, ?>> jobConverterMap = new ConcurrentHashMap<String, PropConverter<?, ?>>(1);
        // 目前只有这个 extParams不是基本类型, 为了不采用流的方式, 从而提升性能
        jobConverterMap.put("extParams", new PropConverter<Job, Map<String, String>>() {
            @Override
            public Map<String, String> convert(Job job) {
                return BeanUtils.copyMap(job.getExtParams());
            }
        });

        return BeanCopierFactory.createCopier(Job.class, Job.class, jobConverterMap);
    }

    private static BeanCopier<JobLogPo, BizLog> generateJobLogPo2BizLog() {
        Map<String, PropConverter<?, ?>> jobLogPoConverterMap = new ConcurrentHashMap<String, PropConverter<?, ?>>(1);
        jobLogPoConverterMap.put("extParams", new PropConverter<JobLogPo, Map<String, String>>() {
            @Override
            public Map<String, String> convert(JobLogPo jobLogPo) {
                return BeanUtils.copyMap(jobLogPo.getExtParams());
            }
        });
        jobLogPoConverterMap.put("internalExtParams", new PropConverter<JobLogPo, Map<String, String>>() {
            @Override
            public Map<String, String> convert(JobLogPo jobLogPo) {
                return BeanUtils.copyMap(jobLogPo.getInternalExtParams());
            }
        });
        return BeanCopierFactory.createCopier(JobLogPo.class, BizLog.class,
                jobLogPoConverterMap);
    }

    public static long getRepeatNextTriggerTime(JobPo jobPo) {
        long firstTriggerTime = Long.valueOf(jobPo.getInternalExtParam(Constants.FIRST_FIRE_TIME));
        long now = SystemClock.now();
        long remainder = (now - firstTriggerTime) % jobPo.getRepeatInterval();
        if (remainder == 0) {
            return now;
        }
        return now + (jobPo.getRepeatInterval() - remainder);
    }

    public static boolean isRelyOnPrevCycle(JobPo jobPo) {
        return (jobPo.getRelyOnPrevCycle() == null || jobPo.getRelyOnPrevCycle().booleanValue());
    }

    public static String generateJobId() {
        return StringUtils.generateUUID();
    }

    public static Job copy(Job source) {
        Job job = new Job();
        JOB_BEAN_COPIER.copyProps(source, job);
        return job;
    }

    public static JobPo copy(JobPo source) {
        JobPo jobPo = new JobPo();
        JOB_PO_BEAN_COPIER.copyProps(source, jobPo);
        return jobPo;
    }

    public static BizLog copyJobLogPo2BizLog(JobLogPo source) {
        BizLog bizLog = new BizLog();

        JOB_LOG_PO_2_BIZ_LOG_BEAN_COPIER.copyProps(source, bizLog);
        return bizLog;
    }

    public static List<String> getParentList(JobPo jobPo) {
        String parents = jobPo.getExtParam(JobInfoConstants.JOB_PARAM_PARENTS_KEY);
        if (StringUtils.isEmpty(parents)) {
            return null;
        }
        String[] parentArr = StringUtils.splitWithTrim(parents, JobInfoConstants
                .JOB_PARENTS_CHILDREN_SEPARATOR);
        return Arrays.asList(parentArr);
    }

    public static boolean isSinglePeriodJob(JobPo jobPo) {
        return jobPo.getJobType().equals(JobType.REAL_TIME) ||
                jobPo.getJobType().equals(JobType.TRIGGER_TIME);
    }

    public static boolean isVirtualNode(JobPo jobPo) {
        JobNodeType jobNodeType = jobPo.getJobNodeType();
        return jobNodeType == JobNodeType.START_JOB
                || jobNodeType == JobNodeType.END_JOB
                || jobNodeType == JobNodeType.DECISION_JOB
                || jobNodeType == JobNodeType.FORK_JOB
                || jobNodeType == JobNodeType.JOIN_JOB;
    }

    public static JobLogPo copyBizLog2JobLogPo(BizLog bizLog) {
        JobLogPo jobLogPo = new JobLogPo();
        BIZ_LOG_2_JOB_LOG_PO_BEAN_COPIER.copyProps(bizLog, jobLogPo);
        return jobLogPo;
    }
}
