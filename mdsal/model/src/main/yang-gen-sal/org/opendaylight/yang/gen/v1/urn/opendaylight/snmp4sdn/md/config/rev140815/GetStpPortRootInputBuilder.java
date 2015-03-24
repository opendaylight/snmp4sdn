package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetStpPortRootInputBuilder {

    private Long _nodeId;
    private Short _port;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>> augmentation = new HashMap<>();

    public GetStpPortRootInputBuilder() {
    } 


    public Long getNodeId() {
        return _nodeId;
    }
    
    public Short getPort() {
        return _port;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetStpPortRootInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public GetStpPortRootInputBuilder setPort(Short value) {
    
        this._port = value;
        return this;
    }
    
    public GetStpPortRootInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetStpPortRootInput build() {
        return new GetStpPortRootInputImpl(this);
    }

    private static final class GetStpPortRootInputImpl implements GetStpPortRootInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput.class;
        }

        private final Long _nodeId;
        private final Short _port;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>> augmentation = new HashMap<>();

        private GetStpPortRootInputImpl(GetStpPortRootInputBuilder builder) {
            this._nodeId = builder.getNodeId();
            this._port = builder.getPort();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Long getNodeId() {
            return _nodeId;
        }
        
        @Override
        public Short getPort() {
            return _port;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortRootInput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_nodeId == null) ? 0 : _nodeId.hashCode());
            result = prime * result + ((_port == null) ? 0 : _port.hashCode());
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
            GetStpPortRootInputImpl other = (GetStpPortRootInputImpl) obj;
            if (_nodeId == null) {
                if (other._nodeId != null) {
                    return false;
                }
            } else if(!_nodeId.equals(other._nodeId)) {
                return false;
            }
            if (_port == null) {
                if (other._port != null) {
                    return false;
                }
            } else if(!_port.equals(other._port)) {
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
            builder.append("GetStpPortRootInput [_nodeId=");
            builder.append(_nodeId);
            builder.append(", _port=");
            builder.append(_port);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
