package org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclAction;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclLayer;
import java.util.List;
import org.opendaylight.yangtools.yang.binding.DataObject;
import org.opendaylight.yang.gen.v1.urn.opendaylight.snmp4sdn.md.acl.rev150119.AclField;
import org.opendaylight.yangtools.yang.common.QName;


/**
**/
public interface AclRule
    extends
    DataObject,
    AclField
{



    public static final QName QNAME = org.opendaylight.yangtools.yang.common.QName.create("urn:opendaylight:snmp4sdn:md:acl","2015-01-19","acl-rule")
    ;

    /**
    **/
    AclAction getAclAction();
    
    /**
    **/
    AclLayer getAclLayer();
    
    /**
    **/
    List<Short> getPortList();

}

