package net.floodlightcontroller.ddsplugin;

public interface DataWriterWrapper<T, S> {
    /**
     * register here
     */
    void register();

    /**
     * unregister here
     */
    void unregister();

    /**
     * send msg
     */
    int write();

    void transform2Sample(T take);



}
