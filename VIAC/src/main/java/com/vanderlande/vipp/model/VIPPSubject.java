package com.vanderlande.vipp.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPSubject.
 */
@Entity
@Table(name = "VIPP_SUBJECT")
public class VIPPSubject implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP subject.
     */
    public VIPPSubject()
    {
    }

    /**
     * Instantiates a new VIPP subject.
     *
     * @param subject_name
     *            the subject name
     */
    public VIPPSubject(String subject_name)
    {
        this.name = subject_name;
    }

    public VIPPSubject(Boolean isFinalExamRelevant, String name, VIPPSubjectArea area, Set<VIPPApprenticeshipArea> apprenticeshipArea, VIPPPresentationCycle presentationCycle) {
    	this.isFinalExamRelevant = isFinalExamRelevant;
    	this.name = name;
    	this.subjectArea = area;
    	this.apprenticeshipArea = apprenticeshipArea;
    	this.presentationCycle = presentationCycle;
	}
    
    public VIPPSubject(Boolean isFinalExamRelevant, String name, VIPPSubjectArea area, List<VIPPLearningUnit> units, Set<VIPPApprenticeshipArea> apprenticeshipArea, VIPPPresentationCycle presentationCycle) {
    	this.isFinalExamRelevant = isFinalExamRelevant;
    	this.name = name;
    	this.subjectArea = area;
    	this.learningUnits = units;
    	this.apprenticeshipArea = apprenticeshipArea;
    	this.presentationCycle = presentationCycle;
	}

	/** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private long id;

    /** The name. */
    @Column(name = "subject_name")
    private String name;
    
    /** The lastPresentedBy. */
    @ManyToOne
    @FilterField(name = "subject_lastPresentedBy")
    private VIPPApprentice lastPresentedBy;
    
    /** The learningUnit. */
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade =
    {
    		CascadeType.MERGE, CascadeType.PERSIST
    })
    private List<VIPPLearningUnit> learningUnits;
    
    /** The relevant for. */
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @OrderColumn
    @JoinTable(name = "VIPP_SUBJECT_VIPP_APPRENTICESHIPAREA", joinColumns = { @JoinColumn(name = "SUBJECT_ID") }, inverseJoinColumns = { @JoinColumn(name = "APPRENTICESHIPAREA_ID") }) 
    private Set<VIPPApprenticeshipArea> apprenticeshipArea = new HashSet<VIPPApprenticeshipArea>();

    /** The finalExaminationRelevant. */
    @Column(name = "subject_isFinalExamRelevant")
    private Boolean isFinalExamRelevant;
    
    /** The isActive. */
    @Column(name = "subject_isActive")
    private Boolean isActive = true;
    
    /** The area. */
    @ManyToOne
    @FilterField(name = "name")
    private VIPPSubjectArea subjectArea;
    
    @ManyToOne
    @FilterField(name = "subject_presentationCycle")
    private VIPPPresentationCycle presentationCycle;
    
    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "performedPresentations")
    private Set<VIPPApprentice> apprentices;
    
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
    
    /**
     * Gets the lastPresentedBy.
     *
     * @return the lastPresentedBy
     */
    public VIPPApprentice getLastPresentedBy()
    {
        return lastPresentedBy;
    }

    /**
     * Sets the lastPresentedBy.
     *
     * @param lastPresentedBy
     *            the new lastPresentedBy
     */
    public void setLastPresentedBy(VIPPApprentice lastPresentedBy)
    {
        this.lastPresentedBy = lastPresentedBy;
    }
    
    /**
     * Gets the subjectArea.
     *
     * @return the subjectArea
     */
    public VIPPSubjectArea getSubjectArea()
    {
        return subjectArea;
    }

    /**
     * Sets the subjectArea.
     *
     * @param subjectArea
     *            the new subjectArea
     */
    public void setSubjectArea(VIPPSubjectArea subjectArea)
    {
        this.subjectArea = subjectArea;
    }
    
    /**
     * Gets the learningUnit.
     *
     * @return the learningUnit
     */
    public List<VIPPLearningUnit> getLearningUnit()
    {
        return learningUnits;
    }

    /**
     * Sets the isFinalExamRelevant.
     *
     * @param isFinalExamRelevant
     *            the new isFinalExamRelevant
     */
    public void setLearningUnit(List<VIPPLearningUnit> selectedUnitsList)
    {
        this.learningUnits = selectedUnitsList;
    }
    
    /**
     * Gets the isFinalExamRelevant.
     *
     * @return the isFinalExamRelevant
     */
    public Boolean getIsFinalExamRelevant()
    {
        return isFinalExamRelevant;
    }

    /**
     * Sets the isFinalExamRelevant.
     *
     * @param isFinalExamRelevant
     *            the new isFinalExamRelevant
     */
    public void setIsFinalExamRelevant(Boolean isFinalExamRelevant)
    {
        this.isFinalExamRelevant = isFinalExamRelevant;
    }
    
    /**
     * Gets the isActive.
     *
     * @return the isActive
     */
    public Boolean isIsActive()
    {
        return isActive;
    }

    /**
     * Sets the isActive.
     *
     * @param isActive
     *            the new isActive
     */
    public void setIsActive(Boolean isActive)
    {
        this.isActive = isActive;
    }
    
    /**
     * Gets the apprenticeshipArea.
     *
     * @return the apprenticeshipArea
     */
    public Set<VIPPApprenticeshipArea> getApprenticeshipArea()
    {
        return apprenticeshipArea;
    }

    /**
     * Sets the apprenticeshipArea.
     *
     * @param apprenticeshipArea
     *            the new apprenticeshipArea
     */
    public void setApprenticeshipArea(Set<VIPPApprenticeshipArea> apprenticeshipArea)
    {
    	this.apprenticeshipArea = apprenticeshipArea;
    }

    /**
     * Sets the apprenticeshipArea.
     *
     * @param apprenticeshipArea
     *            the new apprenticeshipArea
     */
    public void setPresentationCycle(VIPPPresentationCycle presentationCycle)
    {
        this.presentationCycle = presentationCycle;
    }
    
    /**
     * Gets the 
     *
     * @return the 
     */
    public VIPPPresentationCycle getPresentationCycle()
    {
        return presentationCycle;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPSubject)
        {
        	VIPPSubject temp = (VIPPSubject)obj;
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

	public Set<VIPPApprentice> getApprentices() {
		return apprentices;
	}

	public void setApprentices(Set<VIPPApprentice> apprentices) {
		this.apprentices = apprentices;
	}
}
