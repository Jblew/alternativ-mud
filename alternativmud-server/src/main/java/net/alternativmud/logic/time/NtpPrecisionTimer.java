/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.time;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.alternativmud.lib.IdManager;
import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

/**
 *
 * @author jblew
 */
public class NtpPrecisionTimer implements PrecisionTimer {
    private final AtomicLong msCorrection = new AtomicLong(0);
    private final String[] ntpServers;
    private final TimeValue synchronizationFrequency;
    private final AtomicReference<TimeFlag> synchronizeFlag = new AtomicReference<>(new TimeFlag(TimeValue.valueOf("1ms")));
    public NtpPrecisionTimer(String[] ntpServers, TimeValue synchronizationFrequency) {
        this.ntpServers = ntpServers;
        this.synchronizationFrequency = synchronizationFrequency;
        synchronizeFlag.set(new TimeFlag(synchronizationFrequency));
        synchronize();
    }

    /**
     * Synchronizes Timer with ntp server. Synchronization is performed in
     * external thread.
     */
    public void synchronize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Logger.getLogger(NtpPrecisionTimer.class.getName()).info("Synchronizing time with ntp servers...");
                try {
                    NTPUDPClient client = new NTPUDPClient();
                    client.open();
                    boolean gotTime = false;
                    for (String addr : ntpServers) {
                        try {
                            TimeInfo info = client.getTime(Inet4Address.getByName(addr));
                            info.computeDetails();
                            Long offsetValue = info.getOffset();
                            msCorrection.set(offsetValue);
                            gotTime = true;
                            Logger.getLogger(NtpPrecisionTimer.class.getName()).info("Successfully synchronied time with " + addr + ". Currect correction: " + msCorrection.get());
                            break;
                        } catch (IOException ex) {
                            Logger.getLogger(NtpPrecisionTimer.class.getName()).log(Level.WARNING, "Synchronization problem", ex);
                        }
                    }
                    if (!gotTime) {
                        Logger.getLogger(NtpPrecisionTimer.class.getName()).log(Level.SEVERE,
                                "Couldn't get time from any server given in array. Last correction will be kept.");
                    }
                } catch (SocketException ex) {
                    Logger.getLogger(NtpPrecisionTimer.class.getName()).log(Level.WARNING, "Synchronization problem", ex);
                }

                synchronizeFlag.set(new TimeFlag(synchronizationFrequency));    
            }

        }, "ntp-synchronization" + IdManager.getSessionSafe()).start();

    }

    @Override
    public long getCurrentTimeMillis() {
        if (synchronizeFlag.get().hasExpired()) {
            synchronize();
        }
        return System.currentTimeMillis() + msCorrection.get();
    }

    private long getCorrectionMs() {
        if (synchronizeFlag.get().hasExpired()) {
            synchronize();
        }
        return msCorrection.get();
    }

    public long getCorrection(TimeUnit unit) {
        if (synchronizeFlag.get().hasExpired()) {
            synchronize();
        }
        return unit.convert(getCorrectionMs(), TimeUnit.MILLISECONDS);
    }

}
