package com.github.ltsopensource.core.support;

import com.github.ltsopensource.core.exception.CronException;

import java.text.ParseException;
import java.util.Date;
import java.util.TreeSet;

/**
 * @author Robert HG (254963746@qq.com) on 5/27/15.
 */
public class CronExpressionUtils {

    private CronExpressionUtils() {
    }

    public static Date getNextTriggerTime(String cronExpression) {
        return getNextTriggerTime(cronExpression, new Date());
    }

    public static Date getNextTriggerTime(String cronExpression, Date timeAfter) {
        try {
            CronExpression cron = new CronExpression(cronExpression);
            if (timeAfter == null) {
                timeAfter = new Date();
            }
            return cron.getTimeAfter(timeAfter);
        } catch (ParseException e) {
            throw new CronException(e);
        }
    }

    public static boolean isValidExpression(String cronExpression) {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * Gets the previous trigger time based on the cron expression and the time.
     * @param cronExpression crontab expression
     * @param timeBefore the time ruler
     * @return Date which indicates the previous trigger time
     */
    public static Date getPreviousTriggerTime(String cronExpression, Date timeBefore) {
        // TODO (zj: Need to implement)
        return null;
    }

    public static void main(String[] args) {
        String cronExpr = "59 1 * * * ?";
//        System.out.println(getNextTriggerTime(cronExpr, new Date()));
        Date time = getNextTriggerTime(cronExpr, new Date());
        System.out.println(time);
        System.out.println(getNextTriggerTime(cronExpr, time));

        Date previousDate = getPreviousTriggerTime(cronExpr, new Date());
        System.out.println(previousDate);
        System.out.println(getPreviousTriggerTime(cronExpr, previousDate));

        // test treeSet
        TreeSet<Integer> treeSet = new TreeSet<Integer>();
        treeSet.add(10);
        treeSet.add(1);
        treeSet.add(5);
//        System.out.println(treeSet);
//        System.out.println(treeSet.descendingSet());
//        System.out.println(treeSet.descendingSet().tailSet(6));
//        System.out.println("tail set:" + treeSet.tailSet(5));
//        System.out.println("head set" + treeSet.headSet(5));


    }
}
