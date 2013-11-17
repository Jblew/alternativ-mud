/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.logic.time;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa, która łączy jednostkę TimeUnit z jej wartością (long).
 * @author jblew
 */
public class TimeValue {
    public final TimeUnit unit;
    public final long value;
    private static final Pattern PATTERN = Pattern.compile("^([0-9]+)([a-z]+)$");

    public TimeValue(long value, TimeUnit unit) {
        this.unit = unit;
        if(value < 1) throw new IllegalArgumentException("TimeValue value may not be zero!");
        this.value = value;
    }

    public void sleep() throws InterruptedException {
        System.out.println("Sleeping "+value+" "+unit.name()+".");
        unit.sleep(value);
    }
    
    public static TimeValue valueOf(String s) {
        if(s == null) throw new NullPointerException("Parameter s may not be null!");
        if (s.length() < 1) {
            throw new IllegalArgumentException("Cannot parse time: " + s + ".");
        }
        s = s.toLowerCase().replace(" ", "");
        Matcher m = PATTERN.matcher(s);
        
        if (m.find()) {
            int value = Integer.parseInt(m.group(1));
            if(value == 0) throw new IllegalArgumentException("TimeValue value may not be zero!");
            String unitName = m.group(2);
            TimeUnit unit = null;
            switch (unitName) {
                case "d":
                case "day":
                case "days":
                    unit = TimeUnit.DAYS;
                    break;
                case "h":
                case "hour":
                case "hours":
                    unit = TimeUnit.HOURS;
                    break;
                case "m":
                case "min":
                case "minute":
                case "minutes":
                    unit = TimeUnit.MINUTES;
                    break;
                case "s":
                case "sec":
                case "second":
                case "seconds":
                    unit = TimeUnit.SECONDS;
                    break;
                case "ms":
                case "msec":
                case "msecond":
                case "mseconds":
                case "millisecond":
                case "milliseconds":
                    unit = TimeUnit.MILLISECONDS;
                    break;
                case "us":
                case "usec":
                case "usecond":
                case "useconds":
                case "microsecond":
                case "microseconds":
                    unit = TimeUnit.MICROSECONDS;
                    break;
                case "ns":
                case "nsec":
                case "nsecond":
                case "nseconds":
                case "nanosecond":
                case "nanoseconds":
                    unit = TimeUnit.NANOSECONDS;
                    break;
                default:
                    throw new IllegalArgumentException("Cannot parse time: " + s + ".");
            }
            
            return new TimeValue(value, unit);
        }
        else throw new IllegalArgumentException("Cannot parse time: " + s + ".");
    }
    
    public TimeFlag startFlag() {
        return new TimeFlag(this);
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TimeValue)) return false;
        return (((TimeValue)o).unit.equals(unit) && ((TimeValue)o).value == value);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + (this.unit != null ? this.unit.hashCode() : 0);
        hash = 31 * hash + (int) (this.value ^ (this.value >>> 32));
        return hash;
    }
    
        @Override
    public String toString() {
        return value+unit.name().toLowerCase();
    }
}
