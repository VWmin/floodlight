package net.floodlightcontroller.ddsplugin;

import com.google.gson.Gson;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.ddsplugin.messaging.MessageSubject;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;

import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import net.floodlightcontroller.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class DDSPlugin implements IFloodlightModule, ILinkDiscoveryListener, IDDSPluginService {

    protected IFloodlightProviderService floodlightProviderService;
//    protected ILinkDiscoveryService linkDiscoveryService;
    protected static Logger logger;

    //    private DDSPublisher ddsPublisher;
    //    private DDSSubscriber ddsSubscriber;
    //private LDUpdatePublisher ddsPublisher;
    //private LDUpdateSubscriber ddsSubscriber;
//    private JsonPublisher ddsPublisher;
//    private JsonSubscriber<ILinkDiscovery.LDUpdate> ddsSubscriber;
    private Map<String, Pair<JsonPublisher, JsonSubscriber<?>>> publishers = new ConcurrentHashMap<>();

    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return Collections.singletonList(IDDSPluginService.class);
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return Collections.singletonMap(IDDSPluginService.class, this);
    }


    /**
     * 告诉管理器 这个模块需要依赖哪些模块
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        ArrayList<Class<? extends IFloodlightService>> list = new ArrayList<>();
        list.add(IFloodlightProviderService.class);
//        list.add(ILinkDiscoveryService.class);
        return list;
    }

    /**
     * 加载依赖项，初始化数据结构
     */
    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
//        linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
        logger = LoggerFactory.getLogger(DDSPlugin.class);

        loadConfig(context.getConfigParams(this));
    }

    private void loadConfig(Map<String, String> configParams) {
        DDSInfo.INSTANCE_ID = Integer.parseInt(configParams.get("instanceId"));
    }

    /**
     * 将自己注册到 LD updates
     */
    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
//        linkDiscoveryService.addListener(this);
//        ddsPublisher = new DDSPublisher();
//        ddsPublisher = new LDUpdatePublisher();
//        ddsSubscriber = new DDSSubscriber(linkDiscoveryService);
//        ddsSubscriber = new LDUpdateSubscriber(linkDiscoveryService);
//        ddsPublisher = new JsonPublisher();
//        ddsSubscriber = new JsonSubscriber<>(ILinkDiscovery.LDUpdate.class);
//        ddsPublisher.start();
//        ddsSubscriber.start();
    }

    @Override
    public void linkDiscoveryUpdate(List<LDUpdate> updateList) {
        updateList.forEach(ldUpdate -> {
            if (ldUpdate.fromExternal) {
                return;
            }
            logger.info("Read internal link discovery update from LDManager: {}", ldUpdate);
//            ddsPublisher.publish(ldUpdate);
//            ddsPublisher.publishObj(ldUpdate);
        });
    }

    public void broadcast(String topic, Object msg) {
//        ddsPublisher.publishObj(msg);
        if (!publishers.containsKey(topic)) {
            return ;
        }
        publishers.get(topic).getKey().publishObj(msg);
    }

    public <T> void newTopic(String topic, Class<T> clazz, Consumer<T> callback) {
        if (!publishers.containsKey(topic)) {
            JsonPublisher pub = new JsonPublisher(topic);
            JsonSubscriber<T> sub = new JsonSubscriber<>(topic, clazz, callback);
            pub.start();
            sub.start();
            publishers.put(topic, new Pair<>(pub, sub));
        }
    }

    @Override
    public void hello() {
        logger.info("DDS plugin: hello world!");
    }

}
