package net.floodlightcontroller.ddsplugin.device;

import java.net.URI;
import java.util.Objects;

public final class DeviceId {
    public static final DeviceId NONE = deviceId("none:none");
    private final URI uri;
    private final String str;

    private DeviceId(URI uri) {
        this.uri = uri;
        this.str = uri.toString().toLowerCase();
    }

    public DeviceId() {
        this.uri = null;
        this.str = null;
    }

    public static DeviceId deviceId(URI uri) {
        return new DeviceId(uri);
    }

    public static DeviceId deviceId(String string) {
        return deviceId(URI.create(string));
    }

    public URI uri() {
        return uri;
    }

    @Override
    public int hashCode() {
        return str.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof DeviceId) {
            final DeviceId that = (DeviceId) obj;
            return Objects.equals(this.str, that.str);
        }
        return false;
    }

    @Override
    public String toString() {
        return str;
    }
}
