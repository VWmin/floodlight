package net.floodlightcontroller.ddsplugin;

import DDS.*;
import FloodLight.*;
import OpenDDS.DCPS.*;
import org.omg.CORBA.StringSeqHolder;


public class DDSSubscriber {
    private Worker worker;
    private LDUpdateDataReader updateDataReader;
    private DomainParticipantFactory dpf;
    private DomainParticipant dp;

    public DDSSubscriber(){
        init();
    }

    private void init() {
        String[] args = {"-DCPSConfigFile", "transport.ini"};
        dpf = TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (dpf == null) {
            System.err.println ("Domain Participant Factory not found");
            return;
        }
        dp = dpf.create_participant(DDSInfo.DOMAIN_ID,
                PARTICIPANT_QOS_DEFAULT.get(), null, DEFAULT_STATUS_MASK.value);
        if (dp == null) {
            System.err.println("Domain Participant creation failed");
            return;
        }
        LDUpdateTypeSupportImpl servant = new LDUpdateTypeSupportImpl();
        if (servant.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println ("register_type failed");
            return;
        }
        Topic top = dp.create_topic(DDSInfo.TOPIC_NAME,
                servant.get_type_name(),
                TOPIC_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);

        Subscriber sub = dp.create_subscriber(
                SUBSCRIBER_QOS_DEFAULT.get(), null, DEFAULT_STATUS_MASK.value);


        // 向中间件提供listener是通知数据接收和访问数据的最简单方式
        LDUpdateReaderListenerImpl listener = new LDUpdateReaderListenerImpl();
        worker = new Worker(sub, top, listener);

    }

    public void stop() {
        System.out.println("Stop Subscriber");

        // app退出，清理资源
        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        // 清理掉所有OpenDDS相关的资源，以避免DCPSInfoRepo在不存在的endpoint之间创建关联
        TheServiceParticipant.shutdown();

        System.out.println("Subscriber exiting");
    }

    public void start() {
        System.out.println("Start Subscriber");
        worker.start();
    }

    public static class Worker implements Runnable {
        private Subscriber sub;
        private Topic top;
        LDUpdateReaderListenerImpl listener;
        private final Thread workerThread;


        public Worker(Subscriber sub, Topic top, LDUpdateReaderListenerImpl listener) {
            this.sub = sub;
            this.top = top;
            this.listener = listener;
            workerThread = new Thread(this);
        }


        @Override
        public void run() {
            // 会同时开启线程，消息到达的调用会在另外的线程中
            DataReader dr = sub.create_datareader(
                    top, DATAREADER_QOS_DEFAULT.get(), listener,
                    DEFAULT_STATUS_MASK.value);

            if (dr == null) {
                System.err.println("ERROR: DataReader creation failed");
            }

        }


        public void stop() {
            workerThread.interrupt();
        }

        public void start() {
            workerThread.start();
        }
    }
}
