package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class ClearAclTableOutputBuilder {

    private Result _clearAclTableResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>> augmentation = new HashMap<>();

    public ClearAclTableOutputBuilder() {
    } 


    public Result getClearAclTableResult() {
        return _clearAclTableResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public ClearAclTableOutputBuilder setClearAclTableResult(Result value) {
    
        this._clearAclTableResult = value;
        return this;
    }
    
    public ClearAclTableOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public ClearAclTableOutput build() {
        return new ClearAclTableOutputImpl(this);
    }

    private static final class ClearAclTableOutputImpl implements ClearAclTableOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput.class;
        }

        private final Result _clearAclTableResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>> augmentation = new HashMap<>();

        private ClearAclTableOutputImpl(ClearAclTableOutputBuilder builder) {
            this._clearAclTableResult = builder.getClearAclTableResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getClearAclTableResult() {
            return _clearAclTableResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.ClearAclTableOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_clearAclTableResult == null) ? 0 : _clearAclTableResult.hashCode());
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
            ClearAclTableOutputImpl other = (ClearAclTableOutputImpl) obj;
            if (_clearAclTableResult == null) {
                if (other._clearAclTableResult != null) {
                    return false;
                }
            } else if(!_clearAclTableResult.equals(other._clearAclTableResult)) {
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
            builder.append("ClearAclTableOutput [_clearAclTableResult=");
            builder.append(_clearAclTableResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
