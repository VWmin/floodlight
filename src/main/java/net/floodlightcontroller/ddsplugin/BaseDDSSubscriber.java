package net.floodlightcontroller.ddsplugin;

import DDS.*;
import FloodLight.LDUpdateDataReader;
import FloodLight.LDUpdateTypeSupportImpl;
import OpenDDS.DCPS.DEFAULT_STATUS_MASK;
import OpenDDS.DCPS.TheParticipantFactory;
import OpenDDS.DCPS.TheServiceParticipant;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import org.omg.CORBA.StringSeqHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseDDSSubscriber {
    private static final Logger log = LoggerFactory.getLogger(BaseDDSSubscriber.class);
    private DomainParticipantFactory dpf;
    private DomainParticipant dp;
    private Topic top;
    private Subscriber sub;

    /**
     * DDS的数据接收和访问是通过listener来实现的
     */
    private DataReaderListener listener;

    public BaseDDSSubscriber(int domainId, String topic, TypeSupportOperations servant,
                             DataReaderListener listener) {
        String[] args = {"-DCPSConfigFile", "transport.ini"};
        dpf = TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (dpf == null) {
            log.error("Domain Participant Factory not found");
            return;
        }
        dp = dpf.create_participant(domainId,
                PARTICIPANT_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);
        if (dp == null) {
            log.error("Domain Participant creation failed");
            return;
        }
//        LDUpdateTypeSupportImpl servant = new LDUpdateTypeSupportImpl();
        if (servant.register_type(dp, "") != RETCODE_OK.value) {
            log.error("register_type failed");
            return;
        }
        top = dp.create_topic(topic,
                servant.get_type_name(),
                TOPIC_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);

        sub = dp.create_subscriber(
                SUBSCRIBER_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);


        // 向中间件提供listener是通知数据接收和访问数据的最简单方式
//        listener = new LDUpdateReaderListenerImpl(linkDiscoveryService);
        this.listener = listener;
    }

    public void start() {
        log.info("Start Subscriber");

        // 会同时开启线程，消息到达的调用会在另外的线程中
        DataReader dr = sub.create_datareader(
                top, DATAREADER_QOS_DEFAULT.get(), listener,
                DEFAULT_STATUS_MASK.value);

        if (dr == null) {
            log.error("ERROR: DataReader creation failed");
        }
    }


    public void stop() {
        log.info("Stop Subscriber");

        // app退出，清理资源
        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        // 清理掉所有OpenDDS相关的资源，以避免DCPSInfoRepo在不存在的endpoint之间创建关联
        TheServiceParticipant.shutdown();

        log.info("Subscriber exiting");
    }


}
