package net.floodlightcontroller.ddsplugin;

import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;

import java.util.List;

public interface IDDSPluginListener {
    void externalLDUpdates(List<ILinkDiscovery.LDUpdate> updateList);
}
