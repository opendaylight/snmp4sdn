package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import java.util.Collections;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.DataObject;
import java.util.HashMap;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.get.acl.index.on._switch.output.AclIndex;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.Augmentation;


/**
 * Class that builds {@link org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput} instances.
 * 
 * @see org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput
 */
public class GetAclIndexOnSwitchOutputBuilder {

    private List<AclIndex> _aclIndex;

    Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> augmentation = new HashMap<>();

    public GetAclIndexOnSwitchOutputBuilder() {
    } 

    public GetAclIndexOnSwitchOutputBuilder(GetAclIndexOnSwitchOutput base) {
        this._aclIndex = base.getAclIndex();
        if (base instanceof GetAclIndexOnSwitchOutputImpl) {
            GetAclIndexOnSwitchOutputImpl _impl = (GetAclIndexOnSwitchOutputImpl) base;
            this.augmentation = new HashMap<>(_impl.augmentation);
        }
    }


    public List<AclIndex> getAclIndex() {
        return _aclIndex;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetAclIndexOnSwitchOutputBuilder setAclIndex(List<AclIndex> value) {
        this._aclIndex = value;
        return this;
    }
    
    public GetAclIndexOnSwitchOutputBuilder addAugmentation(java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetAclIndexOnSwitchOutput build() {
        return new GetAclIndexOnSwitchOutputImpl(this);
    }

    private static final class GetAclIndexOnSwitchOutputImpl implements GetAclIndexOnSwitchOutput {

        public java.lang.Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput.class;
        }

        private final List<AclIndex> _aclIndex;

        private Map<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> augmentation = new HashMap<>();

        private GetAclIndexOnSwitchOutputImpl(GetAclIndexOnSwitchOutputBuilder base) {
            this._aclIndex = base.getAclIndex();
                switch (base.augmentation.size()) {
                case 0:
                    this.augmentation = Collections.emptyMap();
                    break;
                    case 1:
                        final Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> e = base.augmentation.entrySet().iterator().next();
                        this.augmentation = Collections.<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>singletonMap(e.getKey(), e.getValue());       
                    break;
                default :
                    this.augmentation = new HashMap<>(base.augmentation);
                }
        }

        @Override
        public List<AclIndex> getAclIndex() {
            return _aclIndex;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> E getAugmentation(java.lang.Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_aclIndex == null) ? 0 : _aclIndex.hashCode());
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
            if (!org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput.class.equals(((DataObject)obj).getImplementedInterface())) {
                return false;
            }
            org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput other = (org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput)obj;
            if (_aclIndex == null) {
                if (other.getAclIndex() != null) {
                    return false;
                }
            } else if(!_aclIndex.equals(other.getAclIndex())) {
                return false;
            }
            if (getClass() == obj.getClass()) {
                // Simple case: we are comparing against self
                GetAclIndexOnSwitchOutputImpl otherImpl = (GetAclIndexOnSwitchOutputImpl) obj;
                if (augmentation == null) {
                    if (otherImpl.augmentation != null) {
                        return false;
                    }
                } else if(!augmentation.equals(otherImpl.augmentation)) {
                    return false;
                }
            } else {
                // Hard case: compare our augments with presence there...
                for (Map.Entry<java.lang.Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.GetAclIndexOnSwitchOutput>> e : augmentation.entrySet()) {
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
            java.lang.StringBuilder builder = new java.lang.StringBuilder ("GetAclIndexOnSwitchOutput [");
            boolean first = true;
        
            if (_aclIndex != null) {
                if (first) {
                    first = false;
                } else {
                    builder.append(", ");
                }
                builder.append("_aclIndex=");
                builder.append(_aclIndex);
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
