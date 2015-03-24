package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput
 */
public class GetFdbEntryOutputBuilder {

    private java.lang.Long _destMacAddr;
    private java.lang.Long _nodeId;
    private java.lang.Short _port;
    private java.lang.Integer _vlanId;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> augmentation = new HashMap<>();

    public GetFdbEntryOutputBuilder() {
    } 
    
    public GetFdbEntryOutputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry arg) {
        this._nodeId = arg.getNodeId();
        this._destMacAddr = arg.getDestMacAddr();
        this._vlanId = arg.getVlanId();
        this._port = arg.getPort();
    }

    public GetFdbEntryOutputBuilder(GetFdbEntryOutput base) {
        this._destMacAddr = base.getDestMacAddr();
        this._nodeId = base.getNodeId();
        this._port = base.getPort();
        this._vlanId = base.getVlanId();
        if (base instanceof GetFdbEntryOutputImpl) {
            GetFdbEntryOutputImpl _impl = (GetFdbEntryOutputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
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
            this._nodeId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getNodeId();
            this._destMacAddr = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getDestMacAddr();
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getVlanId();
            this._port = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry)arg).getPort();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.FdbEntry] \n" +
              "but was: " + arg
            );
        }
    }

    public java.lang.Long getDestMacAddr() {
        return _destMacAddr;
    }
    
    public java.lang.Long getNodeId() {
        return _nodeId;
    }
    
    public java.lang.Short getPort() {
        return _port;
    }
    
    public java.lang.Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetFdbEntryOutputBuilder setDestMacAddr(java.lang.Long value) {
        this._destMacAddr = value;
        return this;
    }
    
    public GetFdbEntryOutputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public GetFdbEntryOutputBuilder setPort(java.lang.Short value) {
        this._port = value;
        return this;
    }
    
    public GetFdbEntryOutputBuilder setVlanId(java.lang.Integer value) {
        this._vlanId = value;
        return this;
    }
    
    public GetFdbEntryOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetFdbEntryOutput build() {
        return new GetFdbEntryOutputImpl(this);
    }

    private static final class GetFdbEntryOutputImpl implements GetFdbEntryOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput.class;
        }

        private final java.lang.Long _destMacAddr;
        private final java.lang.Long _nodeId;
        private final java.lang.Short _port;
        private final java.lang.Integer _vlanId;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> augmentation = new HashMap<>();

        private GetFdbEntryOutputImpl(GetFdbEntryOutputBuilder base) {
            this._destMacAddr = base.getDestMacAddr();
            this._nodeId = base.getNodeId();
            this._port = base.getPort();
            this._vlanId = base.getVlanId();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public java.lang.Long getDestMacAddr() {
            return _destMacAddr;
        }
        
        @Override
        public java.lang.Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public java.lang.Short getPort() {
            return _port;
        }
        
        @Override
        public java.lang.Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_vlanId == null) ? 0 : _vlanId.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput)obj;
            if (_destMacAddr == null) {
                if (other.getDestMacAddr() != null) {
                    return false;
                }
            } else if(!_destMacAddr.equals(other.getDestMacAddr())) {
                return false;
            }
            if (_nodeId == null) {
                if (other.getNodeId() != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other.getNodeId())) {
                return false;
            }
            if (_port == null) {
                if (other.getPort() != null) {
                    return false;
                }
            } else if(!_port.equals(other.getPort())) {
                return false;
            }
            if (_vlanId == null) {
                if (other.getVlanId() != null) {
                    return false;
                }
            } else if(!_vlanId.equals(other.getVlanId())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                GetFdbEntryOutputImpl otherImpl = (GetFdbEntryOutputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbEntryOutput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("GetFdbEntryOutput [");
            boolean first = true;
        
            if (_destMacAddr != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_destMacAddr=");
                builder.append(_destMacAddr);
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
            if (_port != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_port=");
                builder.append(_port);
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
