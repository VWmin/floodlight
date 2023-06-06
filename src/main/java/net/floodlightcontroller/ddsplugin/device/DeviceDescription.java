package net.floodlightcontroller.ddsplugin.device;


import java.net.URI;

public class DeviceDescription {
    private URI deviceURI;
    private Device.Type type;
    private String manufacturer;
    private String hwVersion;
    private String swVersion;
    private String serialNumber;
    private ChassisId chassisId;

    private static class ChassisId {

    }

}
