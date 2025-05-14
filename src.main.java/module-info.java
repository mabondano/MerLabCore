/**
 * 
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
    // Si tus tests están bajo com.merlab.signals.test, ábrelo para reflexión:
    opens com.merlab.signals.test to org.junit.jupiter.api;
    exports com.merlab.signals;	
}