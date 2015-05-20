package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry
 */
public class VlanTableEntryBuilder {

    private List<java.lang.Short> _portList;
    private java.lang.Integer _vlanId;
    private java.lang.String _vlanName;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> augmentation = new HashMap<>();

    public VlanTableEntryBuilder() {
    } 
    
    public VlanTableEntryBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry arg) {
        this._vlanId = arg.getVlanId();
        this._vlanName = arg.getVlanName();
        this._portList = arg.getPortList();
    }

    public VlanTableEntryBuilder(VlanTableEntry base) {
        this._portList = base.getPortList();
        this._vlanId = base.getVlanId();
        this._vlanName = base.getVlanName();
        if (base instanceof VlanTableEntryImpl) {
            VlanTableEntryImpl _impl = (VlanTableEntryImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry) {
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry)arg).getVlanId();
            this._vlanName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry)arg).getVlanName();
            this._portList = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry)arg).getPortList();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.VlanEntry] \n" +
              "but was: " + arg
            );
        }
    }

    public List<java.lang.Short> getPortList() {
        return _portList;
    }
    
    public java.lang.Integer getVlanId() {
        return _vlanId;
    }
    
    public java.lang.String getVlanName() {
        return _vlanName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public VlanTableEntryBuilder setPortList(List<java.lang.Short> value) {
        this._portList = value;
        return this;
    }
    
    public VlanTableEntryBuilder setVlanId(java.lang.Integer value) {
        this._vlanId = value;
        return this;
    }
    
    public VlanTableEntryBuilder setVlanName(java.lang.String value) {
        this._vlanName = value;
        return this;
    }
    
    public VlanTableEntryBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public VlanTableEntry build() {
        return new VlanTableEntryImpl(this);
    }

    private static final class VlanTableEntryImpl implements VlanTableEntry {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry.class;
        }

        private final List<java.lang.Short> _portList;
        private final java.lang.Integer _vlanId;
        private final java.lang.String _vlanName;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> augmentation = new HashMap<>();

        private VlanTableEntryImpl(VlanTableEntryBuilder base) {
            this._portList = base.getPortList();
            this._vlanId = base.getVlanId();
            this._vlanName = base.getVlanName();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public List<java.lang.Short> getPortList() {
            return _portList;
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
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_portList == null) ? 0 : _portList.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry)obj;
            if (_portList == null) {
                if (other.getPortList() != null) {
                    return false;
                }
            } else if(!_portList.equals(other.getPortList())) {
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
                VlanTableEntryImpl otherImpl = (VlanTableEntryImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150515.get.vlan.table.output.VlanTableEntry>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("VlanTableEntry [");
            boolean first = true;
        
            if (_portList != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_portList=");
                builder.append(_portList);
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
