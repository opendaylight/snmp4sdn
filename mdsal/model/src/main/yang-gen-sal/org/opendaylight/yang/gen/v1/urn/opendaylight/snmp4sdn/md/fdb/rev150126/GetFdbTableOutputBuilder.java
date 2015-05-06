package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;
import java.util.List;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.get.fdb.table.output.FdbTableEntry;
import java.util.Map;
import org.opendaylight.yangtools.yang.binding.Augmentation;
import java.util.HashMap;



public class GetFdbTableOutputBuilder {

    private List<FdbTableEntry> _fdbTableEntry;
    private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>> augmentation = new HashMap<>();

    public GetFdbTableOutputBuilder() {
    } 


    public List<FdbTableEntry> getFdbTableEntry() {
        return _fdbTableEntry;
    }
    
    @SuppressWarnings("unchecked")
    public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>> E getAugmentation(Class<E> augmentationType) {
        if (augmentationType == null) {
            throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
        }
        return (E) augmentation.get(augmentationType);
    }

    public GetFdbTableOutputBuilder setFdbTableEntry(List<FdbTableEntry> value) {
    
        this._fdbTableEntry = value;
        return this;
    }
    
    public GetFdbTableOutputBuilder addAugmentation(Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>> augmentationType, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput> augmentation) {
        this.augmentation.put(augmentationType, augmentation);
        return this;
    }

    public GetFdbTableOutput build() {
        return new GetFdbTableOutputImpl(this);
    }

    private static final class GetFdbTableOutputImpl implements GetFdbTableOutput {

        public Class<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput> getImplementedInterface() {
            return org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput.class;
        }

        private final List<FdbTableEntry> _fdbTableEntry;
        private Map<Class<? extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>>, Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>> augmentation = new HashMap<>();

        private GetFdbTableOutputImpl(GetFdbTableOutputBuilder builder) {
            this._fdbTableEntry = builder.getFdbTableEntry();
            this.augmentation.putAll(builder.augmentation);
        }

        @Override
        public List<FdbTableEntry> getFdbTableEntry() {
            return _fdbTableEntry;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public <E extends Augmentation<org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126.GetFdbTableOutput>> E getAugmentation(Class<E> augmentationType) {
            if (augmentationType == null) {
                throw new IllegalArgumentException("Augmentation Type reference cannot be NULL!");
            }
            return (E) augmentation.get(augmentationType);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_fdbTableEntry == null) ? 0 : _fdbTableEntry.hashCode());
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
            GetFdbTableOutputImpl other = (GetFdbTableOutputImpl) obj;
            if (_fdbTableEntry == null) {
                if (other._fdbTableEntry != null) {
                    return false;
                }
            } else if(!_fdbTableEntry.equals(other._fdbTableEntry)) {
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
            builder.append("GetFdbTableOutput [_fdbTableEntry=");
            builder.append(_fdbTableEntry);
            builder.append(", augmentation=");
            builder.append(augmentation.values());
            builder.append("]");
            return builder.toString();
        }
    }

}
