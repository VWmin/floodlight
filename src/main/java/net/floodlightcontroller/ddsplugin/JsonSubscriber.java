package net.floodlightcontroller.ddsplugin;

import DDS.*;
import Floodlight.*;
import com.google.gson.Gson;

public class JsonSubscriber<T> extends BaseDDSSubscriber{


    public JsonSubscriber(Class<T> clazz) {
        super(DDSInfo.DOMAIN_ID, "Json", new JsonTypeSupportImpl(), new JsonReaderListenerImpl<>(clazz));
    }

    private static class JsonReaderListenerImpl<T> extends DDS._DataReaderListenerLocalBase {

        private final Gson gson = new Gson();
        private final Class<T> clazz;

        public JsonReaderListenerImpl(Class<T> clazz) {
            this.clazz = clazz;
        }

        T deserialize(String json) {
            return gson.fromJson(json, clazz);
        }

        @Override
        public void on_requested_deadline_missed(DataReader dataReader, RequestedDeadlineMissedStatus requestedDeadlineMissedStatus) {

        }

        @Override
        public void on_requested_incompatible_qos(DataReader dataReader, RequestedIncompatibleQosStatus requestedIncompatibleQosStatus) {

        }

        @Override
        public void on_sample_rejected(DataReader dataReader, SampleRejectedStatus sampleRejectedStatus) {

        }

        @Override
        public void on_liveliness_changed(DataReader dataReader, LivelinessChangedStatus livelinessChangedStatus) {

        }

        @Override
        public void on_data_available(DataReader dataReader) {
            JsonDataReader jsonDataReader = JsonDataReaderHelper.narrow(dataReader);
            if (jsonDataReader == null) {
                System.out.println("read: narrow failed.");
                return;
            }
            JsonHolder jsonHolder = new JsonHolder(new Json());
            SampleInfoHolder sih = new SampleInfoHolder(new SampleInfo(0, 0, 0,
                    new DDS.Time_t(), 0, 0, 0, 0, 0, 0, 0, false, 0));
            int status = jsonDataReader.take_next_sample(jsonHolder, sih);
            if (status == RETCODE_OK.value) {
                System.out.println("SampleInfo.sample_rank = " + sih.value.sample_rank);
                System.out.println("SampleInfo.instance_state = " + sih.value.instance_state);
                System.out.println("json = " + jsonHolder.value);
                System.out.println("obj = " + deserialize(jsonHolder.value.content));
            } else if (status == RETCODE_NO_DATA.value) {
                System.out.println("no more data.");
            } else {
                System.out.println("take_next_sample error " + status);
            }
        }

        @Override
        public void on_subscription_matched(DataReader dataReader, SubscriptionMatchedStatus subscriptionMatchedStatus) {

        }

        @Override
        public void on_sample_lost(DataReader dataReader, SampleLostStatus sampleLostStatus) {

        }
    }
}
