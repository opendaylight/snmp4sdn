package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetAclRuleOutputBuilder {

    private Result _setAclRuleResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>> augmentation = new HashMap<>();

    public SetAclRuleOutputBuilder() {
    } 


    public Result getSetAclRuleResult() {
        return _setAclRuleResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetAclRuleOutputBuilder setSetAclRuleResult(Result value) {
    
        this._setAclRuleResult = value;
        return this;
    }
    
    public SetAclRuleOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetAclRuleOutput build() {
        return new SetAclRuleOutputImpl(this);
    }

    private static final class SetAclRuleOutputImpl implements SetAclRuleOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput.class;
        }

        private final Result _setAclRuleResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>> augmentation = new HashMap<>();

        private SetAclRuleOutputImpl(SetAclRuleOutputBuilder builder) {
            this._setAclRuleResult = builder.getSetAclRuleResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getSetAclRuleResult() {
            return _setAclRuleResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.SetAclRuleOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_setAclRuleResult == null) ? 0 : _setAclRuleResult.hashCode());
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
            SetAclRuleOutputImpl other = (SetAclRuleOutputImpl) obj;
            if (_setAclRuleResult == null) {
                if (other._setAclRuleResult != null) {
                    return false;
                }
            } else if(!_setAclRuleResult.equals(other._setAclRuleResult)) {
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
            builder.append("SetAclRuleOutput [_setAclRuleResult=");
            builder.append(_setAclRuleResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
