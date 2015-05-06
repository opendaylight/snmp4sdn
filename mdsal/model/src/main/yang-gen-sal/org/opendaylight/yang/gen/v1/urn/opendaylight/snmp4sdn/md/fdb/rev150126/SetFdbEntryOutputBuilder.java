package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetFdbEntryOutputBuilder {

    private Result _setFdbEntryResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>> augmentation = new HashMap<>();

    public SetFdbEntryOutputBuilder() {
    } 


    public Result getSetFdbEntryResult() {
        return _setFdbEntryResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetFdbEntryOutputBuilder setSetFdbEntryResult(Result value) {
    
        this._setFdbEntryResult = value;
        return this;
    }
    
    public SetFdbEntryOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetFdbEntryOutput build() {
        return new SetFdbEntryOutputImpl(this);
    }

    private static final class SetFdbEntryOutputImpl implements SetFdbEntryOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput.class;
        }

        private final Result _setFdbEntryResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>> augmentation = new HashMap<>();

        private SetFdbEntryOutputImpl(SetFdbEntryOutputBuilder builder) {
            this._setFdbEntryResult = builder.getSetFdbEntryResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getSetFdbEntryResult() {
            return _setFdbEntryResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.SetFdbEntryOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_setFdbEntryResult == null) ? 0 : _setFdbEntryResult.hashCode());
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
            SetFdbEntryOutputImpl other = (SetFdbEntryOutputImpl) obj;
            if (_setFdbEntryResult == null) {
                if (other._setFdbEntryResult != null) {
                    return false;
                }
            } else if(!_setFdbEntryResult.equals(other._setFdbEntryResult)) {
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
            builder.append("SetFdbEntryOutput [_setFdbEntryResult=");
            builder.append(_setFdbEntryResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
