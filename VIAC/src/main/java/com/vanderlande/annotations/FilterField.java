package com.vanderlande.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vanderlande.viac.filter.VIACFilterPanel;

/**
 * The Annotation FilterField.
 * 
 * @author dekscha
 * 
 *         Used in {@link VIACFilterPanel} to detect a nested object-hierachie within an object. For example: if you
 *         have a model "applicant" who has a reference to another object "status" you can mark this reference with this
 *         annotation to enable status-filtering for the status.
 * 
 *         Example usage:
 * 
 *         public class Applicant {
 *              @FilterField(name = "statusName") 
 *              private Status status; 
 *         }
 * 
 * 
 *         public class Status { 
 *              private int id; 
 *              private String statusName; 
 *         }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FilterField
{

    /**
     * Name of the filterable field.
     *
     * @return the filterable field
     */
    public String name();
}
