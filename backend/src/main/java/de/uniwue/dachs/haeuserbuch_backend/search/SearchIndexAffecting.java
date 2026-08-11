package de.uniwue.dachs.haeuserbuch_backend.search;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Marks a successful mutation that requires rebuilding the global search documents. */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchIndexAffecting {
}
