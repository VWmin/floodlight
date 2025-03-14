package net.floodlightcontroller.ddsplugin;

import DDS.*;
import FloodLight.*;
import OpenDDS.DCPS.*;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import org.omg.CORBA.StringSeqHolder;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;

import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


@Deprecated
public class DDSPublisher {
    private Worker worker;
    private DomainParticipantFactory dpf;
    private DomainParticipant dp;

    private final BlockingQueue<ILinkDiscovery.LDUpdate> toPublish = new LinkedBlockingQueue<>();

    public DDSPublisher(){
        init();
    }

    private void init() {
        String[] args = {"-DCPSConfigFile", "transport.ini"};
        dpf = TheParticipantFactory.WithArgs(new StringSeqHolder(args));
        if (dpf == null) {
            System.err.println("ERROR: Domain Participant Factory not found");
            return;
        }

        dp = dpf.create_participant(DDSInfo.DOMAIN_ID,
                PARTICIPANT_QOS_DEFAULT.get(), null, DEFAULT_STATUS_MASK.value);
        if (dp == null) {
            System.err.println ("Domain Participant creation failed");
            return;
        }

        // BarTypeSupportImpl is type specific
        LDUpdateTypeSupportImpl servant = new LDUpdateTypeSupportImpl();
        if (servant.register_type(dp, "") != RETCODE_OK.value) {
            System.err.println("ERROR: register_type failed");
            return;
        }

        Topic top = dp.create_topic(DDSInfo.TOPIC_NAME,
                servant.get_type_name(),
                TOPIC_QOS_DEFAULT.get(), null,
                DEFAULT_STATUS_MASK.value);

        Publisher pub = dp.create_publisher(
                PUBLISHER_QOS_DEFAULT.get(),
                null,
                DEFAULT_STATUS_MASK.value);

        // data writer is for a specific topic.
        DataWriter dw = pub.create_datawriter(
                top, DATAWRITER_QOS_DEFAULT.get(), null, DEFAULT_STATUS_MASK.value);

        // 将泛型 data writer 缩小到特定类型，并注册希望发布的实例
        LDUpdateDataWriter updateDataWriter = LDUpdateDataWriterHelper.narrow(dw);
        worker = new Worker(updateDataWriter, toPublish);
    }

    public void stop(){
        worker.stop();

        System.out.println("Stop Publisher");

        // Clean up
        dp.delete_contained_entities();
        dpf.delete_participant(dp);
        TheServiceParticipant.shutdown();

        System.out.println("Publisher exiting");
    }

    public void start() {
        System.out.println("Start Publisher");
        worker.start();
    }

    public void publish(ILinkDiscovery.LDUpdate ldUpdate) {
        try {
            toPublish.put(ldUpdate);
        } catch (InterruptedException e) {
            worker.stop();
            stop();
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public static class Worker implements Runnable {
        LDUpdate sample;
        int handle;
        private final Thread workerThread;
        LDUpdateDataWriter updateDataWriter;
        BlockingQueue<ILinkDiscovery.LDUpdate> toPublish;


        public Worker(LDUpdateDataWriter updateDataWriter, BlockingQueue<ILinkDiscovery.LDUpdate> toPublish) {
            this.updateDataWriter = updateDataWriter;
            sample = new LDUpdate();
            handle = updateDataWriter.register_instance(sample);
            workerThread = new Thread(this, "DDSPublisher-Worker");
            this.toPublish = toPublish;
        }


        @Override
        public void run() {
            while(true) {
                Thread currentThread = Thread.currentThread();
                if (currentThread.isInterrupted()){
                    break;
                }

                try{
                    // do something...
                    ILinkDiscovery.LDUpdate take = toPublish.take();
                    Util.DDSTypeHelper.update2Sample(take, sample);
                    send();
                } catch (InterruptedException e) {
                    currentThread.interrupt();
                }
            }
        }

        public void stop(){
            // 通知中间件停止发布
            updateDataWriter.unregister_instance(sample, handle);
            workerThread.interrupt();
        }

        public void send(){
            int ret;
            System.out.println("sending msg ");
            while ((ret = updateDataWriter.write(sample, handle)) == RETCODE_TIMEOUT.value);
            if (ret != RETCODE_OK.value) {
                System.err.println("ERROR, write() returned " + ret);
            }
        }

        public void start() {
            workerThread.start();
        }
    }
}