package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class GetArpEntryOutputBuilder {

    private String _ipAddress;
    private Long _macAddress;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentation = new HashMap<>();

    public GetArpEntryOutputBuilder() {
    } 
    
    public GetArpEntryOutputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.ArpEntry arg) {
        this._ipAddress = arg.getIpAddress();
        this._macAddress = arg.getMacAddress();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
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

    public String getIpAddress() {
        return _ipAddress;
    }
    
    public Long getMacAddress() {
        return _macAddress;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetArpEntryOutputBuilder setIpAddress(String value) {
    
        this._ipAddress = value;
        return this;
    }
    
    public GetArpEntryOutputBuilder setMacAddress(Long value) {
    
        this._macAddress = value;
        return this;
    }
    
    public GetArpEntryOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetArpEntryOutput build() {
        return new GetArpEntryOutputImpl(this);
    }

    private static final class GetArpEntryOutputImpl implements GetArpEntryOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput.class;
        }

        private final String _ipAddress;
        private final Long _macAddress;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> augmentation = new HashMap<>();

        private GetArpEntryOutputImpl(GetArpEntryOutputBuilder builder) {
            this._ipAddress = builder.getIpAddress();
            this._macAddress = builder.getMacAddress();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public String getIpAddress() {
            return _ipAddress;
        }
        
        @Override
        public Long getMacAddress() {
            return _macAddress;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
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
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            GetArpEntryOutputImpl other = (GetArpEntryOutputImpl) obj;
            if (_ipAddress == null) {
                if (other._ipAddress != null) {
                    return false;
                }
            } else if(!_ipAddress.equals(other._ipAddress)) {
                return false;
            }
            if (_macAddress == null) {
                if (other._macAddress != null) {
                    return false;
                }
            } else if(!_macAddress.equals(other._macAddress)) {
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
            builder.append("GetArpEntryOutput [_ipAddress=");
            builder.append(_ipAddress);
            builder.append(", _macAddress=");
            builder.append(_macAddress);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
