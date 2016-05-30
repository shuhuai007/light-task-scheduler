package com.github.ltsopensource.jobtracker;

import com.github.ltsopensource.biz.logger.SmartJobLogger;
import com.github.ltsopensource.core.cluster.AbstractServerNode;
import com.github.ltsopensource.core.spi.ServiceLoader;
import com.github.ltsopensource.jobtracker.channel.ChannelManager;
import com.github.ltsopensource.jobtracker.cmd.KillLTSTaskHttpCmd;
import com.github.ltsopensource.jobtracker.cmd.ResumeLTSTaskHttpCmd;
import com.github.ltsopensource.jobtracker.cmd.SubmitLTSTaskHttpCmd;
import com.github.ltsopensource.jobtracker.cmd.SuspendLTSTaskHttpCmd;
import com.github.ltsopensource.jobtracker.domain.JobTrackerAppContext;
import com.github.ltsopensource.jobtracker.domain.JobTrackerNode;
import com.github.ltsopensource.jobtracker.monitor.JobTrackerMStatReporter;
import com.github.ltsopensource.jobtracker.processor.RemotingDispatcher;
import com.github.ltsopensource.jobtracker.sender.JobSender;
import com.github.ltsopensource.jobtracker.support.JobReceiver;
import com.github.ltsopensource.jobtracker.support.OldDataHandler;
import com.github.ltsopensource.jobtracker.support.checker.ExecutableDeadJobChecker;
import com.github.ltsopensource.jobtracker.support.checker.ExecutingDeadJobChecker;
import com.github.ltsopensource.jobtracker.support.checker.FeedbackJobSendChecker;
import com.github.ltsopensource.jobtracker.support.checker.WaitingJobQueueChecker;
import com.github.ltsopensource.jobtracker.support.cluster.JobClientManager;
import com.github.ltsopensource.jobtracker.support.cluster.TaskTrackerManager;
import com.github.ltsopensource.jobtracker.support.listener.JobNodeChangeListener;
import com.github.ltsopensource.jobtracker.support.listener.JobTrackerMasterChangeListener;
import com.github.ltsopensource.jobtracker.support.policy.OldDataDeletePolicy;
import com.github.ltsopensource.jobtracker.support.scheduler.CronJobScheduler;
import com.github.ltsopensource.queue.JobQueueFactory;
import com.github.ltsopensource.remoting.RemotingProcessor;

/**
 * JobTracker.
 */
public class JobTracker extends AbstractServerNode<JobTrackerNode, JobTrackerAppContext> {

    /**
     * Constructs new {@link JobTracker}.
     */
    public JobTracker() {
        // 添加节点变化监听器
        addNodeChangeListener(new JobNodeChangeListener(appContext));
        // 添加master节点变化监听器
        addMasterChangeListener(new JobTrackerMasterChangeListener(appContext));
    }

    @Override
    protected void beforeStart() {
        // 监控中心
        appContext.setMStatReporter(new JobTrackerMStatReporter(appContext));
        // channel 管理者
        appContext.setChannelManager(new ChannelManager());
        // JobClient 管理者
        appContext.setJobClientManager(new JobClientManager(appContext));
        // TaskTracker 管理者
        appContext.setTaskTrackerManager(new TaskTrackerManager(appContext));
        // injectRemotingServer
        appContext.setRemotingServer(remotingServer);
        appContext.setJobLogger(new SmartJobLogger(appContext));

        JobQueueFactory factory = ServiceLoader.load(JobQueueFactory.class, config);

        appContext.setWaitingJobQueue(factory.getWaitingJobQueue(config));
        appContext.setExecutableJobQueue(factory.getExecutableJobQueue(config));
        appContext.setExecutingJobQueue(factory.getExecutingJobQueue(config));
        appContext.setCronJobQueue(factory.getCronJobQueue(config));
        appContext.setRepeatJobQueue(factory.getRepeatJobQueue(config));
        appContext.setSuspendJobQueue(factory.getSuspendJobQueue(config));
        appContext.setJobFeedbackQueue(factory.getJobFeedbackQueue(config));
        appContext.setNodeGroupStore(factory.getNodeGroupStore(config));
//        appContext.setPreLoader(factory.getPreLoader(appContext));
        appContext.setPreLoader(new VirtualJobFilterMysqlPreLoader(appContext));
        appContext.setJobReceiver(new JobReceiver(appContext));
        appContext.setJobSender(new JobSender(appContext));
//        appContext.setNonRelyOnPrevCycleJobScheduler(new NonRelyOnPrevCycleJobScheduler(appContext));
        appContext.setCronJobScheduler(new CronJobScheduler(appContext));
        appContext.setExecutableDeadJobChecker(new ExecutableDeadJobChecker(appContext));
        appContext.setExecutingDeadJobChecker(new ExecutingDeadJobChecker(appContext));
        appContext.setFeedbackJobSendChecker(new FeedbackJobSendChecker(appContext));
        appContext.setWaitingJobQueueChecker(new WaitingJobQueueChecker(appContext));

        appContext.getHttpCmdServer().registerCommands(
                new SubmitLTSTaskHttpCmd(appContext),
                new KillLTSTaskHttpCmd(appContext),
                new SuspendLTSTaskHttpCmd(appContext),
                new ResumeLTSTaskHttpCmd(appContext));

        if (appContext.getOldDataHandler() == null) {
            appContext.setOldDataHandler(new OldDataDeletePolicy());
        }
    }

    @Override
    protected void afterStart() {
        appContext.getChannelManager().start();
        appContext.getMStatReporter().start();
    }

    @Override
    protected void afterStop() {
        appContext.getChannelManager().stop();
        appContext.getMStatReporter().stop();
        appContext.getHttpCmdServer().stop();
    }

    @Override
    protected void beforeStop() {
    }

    @Override
    protected RemotingProcessor getDefaultProcessor() {
        return new RemotingDispatcher(appContext);
    }

    /**
     * Set {@link OldDataHandler}.
     *
     * @param oldDataHandler old data handler
     */
    public void setOldDataHandler(OldDataHandler oldDataHandler) {
        appContext.setOldDataHandler(oldDataHandler);
    }

}
