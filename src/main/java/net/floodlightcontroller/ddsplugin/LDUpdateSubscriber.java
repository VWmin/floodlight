package net.floodlightcontroller.ddsplugin;

import DDS.*;
import FloodLight.*;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class LDUpdateSubscriber extends BaseDDSSubscriber  {
    private static final Logger log = LoggerFactory.getLogger(LDUpdateSubscriber.class);
    public LDUpdateSubscriber(ILinkDiscoveryService linkDiscoveryService) {
        super(DDSInfo.DOMAIN_ID, DDSInfo.TOPIC_NAME, 
                new LDUpdateTypeSupportImpl(), new LDUpdateReaderListenerImpl(linkDiscoveryService));
    }
    
    private static final class LDUpdateReaderListenerImpl extends DDS._DataReaderListenerLocalBase{

        private final ILinkDiscoveryService linkDiscoveryService;

        public LDUpdateReaderListenerImpl(ILinkDiscoveryService linkDiscoveryService) {
            this.linkDiscoveryService = linkDiscoveryService;
        }

        @Override
        public void on_requested_deadline_missed(DataReader dataReader, RequestedDeadlineMissedStatus requestedDeadlineMissedStatus) {
            log.info("doing noting...");
        }

        @Override
        public void on_requested_incompatible_qos(DataReader dataReader, RequestedIncompatibleQosStatus requestedIncompatibleQosStatus) {
            log.info("doing noting...");
        }

        @Override
        public void on_sample_rejected(DataReader dataReader, SampleRejectedStatus sampleRejectedStatus) {
            log.info("doing noting...");
        }

        @Override
        public void on_liveliness_changed(DataReader dataReader, LivelinessChangedStatus livelinessChangedStatus) {
            log.info("doing noting...");
        }


        @Override
        public void on_data_available(DataReader dataReader) {
            // app需要将泛型缩小到自己的类型
            LDUpdateDataReader barDataReader = LDUpdateDataReaderHelper.narrow(dataReader);
            if (barDataReader == null) {
                log.error ("read: narrow failed.");
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
                    log.info("SampleInfo.sample_rank = " + sih.value.sample_rank);
                    log.info("SampleInfo.instance_state = " + sih.value.instance_state);
                    if (sih.value.valid_data) {
                        // TODO: 校验term，合法的更新传递到控制器内部
                        log.info(Util.DDSTypeHelper.sample2String(bh.value));
                        if (bh.value.instance != DDSInfo.INSTANCE_ID) {
                            linkDiscoveryService.externalLDUpdates(Collections.singletonList(Util.DDSTypeHelper.sample2Update(bh.value)));
                        }
                    }else if (sih.value.instance_state == NOT_ALIVE_DISPOSED_INSTANCE_STATE.value) {
                        log.info ("instance is disposed");
                    }
                    else if (sih.value.instance_state == NOT_ALIVE_NO_WRITERS_INSTANCE_STATE.value) {
                        log.info ("instance is unregistered");
                    } else {
                        log.info ("DataReaderListenerImpl::on_data_available: "+
                                "received unknown instance state "+
                                sih.value.instance_state);
                    }
                }
            } else if (status == RETCODE_NO_DATA.value) {
                log.error ("ERROR: reader received DDS::RETCODE_NO_DATA!");
            } else {
                log.error ("ERROR: read Message: Error: "+ status);
            }

        }

        @Override
        public void on_subscription_matched(DataReader dataReader, SubscriptionMatchedStatus subscriptionMatchedStatus) {
            log.info("doing noting...");
        }

        @Override
        public void on_sample_lost(DataReader dataReader, SampleLostStatus sampleLostStatus) {
            log.info("doing noting...");
        }


    }

}
