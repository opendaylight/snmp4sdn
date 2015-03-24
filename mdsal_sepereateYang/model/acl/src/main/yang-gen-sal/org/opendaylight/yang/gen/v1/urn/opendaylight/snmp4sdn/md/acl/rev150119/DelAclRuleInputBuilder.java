package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput
 */
public class DelAclRuleInputBuilder {

    private java.lang.Long _nodeId;
    private java.lang.Integer _profileId;
    private java.lang.String _profileName;
    private java.lang.Integer _ruleId;
    private java.lang.String _ruleName;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentation = new HashMap<>();

    public DelAclRuleInputBuilder() {
    } 
    
    public DelAclRuleInputBuilder(org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex arg) {
        this._profileId = arg.getProfileId();
        this._ruleId = arg.getRuleId();
        this._profileName = arg.getProfileName();
        this._ruleName = arg.getRuleName();
    }

    public DelAclRuleInputBuilder(DelAclRuleInput base) {
        this._nodeId = base.getNodeId();
        this._profileId = base.getProfileId();
        this._profileName = base.getProfileName();
        this._ruleId = base.getRuleId();
        this._ruleName = base.getRuleName();
        if (base instanceof DelAclRuleInputImpl) {
            DelAclRuleInputImpl _impl = (DelAclRuleInputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }

    /**
     *Set fields from given grouping argument. Valid argument is instance of one of following types:
     * <ul>
     * <li>org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex</li>
     * </ul>
     *
     * @param arg grouping object
     * @throws IllegalArgumentException if given argument is none of valid types
    */
    public void fieldsFrom(DataObject arg) {
        boolean isValidArg = false;
        if (arg instanceof org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex) {
            this._profileId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getProfileId();
            this._ruleId = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getRuleId();
            this._profileName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getProfileName();
            this._ruleName = ((org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex)arg).getRuleName();
            isValidArg = true;
        }
        if (!isValidArg) {
            throw new IllegalArgumentException(
              "expected one of: [org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclIndex] \n" +
              "but was: " + arg
            );
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
    
    public java.lang.Integer getRuleId() {
        return _ruleId;
    }
    
    public java.lang.String getRuleName() {
        return _ruleName;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelAclRuleInputBuilder setNodeId(java.lang.Long value) {
        this._nodeId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setProfileId(java.lang.Integer value) {
        this._profileId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setProfileName(java.lang.String value) {
        this._profileName = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setRuleId(java.lang.Integer value) {
        this._ruleId = value;
        return this;
    }
    
    public DelAclRuleInputBuilder setRuleName(java.lang.String value) {
        this._ruleName = value;
        return this;
    }
    
    public DelAclRuleInputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelAclRuleInput build() {
        return new DelAclRuleInputImpl(this);
    }

    private static final class DelAclRuleInputImpl implements DelAclRuleInput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput.class;
        }

        private final java.lang.Long _nodeId;
        private final java.lang.Integer _profileId;
        private final java.lang.String _profileName;
        private final java.lang.Integer _ruleId;
        private final java.lang.String _ruleName;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> augmentation = new HashMap<>();

        private DelAclRuleInputImpl(DelAclRuleInputBuilder base) {
            this._nodeId = base.getNodeId();
            this._profileId = base.getProfileId();
            this._profileName = base.getProfileName();
            this._ruleId = base.getRuleId();
            this._ruleName = base.getRuleName();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>singletonMap(e.getKey(), e.getValue());       
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
        
        @Override
        public java.lang.Integer getRuleId() {
            return _ruleId;
        }
        
        @Override
        public java.lang.String getRuleName() {
            return _ruleName;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> E getAugmentation(java.lang.Class<E> augmentationType) {
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
            result = prime * result + ((_ruleId == null) ? 0 : _ruleId.hashCode());
            result = prime * result + ((_ruleName == null) ? 0 : _ruleName.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput)obj;
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
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                DelAclRuleInputImpl otherImpl = (DelAclRuleInputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleInput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("DelAclRuleInput [");
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
