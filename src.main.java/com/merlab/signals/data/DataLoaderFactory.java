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

import java.nio.file.Paths;
import java.util.List;

import com.merlab.signals.data.synthetic.SyntheticCirclesLoader;


/**
 * Fábrica para crear DataLoader según el origen de datos configurado.
 */
public class DataLoaderFactory {

    public enum Type {
        CSV,
        DATABASE,
        JSON, 
        HDF5, 
        HTTP,
        SYNTHETIC_CIRCLES
    }

    /**
     * Crea el loader adecuado según el tipo y la configuración.
     *
     * @param type  origen de datos
     * @param cfg   configuración con rutas, credenciales, columnas…
     * @return un DataLoader listo para usar
     */
    public static DataLoader create(Type type, DataLoaderConfig cfg) {
        switch (type) {
            case CSV:
                return new CSVDataLoader(
                    Paths.get(cfg.getCsvPath()),
                    cfg.getNumInputs(),
                    cfg.getNumTargets()
                );
            case DATABASE:
                return new DataSetDatabaseLoader(
                    cfg.getJdbcUrl(),
                    cfg.getUser(),
                    cfg.getPassword(),
                    cfg.getTable(),
                    List.of(cfg.getInputCols().split(",")),
                    List.of(cfg.getTargetCols().split(","))
                );
            case JSON:
                return new JSONDataLoader(
                    Paths.get(cfg.getJsonPath()),
                    List.of(cfg.getInputCols().split(",")),
                    List.of(cfg.getTargetCols().split(","))
                );   
            case HDF5:
                return new HDF5DataLoader(
                    Paths.get(cfg.getHdf5Path()),
                    cfg.getHdf5DatasetInputs(),
                    cfg.getHdf5DatasetTargets()
                );

            case HTTP:
                if (cfg.isHttpIsJson()) {
                    return new HTTPJSONDataLoader(
                        cfg.getHttpUrl(),
                        List.of(cfg.getInputCols().split(",")),
                        List.of(cfg.getTargetCols().split(","))
                    );     
                } else {
                    return new HTTPCSVDataLoader(
                        cfg.getHttpUrl(),
                        List.of(cfg.getInputCols().split(",")),
                        List.of(cfg.getTargetCols().split(","))
                    );
                }    
            case SYNTHETIC_CIRCLES:
                return new SyntheticCirclesLoader(
                    cfg.getSyntheticN(),
                    cfg.getSyntheticRInt(),
                    cfg.getSyntheticRExt()
                );    
            default:
                throw new IllegalArgumentException("Tipo de DataLoader no soportado: " + type);
        }
    }
}

