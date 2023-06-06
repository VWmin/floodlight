package net.floodlightcontroller.ddsplugin.device;

import com.google.common.base.MoreObjects;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public final class Timestamped<T> {
    private final Timestamp timestamp;
    private final T value;

    public Timestamped(T value, Timestamp timestamp) {
        this.value = checkNotNull(value);
        this.timestamp = checkNotNull(timestamp);
    }

    public T value() {
        return value;
    }

    public Timestamp timestamp() {
        return timestamp;
    }

    public boolean isNewer(Timestamped<T> other) {
        return isNewerThan(checkNotNull(other).timestamp());
    }

    public boolean isNewerThan(Timestamp other) {
        return timestamp.isNewerThan(other);
    }

    @Override
    public int hashCode() {
        return timestamp.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Timestamped)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        Timestamped<T> that = (Timestamped<T>) obj;
        return Objects.equals(this.timestamp, that.timestamp);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("timestamp", timestamp)
                .add("value", value)
                .toString();
    }
}
