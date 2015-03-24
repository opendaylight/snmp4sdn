package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput
 */
public class AddAclRuleInputBuilder {

    private AclAction _aclAction;
    private java.lang.Integer _ctrlVid;
    private java.lang.String _dstIp;
    private java.lang.Long _nodeId;
    private List<java.lang.Short> _portList;
    private java.lang.Integer _profileId;
    private java.lang.String _profileName;
    private java.lang.Integer _ruleId;
    private java.lang.String _ruleName;
    private java.lang.String _srcIp;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> augmentation = new HashMap<>();

    public AddAclRuleInputBuilder() {
    } 
    
    public AddAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule arg) {
        this._portList = arg.getPortList();
        this._aclAction = arg.getAclAction();
        this._srcIp = arg.getSrcIp();
        this._dstIp = arg.getDstIp();
        this._ctrlVid = arg.getCtrlVid();
    }
    
    public AddAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField arg) {
        this._srcIp = arg.getSrcIp();
        this._dstIp = arg.getDstIp();
        this._ctrlVid = arg.getCtrlVid();
    }
    
    public AddAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex arg) {
        this._profileId = arg.getProfileId();
        this._ruleId = arg.getRuleId();
        this._profileName = arg.getProfileName();
        this._ruleName = arg.getRuleName();
    }

    public AddAclRuleInputBuilder(AddAclRuleInput base) {
        this._aclAction = base.getAclAction();
        this._ctrlVid = base.getCtrlVid();
        this._dstIp = base.getDstIp();
        this._nodeId = base.getNodeId();
        this._portList = base.getPortList();
        this._profileId = base.getProfileId();
        this._profileName = base.getProfileName();
        this._ruleId = base.getRuleId();
        this._ruleName = base.getRuleName();
        this._srcIp = base.getSrcIp();
        if (base instanceof AddAclRuleInputImpl) {
            AddAclRuleInputImpl _impl = (AddAclRuleInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule) {
            this._portList = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getPortList();
            this._aclAction = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule)arg).getAclAction();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField) {
            this._srcIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getSrcIp();
            this._dstIp = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getDstIp();
            this._ctrlVid = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField)arg).getCtrlVid();
            isValidArg = true;
        }
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex) {
            this._profileId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getProfileId();
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getRuleId();
            this._profileName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getProfileName();
            this._ruleName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getRuleName();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRule, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex] \n" +
              "but was: " + arg
            );
        }
    }

    public AclAction getAclAction() {
        return _aclAction;
    }
    
    public java.lang.Integer getCtrlVid() {
        return _ctrlVid;
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
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddAclRuleInputBuilder setAclAction(AclAction value) {
        this._aclAction = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setCtrlVid(java.lang.Integer value) {
        this._ctrlVid = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setDstIp(java.lang.String value) {
        this._dstIp = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setPortList(List<java.lang.Short> value) {
        this._portList = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setProfileId(java.lang.Integer value) {
        this._profileId = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setProfileName(java.lang.String value) {
        this._profileName = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setRuleId(java.lang.Integer value) {
        this._ruleId = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setRuleName(java.lang.String value) {
        this._ruleName = value;
        return this;
    }
    
    public AddAclRuleInputBuilder setSrcIp(java.lang.String value) {
        this._srcIp = value;
        return this;
    }
    
    public AddAclRuleInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddAclRuleInput build() {
        return new AddAclRuleInputImpl(this);
    }

    private static final class AddAclRuleInputImpl implements AddAclRuleInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput.class;
        }

        private final AclAction _aclAction;
        private final java.lang.Integer _ctrlVid;
        private final java.lang.String _dstIp;
        private final java.lang.Long _nodeId;
        private final List<java.lang.Short> _portList;
        private final java.lang.Integer _profileId;
        private final java.lang.String _profileName;
        private final java.lang.Integer _ruleId;
        private final java.lang.String _ruleName;
        private final java.lang.String _srcIp;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> augmentation = new HashMap<>();

        private AddAclRuleInputImpl(AddAclRuleInputBuilder base) {
            this._aclAction = base.getAclAction();
            this._ctrlVid = base.getCtrlVid();
            this._dstIp = base.getDstIp();
            this._nodeId = base.getNodeId();
            this._portList = base.getPortList();
            this._profileId = base.getProfileId();
            this._profileName = base.getProfileName();
            this._ruleId = base.getRuleId();
            this._ruleName = base.getRuleName();
            this._srcIp = base.getSrcIp();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>singletonMap(e.getKey(), e.getValue());       
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
        public java.lang.Integer getCtrlVid() {
            return _ctrlVid;
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
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_ctrlVid == null) ? 0 : _ctrlVid.hashCode());
            result = prime * result + ((_dstIp == null) ? 0 : _dstIp.hashCode());
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
            result = prime * result + ((_portList == null) ? 0 : _portList.hashCode());
            result = prime * result + ((_profileId == null) ? 0 : _profileId.hashCode());
            result = prime * result + ((_profileName == null) ? 0 : _profileName.hashCode());
            result = prime * result + ((_ruleId == null) ? 0 : _ruleId.hashCode());
            result = prime * result + ((_ruleName == null) ? 0 : _ruleName.hashCode());
            result = prime * result + ((_srcIp == null) ? 0 : _srcIp.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput)obj;
            if (_aclAction == null) {
                if (other.getAclAction() != null) {
                    return false;
                }
            } else if(!_aclAction.equals(other.getAclAction())) {
                return false;
            }
            if (_ctrlVid == null) {
                if (other.getCtrlVid() != null) {
                    return false;
                }
            } else if(!_ctrlVid.equals(other.getCtrlVid())) {
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
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                AddAclRuleInputImpl otherImpl = (AddAclRuleInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("AddAclRuleInput [");
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
            if (_ctrlVid != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_ctrlVid=");
                builder.append(_ctrlVid);
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
