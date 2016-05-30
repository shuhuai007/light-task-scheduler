package com.github.ltsopensource.cmd;

/**
 * Http command processor.
 */
public interface HttpCmdProc {
    /**
     * Gets node identity.
     *
     * @return string of node identity
     */
    String nodeIdentity();

    /**
     * Gets http command.
     *
     * @return http command
     */
    String getCommand();

    /**
     * Sends {@link HttpCmdRequest} remotely.
     *
     * @param request http command request
     * @return http command response
     * @throws Exception if executing this request
     */
    HttpCmdResponse execute(HttpCmdRequest request) throws Exception;
}
