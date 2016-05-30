package com.github.ltsopensource.jobtracker.domain;

import com.github.ltsopensource.biz.logger.JobLogger;
import com.github.ltsopensource.core.AppContext;
import com.github.ltsopensource.core.remoting.RemotingServerDelegate;
import com.github.ltsopensource.jobtracker.channel.ChannelManager;
import com.github.ltsopensource.jobtracker.sender.JobSender;
import com.github.ltsopensource.jobtracker.support.JobReceiver;
import com.github.ltsopensource.jobtracker.support.OldDataHandler;
import com.github.ltsopensource.jobtracker.support.checker.ExecutableDeadJobChecker;
import com.github.ltsopensource.jobtracker.support.checker.ExecutingDeadJobChecker;
import com.github.ltsopensource.jobtracker.support.checker.FeedbackJobSendChecker;
import com.github.ltsopensource.jobtracker.support.checker.WaitingJobQueueChecker;
import com.github.ltsopensource.jobtracker.support.cluster.JobClientManager;
import com.github.ltsopensource.jobtracker.support.cluster.TaskTrackerManager;
import com.github.ltsopensource.jobtracker.support.scheduler.CronJobScheduler;
import com.github.ltsopensource.jobtracker.support.scheduler.NonRelyOnPrevCycleJobScheduler;
import com.github.ltsopensource.queue.CronJobQueue;
import com.github.ltsopensource.queue.ExecutableJobQueue;
import com.github.ltsopensource.queue.ExecutingJobQueue;
import com.github.ltsopensource.queue.JobFeedbackQueue;
import com.github.ltsopensource.queue.NodeGroupStore;
import com.github.ltsopensource.queue.PreLoader;
import com.github.ltsopensource.queue.RepeatJobQueue;
import com.github.ltsopensource.queue.SuspendJobQueue;
import com.github.ltsopensource.queue.WaitingJobQueue;

/**
 * JobTracker Application.
 */
public class JobTrackerAppContext extends AppContext {

    private RemotingServerDelegate remotingServer;
    // channel manager
    private ChannelManager channelManager;
    // JobClient manager for job tracker
    private JobClientManager jobClientManager;
    // TaskTracker manager for job tracker
    private TaskTrackerManager taskTrackerManager;
    // dead job checker
    private ExecutingDeadJobChecker executingDeadJobChecker;
    private FeedbackJobSendChecker feedbackJobSendChecker;
    private ExecutableDeadJobChecker executableDeadJobChecker;

    // old data handler, dirty data
    private OldDataHandler oldDataHandler;
    // biz logger
    private JobLogger jobLogger;

    // waiting job queue (waiting for entering th executable job queue)
    private WaitingJobQueue waitingJobQueue;
    // executable job queue（waiting for exec）
    private ExecutableJobQueue executableJobQueue;
    // executing job queue
    private ExecutingJobQueue executingJobQueue;
    // store the connected node groups
    private NodeGroupStore nodeGroupStore;

    // Cron Job queue
    private CronJobQueue cronJobQueue;
    // Feedback queue
    private JobFeedbackQueue jobFeedbackQueue;
    private SuspendJobQueue suspendJobQueue;
    private RepeatJobQueue repeatJobQueue;
    private PreLoader preLoader;
    private JobReceiver jobReceiver;
    private JobSender jobSender;

    private NonRelyOnPrevCycleJobScheduler nonRelyOnPrevCycleJobScheduler;
    private WaitingJobQueueChecker waitingJobQueueChecker;
    private CronJobScheduler cronJobScheduler;

    /**
     * Gets {@link JobSender}.
     *
     * @return job sender object
     */
    public JobSender getJobSender() {
        return jobSender;
    }

    /**
     * Sets {@link JobSender}.
     *
     * @param jobSender job sender
     */
    public void setJobSender(JobSender jobSender) {
        this.jobSender = jobSender;
    }

    /**
     * Gets {@link JobReceiver}.
     *
     * @return job receiver
     */
    public JobReceiver getJobReceiver() {
        return jobReceiver;
    }

    /**
     * Sets {@link JobReceiver}.
     *
     * @param jobReceiver job receiver
     */
    public void setJobReceiver(JobReceiver jobReceiver) {
        this.jobReceiver = jobReceiver;
    }

    /**
     * Gets {@link PreLoader}.
     *
     * @return pre loader
     */
    public PreLoader getPreLoader() {
        return preLoader;
    }

    /**
     * Sets {@link PreLoader}.
     *
     * @param preLoader pre loader
     */
    public void setPreLoader(PreLoader preLoader) {
        this.preLoader = preLoader;
    }

    /**
     * Gets {@link JobLogger}.
     *
     * @return job logger
     */
    public JobLogger getJobLogger() {
        return jobLogger;
    }

    /**
     * Sets {@link JobLogger}.
     *
     * @param jobLogger job logger
     */
    public void setJobLogger(JobLogger jobLogger) {
        this.jobLogger = jobLogger;
    }

    /**
     * Gets {@link JobFeedbackQueue}.
     *
     * @return job feedback queue
     */
    public JobFeedbackQueue getJobFeedbackQueue() {
        return jobFeedbackQueue;
    }

    /**
     * Sets {@link JobFeedbackQueue}.
     *
     * @param jobFeedbackQueue job feedback queue
     */
    public void setJobFeedbackQueue(JobFeedbackQueue jobFeedbackQueue) {
        this.jobFeedbackQueue = jobFeedbackQueue;
    }

    /**
     * Gets {@link RemotingServerDelegate}.
     *
     * @return remoting server delegate
     */
    public RemotingServerDelegate getRemotingServer() {
        return remotingServer;
    }

    /**
     * Sets {@link RemotingServerDelegate}.
     *
     * @param remotingServer remoting server delegate
     */
    public void setRemotingServer(RemotingServerDelegate remotingServer) {
        this.remotingServer = remotingServer;
    }

    /**
     * Gets {@link ChannelManager}.
     *
     * @return channel manager
     */
    public ChannelManager getChannelManager() {
        return channelManager;
    }

    /**
     * Sets {@link ChannelManager}.
     *
     * @param channelManager channel manager
     */
    public void setChannelManager(ChannelManager channelManager) {
        this.channelManager = channelManager;
    }

    /**
     * Gets {@link JobClientManager}.
     *
     * @return job client manager
     */
    public JobClientManager getJobClientManager() {
        return jobClientManager;
    }

    /**
     * Sets {@link JobClientManager}.
     *
     * @param jobClientManager job client manager
     */
    public void setJobClientManager(JobClientManager jobClientManager) {
        this.jobClientManager = jobClientManager;
    }

    /**
     * Gets {@link TaskTrackerManager}.
     *
     * @return taskTracker manager
     */
    public TaskTrackerManager getTaskTrackerManager() {
        return taskTrackerManager;
    }

    /**
     * Sets {@link TaskTrackerManager}.
     *
     * @param taskTrackerManager taskTracker manager
     */
    public void setTaskTrackerManager(TaskTrackerManager taskTrackerManager) {
        this.taskTrackerManager = taskTrackerManager;
    }

    /**
     * Gets {@link ExecutingDeadJobChecker}.
     *
     * @return executingDeadJobChecker
     */
    public ExecutingDeadJobChecker getExecutingDeadJobChecker() {
        return executingDeadJobChecker;
    }

    /**
     * Sets {@link ExecutingDeadJobChecker}.
     *
     * @param executingDeadJobChecker executing dead job checker
     */
    public void setExecutingDeadJobChecker(ExecutingDeadJobChecker executingDeadJobChecker) {
        this.executingDeadJobChecker = executingDeadJobChecker;
    }

    /**
     * Gets {@link OldDataHandler}.
     *
     * @return old data handler
     */
    public OldDataHandler getOldDataHandler() {
        return oldDataHandler;
    }

    /**
     * Sets {@link OldDataHandler}.
     *
     * @param oldDataHandler old data handler
     */
    public void setOldDataHandler(OldDataHandler oldDataHandler) {
        this.oldDataHandler = oldDataHandler;
    }

    /**
     * Gets {@link CronJobQueue}.
     *
     * @return cron job queue
     */
    public CronJobQueue getCronJobQueue() {
        return cronJobQueue;
    }

    /**
     * Sets {@link CronJobQueue}.
     *
     * @param cronJobQueue cron job queue
     */
    public void setCronJobQueue(CronJobQueue cronJobQueue) {
        this.cronJobQueue = cronJobQueue;
    }

    /**
     * Gets {@link ExecutableJobQueue}.
     *
     * @return executable job queue
     */
    public ExecutableJobQueue getExecutableJobQueue() {
        return executableJobQueue;
    }

    /**
     * Sets {@link ExecutableJobQueue}.
     *
     * @param executableJobQueue executable job queue
     */
    public void setExecutableJobQueue(ExecutableJobQueue executableJobQueue) {
        this.executableJobQueue = executableJobQueue;
    }

    /**
     * Gets {@link ExecutingJobQueue}.
     *
     * @return executing job queue
     */
    public ExecutingJobQueue getExecutingJobQueue() {
        return executingJobQueue;
    }

    /**
     * Sets {@link ExecutingJobQueue}.
     *
     * @param executingJobQueue executing job queue
     */
    public void setExecutingJobQueue(ExecutingJobQueue executingJobQueue) {
        this.executingJobQueue = executingJobQueue;
    }

    /**
     * Gets {@link NodeGroupStore}.
     *
     * @return node group store object
     */
    public NodeGroupStore getNodeGroupStore() {
        return nodeGroupStore;
    }

    /**
     * Sets {@link NodeGroupStore}.
     *
     * @param nodeGroupStore node group store object
     */
    public void setNodeGroupStore(NodeGroupStore nodeGroupStore) {
        this.nodeGroupStore = nodeGroupStore;
    }

    /**
     * Gets {@link SuspendJobQueue}.
     *
     * @return suspend job queue
     */
    public SuspendJobQueue getSuspendJobQueue() {
        return suspendJobQueue;
    }

    /**
     * Sets {@link SuspendJobQueue}.
     *
     * @param suspendJobQueue suspend job queue
     */
    public void setSuspendJobQueue(SuspendJobQueue suspendJobQueue) {
        this.suspendJobQueue = suspendJobQueue;
    }

    /**
     * Gets {@link RepeatJobQueue}.
     *
     * @return repeat job queue
     */
    public RepeatJobQueue getRepeatJobQueue() {
        return repeatJobQueue;
    }

    /**
     * Sets {@link RepeatJobQueue}.
     *
     * @param repeatJobQueue repeat job queue
     */
    public void setRepeatJobQueue(RepeatJobQueue repeatJobQueue) {
        this.repeatJobQueue = repeatJobQueue;
    }

    /**
     * Gets {@link NonRelyOnPrevCycleJobScheduler}.
     *
     * @return non rely on previous cycle job scheduler
     */
    public NonRelyOnPrevCycleJobScheduler getNonRelyOnPrevCycleJobScheduler() {
        return nonRelyOnPrevCycleJobScheduler;
    }

    /**
     * Sets {@link NonRelyOnPrevCycleJobScheduler}.
     *
     * @param nonRelyOnPrevCycleJobScheduler non rely on previous cycle job scheduler
     */
    public void setNonRelyOnPrevCycleJobScheduler(NonRelyOnPrevCycleJobScheduler nonRelyOnPrevCycleJobScheduler) {
        this.nonRelyOnPrevCycleJobScheduler = nonRelyOnPrevCycleJobScheduler;
    }

    /**
     * Gets feedback job send checker.
     *
     * @return feedback job send checker
     */
    public FeedbackJobSendChecker getFeedbackJobSendChecker() {
        return feedbackJobSendChecker;
    }

    /**
     * Sets feedback job send checker.
     *
     * @param feedbackJobSendChecker feedback job send checker
     */
    public void setFeedbackJobSendChecker(FeedbackJobSendChecker feedbackJobSendChecker) {
        this.feedbackJobSendChecker = feedbackJobSendChecker;
    }

    /**
     * Gets {@link ExecutableDeadJobChecker}.
     *
     * @return executable dead job checker
     */
    public ExecutableDeadJobChecker getExecutableDeadJobChecker() {
        return executableDeadJobChecker;
    }

    /**
     * Sets {@link ExecutableDeadJobChecker}.
     *
     * @param executableDeadJobChecker executable dead job checker
     */
    public void setExecutableDeadJobChecker(ExecutableDeadJobChecker executableDeadJobChecker) {
        this.executableDeadJobChecker = executableDeadJobChecker;
    }

    /**
     * Sets {@link WaitingJobQueue}.
     *
     * @param waitingJobQueue waiting job queue
     */
    public void setWaitingJobQueue(WaitingJobQueue waitingJobQueue) {
        this.waitingJobQueue = waitingJobQueue;
    }

    /**
     * Gets {@link WaitingJobQueue}.
     *
     * @return waiting job queue
     */
    public WaitingJobQueue getWaitingJobQueue() {
        return waitingJobQueue;
    }

    /**
     * Sets {@link WaitingJobQueueChecker}.
     *
     * @param waitingJobQueueChecker waiting job queue checker
     */
    public void setWaitingJobQueueChecker(WaitingJobQueueChecker waitingJobQueueChecker) {
        this.waitingJobQueueChecker = waitingJobQueueChecker;
    }

    /**
     * Gets {@link WaitingJobQueueChecker}.
     *
     * @return waiting job queue checker
     */
    public WaitingJobQueueChecker getWaitingJobQueueChecker() {
        return waitingJobQueueChecker;
    }

    /**
     * Sets {@link CronJobScheduler}.
     *
     * @param cronJobScheduler cron job scheduler
     */
    public void setCronJobScheduler(CronJobScheduler cronJobScheduler) {
        this.cronJobScheduler = cronJobScheduler;
    }

    /**
     * Gets {@link CronJobScheduler}.
     *
     * @return cron job scheduler
     */
    public CronJobScheduler getCronJobScheduler() {
        return cronJobScheduler;
    }
}
