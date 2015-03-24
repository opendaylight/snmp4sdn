package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DelFdbEntryInputBuilder {

    private Long _destMacAddr;
    private Long _nodeId;
    private Integer _vlanId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>> augmentation = new HashMap<>();

    public DelFdbEntryInputBuilder() {
    } 


    public Long getDestMacAddr() {
        return _destMacAddr;
    }
    
    public Long getNodeId() {
        return _nodeId;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelFdbEntryInputBuilder setDestMacAddr(Long value) {
    
        this._destMacAddr = value;
        return this;
    }
    
    public DelFdbEntryInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public DelFdbEntryInputBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public DelFdbEntryInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelFdbEntryInput build() {
        return new DelFdbEntryInputImpl(this);
    }

    private static final class DelFdbEntryInputImpl implements DelFdbEntryInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput.class;
        }

        private final Long _destMacAddr;
        private final Long _nodeId;
        private final Integer _vlanId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>> augmentation = new HashMap<>();

        private DelFdbEntryInputImpl(DelFdbEntryInputBuilder builder) {
            this._destMacAddr = builder.getDestMacAddr();
            this._nodeId = builder.getNodeId();
            this._vlanId = builder.getVlanId();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Long getDestMacAddr() {
            return _destMacAddr;
        }
        
        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryInput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_destMacAddr == null) ? 0 : _destMacAddr.hashCode());
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
            result = prime * result + ((_vlanId == null) ? 0 : _vlanId.hashCode());
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
            DelFdbEntryInputImpl other = (DelFdbEntryInputImpl) obj;
            if (_destMacAddr == null) {
                if (other._destMacAddr != null) {
                    return false;
                }
            } else if(!_destMacAddr.equals(other._destMacAddr)) {
                return false;
            }
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
                return false;
            }
            if (_vlanId == null) {
                if (other._vlanId != null) {
                    return false;
                }
            } else if(!_vlanId.equals(other._vlanId)) {
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
            builder.append("DelFdbEntryInput [_destMacAddr=");
            builder.append(_destMacAddr);
            builder.append(", _nodeId=");
            builder.append(_nodeId);
            builder.append(", _vlanId=");
            builder.append(_vlanId);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
