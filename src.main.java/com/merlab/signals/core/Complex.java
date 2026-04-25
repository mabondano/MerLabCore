package com.merlab.signals.core;

import java.util.Objects;

/**
 * Número complejo simple, usado en la FFT.
 */

public class Complex {
    public final double re, im;
    
    public Complex(double re, double im) { 
    	this.re = re; this.im = im; 
    }
    
    public Complex add(Complex o)  { 
    	return new Complex(re+o.re, im+o.im); 
    }
    
    public Complex sub(Complex o)  { 
    	return new Complex(re-o.re, im-o.im); 
    }
    
    public Complex mul(Complex o)  { 
        return new Complex(re*o.re - im*o.im, re*o.im + im*o.re); 
    }
    
    public double abs() { 
    	return Math.hypot(re, im); 
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Complex)) return false;
        Complex c = (Complex) o;
        // exact compare; if you need tolerance, use e.g. Math.abs(...)
        return Double.compare(c.re, re) == 0
            && Double.compare(c.im, im) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    @Override
    public String toString() {
        return String.format("(%f %s %fi)",
            re,
            im < 0 ? "-" : "+",
            Math.abs(im));
    }    
    
    /*
    @Override public String toString2() {
        return String.format("(%f%+f i)", re, im);
    }
    */
}

/*
public class Complex {
    private final double real;
    private final double imag;

    public Complex(double real, double imag) {
        this.real = real;
        this.imag = imag;
    }

    //** Getter para la parte real 
    public double getReal() {
        return real;
    }

    //** Getter para la parte imaginaria 
    public double getImag() {
        return imag;
    }

    @Override
    public String toString() {
        return String.format("(%f %s %fi)", real, (imag<0?"-":"+"), Math.abs(imag));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Complex)) return false;
        Complex c = (Complex) o;
        return Double.compare(c.real, real)==0
            && Double.compare(c.imag, imag)==0;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(real, imag);
    }
}
*/






