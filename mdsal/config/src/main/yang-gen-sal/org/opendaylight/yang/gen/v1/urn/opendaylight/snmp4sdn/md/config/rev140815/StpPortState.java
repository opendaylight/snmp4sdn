package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.config.rev140815;


/**
 * The enumeration built-in type represents values from a set of assigned names.
 */
public enum StpPortState {
    DISABLED(1),
    
    BLOCKING(2),
    
    LISTENING(3),
    
    LEARNING(4),
    
    FORWARDING(5),
    
    BROKEN(6)
    ;


    int value;
    static java.util.Map<java.lang.Integer, StpPortState> valueMap;

    static {
        valueMap = new java.util.HashMap<>();
        for (StpPortState enumItem : StpPortState.values())
        {
            valueMap.put(enumItem.value, enumItem);
        }
    }

    private StpPortState(int value) {
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
     * @return corresponding StpPortState item
     */
    public static StpPortState forValue(int valueArg) {
        return valueMap.get(valueArg);
    }
}
