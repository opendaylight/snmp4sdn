package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class EnableStpOutputBuilder {

    private Result _enableStpResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>> augmentation = new HashMap<>();

    public EnableStpOutputBuilder() {
    } 


    public Result getEnableStpResult() {
        return _enableStpResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public EnableStpOutputBuilder setEnableStpResult(Result value) {
    
        this._enableStpResult = value;
        return this;
    }
    
    public EnableStpOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public EnableStpOutput build() {
        return new EnableStpOutputImpl(this);
    }

    private static final class EnableStpOutputImpl implements EnableStpOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput.class;
        }

        private final Result _enableStpResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>> augmentation = new HashMap<>();

        private EnableStpOutputImpl(EnableStpOutputBuilder builder) {
            this._enableStpResult = builder.getEnableStpResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getEnableStpResult() {
            return _enableStpResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.EnableStpOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_enableStpResult == null) ? 0 : _enableStpResult.hashCode());
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
            EnableStpOutputImpl other = (EnableStpOutputImpl) obj;
            if (_enableStpResult == null) {
                if (other._enableStpResult != null) {
                    return false;
                }
            } else if(!_enableStpResult.equals(other._enableStpResult)) {
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
            builder.append("EnableStpOutput [_enableStpResult=");
            builder.append(_enableStpResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
