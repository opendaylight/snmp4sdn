package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.Result;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput
 */
public class AddAclRuleOutputBuilder {

    private Result _addAclRuleResult;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> augmentation = new HashMap<>();

    public AddAclRuleOutputBuilder() {
    } 

    public AddAclRuleOutputBuilder(AddAclRuleOutput base) {
        this._addAclRuleResult = base.getAddAclRuleResult();
        if (base instanceof AddAclRuleOutputImpl) {
            AddAclRuleOutputImpl _impl = (AddAclRuleOutputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public Result getAddAclRuleResult() {
        return _addAclRuleResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddAclRuleOutputBuilder setAddAclRuleResult(Result value) {
        this._addAclRuleResult = value;
        return this;
    }
    
    public AddAclRuleOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddAclRuleOutput build() {
        return new AddAclRuleOutputImpl(this);
    }

    private static final class AddAclRuleOutputImpl implements AddAclRuleOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput.class;
        }

        private final Result _addAclRuleResult;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> augmentation = new HashMap<>();

        private AddAclRuleOutputImpl(AddAclRuleOutputBuilder base) {
            this._addAclRuleResult = base.getAddAclRuleResult();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public Result getAddAclRuleResult() {
            return _addAclRuleResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_addAclRuleResult == null) ? 0 : _addAclRuleResult.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput)obj;
            if (_addAclRuleResult == null) {
                if (other.getAddAclRuleResult() != null) {
                    return false;
                }
            } else if(!_addAclRuleResult.equals(other.getAddAclRuleResult())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                AddAclRuleOutputImpl otherImpl = (AddAclRuleOutputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AddAclRuleOutput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("AddAclRuleOutput [");
            boolean first = true;
        
            if (_addAclRuleResult != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_addAclRuleResult=");
                builder.append(_addAclRuleResult);
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
