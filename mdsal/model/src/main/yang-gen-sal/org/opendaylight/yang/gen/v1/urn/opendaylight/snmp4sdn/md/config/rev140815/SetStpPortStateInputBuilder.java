package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class SetStpPortStateInputBuilder {

    private Boolean _enable;
    private Long _nodeId;
    private Short _port;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>> augmentation = new HashMap<>();

    public SetStpPortStateInputBuilder() {
    } 


    public Boolean isEnable() {
        return _enable;
    }
    
    public Long getNodeId() {
        return _nodeId;
    }
    
    public Short getPort() {
        return _port;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public SetStpPortStateInputBuilder setEnable(Boolean value) {
    
        this._enable = value;
        return this;
    }
    
    public SetStpPortStateInputBuilder setNodeId(Long value) {
    
        this._nodeId = value;
        return this;
    }
    
    public SetStpPortStateInputBuilder setPort(Short value) {
    
        this._port = value;
        return this;
    }
    
    public SetStpPortStateInputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public SetStpPortStateInput build() {
        return new SetStpPortStateInputImpl(this);
    }

    private static final class SetStpPortStateInputImpl implements SetStpPortStateInput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput.class;
        }

        private final Boolean _enable;
        private final Long _nodeId;
        private final Short _port;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>> augmentation = new HashMap<>();

        private SetStpPortStateInputImpl(SetStpPortStateInputBuilder builder) {
            this._enable = builder.isEnable();
            this._nodeId = builder.getNodeId();
            this._port = builder.getPort();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public Boolean isEnable() {
            return _enable;
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
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815.SetStpPortStateInput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_enable == null) ? 0 : _enable.hashCode());
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
            SetStpPortStateInputImpl other = (SetStpPortStateInputImpl) obj;
            if (_enable == null) {
                if (other._enable != null) {
                    return false;
                }
            } else if(!_enable.equals(other._enable)) {
                return false;
            }
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
            builder.append("SetStpPortStateInput [_enable=");
            builder.append(_enable);
            builder.append(", _nodeId=");
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
