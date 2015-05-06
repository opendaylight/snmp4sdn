package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetVlanPortsOutputBuilder {

    private List<GetPortListEntry> _getPortListEntry;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>> augmentation = new HashMap<>();

    public GetVlanPortsOutputBuilder() {
    } 


    public List<GetPortListEntry> getGetPortListEntry() {
        return _getPortListEntry;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetVlanPortsOutputBuilder setGetPortListEntry(List<GetPortListEntry> value) {
    
        this._getPortListEntry = value;
        return this;
    }
    
    public GetVlanPortsOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetVlanPortsOutput build() {
        return new GetVlanPortsOutputImpl(this);
    }

    private static final class GetVlanPortsOutputImpl implements GetVlanPortsOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput.class;
        }

        private final List<GetPortListEntry> _getPortListEntry;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>> augmentation = new HashMap<>();

        private GetVlanPortsOutputImpl(GetVlanPortsOutputBuilder builder) {
            this._getPortListEntry = builder.getGetPortListEntry();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public List<GetPortListEntry> getGetPortListEntry() {
            return _getPortListEntry;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.GetVlanPortsOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_getPortListEntry == null) ? 0 : _getPortListEntry.hashCode());
            result = prime * result + ((augmentation == null) ? 0 : augmentation.hashCode());
            return result;
        }

        @Override
        public boolean equals(java.lang.Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GetVlanPortsOutputImpl other = (GetVlanPortsOutputImpl) obj;
            if (_getPortListEntry == null) {
                if (other._getPortListEntry != null) {
                    return false;
                }
            } else if(!_getPortListEntry.equals(other._getPortListEntry)) {
                return false;
            }
            if (augmentation == null) {
                if (other.augmentation != null) {
                    return false;
                }
            } else if(!augmentation.equals(other.augmentation)) {
                return false;
            }
            return true;
        }
        
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("GetVlanPortsOutput [_getPortListEntry=");
            builder.append(_getPortListEntry);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
