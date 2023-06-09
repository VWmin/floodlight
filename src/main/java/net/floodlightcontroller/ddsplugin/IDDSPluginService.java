package net.floodlightcontroller.ddsplugin;

import net.floodlightcontroller.core.module.IFloodlightService;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface IDDSPluginService extends IFloodlightService {
    void hello();

    void broadcast(String topic, Object msg);

    <T> void newTopic(String topic, Class<T> clazz, Consumer<T> callback);
}
