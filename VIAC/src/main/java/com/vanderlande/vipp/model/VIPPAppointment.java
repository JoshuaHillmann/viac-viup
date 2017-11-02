package com.vanderlande.vipp.model;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPAppointment.
 */
@Entity
@Table(name = "VIPP_APPOINTMENT")
public class VIPPAppointment implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP appointment.
     */
    public VIPPAppointment()
    {
    }

    /**
     * Instantiates a new VIPP appointment.
     *
     * @param appointment_date
     *            the appointment date
     */
    public VIPPAppointment(Date appointment_date, boolean appointment_morning)
    {
        this.date = appointment_date;
        this.morning = appointment_morning;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private long id;

    /** The date. */
    @Column(name = "appointment_date")
    private Date date;
    
    /** The morning. */
    @Column(name = "appointment_morning")
    private boolean morning;
    
    /** The group. */
    @ManyToOne
    @FilterField(name = "appointment_group")
    private VIPPGroup group;
    

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
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date
     *            the new date
     */
    public void setName(Date date)
    {
        this.date = date;
    }
    
    
    /**
     * Gets the group.
     *
     * @return the group
     */
    public VIPPGroup getGroup()
    {
        return group;
    }

    /**
     * Sets the group.
     *
     * @param group
     *            the new group
     */
    public void setGroup(VIPPGroup group)
    {
        this.group = group;
    }
    
    /**
     * Gets the morning.
     *
     * @return the morning
     */
    public boolean getMorning()
    {
        return morning;
    }

    /**
     * Sets the morning.
     *
     * @param morning
     *            the new morning
     */
    public void setMorning(boolean morning)
    {
        this.morning = morning;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPAppointment)
        {
        	VIPPAppointment temp = (VIPPAppointment)obj;
            return date.equals(temp.getDate()) && morning == temp.getMorning();
        }
        else
        {
            return super.equals(obj);
        }
    }
}
