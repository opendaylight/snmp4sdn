package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DeleteVlanInputBuilder {

    private Long _nodeId;
    private Integer _vlanId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>> augmentation = new HashMap<>();

    public DeleteVlanInputBuilder() {
    } 


    public Long getNodeId() {
        return _nodeId;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DeleteVlanInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public DeleteVlanInputBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public DeleteVlanInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DeleteVlanInput build() {
        return new DeleteVlanInputImpl(this);
    }

    private static final class DeleteVlanInputImpl implements DeleteVlanInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput.class;
        }

        private final Long _nodeId;
        private final Integer _vlanId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>> augmentation = new HashMap<>();

        private DeleteVlanInputImpl(DeleteVlanInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._vlanId = builder.getVlanId();
            this.augmentation.putAll(builder.augmentation);
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
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanInput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
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
            DeleteVlanInputImpl other = (DeleteVlanInputImpl) obj;
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
            builder.append("DeleteVlanInput [_nodeId=");
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
