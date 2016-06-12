package com.travelzen.farerule.jpecker.server;

import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.slf4j.Logger;

import com.travelzen.framework.config.tops.TopsConfReader;
import com.travelzen.framework.config.tops.TopsConfEnum.ConfScope;
import com.travelzen.framework.config.tops.util.TopsAppRegistry;
import com.travelzen.framework.config.tops.zk.TopsZookeeperBalancer;
import com.travelzen.framework.logger.core.ri.RequestIdentityLogger;
import com.travelzen.framework.thrift.protocol.RIThriftProtocolFactory;

public class JpeckerServiceServer {

	private static Logger log = RequestIdentityLogger.getLogger(JpeckerServiceServer.class);
	
    private static final String PORT = "port";
    private static final String ZOOKEEPER_USING = "flag.zookeeper.using";
    private static final String NAME = "name";
    private static final String SHARDID = "shardId";
    private static final String REPLICAID = "replicaId";
    private static final String PREFIX = "prefix";

    public static final String THRIFT_CONFIG_PROPERTIES = "properties/tops-farerule-jpecker-server-thrift-config.properties";

    public static JpeckerServiceHandler jpeckerServiceHandler;

    public static JpeckerService.Processor<JpeckerServiceHandler> processor;

    public static TThreadedSelectorServer server;

    public static void start() {
        try {
            final int port = TopsConfReader.getConfContentForInt(THRIFT_CONFIG_PROPERTIES, PORT, ConfScope.M);
            jpeckerServiceHandler = new JpeckerServiceHandler();
            processor = new JpeckerService.Processor<JpeckerServiceHandler>(jpeckerServiceHandler);
            Runnable simple = new Runnable() {
                public void run() {
                    task(processor, port);
                }
            };
            new Thread(simple).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        try {
            if (server.isServing()) {
                server.stop();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static void task(JpeckerService.Processor<JpeckerServiceHandler> processor, int port) {
        try {
            log.info("Start JpeckerService Server...");
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport).processor(processor);
            args.workerThreads(150);
            args.transportFactory(new TFramedTransport.Factory());
            args.inputProtocolFactory(new RIThriftProtocolFactory());
            args.outputProtocolFactory(new RIThriftProtocolFactory());
            server = new TThreadedSelectorServer(args);

            // 注册zookeeper
            String flag = TopsConfReader.getConfContent(THRIFT_CONFIG_PROPERTIES, ZOOKEEPER_USING, ConfScope.M);
            if (flag != null && flag.equalsIgnoreCase("true")) {
                String serviceName = TopsConfReader.getConfContent(THRIFT_CONFIG_PROPERTIES, NAME, ConfScope.M);
                String YRNS_PREFIX = TopsConfReader.getConfContent(THRIFT_CONFIG_PROPERTIES, PREFIX, ConfScope.M);
                String shardId = TopsConfReader.getConfContent(THRIFT_CONFIG_PROPERTIES, SHARDID, ConfScope.M);
                String replicaId = TopsConfReader.getConfContent(THRIFT_CONFIG_PROPERTIES, REPLICAID, ConfScope.M);
                TopsZookeeperBalancer.registerRpc(TopsAppRegistry.getLocalIP() + ":" + port, YRNS_PREFIX, serviceName,
                        shardId, replicaId);
            }

            server.serve();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
    
    public static void main(String[] args) {
        start();
    }

}
