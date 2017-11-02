package com.vanderlande.vita.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VITACareer.
 */
@Entity
@Table(name = "VITA_CAREER")
public class VITACareer implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VITA career.
     */
    public VITACareer()
    {
    }

    /**
     * Instantiates a new VITA career.
     *
     * @param career_name
     *            the career name
     */
    public VITACareer(String career_name)
    {
        this.name = career_name;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private long id;

    /** The name. */
    @Column(name = "career_name")
    private String name;

    /** The isOutdated. */
    @Column(name = "is_active")
    private boolean isActive = true;

    /**
     * Checks if is active.
     *
     * @return true, if is active
     */
    public boolean isActive()
    {
        return isActive;
    }

    /**
     * Sets the active.
     *
     * @param isActive
     *            the new active
     */
    public void setActive(boolean isActive)
    {
        this.isActive = isActive;
    }

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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VITACareer)
        {
            VITACareer temp = (VITACareer)obj;
            return name.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
