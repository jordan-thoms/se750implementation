package pdintegrate.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface PDAttribute {
	String Role_GUID();
}