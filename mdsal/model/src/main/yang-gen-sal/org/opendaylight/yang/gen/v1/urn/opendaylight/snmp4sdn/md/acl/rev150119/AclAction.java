package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;


public enum AclAction {
    PERMIT(1),
    DENY(0);

    int value;
    static java.util.Map<java.lang.Integer, AclAction> valueMap;

    static {
        valueMap = new java.util.HashMap<>();
        for (AclAction enumItem : AclAction.values())
        {
            valueMap.put(enumItem.value, enumItem);
        }
    }

    private AclAction(int value) {
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
     * @return corresponding AclAction item
     */
    public static AclAction forValue(int valueArg) {
        return valueMap.get(valueArg);
    }
}
