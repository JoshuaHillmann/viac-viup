package com.vanderlande.vipp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;

/**
 * The Class VIPPPerson.
 */
@MappedSuperclass
@Table(name = "VIPP_PERSON")
public abstract class VIPPPerson implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP group.
     */
    public VIPPPerson()
    {
    }

    /**
     * Instantiates a new VIPP group.
     *
     * @param person_name
     *            the person name
     */
    public VIPPPerson(String person_lastname, String person_firstname)
    {
        this.lastname = person_lastname;
        this.firstname = person_firstname;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    private long id;

    /** The name. */
    @Column(name = "person_lastname")
    private String lastname;
    
    /** The firstname. */
    @Column(name = "person_firstname")
    private String firstname;
    

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
     * Gets the lastname.
     *
     * @return the lastname
     */
    public String getLastname()
    {
        return lastname;
    }

    /**
     * Sets the lastname.
     *
     * @param lastname
     *            the new lastname
     */
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }
    
    /**
     * Gets the firstname.
     *
     * @return the firstname
     */
    public String getFirstname()
    {
        return firstname;
    }

    /**
     * Sets the firstname.
     *
     * @param firstname
     *            the new firstname
     */
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPGroup)
        {
        	VIPPGroup temp = (VIPPGroup)obj;
            return lastname.equals(temp.getName()) && firstname.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
