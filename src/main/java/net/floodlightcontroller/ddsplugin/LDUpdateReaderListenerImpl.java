package net.floodlightcontroller.ddsplugin;



import DDS.*;
import FloodLight.*;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;

import java.util.Collections;
import java.util.concurrent.TimeUnit;


public class LDUpdateReaderListenerImpl extends DDS._DataReaderListenerLocalBase{

    private final ILinkDiscoveryService linkDiscoveryService;

    public LDUpdateReaderListenerImpl(ILinkDiscoveryService linkDiscoveryService) {
        this.linkDiscoveryService = linkDiscoveryService;
    }

    @Override
    public void on_requested_deadline_missed(DataReader dataReader, RequestedDeadlineMissedStatus requestedDeadlineMissedStatus) {
        System.out.println("doing noting...");
    }

    @Override
    public void on_requested_incompatible_qos(DataReader dataReader, RequestedIncompatibleQosStatus requestedIncompatibleQosStatus) {
        System.out.println("doing noting...");
    }

    @Override
    public void on_sample_rejected(DataReader dataReader, SampleRejectedStatus sampleRejectedStatus) {
        System.out.println("doing noting...");
    }

    @Override
    public void on_liveliness_changed(DataReader dataReader, LivelinessChangedStatus livelinessChangedStatus) {
        System.out.println("doing noting...");
    }


    @Override
    public void on_data_available(DataReader dataReader) {
        // app需要将泛型缩小到自己的类型
        LDUpdateDataReader barDataReader = LDUpdateDataReaderHelper.narrow(dataReader);
        if (barDataReader == null) {
            System.err.println ("read: narrow failed.");
            return;
        }
        //  为特定消息类型和关联的样本信息(SampleInfo)创建holder
        // sampleInfo中包含了sample的metadata
        LDUpdateHolder bh = new LDUpdateHolder(new LDUpdate());
        SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
                new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
        // 并从DataReader中获取sample，被获取的sample会从DataReader的sample pool中移除
        int status = barDataReader.take_next_sample(bh, sih);
        if (status == RETCODE_OK.value) {
            // 通过SampleInfo中的valid_data判断sample是否有效
            if (sih.value.valid_data) {
                System.out.println("SampleInfo.sample_rank = " + sih.value.sample_rank);
                System.out.println("SampleInfo.instance_state = " + sih.value.instance_state);
                if (sih.value.valid_data) {
                    // TODO: 校验term，合法的更新传递到控制器内部
                    System.out.println(Util.DDSTypeHelper.sample2String(bh.value));
                    if (bh.value.instance != DDSInfo.INSTANCE_ID) {
                        linkDiscoveryService.externalLDUpdates(Collections.singletonList(Util.DDSTypeHelper.sample2Update(bh.value)));
                    }
                }else if (sih.value.instance_state == NOT_ALIVE_DISPOSED_INSTANCE_STATE.value) {
                    System.out.println ("instance is disposed");
                }
                else if (sih.value.instance_state == NOT_ALIVE_NO_WRITERS_INSTANCE_STATE.value) {
                    System.out.println ("instance is unregistered");
                } else {
                    System.out.println ("DataReaderListenerImpl::on_data_available: "+
                            "received unknown instance state "+
                            sih.value.instance_state);
                }
            }
        } else if (status == RETCODE_NO_DATA.value) {
            System.err.println ("ERROR: reader received DDS::RETCODE_NO_DATA!");
        } else {
            System.err.println ("ERROR: read Message: Error: "+ status);
        }

    }

    @Override
    public void on_subscription_matched(DataReader dataReader, SubscriptionMatchedStatus subscriptionMatchedStatus) {
        System.out.println("doing noting...");
    }

    @Override
    public void on_sample_lost(DataReader dataReader, SampleLostStatus sampleLostStatus) {
        System.out.println("doing noting...");
    }


}
