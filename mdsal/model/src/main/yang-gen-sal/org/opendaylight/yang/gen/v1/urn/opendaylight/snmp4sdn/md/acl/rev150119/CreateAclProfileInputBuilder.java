package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class CreateAclProfileInputBuilder {

    private Long _nodeId;
    private AclLayer _aclLayer;
    private String _dstIpMask;
    private String _srcIpMask;
    private Short _vlanMask;
    private Integer _profileId;
    private String _profileName;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentation = new HashMap<>();

    public CreateAclProfileInputBuilder() {
    } 
    
    public CreateAclProfileInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile arg) {
        this._aclLayer = arg.getAclLayer();
        this._dstIpMask = arg.getDstIpMask();
        this._srcIpMask = arg.getSrcIpMask();
        this._vlanMask = arg.getVlanMask();
    }
    
    public CreateAclProfileInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex arg) {
        this._profileId = arg.getProfileId();
        this._profileName = arg.getProfileName();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex) {
            this._profileId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex)arg).getProfileId();
            this._profileName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex)arg).getProfileName();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile) {
            this._aclLayer = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getAclLayer();
            this._dstIpMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getDstIpMask();
            this._srcIpMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getSrcIpMask();
            this._vlanMask = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile)arg).getVlanMask();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfile] \n" +
              "but was: " + arg
            );
        }
    }

    public Long getNodeId() {
        return _nodeId;
    }
    
    public AclLayer getAclLayer() {
        return _aclLayer;
    }
    
    public String getDstIpMask() {
        return _dstIpMask;
    }
    
    public String getSrcIpMask() {
        return _srcIpMask;
    }
    
    public Short getVlanMask() {
        return _vlanMask;
    }
    
    public Integer getProfileId() {
        return _profileId;
    }
    
    public String getProfileName() {
        return _profileName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public CreateAclProfileInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setAclLayer(AclLayer value) {
    
        this._aclLayer = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setDstIpMask(String value) {
    
        this._dstIpMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setSrcIpMask(String value) {
    
        this._srcIpMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setVlanMask(Short value) {
    
        this._vlanMask = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setProfileId(Integer value) {
    
        this._profileId = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder setProfileName(String value) {
    
        this._profileName = value;
        return this;
    }
    
    public CreateAclProfileInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public CreateAclProfileInput build() {
        return new CreateAclProfileInputImpl(this);
    }

    private static final class CreateAclProfileInputImpl implements CreateAclProfileInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput.class;
        }

        private final Long _nodeId;
        private final AclLayer _aclLayer;
        private final String _dstIpMask;
        private final String _srcIpMask;
        private final Short _vlanMask;
        private final Integer _profileId;
        private final String _profileName;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> augmentation = new HashMap<>();

        private CreateAclProfileInputImpl(CreateAclProfileInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._aclLayer = builder.getAclLayer();
            this._dstIpMask = builder.getDstIpMask();
            this._srcIpMask = builder.getSrcIpMask();
            this._vlanMask = builder.getVlanMask();
            this._profileId = builder.getProfileId();
            this._profileName = builder.getProfileName();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public AclLayer getAclLayer() {
            return _aclLayer;
        }
        
        @Override
        public String getDstIpMask() {
            return _dstIpMask;
        }
        
        @Override
        public String getSrcIpMask() {
            return _srcIpMask;
        }
        
        @Override
        public Short getVlanMask() {
            return _vlanMask;
        }
        
        @Override
        public Integer getProfileId() {
            return _profileId;
        }
        
        @Override
        public String getProfileName() {
            return _profileName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.CreateAclProfileInput>> E getAugmentation(Class<E> augmentationType) {
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
            result = prime * result + ((_aclLayer == null) ? 0 : _aclLayer.hashCode());
            result = prime * result + ((_dstIpMask == null) ? 0 : _dstIpMask.hashCode());
            result = prime * result + ((_srcIpMask == null) ? 0 : _srcIpMask.hashCode());
            result = prime * result + ((_vlanMask == null) ? 0 : _vlanMask.hashCode());
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
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            CreateAclProfileInputImpl other = (CreateAclProfileInputImpl) obj;
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
                return false;
            }
            if (_aclLayer == null) {
                if (other._aclLayer != null) {
                    return false;
                }
            } else if(!_aclLayer.equals(other._aclLayer)) {
                return false;
            }
            if (_dstIpMask == null) {
                if (other._dstIpMask != null) {
                    return false;
                }
            } else if(!_dstIpMask.equals(other._dstIpMask)) {
                return false;
            }
            if (_srcIpMask == null) {
                if (other._srcIpMask != null) {
                    return false;
                }
            } else if(!_srcIpMask.equals(other._srcIpMask)) {
                return false;
            }
            if (_vlanMask == null) {
                if (other._vlanMask != null) {
                    return false;
                }
            } else if(!_vlanMask.equals(other._vlanMask)) {
                return false;
            }
            if (_profileId == null) {
                if (other._profileId != null) {
                    return false;
                }
            } else if(!_profileId.equals(other._profileId)) {
                return false;
            }
            if (_profileName == null) {
                if (other._profileName != null) {
                    return false;
                }
            } else if(!_profileName.equals(other._profileName)) {
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
            builder.append("CreateAclProfileInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _aclLayer=");
            builder.append(_aclLayer);
            builder.append(", _dstIpMask=");
            builder.append(_dstIpMask);
            builder.append(", _srcIpMask=");
            builder.append(_srcIpMask);
            builder.append(", _vlanMask=");
            builder.append(_vlanMask);
            builder.append(", _profileId=");
            builder.append(_profileId);
            builder.append(", _profileName=");
            builder.append(_profileName);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
