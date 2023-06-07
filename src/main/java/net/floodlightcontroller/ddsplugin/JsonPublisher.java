package net.floodlightcontroller.ddsplugin;

import DDS.TypeSupportOperations;
import Floodlight.*;
import com.google.gson.Gson;

import java.util.concurrent.BlockingQueue;

public class JsonPublisher extends BaseDDSPublisher<String, Json> {
    private final Gson gson = new Gson();

    public JsonPublisher() {
        super(DDSInfo.DOMAIN_ID, "Json", new JsonTypeSupportImpl());
    }

    @Override
    void initWorker() {
        JsonDataWriter jsonDataWriter = JsonDataWriterHelper.narrow(dw);
        JsonDataWriterWrapper dataWriter = new JsonDataWriterWrapper(jsonDataWriter);
        worker = new PublishWorker<>(dataWriter, toPublish);
    }

    public void publishObj(Object obj) {
        publish(simpleObjToJson(obj));
    }

    String simpleObjToJson(Object obj) {
        return gson.toJson(obj);
    }

    private static class JsonDataWriterWrapper implements DataWriterWrapper<String, Json> {
        private final Json sample = new Json();
        private final JsonDataWriter writer;
        int handle;

        public JsonDataWriterWrapper(JsonDataWriter writer) {
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
        public void transform2Sample(String take) {
            sample.content = take;
        }
    }


}
