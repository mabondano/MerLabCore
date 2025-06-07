/*
 * Copyright 2025 Merly Abondano
 *
 * Created:   2025-06-07
 * Author:    Merly Abondano
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merlab.signals.data;

import java.nio.file.Path;
import java.util.List;

/**
 * Parámetros para configurar un DataLoader genérico.
 */
public class DataLoaderConfig {
    // CSV
    private String csvPath;
    private int    numInputs;
    private int    numTargets;

    // Base de datos
    private String jdbcUrl;
    private String user;
    private String password;
    private String table;
    private String inputCols;   // "sqft,bedrooms,age"
    private String targetCols;  // "price"

    // JSON
    private String jsonPath;

    // HDF5
    private String hdf5Path;
    private String hdf5DatasetInputs;   // ruta interna al dataset de inputs
    private String hdf5DatasetTargets;  // ruta interna al dataset de targets

    // HTTP
    private String httpUrl;      // URL de la cual descargar datos
    private boolean httpIsJson;  // true si la URL devuelve JSON, false si CSV
    
    // ** Synthetic **
    private int syntheticN;
    private double syntheticRInt;
    private double syntheticRExt;

    // —— Getters & Setters ——

    // CSV
    public String getCsvPath()              { return csvPath; }
    public void   setCsvPath(String csvPath){ this.csvPath = csvPath; }
    public int    getNumInputs()            { return numInputs; }
    public void   setNumInputs(int n)       { this.numInputs = n; }
    public int    getNumTargets()           { return numTargets; }
    public void   setNumTargets(int n)      { this.numTargets = n; }

    // Base de datos
    public String getJdbcUrl()              { return jdbcUrl; }
    public void   setJdbcUrl(String u)      { this.jdbcUrl = u; }
    public String getUser()                 { return user; }
    public void   setUser(String u)         { this.user = u; }
    public String getPassword()             { return password; }
    public void   setPassword(String p)     { this.password = p; }
    public String getTable()                { return table; }
    public void   setTable(String t)        { this.table = t; }
    public String getInputCols()            { return inputCols; }
    public void   setInputCols(String cols) { this.inputCols = cols; }
    public String getTargetCols()           { return targetCols; }
    public void   setTargetCols(String cols){ this.targetCols = cols; }

    // JSON
    public String getJsonPath()              { return jsonPath; }
    public void   setJsonPath(String path)   { this.jsonPath = path; }

    // HDF5
    public String getHdf5Path()                             { return hdf5Path; }
    public void   setHdf5Path(String hdf5Path)              { this.hdf5Path = hdf5Path; }
    public String getHdf5DatasetInputs()                    { return hdf5DatasetInputs; }
    public void   setHdf5DatasetInputs(String datasetInputs){ this.hdf5DatasetInputs = datasetInputs; }
    public String getHdf5DatasetTargets()                   { return hdf5DatasetTargets; }
    public void   setHdf5DatasetTargets(String datasetTgts) { this.hdf5DatasetTargets = datasetTgts; }

    // HTTP
    public String  getHttpUrl()             { return httpUrl; }
    public void    setHttpUrl(String url)   { this.httpUrl = url; }
    public boolean isHttpIsJson()           { return httpIsJson; }
    public void    setHttpIsJson(boolean j) { this.httpIsJson = j; }
    
    // Synthetic
    public int getSyntheticN() { return syntheticN; }
    public void setSyntheticN(int syntheticN) { this.syntheticN = syntheticN; }
    public double getSyntheticRInt() { return syntheticRInt; }
    public void setSyntheticRInt(double syntheticRInt) { this.syntheticRInt = syntheticRInt; }
    public double getSyntheticRExt() { return syntheticRExt; }
    public void setSyntheticRExt(double syntheticRExt) { this.syntheticRExt = syntheticRExt; }

}