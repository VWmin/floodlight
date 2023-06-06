package net.floodlightcontroller.ddsplugin.device;

import static com.google.common.base.Preconditions.checkNotNull;

public interface Timestamp extends Comparable<Timestamp>{
    default boolean isNewerThan(Timestamp other) {
        return this.compareTo(checkNotNull(other)) > 0;
    }


    default boolean isOlderThan(Timestamp other) {
        return this.compareTo(checkNotNull(other)) < 0;
    }

}
