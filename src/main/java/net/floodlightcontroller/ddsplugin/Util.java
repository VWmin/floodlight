package net.floodlightcontroller.ddsplugin;

import FloodLight.LDUpdate;
import net.floodlightcontroller.linkdiscovery.ILinkDiscovery;
import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;

import java.util.Optional;

public class Util {
    private Util(){

    }

    public static class LinkTypeHelper{

        private LinkTypeHelper(){

        }
        public static int type2Int(ILinkDiscovery.LinkType type){
            if (type == null) {
                return -1;
            }
            switch(type){
                case INVALID_LINK:
                    return 0;
                case DIRECT_LINK:
                    return 1;
                case MULTIHOP_LINK:
                    return 2;
                case TUNNEL:
                    return 3;
                default:
                    throw new IllegalArgumentException("Unknown LinkType: " + type);
            }
        }

        public static ILinkDiscovery.LinkType int2Type(int type){
            if (type == -1) {
                return null;
            }
            switch(type){
                case 0:
                    return ILinkDiscovery.LinkType.INVALID_LINK;
                case 1:
                    return ILinkDiscovery.LinkType.DIRECT_LINK;
                case 2:
                    return ILinkDiscovery.LinkType.MULTIHOP_LINK;
                case 3:
                    return ILinkDiscovery.LinkType.TUNNEL;
                default:
                    throw new IllegalArgumentException("Unknown LinkType: " + type);

            }
        }
    }

    public static class UpdateOperationHelper{

            private UpdateOperationHelper(){

            }

            public static int operation2Int(ILinkDiscovery.UpdateOperation operation){
                if (operation == null) {
                    return -1;
                }
                switch(operation){
                    case LINK_UPDATED:
                        return 0;
                    case LINK_REMOVED:
                        return 1;
                    case SWITCH_UPDATED:
                        return 2;
                    case SWITCH_REMOVED:
                        return 3;
                    case PORT_UP:
                        return 4;
                    case PORT_DOWN:
                        return 5;
                    case TUNNEL_PORT_ADDED:
                        return 6;
                    case TUNNEL_PORT_REMOVED:
                        return 7;
                    default:
                        throw new IllegalArgumentException("Unknown UpdateOperation: " + operation);
                }
            }

            public static ILinkDiscovery.UpdateOperation int2Operation(int operation){
                if (operation == -1) {
                    return null;
                }
                switch(operation){
                    case 0:
                        return ILinkDiscovery.UpdateOperation.LINK_UPDATED;
                    case 1:
                        return ILinkDiscovery.UpdateOperation.LINK_REMOVED;
                    case 2:
                        return ILinkDiscovery.UpdateOperation.SWITCH_UPDATED;
                    case 3:
                        return ILinkDiscovery.UpdateOperation.SWITCH_REMOVED;
                    case 4:
                        return ILinkDiscovery.UpdateOperation.PORT_UP;
                    case 5:
                        return ILinkDiscovery.UpdateOperation.PORT_DOWN;
                    case 6:
                        return ILinkDiscovery.UpdateOperation.TUNNEL_PORT_ADDED;
                    case 7:
                        return ILinkDiscovery.UpdateOperation.TUNNEL_PORT_REMOVED;
                    default:
                        throw new IllegalArgumentException("Unknown UpdateOperation: " + operation);
                }
            }
    }

    public static class DDSTypeHelper {
        private DDSTypeHelper() {
        }
        public static ILinkDiscovery.LDUpdate sample2Update(FloodLight.LDUpdate sample) {
            ILinkDiscovery.LDUpdate update = new ILinkDiscovery.LDUpdate(
                    DatapathId.of(sample.src),
                    OFPort.of(sample.srcPort),
                    DatapathId.of(sample.dst),
                    OFPort.of(sample.dstPort),
                    U64.of(sample.latency),
                    LinkTypeHelper.int2Type(sample.type),
                    UpdateOperationHelper.int2Operation(sample.operation)
            );
            update.fromExternal = true;
            return update;
        }
        public static void update2Sample(ILinkDiscovery.LDUpdate update, FloodLight.LDUpdate sample){
            sample.instance = DDSInfo.INSTANCE_ID;
            sample.src = (int) Optional.ofNullable(update.getSrc()).map(DatapathId::getLong).orElse(0L).longValue();
            sample.dst = (int) Optional.ofNullable(update.getDst()).map(DatapathId::getLong).orElse(0L).longValue();
            sample.srcPort = (byte) Optional.ofNullable(update.getSrcPort()).map(OFPort::getPortNumber).orElse(0).intValue();
            sample.dstPort = (byte) Optional.ofNullable(update.getDstPort()).map(OFPort::getPortNumber).orElse(0).intValue();
            sample.latency = (int) Optional.ofNullable(update.getLatency()).orElse(U64.ZERO).getValue();
            sample.type = (byte) Util.LinkTypeHelper.type2Int(update.getType());
            sample.operation = (byte) Util.UpdateOperationHelper.operation2Int(update.getOperation());
            // TODO switch type?

        }

        public static String sample2String(FloodLight.LDUpdate sample) {
            return "[instance: " + sample.instance + ", " +
                    "src: " + sample.src + ", " +
                    "dst: " + sample.dst + ", " +
                    "srcPort: " + sample.srcPort + ", " +
                    "dstPort: " + sample.dstPort + ", " +
                    "latency: " + sample.latency + ", " +
                    "type: " + sample.type + ", " +
                    "operation: " + sample.operation + "]";

        }
    }
}
