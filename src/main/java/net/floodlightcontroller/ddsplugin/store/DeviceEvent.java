package net.floodlightcontroller.ddsplugin.store;

import com.google.common.base.MoreObjects;
import net.floodlightcontroller.ddsplugin.device.DeviceDescription;
import net.floodlightcontroller.ddsplugin.device.DeviceId;
import net.floodlightcontroller.ddsplugin.device.Timestamped;

public class DeviceEvent {
    private final DeviceId deviceId;
    private final Timestamped<DeviceDescription> deviceDescription;

    public DeviceEvent(DeviceId DeviceId, Timestamped<DeviceDescription> deviceDescription) {
        this.deviceId = DeviceId;
        this.deviceDescription = deviceDescription;
    }

    public DeviceId getDeviceId() {
        return deviceId;
    }

    public Timestamped<DeviceDescription> getDeviceDescription() {
        return deviceDescription;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("deviceId", deviceId)
                .add("deviceDescription", deviceDescription)
                .toString();
    }

    public DeviceEvent(){
        this.deviceId = null;
        this.deviceDescription = null;
    }
}
