package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetStpPortStateInputBuilder {

    private Long _nodeId;
    private Short _port;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>> augmentation = new HashMap<>();

    public GetStpPortStateInputBuilder() {
    } 


    public Long getNodeId() {
        return _nodeId;
    }
    
    public Short getPort() {
        return _port;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetStpPortStateInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public GetStpPortStateInputBuilder setPort(Short value) {
    
        this._port = value;
        return this;
    }
    
    public GetStpPortStateInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetStpPortStateInput build() {
        return new GetStpPortStateInputImpl(this);
    }

    private static final class GetStpPortStateInputImpl implements GetStpPortStateInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput.class;
        }

        private final Long _nodeId;
        private final Short _port;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>> augmentation = new HashMap<>();

        private GetStpPortStateInputImpl(GetStpPortStateInputBuilder builder) {
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
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.GetStpPortStateInput>> E getAugmentation(Class<E> augmentationType) {
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
            GetStpPortStateInputImpl other = (GetStpPortStateInputImpl) obj;
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
            builder.append("GetStpPortStateInput [_nodeId=");
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
