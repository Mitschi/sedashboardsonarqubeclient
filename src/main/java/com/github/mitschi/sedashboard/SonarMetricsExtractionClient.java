package com.github.mitschi.sedashboard;

import com.github.mitschi.sedashboard.messaging.MessagingCommit;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.io.FileUtils;

import javax.jms.*;
import java.io.File;
import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

public class SonarMetricsExtractionClient {
    private static File tmpFolder=new File("tmpFolderSourceCodeChangeClient");
    private static String hostname = "localhost";

    public static void main(String[] args) throws Exception {
        try {
            if(args.length>0) {
                tmpFolder=new File("tmpFolderSourceCodeChangeClient"+args[0]);
            }
            if(args.length>1) {
                hostname=args[1];
            }
            System.out.println("Hostname: "+hostname);
            tmpFolder.mkdirs();
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://"+hostname+":61616");
//            connectionFactory.setTrustedPackages(Arrays.asList(new String[]{"at.aau"}));
            connectionFactory.setTrustAllPackages(true);

            // Create a Connection
            Connection connection = connectionFactory.createConnection();
            connection.start();

//        connection.setExceptionListener(this);

            // Create a Session
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Create the destination (Topic or Queue)
            Destination destination = session.createQueue("sonarMetricsQueue?consumer.prefetchSize=1");

            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            // Wait for a message
            Message message = null;
            boolean running=true;
            ActiveMQUtils sonarMetricsDoneQueue = new ActiveMQUtils();
            sonarMetricsDoneQueue.init("sonarMetricsDoneQueue",hostname);
//            long messageCount=0;
            Set<String> currentDistinctRepos = new TreeSet<>();

            while(running) {
                message=consumer.receive();
                if (message instanceof ObjectMessage) {
                    Serializable object = ((ObjectMessage) message).getObject();
                    if (object instanceof MessagingCommit) {
                        MessagingCommit commit = (MessagingCommit) object;
                        System.out.println("Retrieved Commit: " + commit);

                        currentDistinctRepos.add(commit.getCloneUrl());
                        if(currentDistinctRepos.size()>3) { //disallow more than 3 repositories at the same time
                            FileUtils.deleteDirectory(tmpFolder);
                            tmpFolder.mkdirs();
                            currentDistinctRepos.clear();
                            currentDistinctRepos.add(commit.getCloneUrl());
                        }

                        SonarMetricsExtractionWorker worker = new SonarMetricsExtractionWorker(commit,tmpFolder);
                        worker.doJob();
                        System.out.println("===========================================================================================");
                        System.out.println("Finished Commit: " + commit.getCommitId());
                        System.out.println("===========================================================================================");

                        SonarCommitAnalysis sonarCommitAnalysis = worker.getSonarCommitAnalysis();
                        sonarMetricsDoneQueue.send(sonarCommitAnalysis);

                        message.acknowledge();
                    }
                }
            }
            sonarMetricsDoneQueue.close();
            consumer.close();
            session.close();
            connection.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }


    }

}
