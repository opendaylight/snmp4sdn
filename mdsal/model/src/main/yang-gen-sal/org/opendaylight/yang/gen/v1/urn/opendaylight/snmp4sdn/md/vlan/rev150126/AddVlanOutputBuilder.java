package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class AddVlanOutputBuilder {

    private Result _addVlanResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>> augmentation = new HashMap<>();

    public AddVlanOutputBuilder() {
    } 


    public Result getAddVlanResult() {
        return _addVlanResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public AddVlanOutputBuilder setAddVlanResult(Result value) {
    
        this._addVlanResult = value;
        return this;
    }
    
    public AddVlanOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public AddVlanOutput build() {
        return new AddVlanOutputImpl(this);
    }

    private static final class AddVlanOutputImpl implements AddVlanOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput.class;
        }

        private final Result _addVlanResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>> augmentation = new HashMap<>();

        private AddVlanOutputImpl(AddVlanOutputBuilder builder) {
            this._addVlanResult = builder.getAddVlanResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getAddVlanResult() {
            return _addVlanResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.AddVlanOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_addVlanResult == null) ? 0 : _addVlanResult.hashCode());
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
            AddVlanOutputImpl other = (AddVlanOutputImpl) obj;
            if (_addVlanResult == null) {
                if (other._addVlanResult != null) {
                    return false;
                }
            } else if(!_addVlanResult.equals(other._addVlanResult)) {
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
            builder.append("AddVlanOutput [_addVlanResult=");
            builder.append(_addVlanResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
