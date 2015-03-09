package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput
 */
public class SetAclRuleInputBuilder {

    private AclAction _aclAction;
    private AclLayer _aclLayer;
    private java.lang.String _dstIp;
    private java.lang.Long _nodeId;
    private List<java.lang.Short> _portList;
    private java.lang.Integer _profileId;
    private java.lang.String _profileName;
    private java.lang.Integer _ruleId;
    private java.lang.String _ruleName;
    private java.lang.String _srcIp;
    private java.lang.Integer _vlanId;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentation = new HashMap<>();

    public SetAclRuleInputBuilder() {
    } 
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule arg) {
        this._portList = arg.getPortList();
        this._aclLayer = arg.getAclLayer();
        this._aclAction = arg.getAclAction();
        this._vlanId = arg.getVlanId();
        this._srcIp = arg.getSrcIp();
        this._dstIp = arg.getDstIp();
    }
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField arg) {
        this._vlanId = arg.getVlanId();
        this._srcIp = arg.getSrcIp();
        this._dstIp = arg.getDstIp();
    }
    
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex arg) {
        this._profileId = arg.getProfileId();
        this._profileName = arg.getProfileName();
    }
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex arg) {
        this._ruleId = arg.getRuleId();
        this._ruleName = arg.getRuleName();
    }

    public SetAclRuleInputBuilder(SetAclRuleInput base) {
        this._aclAction = base.getAclAction();
        this._aclLayer = base.getAclLayer();
        this._dstIp = base.getDstIp();
        this._nodeId = base.getNodeId();
        this._portList = base.getPortList();
        this._profileId = base.getProfileId();
        this._profileName = base.getProfileName();
        this._ruleId = base.getRuleId();
        this._ruleName = base.getRuleName();
        this._srcIp = base.getSrcIp();
        this._vlanId = base.getVlanId();
        if (base instanceof SetAclRuleInputImpl) {
            SetAclRuleInputImpl _impl = (SetAclRuleInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField</li>
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
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule) {
            this._portList = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getPortList();
            this._aclLayer = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getAclLayer();
            this._aclAction = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getAclAction();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex) {
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleId();
            this._ruleName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleName();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField) {
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getVlanId();
            this._srcIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getSrcIp();
            this._dstIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getDstIp();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField] \n" +
              "but was: " + arg
            );
        }
    }

    public AclAction getAclAction() {
        return _aclAction;
    }
    
    public AclLayer getAclLayer() {
        return _aclLayer;
    }
    
    public java.lang.String getDstIp() {
        return _dstIp;
    }
    
    public java.lang.Long getNodeId() {
        return _nodeId;
    }
    
    public List<java.lang.Short> getPortList() {
        return _portList;
    }
    
    public java.lang.Integer getProfileId() {
        return _profileId;
    }
    
    public java.lang.String getProfileName() {
        return _profileName;
    }
    
    public java.lang.Integer getRuleId() {
        return _ruleId;
    }
    
    public java.lang.String getRuleName() {
        return _ruleName;
    }
    
    public java.lang.String getSrcIp() {
        return _srcIp;
    }
    
    public java.lang.Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetAclRuleInputBuilder setAclAction(AclAction value) {
        this._aclAction = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setAclLayer(AclLayer value) {
        this._aclLayer = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setDstIp(java.lang.String value) {
        this._dstIp = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setPortList(List<java.lang.Short> value) {
        this._portList = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setProfileId(java.lang.Integer value) {
        this._profileId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setProfileName(java.lang.String value) {
        this._profileName = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setRuleId(java.lang.Integer value) {
        this._ruleId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setRuleName(java.lang.String value) {
        this._ruleName = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setSrcIp(java.lang.String value) {
        this._srcIp = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setVlanId(java.lang.Integer value) {
        this._vlanId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetAclRuleInput build() {
        return new SetAclRuleInputImpl(this);
    }

    private static final class SetAclRuleInputImpl implements SetAclRuleInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput.class;
        }

        private final AclAction _aclAction;
        private final AclLayer _aclLayer;
        private final java.lang.String _dstIp;
        private final java.lang.Long _nodeId;
        private final List<java.lang.Short> _portList;
        private final java.lang.Integer _profileId;
        private final java.lang.String _profileName;
        private final java.lang.Integer _ruleId;
        private final java.lang.String _ruleName;
        private final java.lang.String _srcIp;
        private final java.lang.Integer _vlanId;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentation = new HashMap<>();

        private SetAclRuleInputImpl(SetAclRuleInputBuilder base) {
            this._aclAction = base.getAclAction();
            this._aclLayer = base.getAclLayer();
            this._dstIp = base.getDstIp();
            this._nodeId = base.getNodeId();
            this._portList = base.getPortList();
            this._profileId = base.getProfileId();
            this._profileName = base.getProfileName();
            this._ruleId = base.getRuleId();
            this._ruleName = base.getRuleName();
            this._srcIp = base.getSrcIp();
            this._vlanId = base.getVlanId();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public AclAction getAclAction() {
            return _aclAction;
        }
        
        @Override
        public AclLayer getAclLayer() {
            return _aclLayer;
        }
        
        @Override
        public java.lang.String getDstIp() {
            return _dstIp;
        }
        
        @Override
        public java.lang.Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public List<java.lang.Short> getPortList() {
            return _portList;
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
        public java.lang.Integer getRuleId() {
            return _ruleId;
        }
        
        @Override
        public java.lang.String getRuleName() {
            return _ruleName;
        }
        
        @Override
        public java.lang.String getSrcIp() {
            return _srcIp;
        }
        
        @Override
        public java.lang.Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_aclAction == null) ? 0 : _aclAction.hashCode());
            result = prime * result + ((_aclLayer == null) ? 0 : _aclLayer.hashCode());
            result = prime * result + ((_dstIp == null) ? 0 : _dstIp.hashCode());
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
            result = prime * result + ((_portList == null) ? 0 : _portList.hashCode());
            result = prime * result + ((_profileId == null) ? 0 : _profileId.hashCode());
            result = prime * result + ((_profileName == null) ? 0 : _profileName.hashCode());
            result = prime * result + ((_ruleId == null) ? 0 : _ruleId.hashCode());
            result = prime * result + ((_ruleName == null) ? 0 : _ruleName.hashCode());
            result = prime * result + ((_srcIp == null) ? 0 : _srcIp.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput)obj;
            if (_aclAction == null) {
                if (other.getAclAction() != null) {
                    return false;
                }
            } else if(!_aclAction.equals(other.getAclAction())) {
                return false;
            }
            if (_aclLayer == null) {
                if (other.getAclLayer() != null) {
                    return false;
                }
            } else if(!_aclLayer.equals(other.getAclLayer())) {
                return false;
            }
            if (_dstIp == null) {
                if (other.getDstIp() != null) {
                    return false;
                }
            } else if(!_dstIp.equals(other.getDstIp())) {
                return false;
            }
            if (_nodeId == null) {
                if (other.getNodeId() != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other.getNodeId())) {
                return false;
            }
            if (_portList == null) {
                if (other.getPortList() != null) {
                    return false;
                }
            } else if(!_portList.equals(other.getPortList())) {
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
            if (_ruleId == null) {
                if (other.getRuleId() != null) {
                    return false;
                }
            } else if(!_ruleId.equals(other.getRuleId())) {
                return false;
            }
            if (_ruleName == null) {
                if (other.getRuleName() != null) {
                    return false;
                }
            } else if(!_ruleName.equals(other.getRuleName())) {
                return false;
            }
            if (_srcIp == null) {
                if (other.getSrcIp() != null) {
                    return false;
                }
            } else if(!_srcIp.equals(other.getSrcIp())) {
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
                SetAclRuleInputImpl otherImpl = (SetAclRuleInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("SetAclRuleInput [");
            boolean first = true;
        
            if (_aclAction != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_aclAction=");
                builder.append(_aclAction);
             }
            if (_aclLayer != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_aclLayer=");
                builder.append(_aclLayer);
             }
            if (_dstIp != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_dstIp=");
                builder.append(_dstIp);
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
            if (_portList != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_portList=");
                builder.append(_portList);
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
            if (_ruleId != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_ruleId=");
                builder.append(_ruleId);
             }
            if (_ruleName != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_ruleName=");
                builder.append(_ruleName);
             }
            if (_srcIp != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_srcIp=");
                builder.append(_srcIp);
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
