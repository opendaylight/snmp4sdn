package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.types.rev150126.Result;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetVlanPortsOutputBuilder {

    private Result _setVlanPortsResult;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>> augmentation = new HashMap<>();

    public SetVlanPortsOutputBuilder() {
    } 


    public Result getSetVlanPortsResult() {
        return _setVlanPortsResult;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetVlanPortsOutputBuilder setSetVlanPortsResult(Result value) {
    
        this._setVlanPortsResult = value;
        return this;
    }
    
    public SetVlanPortsOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetVlanPortsOutput build() {
        return new SetVlanPortsOutputImpl(this);
    }

    private static final class SetVlanPortsOutputImpl implements SetVlanPortsOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput.class;
        }

        private final Result _setVlanPortsResult;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>> augmentation = new HashMap<>();

        private SetVlanPortsOutputImpl(SetVlanPortsOutputBuilder builder) {
            this._setVlanPortsResult = builder.getSetVlanPortsResult();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Result getSetVlanPortsResult() {
            return _setVlanPortsResult;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.vlan.rev150126.SetVlanPortsOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_setVlanPortsResult == null) ? 0 : _setVlanPortsResult.hashCode());
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
            SetVlanPortsOutputImpl other = (SetVlanPortsOutputImpl) obj;
            if (_setVlanPortsResult == null) {
                if (other._setVlanPortsResult != null) {
                    return false;
                }
            } else if(!_setVlanPortsResult.equals(other._setVlanPortsResult)) {
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
            builder.append("SetVlanPortsOutput [_setVlanPortsResult=");
            builder.append(_setVlanPortsResult);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
