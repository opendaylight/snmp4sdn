package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;


/**
 * The enumeration built-in type represents values from a set of assigned names.
 */
public enum Result {
    SUCCESS(1),
    
    FAIL(2),
    
    EMPTY(3),
    
    INVALIDPARAM(4),
    
    ALREADYEXIST(5),
    
    NOTEXIST(6),
    
    NOTREADY(7),
    
    INTERRUPT(8)
    ;


    int value;
    static java.util.Map<java.lang.Integer, Result> valueMap;

    static {
        valueMap = new java.util.HashMap<>();
        for (Result enumItem : Result.values())
        {
            valueMap.put(enumItem.value, enumItem);
        }
    }

    private Result(int value) {
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
     * @return corresponding Result item
     */
    public static Result forValue(int valueArg) {
        return valueMap.get(valueArg);
    }
}
