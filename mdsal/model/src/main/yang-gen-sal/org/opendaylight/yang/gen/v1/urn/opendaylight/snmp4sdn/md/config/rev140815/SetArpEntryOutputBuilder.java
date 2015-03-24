package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetArpEntryOutputBuilder {

    private Result _setArpEntryResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>> augmentation = new HashMap<>();

    public SetArpEntryOutputBuilder() {
    } 


    public Result getSetArpEntryResult() {
        return _setArpEntryResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetArpEntryOutputBuilder setSetArpEntryResult(Result value) {
    
        this._setArpEntryResult = value;
        return this;
    }
    
    public SetArpEntryOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetArpEntryOutput build() {
        return new SetArpEntryOutputImpl(this);
    }

    private static final class SetArpEntryOutputImpl implements SetArpEntryOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput.class;
        }

        private final Result _setArpEntryResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>> augmentation = new HashMap<>();

        private SetArpEntryOutputImpl(SetArpEntryOutputBuilder builder) {
            this._setArpEntryResult = builder.getSetArpEntryResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getSetArpEntryResult() {
            return _setArpEntryResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetArpEntryOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_setArpEntryResult == null) ? 0 : _setArpEntryResult.hashCode());
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
            SetArpEntryOutputImpl other = (SetArpEntryOutputImpl) obj;
            if (_setArpEntryResult == null) {
                if (other._setArpEntryResult != null) {
                    return false;
                }
            } else if(!_setArpEntryResult.equals(other._setArpEntryResult)) {
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
            builder.append("SetArpEntryOutput [_setArpEntryResult=");
            builder.append(_setArpEntryResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
