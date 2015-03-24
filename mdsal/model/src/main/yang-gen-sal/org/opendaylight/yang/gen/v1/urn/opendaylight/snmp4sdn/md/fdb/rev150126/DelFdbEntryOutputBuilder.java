package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class DelFdbEntryOutputBuilder {

    private Result _delFdbEntryResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>> augmentation = new HashMap<>();

    public DelFdbEntryOutputBuilder() {
    } 


    public Result getDelFdbEntryResult() {
        return _delFdbEntryResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public DelFdbEntryOutputBuilder setDelFdbEntryResult(Result value) {
    
        this._delFdbEntryResult = value;
        return this;
    }
    
    public DelFdbEntryOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public DelFdbEntryOutput build() {
        return new DelFdbEntryOutputImpl(this);
    }

    private static final class DelFdbEntryOutputImpl implements DelFdbEntryOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput.class;
        }

        private final Result _delFdbEntryResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>> augmentation = new HashMap<>();

        private DelFdbEntryOutputImpl(DelFdbEntryOutputBuilder builder) {
            this._delFdbEntryResult = builder.getDelFdbEntryResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getDelFdbEntryResult() {
            return _delFdbEntryResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.DelFdbEntryOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_delFdbEntryResult == null) ? 0 : _delFdbEntryResult.hashCode());
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
            DelFdbEntryOutputImpl other = (DelFdbEntryOutputImpl) obj;
            if (_delFdbEntryResult == null) {
                if (other._delFdbEntryResult != null) {
                    return false;
                }
            } else if(!_delFdbEntryResult.equals(other._delFdbEntryResult)) {
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
            builder.append("DelFdbEntryOutput [_delFdbEntryResult=");
            builder.append(_delFdbEntryResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
