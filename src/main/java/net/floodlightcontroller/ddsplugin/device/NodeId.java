package net.floodlightcontroller.ddsplugin.device;

import java.util.Objects;

public class NodeId implements Comparable<NodeId>{

    private final String id;

    public NodeId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof NodeId) {
            final NodeId other = (NodeId) obj;
            return Objects.equals(this.id, other.id);
        }
        return false;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public int compareTo(NodeId o) {
        return this.id.compareTo(o.id);
    }
}
