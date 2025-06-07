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

/**
 * @author merly.abondano
 *
 */
module MerLabSignalStudio {
	
    requires org.mariadb.jdbc;  // el Automatic-Module-Name
    requires waffle.jna;     	// ← nuevo
    requires java.sql;			// para JDBC
	requires org.knowm.xchart;
	//requires junit;  
    // agrega esta línea para compilar los tests (scope static)
    requires static org.junit.jupiter.api;
    requires commons.math3; 
    requires org.junit.jupiter.params;
    requires org.json;
    requires java.net.http;
    requires java.desktop;         // <— para AWT/Swing
    requires javafx.controls;
    requires javafx.web;
    requires javafx.graphics;      // a veces necesario
    opens com.merlab.signals.plot to javafx.graphics;
    // Si tus tests están bajo com.merlab.signals.test, ábrelo para reflexión:
    opens com.merlab.signals.test to org.junit.jupiter.api;
    //exports com.merlab.signals;	
    exports com.merlab.signals.core;
    exports com.merlab.signals.rpn;
    exports com.merlab.signals.features;
    exports com.merlab.signals.nn.manager;
    exports com.merlab.signals.nn.processor;
    exports com.merlab.signals.nn.trainer;
    exports com.merlab.signals.persistence;
}