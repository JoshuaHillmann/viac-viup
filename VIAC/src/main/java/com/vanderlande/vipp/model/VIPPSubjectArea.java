package com.vanderlande.vipp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VIPPSubjectArea.
 */
@Entity
@Table(name = "VIPP_SUBJECTAREA")
public class VIPPSubjectArea implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP subjectArea.
     */
    public VIPPSubjectArea()
    {
    }

    /**
     * Instantiates a new VIPP subjectArea.
     *
     * @param subjectArea_name
     *            the subjectArea name
     */
    public VIPPSubjectArea(String subjectArea_name)
    {
        this.name = subjectArea_name;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subjectArea_id")
    private long id;

    /** The name. */
    @Column(name = "subjectArea_name")
    private String name;    

    /**
     * Gets the id.
     *
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id
     *            the new id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name != null ? name : "";
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String toString()
    {
    	return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPSubjectArea)
        {
        	VIPPSubjectArea temp = (VIPPSubjectArea)obj;
            return name.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
