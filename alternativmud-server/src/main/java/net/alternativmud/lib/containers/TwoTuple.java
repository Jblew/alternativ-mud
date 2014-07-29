/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.alternativmud.lib.containers;

import java.util.Objects;

/**
 *
 * @author jblew
 */
public class TwoTuple<A,B> {
    public final A a;
    public final B b;
    
    public TwoTuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "TwoTuple{" + "a=" + a + ", b=" + b + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TwoTuple<A, B> other = (TwoTuple<A, B>) obj;
        if (!Objects.equals(this.a, other.a)) {
            return false;
        }
        if (!Objects.equals(this.b, other.b)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.a);
        hash = 79 * hash + Objects.hashCode(this.b);
        return hash;
    }
    
    
}
