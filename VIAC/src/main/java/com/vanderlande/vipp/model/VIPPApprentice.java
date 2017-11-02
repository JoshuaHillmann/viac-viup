package com.vanderlande.vipp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPApprentice.
 */
@Entity
@Table(name = "VIPP_APPRENTICE")
public class VIPPApprentice implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP apprentice.
     */
    public VIPPApprentice()
    {
    }

    /**
     * Instantiates a new VIPP apprentice.
     *
     * @param apprentice_name
     *            the apprentice name
     */
    public VIPPApprentice(String apprentice_firstname, String apprentice_lastname, VIPPApprenticeshipArea apprentice_apprenticeshipArea, int apprentice_yearOfApprenticeship)
    {
    	this.firstname = apprentice_firstname;
    	this.lastname = apprentice_lastname;
        this.apprenticeshipArea = apprentice_apprenticeshipArea;
        this.yearOfApprenticeship = apprentice_yearOfApprenticeship;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "apprentice_id")
    private long id;
    
    @Column(name = "apprentice_firstname")
    private String firstname;
    
    @Column(name = "apprentice_lastname")
    private String lastname;    
    
    @Column(name = "apprentice_yearOfApprenticeship")
    private int yearOfApprenticeship;
    
    @ManyToOne
    @FilterField(name = "apprentice_apprenticeshipArea")
    private VIPPApprenticeshipArea apprenticeshipArea;
    
    @Column(name = "apprentice_isActive")
    private Boolean isActive = true;
    
    @ManyToOne
    @FilterField(name = "apprentice_assignedSubject")
    private VIPPSubject assignedSubject;

    /** The performedPresentations. */
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade =
    {
      CascadeType.MERGE, CascadeType.PERSIST
    })
    private List<VIPPSubject> performedPresentations;
    
    @ManyToOne
    @FilterField(name = "group_name")
    private VIPPGroup currentGroup;
    
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
     * Gets the yearOfTraining.
     *
     * @return the yearOfTraining
     */
    public int getYearOfApprenticeship()
    {
        return yearOfApprenticeship;
    }

    /**
     * Sets the yearOfTraining.
     *
     * @param yearOfTraining
     *            the new yearOfTraining
     */
    public void setYearOfApprenticeship(int yearOfApprenticeship)
    {
        this.yearOfApprenticeship = yearOfApprenticeship;
    }
    
    /**
     * Gets the apprenticeshipArea.
     *
     * @return the apprenticeshipArea
     */
    public VIPPApprenticeshipArea getApprenticeshipArea()
    {
        return apprenticeshipArea;
    }

    /**
     * Sets the apprenticeshipArea.
     *
     * @param apprenticeshipArea
     *            the new apprenticeshipArea
     */
    public void setApprenticeshipArea(VIPPApprenticeshipArea apprenticeshipArea)
    {
        this.apprenticeshipArea = apprenticeshipArea;
    }
    
    public Boolean isActive()
    {
    	return isActive;
    }
    
    public void setActive(Boolean isActive)
    {
    	this.isActive = isActive;
    }
    
    public VIPPSubject getAssignedSubject()
    {
    	return assignedSubject;
    }
    
    public void setAssignedSubject(VIPPSubject assignedSubject)
    {
    	this.assignedSubject = assignedSubject;
    }
    
    /**
     * Gets the 
     *
     * @return the 
     */
    public List<VIPPSubject> getPerformedPresentations()
    {
        return performedPresentations;
    }

    /**
     * Sets the isFinalExamRelevant.
     *
     * @param isFinalExamRelevant
     *            the new isFinalExamRelevant
     */
    public void setPerformedPresentations(List<VIPPSubject> performedPresentations)
    {
        this.performedPresentations = performedPresentations;
    }
    
    
    public VIPPGroup getCurrentGroup()
    {
    	return currentGroup;
    }
    
    public void setCurrentGroup(VIPPGroup currentGroup)
    {
    	this.currentGroup = currentGroup;
    }
    
    public List<VIPPSubject> getSubjectList()
    {
    	List<VIPPSubject> performedPresentations = new ArrayList<VIPPSubject>();
    	performedPresentations.addAll(this.getPerformedPresentations());
    	return performedPresentations;
    }
    
    public String toString()
    {
    	return firstname + " " + lastname;
    }    
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPApprentice)
        	return this.getId() == ((VIPPApprentice)obj).getId();
        return false;
    }
}
