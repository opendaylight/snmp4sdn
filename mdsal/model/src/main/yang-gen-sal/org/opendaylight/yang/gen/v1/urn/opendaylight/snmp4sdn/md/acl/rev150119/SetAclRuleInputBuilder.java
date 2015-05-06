package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import java.util.List;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class SetAclRuleInputBuilder {

    private Long _nodeId;
    private Integer _ruleId;
    private String _ruleName;
    private Integer _profileId;
    private String _profileName;
    private AclAction _aclAction;
    private AclLayer _aclLayer;
    private List<Short> _portList;
    private String _dstIp;
    private String _srcIp;
    private Integer _vlanId;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentation = new HashMap<>();

    public SetAclRuleInputBuilder() {
    } 
    
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex arg) {
        this._ruleId = arg.getRuleId();
        this._ruleName = arg.getRuleName();
    }
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex arg) {
        this._profileId = arg.getProfileId();
        this._profileName = arg.getProfileName();
    }
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule arg) {
        this._aclAction = arg.getAclAction();
        this._aclLayer = arg.getAclLayer();
        this._portList = arg.getPortList();
        this._dstIp = arg.getDstIp();
        this._srcIp = arg.getSrcIp();
        this._vlanId = arg.getVlanId();
    }
    
    public SetAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField arg) {
        this._dstIp = arg.getDstIp();
        this._srcIp = arg.getSrcIp();
        this._vlanId = arg.getVlanId();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
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
            this._aclAction = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getAclAction();
            this._aclLayer = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getAclLayer();
            this._portList = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getPortList();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex) {
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleId();
            this._ruleName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleName();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField) {
            this._dstIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getDstIp();
            this._srcIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getSrcIp();
            this._vlanId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getVlanId();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField] \n" +
              "but was: " + arg
            );
        }
    }

    public Long getNodeId() {
        return _nodeId;
    }
    
    public Integer getRuleId() {
        return _ruleId;
    }
    
    public String getRuleName() {
        return _ruleName;
    }
    
    public Integer getProfileId() {
        return _profileId;
    }
    
    public String getProfileName() {
        return _profileName;
    }
    
    public AclAction getAclAction() {
        return _aclAction;
    }
    
    public AclLayer getAclLayer() {
        return _aclLayer;
    }
    
    public List<Short> getPortList() {
        return _portList;
    }
    
    public String getDstIp() {
        return _dstIp;
    }
    
    public String getSrcIp() {
        return _srcIp;
    }
    
    public Integer getVlanId() {
        return _vlanId;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetAclRuleInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setRuleId(Integer value) {
    
        this._ruleId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setRuleName(String value) {
    
        this._ruleName = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setProfileId(Integer value) {
    
        this._profileId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setProfileName(String value) {
    
        this._profileName = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setAclAction(AclAction value) {
    
        this._aclAction = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setAclLayer(AclLayer value) {
    
        this._aclLayer = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setPortList(List<Short> value) {
    
        this._portList = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setDstIp(String value) {
    
        this._dstIp = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setSrcIp(String value) {
    
        this._srcIp = value;
        return this;
    }
    
    public SetAclRuleInputBuilder setVlanId(Integer value) {
    
        this._vlanId = value;
        return this;
    }
    
    public SetAclRuleInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetAclRuleInput build() {
        return new SetAclRuleInputImpl(this);
    }

    private static final class SetAclRuleInputImpl implements SetAclRuleInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput.class;
        }

        private final Long _nodeId;
        private final Integer _ruleId;
        private final String _ruleName;
        private final Integer _profileId;
        private final String _profileName;
        private final AclAction _aclAction;
        private final AclLayer _aclLayer;
        private final List<Short> _portList;
        private final String _dstIp;
        private final String _srcIp;
        private final Integer _vlanId;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> augmentation = new HashMap<>();

        private SetAclRuleInputImpl(SetAclRuleInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._ruleId = builder.getRuleId();
            this._ruleName = builder.getRuleName();
            this._profileId = builder.getProfileId();
            this._profileName = builder.getProfileName();
            this._aclAction = builder.getAclAction();
            this._aclLayer = builder.getAclLayer();
            this._portList = builder.getPortList();
            this._dstIp = builder.getDstIp();
            this._srcIp = builder.getSrcIp();
            this._vlanId = builder.getVlanId();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public Integer getRuleId() {
            return _ruleId;
        }
        
        @Override
        public String getRuleName() {
            return _ruleName;
        }
        
        @Override
        public Integer getProfileId() {
            return _profileId;
        }
        
        @Override
        public String getProfileName() {
            return _profileName;
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
        public List<Short> getPortList() {
            return _portList;
        }
        
        @Override
        public String getDstIp() {
            return _dstIp;
        }
        
        @Override
        public String getSrcIp() {
            return _srcIp;
        }
        
        @Override
        public Integer getVlanId() {
            return _vlanId;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleInput>> E getAugmentation(Class<E> augmentationType) {
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
            result = prime * result + ((_ruleId == null) ? 0 : _ruleId.hashCode());
            result = prime * result + ((_ruleName == null) ? 0 : _ruleName.hashCode());
            result = prime * result + ((_profileId == null) ? 0 : _profileId.hashCode());
            result = prime * result + ((_profileName == null) ? 0 : _profileName.hashCode());
            result = prime * result + ((_aclAction == null) ? 0 : _aclAction.hashCode());
            result = prime * result + ((_aclLayer == null) ? 0 : _aclLayer.hashCode());
            result = prime * result + ((_portList == null) ? 0 : _portList.hashCode());
            result = prime * result + ((_dstIp == null) ? 0 : _dstIp.hashCode());
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
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            SetAclRuleInputImpl other = (SetAclRuleInputImpl) obj;
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
                return false;
            }
            if (_ruleId == null) {
                if (other._ruleId != null) {
                    return false;
                }
            } else if(!_ruleId.equals(other._ruleId)) {
                return false;
            }
            if (_ruleName == null) {
                if (other._ruleName != null) {
                    return false;
                }
            } else if(!_ruleName.equals(other._ruleName)) {
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
            if (_aclAction == null) {
                if (other._aclAction != null) {
                    return false;
                }
            } else if(!_aclAction.equals(other._aclAction)) {
                return false;
            }
            if (_aclLayer == null) {
                if (other._aclLayer != null) {
                    return false;
                }
            } else if(!_aclLayer.equals(other._aclLayer)) {
                return false;
            }
            if (_portList == null) {
                if (other._portList != null) {
                    return false;
                }
            } else if(!_portList.equals(other._portList)) {
                return false;
            }
            if (_dstIp == null) {
                if (other._dstIp != null) {
                    return false;
                }
            } else if(!_dstIp.equals(other._dstIp)) {
                return false;
            }
            if (_srcIp == null) {
                if (other._srcIp != null) {
                    return false;
                }
            } else if(!_srcIp.equals(other._srcIp)) {
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
            builder.append("SetAclRuleInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _ruleId=");
            builder.append(_ruleId);
            builder.append(", _ruleName=");
            builder.append(_ruleName);
            builder.append(", _profileId=");
            builder.append(_profileId);
            builder.append(", _profileName=");
            builder.append(_profileName);
            builder.append(", _aclAction=");
            builder.append(_aclAction);
            builder.append(", _aclLayer=");
            builder.append(_aclLayer);
            builder.append(", _portList=");
            builder.append(_portList);
            builder.append(", _dstIp=");
            builder.append(_dstIp);
            builder.append(", _srcIp=");
            builder.append(_srcIp);
            builder.append(", _vlanId=");
            builder.append(_vlanId);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
