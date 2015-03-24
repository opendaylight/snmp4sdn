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
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput
 */
public class SetVlanPortsInputBuilder {

    private java.lang.Long _nodeID;
    private java.lang.String _portList;
    private static List<Range<BigInteger>> _portList_length;
    private java.lang.Integer _vlanID;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> augmentation = new HashMap<>();

    public SetVlanPortsInputBuilder() {
    } 

    public SetVlanPortsInputBuilder(SetVlanPortsInput base) {
        this._nodeID = base.getNodeID();
        this._portList = base.getPortList();
        this._vlanID = base.getVlanID();
        if (base instanceof SetVlanPortsInputImpl) {
            SetVlanPortsInputImpl _impl = (SetVlanPortsInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public java.lang.Long getNodeID() {
        return _nodeID;
    }
    
    public java.lang.String getPortList() {
        return _portList;
    }
    
    public java.lang.Integer getVlanID() {
        return _vlanID;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetVlanPortsInputBuilder setNodeID(java.lang.Long value) {
        this._nodeID = value;
        return this;
    }
    
    public SetVlanPortsInputBuilder setPortList(java.lang.String value) {
        if (value != null) {
            BigInteger _constraint = BigInteger.valueOf(value.length());
            boolean isValidLength = false;
            for (Range<BigInteger> r : _portList_length()) {
                if (r.contains(_constraint)) {
                    isValidLength = true;
                }
            }
            if (!isValidLength) {
                throw new IllegalArgumentException(String.format("Invalid length: %s, expected: %s.", value, _portList_length));
            }
        }
        this._portList = value;
        return this;
    }
    public static List<Range<BigInteger>> _portList_length() {
        if (_portList_length == null) {
            synchronized (SetVlanPortsInputBuilder.class) {
                if (_portList_length == null) {
                    ImmutableList.Builder<Range<BigInteger>> builder = ImmutableList.builder();
                    builder.add(Range.closed(BigInteger.ZERO, BigInteger.valueOf(200L)));
                    _portList_length = builder.build();
                }
            }
        }
        return _portList_length;
    }
    
    public SetVlanPortsInputBuilder setVlanID(java.lang.Integer value) {
        this._vlanID = value;
        return this;
    }
    
    public SetVlanPortsInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetVlanPortsInput build() {
        return new SetVlanPortsInputImpl(this);
    }

    private static final class SetVlanPortsInputImpl implements SetVlanPortsInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput.class;
        }

        private final java.lang.Long _nodeID;
        private final java.lang.String _portList;
        private final java.lang.Integer _vlanID;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> augmentation = new HashMap<>();

        private SetVlanPortsInputImpl(SetVlanPortsInputBuilder base) {
            this._nodeID = base.getNodeID();
            this._portList = base.getPortList();
            this._vlanID = base.getVlanID();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>singletonMap(e.getKey(), e.getValue());       
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
        public java.lang.String getPortList() {
            return _portList;
        }
        
        @Override
        public java.lang.Integer getVlanID() {
            return _vlanID;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_portList == null) ? 0 : _portList.hashCode());
            result = prime * result + ((_vlanID == null) ? 0 : _vlanID.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput)obj;
            if (_nodeID == null) {
                if (other.getNodeID() != null) {
                    return false;
                }
            } else if(!_nodeID.equals(other.getNodeID())) {
                return false;
            }
            if (_portList == null) {
                if (other.getPortList() != null) {
                    return false;
                }
            } else if(!_portList.equals(other.getPortList())) {
                return false;
            }
            if (_vlanID == null) {
                if (other.getVlanID() != null) {
                    return false;
                }
            } else if(!_vlanID.equals(other.getVlanID())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                SetVlanPortsInputImpl otherImpl = (SetVlanPortsInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev140815.SetVlanPortsInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("SetVlanPortsInput [");
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
            if (_portList != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_portList=");
                builder.append(_portList);
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
