package com.vanderlande.vipp.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VIPPPresentationCycle.
 */
@Entity
@Table(name = "VIPP_PRESENTATIONCYCLE")
public class VIPPPresentationCycle implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP presentationCycle.
     */
    public VIPPPresentationCycle()
    {
    }

    /**
     * Instantiates a new VIPP presentationCycle.
     *
     * @param presentationCycle_name
     *            the presentationCycle name
     */
    public VIPPPresentationCycle(String presentationCycle_name)
    {
        this.name = presentationCycle_name;
    }

	/** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentationCycle_id")
    private long id;

    /** The name. */
    @Column(name = "presentationCycle_name")
    private String name;
    
    /** The requestedOn. */
    @Column(name = "presentationCycle_requestedOn")
    private Date requestedOn;
    
    /** The presentedOn. */
    @Column(name = "presentationCycle_presentedOn")
    private Date presentedOn;
    

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
        return name;
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

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPGroup)
        {
        	VIPPGroup temp = (VIPPGroup)obj;
            return name.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
    
    public String toString()
    {
    	return name;
    }
}
