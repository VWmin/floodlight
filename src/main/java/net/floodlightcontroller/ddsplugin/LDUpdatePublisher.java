package net.floodlightcontroller.ddsplugin;

import FloodLight.LDUpdate;
import FloodLight.LDUpdateDataWriter;
import FloodLight.LDUpdateDataWriterHelper;
import FloodLight.LDUpdateTypeSupportImpl;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;

public final class LDUpdatePublisher extends BaseDDSPublisher<ILinkDiscovery.LDUpdate, LDUpdate> {

    public LDUpdatePublisher() {
        super(DDSInfo.TOPIC_NAME, new LDUpdateTypeSupportImpl());
    }

    @Override
    void initWorker() {
        // 将泛型 data writer 缩小到特定类型，并注册希望发布的实例
        LDUpdateDataWriter ldUpdateDataWriter = LDUpdateDataWriterHelper.narrow(dw);
        LDUpdateDataWriterWrapper dataWriter = new LDUpdateDataWriterWrapper(ldUpdateDataWriter);
        worker = new PublishWorker<>(dataWriter, toPublish);
    }


    public static class LDUpdateDataWriterWrapper implements DataWriterWrapper<ILinkDiscovery.LDUpdate, LDUpdate> {

        private LDUpdate sample = new LDUpdate();
        private final LDUpdateDataWriter writer;

        int handle;

        public LDUpdateDataWriterWrapper(LDUpdateDataWriter writer) {
            this.writer = writer;
        }


        @Override
        public void register() {
            handle = writer.register_instance(sample);
        }

        @Override
        public void unregister() {
            writer.unregister_instance(sample, handle);
        }

        @Override
        public int write() {
            return writer.write(sample, handle);
        }

        @Override
        public void transform2Sample(ILinkDiscovery.LDUpdate take) {
            Util.DDSTypeHelper.update2Sample(take, sample);
        }
    }

}
