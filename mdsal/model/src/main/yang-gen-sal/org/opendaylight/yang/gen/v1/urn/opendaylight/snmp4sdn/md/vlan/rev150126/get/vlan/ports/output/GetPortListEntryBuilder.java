package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class GetPortListEntryBuilder {

    private Short _port;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>> augmentation = new HashMap<>();

    public GetPortListEntryBuilder() {
    } 
    
    public GetPortListEntryBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort arg) {
        this._port = arg.getPort();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort) {
            this._port = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort)arg).getPort();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.VlanPort] \n" +
              "but was: " + arg
            );
        }
    }

    public Short getPort() {
        return _port;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetPortListEntryBuilder setPort(Short value) {
    
        this._port = value;
        return this;
    }
    
    public GetPortListEntryBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetPortListEntry build() {
        return new GetPortListEntryImpl(this);
    }

    private static final class GetPortListEntryImpl implements GetPortListEntry {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry.class;
        }

        private final Short _port;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>> augmentation = new HashMap<>();

        private GetPortListEntryImpl(GetPortListEntryBuilder builder) {
            this._port = builder.getPort();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Short getPort() {
            return _port;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.get.vlan.ports.output.GetPortListEntry>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_port == null) ? 0 : _port.hashCode());
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
            GetPortListEntryImpl other = (GetPortListEntryImpl) obj;
            if (_port == null) {
                if (other._port != null) {
                    return false;
                }
            } else if(!_port.equals(other._port)) {
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
            builder.append("GetPortListEntry [_port=");
            builder.append(_port);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
