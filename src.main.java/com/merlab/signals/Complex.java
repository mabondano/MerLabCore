package com.merlab.signals;

public class Complex {
    public final double re, im;
    public Complex(double re, double im) { this.re = re; this.im = im; }
    public Complex add(Complex o)  { return new Complex(re+o.re, im+o.im); }
    public Complex sub(Complex o)  { return new Complex(re-o.re, im-o.im); }
    public Complex mul(Complex o)  { 
        return new Complex(re*o.re - im*o.im, re*o.im + im*o.re); 
    }
    public double abs() { return Math.hypot(re, im); }
    @Override public String toString() {
        return String.format("(%f%+f i)", re, im);
    }
}
