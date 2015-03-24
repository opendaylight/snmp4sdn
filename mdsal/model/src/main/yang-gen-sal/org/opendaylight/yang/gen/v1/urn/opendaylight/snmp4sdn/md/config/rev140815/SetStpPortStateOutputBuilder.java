package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetStpPortStateOutputBuilder {

    private Result _setStpPortStateResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>> augmentation = new HashMap<>();

    public SetStpPortStateOutputBuilder() {
    } 


    public Result getSetStpPortStateResult() {
        return _setStpPortStateResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetStpPortStateOutputBuilder setSetStpPortStateResult(Result value) {
    
        this._setStpPortStateResult = value;
        return this;
    }
    
    public SetStpPortStateOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetStpPortStateOutput build() {
        return new SetStpPortStateOutputImpl(this);
    }

    private static final class SetStpPortStateOutputImpl implements SetStpPortStateOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput.class;
        }

        private final Result _setStpPortStateResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>> augmentation = new HashMap<>();

        private SetStpPortStateOutputImpl(SetStpPortStateOutputBuilder builder) {
            this._setStpPortStateResult = builder.getSetStpPortStateResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getSetStpPortStateResult() {
            return _setStpPortStateResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_setStpPortStateResult == null) ? 0 : _setStpPortStateResult.hashCode());
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
            SetStpPortStateOutputImpl other = (SetStpPortStateOutputImpl) obj;
            if (_setStpPortStateResult == null) {
                if (other._setStpPortStateResult != null) {
                    return false;
                }
            } else if(!_setStpPortStateResult.equals(other._setStpPortStateResult)) {
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
            builder.append("SetStpPortStateOutput [_setStpPortStateResult=");
            builder.append(_setStpPortStateResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
