package com.vanderlande.vita.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VITATestStatus.
 */
@Entity
@Table(name = "VITA_TEST_STATUS")
public class VITATestStatus implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 965533530685869497L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_status_id")
    private long id;

    /** The name. */
    @Column(name = "test_status_name")
    private String name;

    /**
     * Instantiates a new VITA test status.
     */
    public VITATestStatus()
    {
    }

    /**
     * Instantiates a new VITA test status.
     *
     * @param name
     *            the name
     */
    public VITATestStatus(String name)
    {
        this.name = name;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        return (this.id == ((VITATestStatus)o).getId());
    }
}
