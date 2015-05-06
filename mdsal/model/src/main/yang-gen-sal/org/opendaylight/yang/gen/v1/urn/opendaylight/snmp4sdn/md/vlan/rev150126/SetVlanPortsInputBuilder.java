package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.set.vlan.ports.input.SetPortListEntry;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetVlanPortsInputBuilder {

    private Long _nodeId;
    private List<SetPortListEntry> _setPortListEntry;
    private Integer _vlanId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>> augmentation = new HashMap<>();

    public SetVlanPortsInputBuilder() {
    } 


    public Long getNodeId() {
        return _nodeId;
    }
    
    public List<SetPortListEntry> getSetPortListEntry() {
        return _setPortListEntry;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetVlanPortsInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public SetVlanPortsInputBuilder setSetPortListEntry(List<SetPortListEntry> value) {
    
        this._setPortListEntry = value;
        return this;
    }
    
    public SetVlanPortsInputBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public SetVlanPortsInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetVlanPortsInput build() {
        return new SetVlanPortsInputImpl(this);
    }

    private static final class SetVlanPortsInputImpl implements SetVlanPortsInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput.class;
        }

        private final Long _nodeId;
        private final List<SetPortListEntry> _setPortListEntry;
        private final Integer _vlanId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>> augmentation = new HashMap<>();

        private SetVlanPortsInputImpl(SetVlanPortsInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._setPortListEntry = builder.getSetPortListEntry();
            this._vlanId = builder.getVlanId();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public List<SetPortListEntry> getSetPortListEntry() {
            return _setPortListEntry;
        }
        
        @Override
        public Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsInput>> E getAugmentation(Class<E> augmentationType) {
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
            result = prime * result + ((_setPortListEntry == null) ? 0 : _setPortListEntry.hashCode());
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
            SetVlanPortsInputImpl other = (SetVlanPortsInputImpl) obj;
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
                return false;
            }
            if (_setPortListEntry == null) {
                if (other._setPortListEntry != null) {
                    return false;
                }
            } else if(!_setPortListEntry.equals(other._setPortListEntry)) {
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
            builder.append("SetVlanPortsInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _setPortListEntry=");
            builder.append(_setPortListEntry);
            builder.append(", _vlanId=");
            builder.append(_vlanId);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
