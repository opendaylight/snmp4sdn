package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.StpPortState;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetStpPortStateOutputBuilder {

    private StpPortState _stpPortState;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>> augmentation = new HashMap<>();

    public GetStpPortStateOutputBuilder() {
    } 


    public StpPortState getStpPortState() {
        return _stpPortState;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetStpPortStateOutputBuilder setStpPortState(StpPortState value) {
    
        this._stpPortState = value;
        return this;
    }
    
    public GetStpPortStateOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetStpPortStateOutput build() {
        return new GetStpPortStateOutputImpl(this);
    }

    private static final class GetStpPortStateOutputImpl implements GetStpPortStateOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput.class;
        }

        private final StpPortState _stpPortState;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>> augmentation = new HashMap<>();

        private GetStpPortStateOutputImpl(GetStpPortStateOutputBuilder builder) {
            this._stpPortState = builder.getStpPortState();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public StpPortState getStpPortState() {
            return _stpPortState;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_stpPortState == null) ? 0 : _stpPortState.hashCode());
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
            GetStpPortStateOutputImpl other = (GetStpPortStateOutputImpl) obj;
            if (_stpPortState == null) {
                if (other._stpPortState != null) {
                    return false;
                }
            } else if(!_stpPortState.equals(other._stpPortState)) {
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
            builder.append("GetStpPortStateOutput [_stpPortState=");
            builder.append(_stpPortState);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
