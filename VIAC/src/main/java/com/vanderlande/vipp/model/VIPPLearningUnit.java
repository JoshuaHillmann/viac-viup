package com.vanderlande.vipp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VIPPLearningUnit.
 */
@Entity
@Table(name = "VIPP_LEARNINGUNIT")
public class VIPPLearningUnit implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP learningUnit.
     */
    public VIPPLearningUnit()
    {
    }

    /**
     * Instantiates a new VIPP learningUnit.
     *
     * @param learningUnit_name
     *            the learningUnit name
     */
    public VIPPLearningUnit(String learningUnit_number, String learningUnit_name)
    {
        this.name = learningUnit_name;
        this.number = learningUnit_number;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learningUnit_id")
    private long id;

    /** The number. */
    @Column(name = "learningUnit_number")
    private String number;

    /** The name. */
    @Column(name = "learningUnit_name")
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
     * Gets the number.
     *
     * @return the number
     */
    public String getNumber()
    {
        return number;
    }

    /**
     * Sets the number.
     *
     * @param number
     *            the new number
     */
    public void setNumber(String number)
    {
        this.number = number;
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
    	return number;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPLearningUnit)
        {
        	VIPPLearningUnit temp = (VIPPLearningUnit)obj;
            return number == temp.getNumber();
        }
        else
        {
            return super.equals(obj);
        }
    }
}
