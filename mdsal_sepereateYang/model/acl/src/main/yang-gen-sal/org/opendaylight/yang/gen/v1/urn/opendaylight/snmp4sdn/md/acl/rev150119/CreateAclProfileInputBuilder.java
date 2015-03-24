package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput
 */
public class CreateAclProfileInputBuilder {

    private AclLayer _aclLayer;
    private java.lang.String _dstIpMask;
    private java.lang.Long _nodeId;
    private java.lang.Integer _profileId;
    private java.lang.String _profileName;
    private java.lang.String _srcIpMask;
    private java.lang.Integer _vlanMask;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentation = new HashMap<>();

    public CreateAclProfileInputBuilder() {
    } 
    
    public CreateAclProfileInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile arg) {
        this._aclLayer = arg.getAclLayer();
        this._vlanMask = arg.getVlanMask();
        this._srcIpMask = arg.getSrcIpMask();
        this._dstIpMask = arg.getDstIpMask();
    }

    public CreateAclProfileInputBuilder(CreateAclProfileInput base) {
        this._aclLayer = base.getAclLayer();
        this._dstIpMask = base.getDstIpMask();
        this._nodeId = base.getNodeId();
        this._profileId = base.getProfileId();
        this._profileName = base.getProfileName();
        this._srcIpMask = base.getSrcIpMask();
        this._vlanMask = base.getVlanMask();
        if (base instanceof CreateAclProfileInputImpl) {
            CreateAclProfileInputImpl _impl = (CreateAclProfileInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile) {
            this._aclLayer = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getAclLayer();
            this._vlanMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getVlanMask();
            this._srcIpMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getSrcIpMask();
            this._dstIpMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getDstIpMask();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile] \n" +
              "but was: " + arg
            );
        }
    }

    public AclLayer getAclLayer() {
        return _aclLayer;
    }
    
    public java.lang.String getDstIpMask() {
        return _dstIpMask;
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
    
    public java.lang.String getSrcIpMask() {
        return _srcIpMask;
    }
    
    public java.lang.Integer getVlanMask() {
        return _vlanMask;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public CreateAclProfileInputBuilder setAclLayer(AclLayer value) {
        this._aclLayer = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setDstIpMask(java.lang.String value) {
        this._dstIpMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setProfileId(java.lang.Integer value) {
        this._profileId = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setProfileName(java.lang.String value) {
        this._profileName = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setSrcIpMask(java.lang.String value) {
        this._srcIpMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setVlanMask(java.lang.Integer value) {
        this._vlanMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public CreateAclProfileInput build() {
        return new CreateAclProfileInputImpl(this);
    }

    private static final class CreateAclProfileInputImpl implements CreateAclProfileInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput.class;
        }

        private final AclLayer _aclLayer;
        private final java.lang.String _dstIpMask;
        private final java.lang.Long _nodeId;
        private final java.lang.Integer _profileId;
        private final java.lang.String _profileName;
        private final java.lang.String _srcIpMask;
        private final java.lang.Integer _vlanMask;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentation = new HashMap<>();

        private CreateAclProfileInputImpl(CreateAclProfileInputBuilder base) {
            this._aclLayer = base.getAclLayer();
            this._dstIpMask = base.getDstIpMask();
            this._nodeId = base.getNodeId();
            this._profileId = base.getProfileId();
            this._profileName = base.getProfileName();
            this._srcIpMask = base.getSrcIpMask();
            this._vlanMask = base.getVlanMask();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public AclLayer getAclLayer() {
            return _aclLayer;
        }
        
        @Override
        public java.lang.String getDstIpMask() {
            return _dstIpMask;
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
        
        @Override
        public java.lang.String getSrcIpMask() {
            return _srcIpMask;
        }
        
        @Override
        public java.lang.Integer getVlanMask() {
            return _vlanMask;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_aclLayer == null) ? 0 : _aclLayer.hashCode());
            result = prime * result + ((_dstIpMask == null) ? 0 : _dstIpMask.hashCode());
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
            result = prime * result + ((_profileId == null) ? 0 : _profileId.hashCode());
            result = prime * result + ((_profileName == null) ? 0 : _profileName.hashCode());
            result = prime * result + ((_srcIpMask == null) ? 0 : _srcIpMask.hashCode());
            result = prime * result + ((_vlanMask == null) ? 0 : _vlanMask.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput)obj;
            if (_aclLayer == null) {
                if (other.getAclLayer() != null) {
                    return false;
                }
            } else if(!_aclLayer.equals(other.getAclLayer())) {
                return false;
            }
            if (_dstIpMask == null) {
                if (other.getDstIpMask() != null) {
                    return false;
                }
            } else if(!_dstIpMask.equals(other.getDstIpMask())) {
                return false;
            }
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
            if (_srcIpMask == null) {
                if (other.getSrcIpMask() != null) {
                    return false;
                }
            } else if(!_srcIpMask.equals(other.getSrcIpMask())) {
                return false;
            }
            if (_vlanMask == null) {
                if (other.getVlanMask() != null) {
                    return false;
                }
            } else if(!_vlanMask.equals(other.getVlanMask())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                CreateAclProfileInputImpl otherImpl = (CreateAclProfileInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("CreateAclProfileInput [");
            boolean first = true;
        
            if (_aclLayer != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_aclLayer=");
                builder.append(_aclLayer);
             }
            if (_dstIpMask != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_dstIpMask=");
                builder.append(_dstIpMask);
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
            if (_srcIpMask != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_srcIpMask=");
                builder.append(_srcIpMask);
             }
            if (_vlanMask != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_vlanMask=");
                builder.append(_vlanMask);
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
