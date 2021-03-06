package jmri.jmrix.can.cbus;

import jmri.Reporter;
import jmri.jmrix.can.CanListener;
import jmri.jmrix.can.CanMessage;
import jmri.jmrix.can.CanReply;
import jmri.jmrix.can.CanSystemConnectionMemo;
import jmri.jmrix.can.TrafficController;
import jmri.managers.AbstractReporterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manage the CBUS-specific Reporter implementation.
 * <p>
 * System names are "MRnnn", where M is the user-configurable system prefix,
 * nnn is the reporter number without padding.
 *
 * @author Mark Riddoch Copyright (C) 2015
 */
public class CbusReporterManager extends AbstractReporterManager implements
        CanListener {

    @SuppressWarnings("LeakingThisInConstructor")
    public CbusReporterManager(CanSystemConnectionMemo memo) {
        this.memo = memo;
        this.tc = memo.getTrafficController();
        this.prefix = memo.getSystemPrefix();
        this.memo = memo;
        if (tc != null) {
            tc.addCanListener(this);
        } else {
            log.error("CbusReporterManager: Failed to register listener");
        }
    }

    CanSystemConnectionMemo memo;
    TrafficController tc;
    String prefix;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSystemPrefix() {
        return prefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dispose() {
        if (tc != null) {
            tc.removeCanListener(this);
        }
        super.dispose();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Reporter createNewReporter(String systemName, String userName) {
        Reporter t;
        log.debug("ReporterManager create new CbusReporter: {}", systemName);
        int addr = Integer.parseInt(systemName.substring(prefix.length() + 1));
        t = new CbusReporter(addr, tc, prefix);
        t.setUserName(userName);
        t.addPropertyChangeListener(this);

        return t;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public NameValidity validSystemNameFormat(String systemName) {
        // name must be in the MSnnnnn format (M is user configurable); no + or ; or - for Reporter address
        try {
            // try to parse the string; success returns true
            Integer.valueOf(systemName.substring(getSystemPrefix().length() + 1, systemName.length()));
        } catch (NumberFormatException e) {
            log.debug("Warning: illegal character in number field of system name: {}", systemName);
            return NameValidity.INVALID;
        }
        return NameValidity.VALID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEntryToolTip() {
        String entryToolTip = Bundle.getMessage("AddReporterEntryToolTip");
        return entryToolTip;
    }

    /**
     * {@inheritDoc}
     *
     * @see jmri.jmrix.can.CanListener#message(jmri.jmrix.can.CanMessage)
     */
    @Override
    public void message(CanMessage m) {
        // TODO Auto-generated method stub
        if (m.getOpCode() != CbusConstants.CBUS_DDES) {
            return;
        }
        // message type OK, check address
        log.debug("CbusReporterManager: handle message: {}", m.getOpCode());
        int addr = CbusMessage.getNodeNumber(m);

        CbusReporter r = (CbusReporter) provideReporter("MR" + addr);
        r.message(m);       // make sure it got the message

    }

    /**
     * {@inheritDoc}
     *
     * @see jmri.jmrix.can.CanListener#reply(jmri.jmrix.can.CanReply)
     */
    @Override
    public void reply(CanReply m) {
        // TODO Auto-generated method stub
        if (m.getOpCode() != CbusConstants.CBUS_DDES || m.getOpCode() != CbusConstants.CBUS_ACDAT) {
            return;
        }
        // message type OK, check address
        log.debug("CbusReporterManager: handle reply: {} node: {}", m.getOpCode(), CbusMessage.getNodeNumber(m));
        int addr = m.getElement(1) * 256 + m.getElement(2);

        CbusReporter r = (CbusReporter) provideReporter("MR" + addr);
        r.reply(m);     // make sure it got the message

    }

    private static final Logger log = LoggerFactory.getLogger(CbusReporterManager.class);

}
