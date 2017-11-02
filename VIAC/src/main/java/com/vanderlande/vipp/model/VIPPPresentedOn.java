package com.vanderlande.vipp.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPPresentedOn.
 */
@Entity
@Table(name = "VIPP_PresentedOn")
public class VIPPPresentedOn implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP presentedOn.
     */
    public VIPPPresentedOn()
    {
    }

    /**
     * Instantiates a new VIPP presentedOn.
     */
    public VIPPPresentedOn(VIPPApprentice apprentice, VIPPSubject subject, VIPPPresentationCycle cycle)
    {
    	this.apprentice = apprentice;
    	this.subject = subject;
    	this.cycle = cycle;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentedOn_id")
    private long id;
    
	/** The apprentice. */
    @ManyToOne
    @FilterField(name = "presentedOn_apprentice")
    private VIPPApprentice apprentice;
    
    @ManyToOne
    @FilterField(name = "presentedOn_subject")
    private VIPPSubject subject;
    
    @ManyToOne
    @FilterField(name = "presentedOn_cycle")
    private VIPPPresentationCycle cycle;
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public VIPPApprentice getApprentice() {
		return apprentice;
	}

	public void setApprentice(VIPPApprentice apprentice) {
		this.apprentice = apprentice;
	}

	public VIPPSubject getSubject() {
		return subject;
	}

	public void setSubject(VIPPSubject subject) {
		this.subject = subject;
	}

	public VIPPPresentationCycle getCycle() {
		return cycle;
	}

	public void setCycle(VIPPPresentationCycle cycle) {
		this.cycle = cycle;
	}
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPPresentedOn)
        	return this.getId() == ((VIPPPresentedOn)obj).getId();
        return false;
    }
}
