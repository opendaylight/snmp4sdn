package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;


/**
 * The enumeration built-in type represents values from a set of assigned names.
 */
public enum AclLayer {
    ACLLAYERETHERNET(1),
    
    ACLLAYERIP(2)
    ;


    int value;
    static java.util.Map<java.lang.Integer, AclLayer> valueMap;

    static {
        valueMap = new java.util.HashMap<>();
        for (AclLayer enumItem : AclLayer.values())
        {
            valueMap.put(enumItem.value, enumItem);
        }
    }

    private AclLayer(int value) {
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
     * @return corresponding AclLayer item
     */
    public static AclLayer forValue(int valueArg) {
        return valueMap.get(valueArg);
    }
}
