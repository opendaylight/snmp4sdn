package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntryType;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class FdbTableEntryBuilder {

    private Long _destMacAddr;
    private Long _nodeId;
    private Short _port;
    private FdbEntryType _type;
    private Integer _vlanId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>> augmentation = new HashMap<>();

    public FdbTableEntryBuilder() {
    } 
    
    public FdbTableEntryBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry arg) {
        this._destMacAddr = arg.getDestMacAddr();
        this._nodeId = arg.getNodeId();
        this._port = arg.getPort();
        this._type = arg.getType();
        this._vlanId = arg.getVlanId();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry) {
            this._destMacAddr = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getDestMacAddr();
            this._nodeId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getNodeId();
            this._port = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getPort();
            this._type = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getType();
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getVlanId();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry] \n" +
              "but was: " + arg
            );
        }
    }

    public Long getDestMacAddr() {
        return _destMacAddr;
    }
    
    public Long getNodeId() {
        return _nodeId;
    }
    
    public Short getPort() {
        return _port;
    }
    
    public FdbEntryType getType() {
        return _type;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public FdbTableEntryBuilder setDestMacAddr(Long value) {
    
        this._destMacAddr = value;
        return this;
    }
    
    public FdbTableEntryBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public FdbTableEntryBuilder setPort(Short value) {
    
        this._port = value;
        return this;
    }
    
    public FdbTableEntryBuilder setType(FdbEntryType value) {
    
        this._type = value;
        return this;
    }
    
    public FdbTableEntryBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public FdbTableEntryBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public FdbTableEntry build() {
        return new FdbTableEntryImpl(this);
    }

    private static final class FdbTableEntryImpl implements FdbTableEntry {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry.class;
        }

        private final Long _destMacAddr;
        private final Long _nodeId;
        private final Short _port;
        private final FdbEntryType _type;
        private final Integer _vlanId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>> augmentation = new HashMap<>();

        private FdbTableEntryImpl(FdbTableEntryBuilder builder) {
            this._destMacAddr = builder.getDestMacAddr();
            this._nodeId = builder.getNodeId();
            this._port = builder.getPort();
            this._type = builder.getType();
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
        public Short getPort() {
            return _port;
        }
        
        @Override
        public FdbEntryType getType() {
            return _type;
        }
        
        @Override
        public Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry>> E getAugmentation(Class<E> augmentationType) {
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
            result = prime * result + ((_port == null) ? 0 : _port.hashCode());
            result = prime * result + ((_type == null) ? 0 : _type.hashCode());
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
            FdbTableEntryImpl other = (FdbTableEntryImpl) obj;
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
            if (_port == null) {
                if (other._port != null) {
                    return false;
                }
            } else if(!_port.equals(other._port)) {
                return false;
            }
            if (_type == null) {
                if (other._type != null) {
                    return false;
                }
            } else if(!_type.equals(other._type)) {
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
            builder.append("FdbTableEntry [_destMacAddr=");
            builder.append(_destMacAddr);
            builder.append(", _nodeId=");
            builder.append(_nodeId);
            builder.append(", _port=");
            builder.append(_port);
            builder.append(", _type=");
            builder.append(_type);
            builder.append(", _vlanId=");
            builder.append(_vlanId);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
