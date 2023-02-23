package net.floodlightcontroller.ddsplugin;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryListener;

import net.floodlightcontroller.linkdiscovery.ILinkDiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class DDSPlugin implements IFloodlightModule, ILinkDiscoveryListener {

    protected IFloodlightProviderService floodlightProviderService;
    protected ILinkDiscoveryService linkDiscoveryService;
    protected static Logger logger;


    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleServices() {
        return null;
    }

    @Override
    public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
        return null;
    }


    /**
     * 告诉管理器 这个模块需要依赖哪些模块
     */
    @Override
    public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
        ArrayList<Class<? extends IFloodlightService>> list = new ArrayList<>();
        list.add(IFloodlightProviderService.class);
        list.add(ILinkDiscoveryService.class);
        return list;
    }

    /**
     * 加载依赖项，初始化数据结构
     */
    @Override
    public void init(FloodlightModuleContext context) throws FloodlightModuleException {
        floodlightProviderService = context.getServiceImpl(IFloodlightProviderService.class);
        linkDiscoveryService = context.getServiceImpl(ILinkDiscoveryService.class);
        logger = LoggerFactory.getLogger(DDSPlugin.class);
    }

    /**
     * 将自己注册到 LD updates
     */
    @Override
    public void startUp(FloodlightModuleContext context) throws FloodlightModuleException {
        linkDiscoveryService.addListener(this);
    }

    @Override
    public void linkDiscoveryUpdate(List<LDUpdate> updateList) {
        updateList.forEach(ldUpdate -> logger.info("DDS plugin read link discovery update from internal: {}", ldUpdate));
    }
}
