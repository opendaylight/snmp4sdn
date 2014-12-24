package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput} instances.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput
 */
public class GetArpEntryInputBuilder {

    private java.lang.String _ipAddress;
    private java.lang.Long _nodeId;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentation = new HashMap<>();

    public GetArpEntryInputBuilder() {
    } 

    public GetArpEntryInputBuilder(GetArpEntryInput base) {
        this._ipAddress = base.getIpAddress();
        this._nodeId = base.getNodeId();
        if (base instanceof GetArpEntryInputImpl) {
            GetArpEntryInputImpl _impl = (GetArpEntryInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public java.lang.String getIpAddress() {
        return _ipAddress;
    }
    
    public java.lang.Long getNodeId() {
        return _nodeId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetArpEntryInputBuilder setIpAddress(java.lang.String value) {
        this._ipAddress = value;
        return this;
    }
    
    public GetArpEntryInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public GetArpEntryInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetArpEntryInput build() {
        return new GetArpEntryInputImpl(this);
    }

    private static final class GetArpEntryInputImpl implements GetArpEntryInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput.class;
        }

        private final java.lang.String _ipAddress;
        private final java.lang.Long _nodeId;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> augmentation = new HashMap<>();

        private GetArpEntryInputImpl(GetArpEntryInputBuilder base) {
            this._ipAddress = base.getIpAddress();
            this._nodeId = base.getNodeId();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public java.lang.String getIpAddress() {
            return _ipAddress;
        }
        
        @Override
        public java.lang.Long getNodeId() {
            return _nodeId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            if (!(obj instanceof DataObject)) {
                return false;
            }
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput)obj;
            if (_ipAddress == null) {
                if (other.getIpAddress() != null) {
                    return false;
                }
            } else if(!_ipAddress.equals(other.getIpAddress())) {
                return false;
            }
            if (_nodeId == null) {
                if (other.getNodeId() != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other.getNodeId())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                GetArpEntryInputImpl otherImpl = (GetArpEntryInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryInput>> e : augmentation.entrySet()) {
                    if (!e.getValue().equals(other.getAugmentation(e.getKey()))) {
                        return false;
                    }
                }
                // .. and give the other one the chance to do the same
                if (!obj.equals(this)) {
                    return false;
                }
            }
            return true;
        }
        
        @Override
        public java.lang.String toString() {
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("GetArpEntryInput [");
            boolean first = true;
        
            if (_ipAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_ipAddress=");
                builder.append(_ipAddress);
             }
            if (_nodeId != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_nodeId=");
                builder.append(_nodeId);
             }
            if (first) {
                first = false;
            } else {
                builder.append(", ");
            }
            builder.append("augmentation=");
            builder.append(augmentation.values());
            return builder.append(']').toString();
        }
    }

}
