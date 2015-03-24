package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.DataObject;



public class DelAclRuleInputBuilder {

    private Long _nodeId;
    private Integer _ruleId;
    private String _ruleName;
    private Integer _profileId;
    private String _profileName;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentation = new HashMap<>();

    public DelAclRuleInputBuilder() {
    } 
    
    
    public DelAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex arg) {
        this._ruleId = arg.getRuleId();
        this._ruleName = arg.getRuleName();
    }
    
    public DelAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex arg) {
        this._profileId = arg.getProfileId();
        this._profileName = arg.getProfileName();
    }

    /**
     Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex</li>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex</li>
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
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex) {
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleId();
            this._ruleName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex)arg).getRuleName();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclProfileIndex, org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclRuleIndex] \n" +
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
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelAclRuleInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setRuleId(Integer value) {
    
        this._ruleId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setRuleName(String value) {
    
        this._ruleName = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setProfileId(Integer value) {
    
        this._profileId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setProfileName(String value) {
    
        this._profileName = value;
        return this;
    }
    
    public DelAclRuleInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelAclRuleInput build() {
        return new DelAclRuleInputImpl(this);
    }

    private static final class DelAclRuleInputImpl implements DelAclRuleInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput.class;
        }

        private final Long _nodeId;
        private final Integer _ruleId;
        private final String _ruleName;
        private final Integer _profileId;
        private final String _profileName;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentation = new HashMap<>();

        private DelAclRuleInputImpl(DelAclRuleInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._ruleId = builder.getRuleId();
            this._ruleName = builder.getRuleName();
            this._profileId = builder.getProfileId();
            this._profileName = builder.getProfileName();
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
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> E getAugmentation(Class<E> augmentationType) {
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
            DelAclRuleInputImpl other = (DelAclRuleInputImpl) obj;
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
            builder.append("DelAclRuleInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _ruleId=");
            builder.append(_ruleId);
            builder.append(", _ruleName=");
            builder.append(_ruleName);
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
