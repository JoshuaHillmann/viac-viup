package com.vanderlande.vipp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VIPPApprenticeshipArea.
 */
@Entity
@Table(name = "VIPP_APPRENTICESHIPAREA")
public class VIPPApprenticeshipArea implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP apprenticeshipArea.
     */
    public VIPPApprenticeshipArea()
    {
    }

    /**
     * Instantiates a new VIPP apprenticeshipArea.
     *
     * @param apprenticeshipArea_name
     *            the apprenticeshipArea name
     */
    public VIPPApprenticeshipArea(String name)
    {
        this.name = name;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apprenticeshipArea_id")
    private long id;
    
    /** The name. */
    @Column(name = "apprenticeshipArea_name")
    private String name;
    
    @Column(name = "apprenticeshipArea_active")
    private Boolean active = true;

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
    
    public String toString()
    {
    	return name;
    }
    
    public Boolean isActive() {
 		return active;
 	}

 	public void setActive(Boolean isActive) {
 		this.active = isActive;
 	}
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPApprenticeshipArea)
        {
        	VIPPApprenticeshipArea temp = (VIPPApprenticeshipArea)obj;
            return name.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
