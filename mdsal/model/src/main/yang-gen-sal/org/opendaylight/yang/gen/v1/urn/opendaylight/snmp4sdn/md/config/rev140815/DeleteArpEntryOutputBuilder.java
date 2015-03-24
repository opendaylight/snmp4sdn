package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DeleteArpEntryOutputBuilder {

    private Result _deleteArpEntryResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>> augmentation = new HashMap<>();

    public DeleteArpEntryOutputBuilder() {
    } 


    public Result getDeleteArpEntryResult() {
        return _deleteArpEntryResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DeleteArpEntryOutputBuilder setDeleteArpEntryResult(Result value) {
    
        this._deleteArpEntryResult = value;
        return this;
    }
    
    public DeleteArpEntryOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DeleteArpEntryOutput build() {
        return new DeleteArpEntryOutputImpl(this);
    }

    private static final class DeleteArpEntryOutputImpl implements DeleteArpEntryOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput.class;
        }

        private final Result _deleteArpEntryResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>> augmentation = new HashMap<>();

        private DeleteArpEntryOutputImpl(DeleteArpEntryOutputBuilder builder) {
            this._deleteArpEntryResult = builder.getDeleteArpEntryResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getDeleteArpEntryResult() {
            return _deleteArpEntryResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.DeleteArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_deleteArpEntryResult == null) ? 0 : _deleteArpEntryResult.hashCode());
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
            DeleteArpEntryOutputImpl other = (DeleteArpEntryOutputImpl) obj;
            if (_deleteArpEntryResult == null) {
                if (other._deleteArpEntryResult != null) {
                    return false;
                }
            } else if(!_deleteArpEntryResult.equals(other._deleteArpEntryResult)) {
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
            builder.append("DeleteArpEntryOutput [_deleteArpEntryResult=");
            builder.append(_deleteArpEntryResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
