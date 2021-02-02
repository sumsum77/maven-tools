/*
 Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.synapse.unittest;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import java.io.IOException;

/**
 * SynapseTestCase file read class in unit test framework.
 */
class UnitTestClient {

    private static Log log;

    /**
     * private constructor of the UnitTestClient.
     */
    private UnitTestClient() {
    }

    /**
     * static method of executing the synapse unit testing client.
     *
     * @param synapseTestCaseFilePath synapse test case file path
     * @param synapseHost synapse unit test server host
     * @param synapsePort synapse unit test server port
     * @return response from the unit testing agent received via TCP transport
     * @throws IOException when tcp socket not initialized
     */
    static String executeTests(String synapseTestCaseFilePath, String synapseHost, String synapsePort)
            throws IOException {
        String responseFromServer = null;
        String deployableMessage = null;
        try {
            //check whether unit test suite has test cases or not
            deployableMessage = SynapseTestCaseFileReader.processArtifactData(synapseTestCaseFilePath);
            if (deployableMessage != null && deployableMessage.equals(Constants.NO_TEST_CASES)) {
                return deployableMessage;
            }
        } catch (Exception e) {
            getLog().error("Error while executing client", e);
        }

        //process SynapseTestCase data for send to the server
        if (deployableMessage != null) {
            //create tcp connection, send SynapseTestCase file to server and get the response from the server
            TCPClient tcpClient = new TCPClient(synapseHost, synapsePort);
            if (!tcpClient.isSocketInitialized()) {
                throw new IOException("Error while sending test data to the unit testing server. " +
                        "Hence aborting the testing operation");
            }
            tcpClient.writeData(deployableMessage);
            responseFromServer = tcpClient.readData();
            tcpClient.closeSocket();

        } else {
            getLog().error("Error while creating a deployable message with test suites");
        }

        return responseFromServer;
    }

    /**
     * Method of initiating logger.
     */
    private static Log getLog() {
        if (log == null) {
            log = new SystemStreamLog();
        }

        return log;
    }

}
