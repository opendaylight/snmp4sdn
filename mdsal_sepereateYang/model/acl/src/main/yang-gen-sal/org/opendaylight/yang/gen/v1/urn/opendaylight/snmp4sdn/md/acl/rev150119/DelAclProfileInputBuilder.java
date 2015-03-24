package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput
 */
public class DelAclProfileInputBuilder {

    private java.lang.Long _nodeId;
    private java.lang.Integer _profileId;
    private java.lang.String _profileName;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> augmentation = new HashMap<>();

    public DelAclProfileInputBuilder() {
    } 

    public DelAclProfileInputBuilder(DelAclProfileInput base) {
        this._nodeId = base.getNodeId();
        this._profileId = base.getProfileId();
        this._profileName = base.getProfileName();
        if (base instanceof DelAclProfileInputImpl) {
            DelAclProfileInputImpl _impl = (DelAclProfileInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public java.lang.Long getNodeId() {
        return _nodeId;
    }
    
    public java.lang.Integer getProfileId() {
        return _profileId;
    }
    
    public java.lang.String getProfileName() {
        return _profileName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelAclProfileInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public DelAclProfileInputBuilder setProfileId(java.lang.Integer value) {
        this._profileId = value;
        return this;
    }
    
    public DelAclProfileInputBuilder setProfileName(java.lang.String value) {
        this._profileName = value;
        return this;
    }
    
    public DelAclProfileInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelAclProfileInput build() {
        return new DelAclProfileInputImpl(this);
    }

    private static final class DelAclProfileInputImpl implements DelAclProfileInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput.class;
        }

        private final java.lang.Long _nodeId;
        private final java.lang.Integer _profileId;
        private final java.lang.String _profileName;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> augmentation = new HashMap<>();

        private DelAclProfileInputImpl(DelAclProfileInputBuilder base) {
            this._nodeId = base.getNodeId();
            this._profileId = base.getProfileId();
            this._profileName = base.getProfileName();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>singletonMap(e.getKey(), e.getValue());       
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
        public java.lang.Integer getProfileId() {
            return _profileId;
        }
        
        @Override
        public java.lang.String getProfileName() {
            return _profileName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_profileId == null) ? 0 : _profileId.hashCode());
            result = prime * result + ((_profileName == null) ? 0 : _profileName.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput)obj;
            if (_nodeId == null) {
                if (other.getNodeId() != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other.getNodeId())) {
                return false;
            }
            if (_profileId == null) {
                if (other.getProfileId() != null) {
                    return false;
                }
            } else if(!_profileId.equals(other.getProfileId())) {
                return false;
            }
            if (_profileName == null) {
                if (other.getProfileName() != null) {
                    return false;
                }
            } else if(!_profileName.equals(other.getProfileName())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                DelAclProfileInputImpl otherImpl = (DelAclProfileInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclProfileInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("DelAclProfileInput [");
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
            if (_profileId != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_profileId=");
                builder.append(_profileId);
             }
            if (_profileName != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_profileName=");
                builder.append(_profileName);
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
