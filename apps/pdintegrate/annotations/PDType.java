/**
 * 
 */
package pdintegrate.annotations;

import java.lang.annotation.*;


/**
 * 
 * Marker annotation for PDEntities, i.e. classes which are part of a domain model, and so 
 * must be modified.
 * 
 * Note: if String Entity_GUID is empty string or null, a new GUID will be used.
 *
 * 
 * @author Danver
 *
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface PDType {
	String Entity_GUID();
}




