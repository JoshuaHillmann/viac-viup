package com.vanderlande.vita.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class VITAStatusHistory.
 */
@Entity
@Table(name = "VITA_STATUS_HISTORY")
public class VITAHistoricStatus implements Serializable
{
    /**
     * Instantiates a new VITA historic status.
     */
    public VITAHistoricStatus()
    {
    }

    /**
     * Instantiates a new VITA historic status.
     *
     * @param name
     *            the name
     */
    public VITAHistoricStatus(VITAApplicantStatus name)
    {
        this.name = name;
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7699661823752046376L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_history_id")
    private long id;

    /** The status. */
    @ManyToOne
    private VITAApplicantStatus name;

    /** The changed on. */
    @Column(name = "changed")
    private Date changedOn = new Date();

    /** The changed from. */
    @ManyToOne
    private VITAApplicant changedFrom;

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
    public VITAApplicantStatus getName()
    {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(VITAApplicantStatus name)
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
        return name.toString();
    }

    /**
     * Gets the changed on.
     *
     * @return the changed on
     */
    public Date getChangedOn()
    {
        return changedOn;
    }

    /**
     * Sets the changed on.
     *
     * @param changed
     *            On the new changed on
     */
    public void setChangedOn(Date changedOn)
    {
        this.changedOn = changedOn;
    }

    /**
     * Gets the changed from.
     *
     * @return the changed from
     */
    public VITAApplicant getChangedFrom()
    {
        return changedFrom;
    }

    /**
     * Sets the changed from.
     *
     * @param changedFrom
     *            the new changed from
     */
    public void setChangedFrom(VITAApplicant changedFrom)
    {
        this.changedFrom = changedFrom;
    }
}
