package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515;
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
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput
 */
public class AddVlanAndSetPortsInputBuilder {

    private java.lang.Long _nodeId;
    private java.lang.String _taggedPortList;
    private static List<Range<BigInteger>> _taggedPortList_length;
    private java.lang.String _untaggedPortList;
    private static List<Range<BigInteger>> _untaggedPortList_length;
    private java.lang.Integer _vlanId;
    private java.lang.String _vlanName;
    private static List<Range<BigInteger>> _vlanName_length;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> augmentation = new HashMap<>();

    public AddVlanAndSetPortsInputBuilder() {
    } 

    public AddVlanAndSetPortsInputBuilder(AddVlanAndSetPortsInput base) {
        this._nodeId = base.getNodeId();
        this._taggedPortList = base.getTaggedPortList();
        this._untaggedPortList = base.getUntaggedPortList();
        this._vlanId = base.getVlanId();
        this._vlanName = base.getVlanName();
        if (base instanceof AddVlanAndSetPortsInputImpl) {
            AddVlanAndSetPortsInputImpl _impl = (AddVlanAndSetPortsInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public java.lang.Long getNodeId() {
        return _nodeId;
    }
    
    public java.lang.String getTaggedPortList() {
        return _taggedPortList;
    }
    
    public java.lang.String getUntaggedPortList() {
        return _untaggedPortList;
    }
    
    public java.lang.Integer getVlanId() {
        return _vlanId;
    }
    
    public java.lang.String getVlanName() {
        return _vlanName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddVlanAndSetPortsInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public AddVlanAndSetPortsInputBuilder setTaggedPortList(java.lang.String value) {
        if (value != null) {
            BigInteger _constraint = BigInteger.valueOf(value.length());
            boolean isValidLength = false;
            for (Range<BigInteger> r : _taggedPortList_length()) {
                if (r.contains(_constraint)) {
                    isValidLength = true;
                }
            }
            if (!isValidLength) {
                throw new IllegalArgumentException(String.format("Invalid length: %s, expected: %s.", value, _taggedPortList_length));
            }
        }
        this._taggedPortList = value;
        return this;
    }
    public static List<Range<BigInteger>> _taggedPortList_length() {
        if (_taggedPortList_length == null) {
            synchronized (AddVlanAndSetPortsInputBuilder.class) {
                if (_taggedPortList_length == null) {
                    ImmutableList.Builder<Range<BigInteger>> builder = ImmutableList.builder();
                    builder.add(Range.closed(BigInteger.ZERO, BigInteger.valueOf(200L)));
                    _taggedPortList_length = builder.build();
                }
            }
        }
        return _taggedPortList_length;
    }
    
    public AddVlanAndSetPortsInputBuilder setUntaggedPortList(java.lang.String value) {
        if (value != null) {
            BigInteger _constraint = BigInteger.valueOf(value.length());
            boolean isValidLength = false;
            for (Range<BigInteger> r : _untaggedPortList_length()) {
                if (r.contains(_constraint)) {
                    isValidLength = true;
                }
            }
            if (!isValidLength) {
                throw new IllegalArgumentException(String.format("Invalid length: %s, expected: %s.", value, _untaggedPortList_length));
            }
        }
        this._untaggedPortList = value;
        return this;
    }
    public static List<Range<BigInteger>> _untaggedPortList_length() {
        if (_untaggedPortList_length == null) {
            synchronized (AddVlanAndSetPortsInputBuilder.class) {
                if (_untaggedPortList_length == null) {
                    ImmutableList.Builder<Range<BigInteger>> builder = ImmutableList.builder();
                    builder.add(Range.closed(BigInteger.ZERO, BigInteger.valueOf(200L)));
                    _untaggedPortList_length = builder.build();
                }
            }
        }
        return _untaggedPortList_length;
    }
    
    public AddVlanAndSetPortsInputBuilder setVlanId(java.lang.Integer value) {
        this._vlanId = value;
        return this;
    }
    
    public AddVlanAndSetPortsInputBuilder setVlanName(java.lang.String value) {
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
            synchronized (AddVlanAndSetPortsInputBuilder.class) {
                if (_vlanName_length == null) {
                    ImmutableList.Builder<Range<BigInteger>> builder = ImmutableList.builder();
                    builder.add(Range.closed(BigInteger.ZERO, BigInteger.valueOf(20L)));
                    _vlanName_length = builder.build();
                }
            }
        }
        return _vlanName_length;
    }
    
    public AddVlanAndSetPortsInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddVlanAndSetPortsInput build() {
        return new AddVlanAndSetPortsInputImpl(this);
    }

    private static final class AddVlanAndSetPortsInputImpl implements AddVlanAndSetPortsInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput.class;
        }

        private final java.lang.Long _nodeId;
        private final java.lang.String _taggedPortList;
        private final java.lang.String _untaggedPortList;
        private final java.lang.Integer _vlanId;
        private final java.lang.String _vlanName;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> augmentation = new HashMap<>();

        private AddVlanAndSetPortsInputImpl(AddVlanAndSetPortsInputBuilder base) {
            this._nodeId = base.getNodeId();
            this._taggedPortList = base.getTaggedPortList();
            this._untaggedPortList = base.getUntaggedPortList();
            this._vlanId = base.getVlanId();
            this._vlanName = base.getVlanName();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public java.lang.Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public java.lang.String getTaggedPortList() {
            return _taggedPortList;
        }
        
        @Override
        public java.lang.String getUntaggedPortList() {
            return _untaggedPortList;
        }
        
        @Override
        public java.lang.Integer getVlanId() {
            return _vlanId;
        }
        
        @Override
        public java.lang.String getVlanName() {
            return _vlanName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_taggedPortList == null) ? 0 : _taggedPortList.hashCode());
            result = prime * result + ((_untaggedPortList == null) ? 0 : _untaggedPortList.hashCode());
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
            if (!(obj instanceof DataObject)) {
                return false;
            }
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput)obj;
            if (_nodeId == null) {
                if (other.getNodeId() != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other.getNodeId())) {
                return false;
            }
            if (_taggedPortList == null) {
                if (other.getTaggedPortList() != null) {
                    return false;
                }
            } else if(!_taggedPortList.equals(other.getTaggedPortList())) {
                return false;
            }
            if (_untaggedPortList == null) {
                if (other.getUntaggedPortList() != null) {
                    return false;
                }
            } else if(!_untaggedPortList.equals(other.getUntaggedPortList())) {
                return false;
            }
            if (_vlanId == null) {
                if (other.getVlanId() != null) {
                    return false;
                }
            } else if(!_vlanId.equals(other.getVlanId())) {
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
                AddVlanAndSetPortsInputImpl otherImpl = (AddVlanAndSetPortsInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.AddVlanAndSetPortsInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("AddVlanAndSetPortsInput [");
            boolean first = true;
        
            if (_nodeId != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_nodeId=");
                builder.append(_nodeId);
             }
            if (_taggedPortList != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_taggedPortList=");
                builder.append(_taggedPortList);
             }
            if (_untaggedPortList != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_untaggedPortList=");
                builder.append(_untaggedPortList);
             }
            if (_vlanId != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_vlanId=");
                builder.append(_vlanId);
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
