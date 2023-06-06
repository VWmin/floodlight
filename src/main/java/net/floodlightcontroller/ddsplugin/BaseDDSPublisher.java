package net.floodlightcontroller.ddsplugin;

import DDS.*;
import OpenDDS.DCPS.DEFAULT_STATUS_MASK;
import OpenDDS.DCPS.TheParticipantFactory;
import OpenDDS.DCPS.TheServiceParticipant;
import org.omg.CORBA.StringSeqHolder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class BaseDDSPublisher<T, S> {
    protected DomainParticipantFactory dpf;
    protected DomainParticipant dp;
    protected Topic top;
    protected Publisher pub;
    protected DataWriter dw;

    protected final String topic;
    protected PublishWorker<T, S> worker;
    protected final BlockingQueue<T> toPublish = new LinkedBlockingQueue<>();

    public BaseDDSPublisher(String topic, TypeSupportOperations servant) {
        this.topic = topic;

        String[] args = {"-DCPSConfigFile", "transport.ini"};
        dpf = TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (dpf == null) {
            System.err.println("ERROR: Domain Participant Factory not found");
            return;
        }

        dp = dpf.create_participant(DDSInfo.DOMAIN_ID,
                PARTICIPANT_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);
        if (dp == null) {
            System.err.println("Domain Participant creation failed");
            return;
        }

        // BarTypeSupportImpl is type specific
//        LDUpdateTypeSupportImpl servant = new LDUpdateTypeSupportImpl();
        if (servant.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        top = dp.create_topic(topic,
                servant.get_type_name(),
                TOPIC_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);

        pub = dp.create_publisher(
                PUBLISHER_QOS_DEFAULT.get(),
                null,
                DEFAULT_STATUS_MASK.value);

        // data writer is for a specific topic.
        dw = pub.create_datawriter(
                top, DATAWRITER_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);
    }

    abstract void initWorker();

    public void start() {
        System.out.println("Start Publisher");
        initWorker();
        worker.start();
    }

    public void stop() {
        worker.stop();

        System.out.println("Stop Publisher");

        // Clean up
        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        TheServiceParticipant.shutdown();

        System.out.println("Publisher exiting");
    }

    public void publish(T rawType) {
        try {
            toPublish.put(rawType);
        } catch (InterruptedException e) {
            worker.stop();
            stop();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
