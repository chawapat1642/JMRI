package jmri.jmrix.easydcc;

import jmri.util.JUnitUtil;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Paul Bender Copyright (C) 2017	
 */
public class EasyDccOpsModeProgrammerTest {

    @Test
    public void testCTor() {
        EasyDccOpsModeProgrammer t = new EasyDccOpsModeProgrammer(100, false, memo);
        Assert.assertNotNull("exists",t);
    }

    private EasyDccSystemConnectionMemo memo = null;

    // The minimal setup for log4J
    @Before
    public void setUp() {
        JUnitUtil.setUp();
        EasyDccSystemConnectionMemo memo = new EasyDccSystemConnectionMemo();
    }

    @After
    public void tearDown() {
        JUnitUtil.tearDown();
    }

    // private final static Logger log = LoggerFactory.getLogger(EasyDccOpsModeProgrammerTest.class);

}
