
Code status:

SNMP related functions include (1) add/modify/remove entries on the forwarding table (2) Switch discovery (3) Toplogy Discovery.
They are all done.

Unit Test with FlowProgrammer and DiscoveryService passed (see ./snmp4sdn/src/test). But currently in the tests' code, the label "@Test" is marked to skip testing because the code cannot pass Jenkins since there is no real Ethernet switches for the Jenkins to test. If one have a real network of Ethernet switches, the label "@Test" could be reopened to proceed testing.



