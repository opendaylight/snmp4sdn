package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import java.util.List;
import com.google.common.collect.Range;
import java.util.ArrayList;



public class AddVlanInputBuilder {

    private Long _nodeId;
    private Integer _vlanId;
    private String _vlanName;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>> augmentation = new HashMap<>();

    public AddVlanInputBuilder() {
    } 


    public Long getNodeId() {
        return _nodeId;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    public String getVlanName() {
        return _vlanName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddVlanInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public AddVlanInputBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public AddVlanInputBuilder setVlanName(String value) {
        if (value != null) {
            boolean isValidLength = false;
            List<Range<Integer>> lengthConstraints = new ArrayList<>(); 
            lengthConstraints.add(Range.closed(0, 20));
            for (Range<Integer> r : lengthConstraints) {
                if (r.contains(value.length())) {
                isValidLength = true;
                }
            }
            if (!isValidLength) {
                throw new IllegalArgumentException(String.format("Invalid length: {}, expected: {}.", value, lengthConstraints));
            }
        }
    
        this._vlanName = value;
        return this;
    }
    
    public AddVlanInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddVlanInput build() {
        return new AddVlanInputImpl(this);
    }

    private static final class AddVlanInputImpl implements AddVlanInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput.class;
        }

        private final Long _nodeId;
        private final Integer _vlanId;
        private final String _vlanName;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>> augmentation = new HashMap<>();

        private AddVlanInputImpl(AddVlanInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._vlanId = builder.getVlanId();
            this._vlanName = builder.getVlanName();
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
        
        @Override
        public String getVlanName() {
            return _vlanName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanInput>> E getAugmentation(Class<E> augmentationType) {
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
            result = prime * result + ((_vlanName == null) ? 0 : _vlanName.hashCode());
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
            AddVlanInputImpl other = (AddVlanInputImpl) obj;
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
            if (_vlanName == null) {
                if (other._vlanName != null) {
                    return false;
                }
            } else if(!_vlanName.equals(other._vlanName)) {
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
            builder.append("AddVlanInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _vlanId=");
            builder.append(_vlanId);
            builder.append(", _vlanName=");
            builder.append(_vlanName);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
