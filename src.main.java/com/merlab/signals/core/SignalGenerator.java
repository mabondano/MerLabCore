package com.merlab.signals.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;



//import java.sql.*;

//import javax.script.*;


/**
 * Genera señales sintéticas de prueba según el tipo y los parámetros indicados.
 */
public class SignalGenerator implements SignalProvider {

    public enum Type {
        SINE,
        SINEWAVE,
        SINEALL,
        SQUARE,
        TRIANGLE,
        NOISE,
        DELTA,
        DC,
        SAWTOOTH,
        FROM_FILE
    }

    private final Type type;
    private final int size;
    private final double amplitude;
    private final double frequency;
    private final double phase;     // para TRIANGLE o SINE si se quiere
    private final boolean normalizeToPercent;
    private final int deltaPos;     // para DELTA
    private final double level;     // para DC
    private final String filePath;  // para FROM_FILE
    
    /** Constructor por defecto: genera una onda seno de 128 muestras, amplitud 1, fase 0 */
    public SignalGenerator() {
        this(Type.SINE,       // tipo por defecto
             128,             // tamaño por defecto
             1.0,             // amplitud por defecto
             1.0,             // frecuencia (1 ciclo) por defecto
             0.0,             // fase por defecto
             false,           // sin normalizar a 0–100
             0,               // deltaPos por defecto
             1,				  // level	
             ""               // filePath vacío
        );
    }  

    public SignalGenerator(Type type,
                           int size,
                           double amplitude,
                           double frequency,
                           double phase,
                           boolean normalizeToPercent,
                           int deltaPos,
                           double level,
                           String filePath) {
        this.type      = type;
        this.size      = size;
        this.amplitude = amplitude;
        this.frequency = frequency;
        this.phase     = phase;
        this.normalizeToPercent = normalizeToPercent;
        this.deltaPos  = deltaPos;
        this.level = level;
        this.filePath  = filePath;
    }

    @Override
    public Signal getSignal() {
        switch (type) {
            case SINE:
                return generateSine(size, amplitude, frequency);
            case SINEWAVE:
                return generateSineWave(size, amplitude, phase);             
            case SINEALL:
                return generateSineAll(size, amplitude, frequency, phase, normalizeToPercent);                
            case SQUARE:
                return generateSquare(size, amplitude, frequency);
            case TRIANGLE:
                return generateTriangle(size, amplitude, frequency);
            case NOISE:
                return generateWhiteNoise(size, amplitude);
            case DELTA:
                return generateDelta(size, deltaPos);
            case DC:
                return generateDC(size, level);
            case SAWTOOTH:
                return generateSawtooth(size, amplitude);    
            case FROM_FILE:
			try {
				return (Signal) loadSignalFromFile(filePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            default:
                throw new IllegalStateException("Tipo de señal no soportado: " + type);
        }
    }

    
    public static Signal generateSine(int size, double amplitude, double frequency) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double value = amplitude * Math.sin(2 * Math.PI * frequency * i / size);
            values.add(value * 50 + 50); // Normalizado a 0-100
        }
        return new Signal(values);
    }
    
    public static Signal generateSineWave(int length, double amplitude, double phase) {
        List<Double> values = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            double x = 2 * Math.PI * i / length + phase;
            values.add(amplitude * Math.sin(x));
        }
        return new Signal(values);
    }    
    
    public List<Double> generateSineWave2(int length, double amplitude, double phase) {
        List<Double> signal = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            double x = i;
            double value = amplitude * Math.sin(2 * Math.PI * x / length + phase);
            signal.add(value);
        }
        return signal;
    }    
    
    /**
     * Genera una señal senoidal “todo en uno”:
     * - size muestras
     * - amplitude controla el pico ±amplitude
     * - frequency ciclos completos dentro de size
     * - phase desplazamiento de fase en radianes
     * - normalizeToPercent: si es true, mapea [-amplitude, +amplitude] → [0,100]
     */
    public static Signal generateSineAll(int size,
                                          double amplitude,
                                          double frequency,
                                          double phase,
                                          boolean normalizeToPercent) {
        List<Double> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double angle = 2 * Math.PI * frequency * i / size + phase;
            double v = amplitude * Math.sin(angle);
            if (normalizeToPercent) {
                // mapear [-amplitude, +amplitude] a [0, 100]
                v = (v + amplitude) / (2 * amplitude) * 100.0;
            }
            values.add(v);
        }
        return new Signal(values);
    }    

    public static Signal generateSquare(int size, double amplitude, double frequency) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double value = Math.sin(2 * Math.PI * frequency * i / size);
            values.add((value >= 0 ? amplitude : 0) * 50 + 50);
        }
        return new Signal(values);
    }

    public static Signal generateTriangle(int size, double amplitude, double frequency) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            double phase = (i * frequency * 2.0 / size) % 1;
            double value = 2 * amplitude * (phase < 0.5 ? phase : 1 - phase);
            values.add(value * 50 + 50);
        }
        return new Signal(values);
    }

    public static Signal generateWhiteNoise(int size, double amplitude) {
        List<Double> values = new ArrayList<>();
        Random rand = new Random();
        for (int i = 0; i < size; i++) {
            double value = rand.nextGaussian() * amplitude;
            values.add(value * 50 + 50); // Normalizado a 0-100
        }
        return new Signal(values);
    }

    public static Signal generateDelta(int size, int position) {
        List<Double> values = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            values.add(i == position ? 100.0 : 0.0);
        }
        return new Signal(values);
    }
    
    /**
     * Genera una señal DC (continua) de valor constante.
     *
     * @param size  número de muestras
     * @param level valor constante que tendrá cada muestra
     * @return      una Signal con todas las muestras igual a 'level'
     */
    public static Signal generateDC(int size, double level) {
        List<Double> values = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            values.add(level);
        }
        return new Signal(values);
    }

    /**
     * Genera una señal diente de sierra (sawtooth).
     *
     * La rampa va de 0 hasta 'amplitude', repartida en 'size' puntos.
     *
     * @param size      número de muestras
     * @param amplitude valor pico de la rampa
     * @return          una Signal que sube linealmente de 0 a 'amplitude'
     */
    public static Signal generateSawtooth(int size, double amplitude) {
        List<Double> values = new ArrayList<>(size);
        if (size <= 1) {
            // Caso trivial
            for (int i = 0; i < size; i++) {
                values.add(0.0);
            }
            return new Signal(values);
        }
        for (int i = 0; i < size; i++) {
            double ramp = amplitude * i / (double)(size - 1);
            values.add(ramp);
        }
        return new Signal(values);
    }    
    
    public static List<Double> loadSignalFromFile2(String filePath) throws IOException {
        List<Double> signal = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                signal.add(Double.parseDouble(line));
            }
        }
        return signal;
    }    
    
    // Método para cargar una señal desde un archivo
    public static List<Double> loadSignalFromFile(String filePath) throws IOException {
        List<Double> signal = new ArrayList<>();
        // Abrimos el archivo de lectura
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // Leemos cada línea del archivo y la convertimos a Double
            while ((line = reader.readLine()) != null) {
                try {
                    signal.add(Double.parseDouble(line)); // Parseamos cada valor a Double
                } catch (NumberFormatException e) {
                    System.err.println("Error al leer el valor: " + line); // Manejo de errores si no es un número
                }
            }
        }
        return signal;
    }
    

    
}

/*
public static List<Double> loadSignalFromDatabase(String query) {
    List<Double> signal = new ArrayList<>();
    String url = "jdbc:mariadb://localhost:3306/yourdatabase";
    String user = "yourusername";
    String password = "yourpassword";

    try (Connection connection = DriverManager.getConnection(url, user, password);
         Statement statement = connection.createStatement();
         ResultSet resultSet = statement.executeQuery(query)) {

        while (resultSet.next()) {
            signal.add(resultSet.getDouble("signal_value"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return signal;
}
*/

/*
public static List<Double> generateCustomSignal(String expression, int numPoints) throws ScriptException {
    List<Double> signal = new ArrayList<>();
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    for (int i = 0; i < numPoints; i++) {
        String formula = expression.replace("x", String.valueOf(i));
        signal.add((Double) engine.eval(formula));
    }
    return signal;
}
*/
