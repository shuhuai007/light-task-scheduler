package com.github.ltsopensource.client.jdl;

import java.beans.beancontext.BeanContextMembershipEvent;
import java.util.List;

/**
 * Represent job info of {@link WorkflowObject}
 */
public class JobObject {


    private String type;
    private String name;
    private int retryMax;
    private int retryInterval;
    private String prepare;
    private List<ConfigurationObject> configurationList;
    private String exec;
    private List<String> files;
    private List<String> arguments;
    private String OK;
    private String error;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRetryMax() {
        return retryMax;
    }

    public void setRetryMax(int retryMax) {
        this.retryMax = retryMax;
    }

    public int getRetryInterval() {
        return retryInterval;
    }

    public void setRetryInterval(int retryInterval) {
        this.retryInterval = retryInterval;
    }

    public String getPrepare() {
        return prepare;
    }

    public void setPrepare(String prepare) {
        this.prepare = prepare;
    }

    public void setConfiguration(List<ConfigurationObject> configurationList) {
        this.configurationList = configurationList;
    }

    public List<ConfigurationObject> getConfiguration() {
        return configurationList;
    }

    public String getExec() {
        return exec;
    }

    public void setExec(String exec) {
        this.exec = exec;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public String getOK() {
        return OK;
    }

    public void setOK(String OK) {
        this.OK = OK;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
