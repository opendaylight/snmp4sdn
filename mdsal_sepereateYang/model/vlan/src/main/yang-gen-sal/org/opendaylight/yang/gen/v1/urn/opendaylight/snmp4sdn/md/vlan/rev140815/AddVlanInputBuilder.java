package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815;
import com.google.common.collect.Range;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import com.google.common.collect.ImmutableList;
import java.math.BigInteger;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput
 */
public class AddVlanInputBuilder {

    private java.lang.Long _nodeID;
    private java.lang.Integer _vlanID;
    private java.lang.String _vlanName;
    private static List<Range<BigInteger>> _vlanName_length;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> augmentation = new HashMap<>();

    public AddVlanInputBuilder() {
    } 

    public AddVlanInputBuilder(AddVlanInput base) {
        this._nodeID = base.getNodeID();
        this._vlanID = base.getVlanID();
        this._vlanName = base.getVlanName();
        if (base instanceof AddVlanInputImpl) {
            AddVlanInputImpl _impl = (AddVlanInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public java.lang.Long getNodeID() {
        return _nodeID;
    }
    
    public java.lang.Integer getVlanID() {
        return _vlanID;
    }
    
    public java.lang.String getVlanName() {
        return _vlanName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddVlanInputBuilder setNodeID(java.lang.Long value) {
        this._nodeID = value;
        return this;
    }
    
    public AddVlanInputBuilder setVlanID(java.lang.Integer value) {
        this._vlanID = value;
        return this;
    }
    
    public AddVlanInputBuilder setVlanName(java.lang.String value) {
        if (value != null) {
            BigInteger _constraint = BigInteger.valueOf(value.length());
            boolean isValidLength = false;
            for (Range<BigInteger> r : _vlanName_length()) {
                if (r.contains(_constraint)) {
                    isValidLength = true;
                }
            }
            if (!isValidLength) {
                throw new IllegalArgumentException(String.format("Invalid length: %s, expected: %s.", value, _vlanName_length));
            }
        }
        this._vlanName = value;
        return this;
    }
    public static List<Range<BigInteger>> _vlanName_length() {
        if (_vlanName_length == null) {
            synchronized (AddVlanInputBuilder.class) {
                if (_vlanName_length == null) {
                    ImmutableList.Builder<Range<BigInteger>> builder = ImmutableList.builder();
                    builder.add(Range.closed(BigInteger.ZERO, BigInteger.valueOf(20L)));
                    _vlanName_length = builder.build();
                }
            }
        }
        return _vlanName_length;
    }
    
    public AddVlanInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddVlanInput build() {
        return new AddVlanInputImpl(this);
    }

    private static final class AddVlanInputImpl implements AddVlanInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput.class;
        }

        private final java.lang.Long _nodeID;
        private final java.lang.Integer _vlanID;
        private final java.lang.String _vlanName;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> augmentation = new HashMap<>();

        private AddVlanInputImpl(AddVlanInputBuilder base) {
            this._nodeID = base.getNodeID();
            this._vlanID = base.getVlanID();
            this._vlanName = base.getVlanName();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public java.lang.Long getNodeID() {
            return _nodeID;
        }
        
        @Override
        public java.lang.Integer getVlanID() {
            return _vlanID;
        }
        
        @Override
        public java.lang.String getVlanName() {
            return _vlanName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_nodeID == null) ? 0 : _nodeID.hashCode());
            result = prime * result + ((_vlanID == null) ? 0 : _vlanID.hashCode());
            result = prime * result + ((_vlanName == null) ? 0 : _vlanName.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput)obj;
            if (_nodeID == null) {
                if (other.getNodeID() != null) {
                    return false;
                }
            } else if(!_nodeID.equals(other.getNodeID())) {
                return false;
            }
            if (_vlanID == null) {
                if (other.getVlanID() != null) {
                    return false;
                }
            } else if(!_vlanID.equals(other.getVlanID())) {
                return false;
            }
            if (_vlanName == null) {
                if (other.getVlanName() != null) {
                    return false;
                }
            } else if(!_vlanName.equals(other.getVlanName())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                AddVlanInputImpl otherImpl = (AddVlanInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.AddVlanInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("AddVlanInput [");
            boolean first = true;
        
            if (_nodeID != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_nodeID=");
                builder.append(_nodeID);
             }
            if (_vlanID != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_vlanID=");
                builder.append(_vlanID);
             }
            if (_vlanName != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_vlanName=");
                builder.append(_vlanName);
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
