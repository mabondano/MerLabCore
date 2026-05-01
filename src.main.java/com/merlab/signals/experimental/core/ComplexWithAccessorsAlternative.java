package com.merlab.signals.experimental.core;

import java.util.Objects;

/**
 * Alternative complex-number structure kept for design comparison.
 * This version uses private fields and accessors instead of public final fields.
 */
public class ComplexWithAccessorsAlternative {
    private final double real;
    private final double imag;

    public ComplexWithAccessorsAlternative(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    public double getReal() {
        return real;
    }

    public double getImag() {
        return imag;
    }

    @Override
    public String toString() {
        return String.format("(%f %s %fi)", real, imag < 0 ? "-" : "+", Math.abs(imag));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ComplexWithAccessorsAlternative)) return false;
        ComplexWithAccessorsAlternative c = (ComplexWithAccessorsAlternative) o;
        return Double.compare(c.real, real) == 0
            && Double.compare(c.imag, imag) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(real, imag);
    }
}
