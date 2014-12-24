package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput} instances.
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput
 */
public class GetArpEntryOutputBuilder {

    private java.lang.String _ipAddress;
    private java.lang.Long _macAddress;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentation = new HashMap<>();

    public GetArpEntryOutputBuilder() {
    } 
    
    public GetArpEntryOutputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry arg) {
        this._ipAddress = arg.getIpAddress();
        this._macAddress = arg.getMacAddress();
    }

    public GetArpEntryOutputBuilder(GetArpEntryOutput base) {
        this._ipAddress = base.getIpAddress();
        this._macAddress = base.getMacAddress();
        if (base instanceof GetArpEntryOutputImpl) {
            GetArpEntryOutputImpl _impl = (GetArpEntryOutputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry) {
            this._ipAddress = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry)arg).getIpAddress();
            this._macAddress = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry)arg).getMacAddress();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry] \n" +
              "but was: " + arg
            );
        }
    }

    public java.lang.String getIpAddress() {
        return _ipAddress;
    }
    
    public java.lang.Long getMacAddress() {
        return _macAddress;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetArpEntryOutputBuilder setIpAddress(java.lang.String value) {
        this._ipAddress = value;
        return this;
    }
    
    public GetArpEntryOutputBuilder setMacAddress(java.lang.Long value) {
        this._macAddress = value;
        return this;
    }
    
    public GetArpEntryOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetArpEntryOutput build() {
        return new GetArpEntryOutputImpl(this);
    }

    private static final class GetArpEntryOutputImpl implements GetArpEntryOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput.class;
        }

        private final java.lang.String _ipAddress;
        private final java.lang.Long _macAddress;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentation = new HashMap<>();

        private GetArpEntryOutputImpl(GetArpEntryOutputBuilder base) {
            this._ipAddress = base.getIpAddress();
            this._macAddress = base.getMacAddress();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public java.lang.String getIpAddress() {
            return _ipAddress;
        }
        
        @Override
        public java.lang.Long getMacAddress() {
            return _macAddress;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_ipAddress == null) ? 0 : _ipAddress.hashCode());
            result = prime * result + ((_macAddress == null) ? 0 : _macAddress.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput)obj;
            if (_ipAddress == null) {
                if (other.getIpAddress() != null) {
                    return false;
                }
            } else if(!_ipAddress.equals(other.getIpAddress())) {
                return false;
            }
            if (_macAddress == null) {
                if (other.getMacAddress() != null) {
                    return false;
                }
            } else if(!_macAddress.equals(other.getMacAddress())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                GetArpEntryOutputImpl otherImpl = (GetArpEntryOutputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("GetArpEntryOutput [");
            boolean first = true;
        
            if (_ipAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_ipAddress=");
                builder.append(_ipAddress);
             }
            if (_macAddress != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_macAddress=");
                builder.append(_macAddress);
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
