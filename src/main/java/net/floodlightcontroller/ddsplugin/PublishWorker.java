package net.floodlightcontroller.ddsplugin;

import DDS.RETCODE_OK;
import DDS.RETCODE_TIMEOUT;

import java.util.concurrent.BlockingQueue;

public class PublishWorker<T, S> implements Runnable {
    private final Thread workerThread;
    DataWriterWrapper<T, S> dataWriter;
    BlockingQueue<T> toPublish;


    public PublishWorker(DataWriterWrapper<T, S> dataWriter, BlockingQueue<T> toPublish) {
        this.dataWriter = dataWriter;
        workerThread = new Thread(this, "DDSPublisher-Worker");
        this.toPublish = toPublish;
    }


    @Override
    public void run() {
        while (true) {
            Thread currentThread = Thread.currentThread();
            if (currentThread.isInterrupted()) {
                break;
            }

            try {
                // do something...
                T take = toPublish.take();
                dataWriter.transform2Sample(take);
                send();
            } catch (InterruptedException e) {
                currentThread.interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void send() {
        int ret;
        System.out.println("sending msg ");
        while ((ret = dataWriter.write()) == RETCODE_TIMEOUT.value) ;
        if (ret != RETCODE_OK.value) {
            System.err.println("ERROR, write() returned " + ret);
        }
    }

    public void start() {
        dataWriter.register();
        workerThread.start();
    }

    public void stop() {
        dataWriter.unregister();
        // 通知中间件停止发布
        workerThread.interrupt();
    }
}
