package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DelAclRuleOutputBuilder {

    private Result _delAclRuleResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>> augmentation = new HashMap<>();

    public DelAclRuleOutputBuilder() {
    } 


    public Result getDelAclRuleResult() {
        return _delAclRuleResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelAclRuleOutputBuilder setDelAclRuleResult(Result value) {
    
        this._delAclRuleResult = value;
        return this;
    }
    
    public DelAclRuleOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelAclRuleOutput build() {
        return new DelAclRuleOutputImpl(this);
    }

    private static final class DelAclRuleOutputImpl implements DelAclRuleOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput.class;
        }

        private final Result _delAclRuleResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>> augmentation = new HashMap<>();

        private DelAclRuleOutputImpl(DelAclRuleOutputBuilder builder) {
            this._delAclRuleResult = builder.getDelAclRuleResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getDelAclRuleResult() {
            return _delAclRuleResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.DelAclRuleOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_delAclRuleResult == null) ? 0 : _delAclRuleResult.hashCode());
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
            DelAclRuleOutputImpl other = (DelAclRuleOutputImpl) obj;
            if (_delAclRuleResult == null) {
                if (other._delAclRuleResult != null) {
                    return false;
                }
            } else if(!_delAclRuleResult.equals(other._delAclRuleResult)) {
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
            builder.append("DelAclRuleOutput [_delAclRuleResult=");
            builder.append(_delAclRuleResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
