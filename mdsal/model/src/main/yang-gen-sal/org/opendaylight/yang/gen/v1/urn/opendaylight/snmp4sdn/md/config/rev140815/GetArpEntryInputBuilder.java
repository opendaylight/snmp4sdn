package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetArpEntryInputBuilder {

    private String _ipAddress;
    private Long _nodeId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentation = new HashMap<>();

    public GetArpEntryInputBuilder() {
    } 


    public String getIpAddress() {
        return _ipAddress;
    }
    
    public Long getNodeId() {
        return _nodeId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetArpEntryInputBuilder setIpAddress(String value) {
    
        this._ipAddress = value;
        return this;
    }
    
    public GetArpEntryInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public GetArpEntryInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetArpEntryInput build() {
        return new GetArpEntryInputImpl(this);
    }

    private static final class GetArpEntryInputImpl implements GetArpEntryInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput.class;
        }

        private final String _ipAddress;
        private final Long _nodeId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentation = new HashMap<>();

        private GetArpEntryInputImpl(GetArpEntryInputBuilder builder) {
            this._ipAddress = builder.getIpAddress();
            this._nodeId = builder.getNodeId();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public String getIpAddress() {
            return _ipAddress;
        }
        
        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_ipAddress == null) ? 0 : _ipAddress.hashCode());
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
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
            GetArpEntryInputImpl other = (GetArpEntryInputImpl) obj;
            if (_ipAddress == null) {
                if (other._ipAddress != null) {
                    return false;
                }
            } else if(!_ipAddress.equals(other._ipAddress)) {
                return false;
            }
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
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
            builder.append("GetArpEntryInput [_ipAddress=");
            builder.append(_ipAddress);
            builder.append(", _nodeId=");
            builder.append(_nodeId);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
