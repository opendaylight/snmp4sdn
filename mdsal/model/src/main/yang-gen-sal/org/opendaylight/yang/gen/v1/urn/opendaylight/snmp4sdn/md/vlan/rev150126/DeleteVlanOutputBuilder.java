package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DeleteVlanOutputBuilder {

    private Result _deleteVlanResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>> augmentation = new HashMap<>();

    public DeleteVlanOutputBuilder() {
    } 


    public Result getDeleteVlanResult() {
        return _deleteVlanResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DeleteVlanOutputBuilder setDeleteVlanResult(Result value) {
    
        this._deleteVlanResult = value;
        return this;
    }
    
    public DeleteVlanOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DeleteVlanOutput build() {
        return new DeleteVlanOutputImpl(this);
    }

    private static final class DeleteVlanOutputImpl implements DeleteVlanOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput.class;
        }

        private final Result _deleteVlanResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>> augmentation = new HashMap<>();

        private DeleteVlanOutputImpl(DeleteVlanOutputBuilder builder) {
            this._deleteVlanResult = builder.getDeleteVlanResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getDeleteVlanResult() {
            return _deleteVlanResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.DeleteVlanOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_deleteVlanResult == null) ? 0 : _deleteVlanResult.hashCode());
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
            DeleteVlanOutputImpl other = (DeleteVlanOutputImpl) obj;
            if (_deleteVlanResult == null) {
                if (other._deleteVlanResult != null) {
                    return false;
                }
            } else if(!_deleteVlanResult.equals(other._deleteVlanResult)) {
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
            builder.append("DeleteVlanOutput [_deleteVlanResult=");
            builder.append(_deleteVlanResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
