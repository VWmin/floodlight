package net.floodlightcontroller.ddsplugin.messaging;

import java.util.Objects;

public class MessageSubject {
    private final String value;

    public MessageSubject(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MessageSubject that = (MessageSubject) obj;
        return Objects.equals(this.value, that.value);
    }
}
