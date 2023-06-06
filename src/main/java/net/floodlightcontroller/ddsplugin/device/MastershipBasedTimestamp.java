package net.floodlightcontroller.ddsplugin.device;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ComparisonChain;

import javax.validation.constraints.NotNull;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

public class MastershipBasedTimestamp implements Timestamp{

    private final long nodeNumber;
    private final long sequenceNumber;

    public MastershipBasedTimestamp(long nodeNumber, long sequenceNumber) {
        this.nodeNumber = nodeNumber;
        this.sequenceNumber = sequenceNumber;
    }


    @Override
    public int compareTo(Timestamp o) {
        checkArgument(o instanceof MastershipBasedTimestamp,
                "Must be MastershipBasedTimestamp", o);
        MastershipBasedTimestamp that = (MastershipBasedTimestamp) o;
        return ComparisonChain.start()
                .compare(this.nodeNumber, that.nodeNumber)
                .compare(this.sequenceNumber, that.sequenceNumber)
                .result();
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeNumber, sequenceNumber);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MastershipBasedTimestamp)) {
            return false;
        }
        MastershipBasedTimestamp that = (MastershipBasedTimestamp) obj;
        return Objects.equals(this.nodeNumber, that.nodeNumber) &&
                Objects.equals(this.sequenceNumber, that.sequenceNumber);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(getClass())
                .add("nodeNumber", nodeNumber)
                .add("sequenceNumber", sequenceNumber)
                .toString();
    }


    public long nodeNumber() {
        return nodeNumber;
    }


    public long sequenceNumber() {
        return sequenceNumber;
    }

    // Default constructor for serialization
    protected MastershipBasedTimestamp() {
        this.nodeNumber = -1;
        this.sequenceNumber = -1;
    }
}
