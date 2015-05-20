package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.fdb.rev150126;


/**
 * The enumeration built-in type represents values from a set of assigned names.
 */
public enum FdbEntryType {
    OTHER(1),
    
    INVALID(2),
    
    LEARNED(3),
    
    SELF(4),
    
    MGMT(5)
    ;


    int value;
    static java.util.Map<java.lang.Integer, FdbEntryType> valueMap;

    static {
        valueMap = new java.util.HashMap<>();
        for (FdbEntryType enumItem : FdbEntryType.values())
        {
            valueMap.put(enumItem.value, enumItem);
        }
    }

    private FdbEntryType(int value) {
        this.value = value;
    }
    
    /**
     * @return integer value
     */
    public int getIntValue() {
        return value;
    }

    /**
     * @param valueArg
     * @return corresponding FdbEntryType item
     */
    public static FdbEntryType forValue(int valueArg) {
        return valueMap.get(valueArg);
    }
}
