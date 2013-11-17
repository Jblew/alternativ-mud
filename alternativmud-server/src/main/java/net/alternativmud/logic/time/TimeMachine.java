/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.time;

import java.util.Date;
import net.alternativmud.StaticConfig;
import net.alternativmud.logic.geo.Coordinates3l;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;

/**
 *
 * @author jblew
 */
public class TimeMachine {
    private Config config = new Config();
    private DateTime dateTime = new DateTime();
    public TimeMachine() {
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    /**
     * Get time parameter. AlternativMUD uses gregorian calendar.
     *
     * @param field GregorianCalendar field
     * @return
     */
    @JsonIgnore
    public int get(DateTimeFieldType field) {
        updateTime(0);
        return dateTime.get(field);
    }

    /**
     * Get time parameter for specific place. AlternativMUD uses gregorian
     * calendar.
     *
     * @param field GregorianCalendar field
     * @return
     */
    @JsonIgnore
    public int get(Coordinates3l location, DateTimeFieldType field) {
        updateTime((int) (location.getLongitudeCircleFraction() * (1000 * 60 * 60 * 24)));
        return dateTime.get(field);
    }

    @JsonIgnore
    public String getString() {
        updateTime(0);
        return dateTime.toString();
    }

    @JsonIgnore
    public String getString(Coordinates3l location) {
        updateTime((int) (location.getLongitudeCircleFraction() * (1000 * 60 * 60 * 24)));
        return dateTime.toString();
    }

    private void updateTime(int correctionMs) {
        dateTime = new DateTime(config.getBaseDate()).plusMillis((int) config.getLocalTimestamp().getTimestamp() + correctionMs);
    }

    @JsonIgnore
    public long getTimestamp() {
        return config.getLocalTimestamp().getTimestamp();
    }

    public class Config {
        private LocalTimeAcceleratingTimestamp localTimestamp =
                new LocalTimeAcceleratingTimestamp(0, 60);
        private Date baseDate = new Date();

        public Config() {
            localTimestamp.setPrecisionTimer(new NtpPrecisionTimer(StaticConfig.NTP_SERVERS, TimeValue.valueOf("2h")));
        }

        @JsonIgnore
        public int getTickDurationMs() {
            return (int) (60 * 60 * 1000 / localTimestamp.getAccelerationFactor());
        }

        @JsonIgnore
        public int getPulseDurationMs() {
            return (int) (getTickDurationMs() / 15);
        }

        public LocalTimeAcceleratingTimestamp getLocalTimestamp() {
            return localTimestamp;
        }

        public void setLocalTimestamp(LocalTimeAcceleratingTimestamp localTimestamp) {
            this.localTimestamp = localTimestamp;
        }

        public Date getBaseDate() {
            return baseDate;
        }

        public void setBaseDate(Date baseDate) {
            this.baseDate = baseDate;
        }

    }
}
