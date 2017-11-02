package com.vanderlande.vipp.subject.data;

import java.io.Serializable;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPLearningUnit;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.model.VIPPSubjectArea;

public class SubjectTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;

	private String name;

    private String isFinalExamRelevant;
        
    @FilterField(name="name")
    private VIPPSubjectArea subjectArea;
    
    private String apprenticeshipArea;
    
    @FilterField(name = "name")
    private VIPPApprentice lastPresentedBy;
    
    private String learningUnit;
    
    @FilterField(name = "name")
    private VIPPPresentationCycle presentationCycle;

    private VIPPSubject subject;

	/**
     * Instantiates a new subject table model.
     *
     * @param subject
     *            the subject
     */
    public SubjectTableModel(VIPPSubject subject)
    {
        this.name = subject.getName();
        if(subject.getIsFinalExamRelevant())
        {
        	this.isFinalExamRelevant = "ja";
        }
        else
        {
        	this.isFinalExamRelevant = "nein";
        }
        this.subjectArea = subject.getSubjectArea();
        for(VIPPApprenticeshipArea area : subject.getApprenticeshipArea())
        {
        	if(apprenticeshipArea == null)
        	{
        		apprenticeshipArea = area.getName();
        	}
        	else
        	{
        		apprenticeshipArea = apprenticeshipArea + ", " +  area.getName();
        	}
        }
        for(VIPPLearningUnit unit : subject.getLearningUnit())
        {
        	if(learningUnit == null)
        	{
        		learningUnit = unit.getNumber();
        	}
        	else
        	{
        		learningUnit = learningUnit + ", " +  unit.getNumber();
        	}
        }
        this.lastPresentedBy = subject.getLastPresentedBy();
        this.presentationCycle = subject.getPresentationCycle();
        this.subject = subject;
        if(subject.getId() == 1)
        {
        	this.learningUnit = "";
        	this.apprenticeshipArea = "";
        	this.isFinalExamRelevant = "";
        }
    }

    public SubjectTableModel()
    {
    }
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIsFinalExamRelevant() {
		return isFinalExamRelevant;
	}

	public void setIsFinalExamRelevant(String isFinalExamRelevant) {
		this.isFinalExamRelevant = isFinalExamRelevant;
	}

	public VIPPSubjectArea getSubjectArea() {
		return subjectArea;
	}

	public void setSubjectArea(VIPPSubjectArea subjectArea) {
		this.subjectArea = subjectArea;
	}

	public String getApprenticeshipArea() {
		return apprenticeshipArea;
	}

	public void setApprenticeshipArea(String apprenticeshipArea) {
		this.apprenticeshipArea = apprenticeshipArea;
	}

	public VIPPApprentice getLastPresentedBy() {
		return lastPresentedBy;
	}

	public void setLastPresentedBy(VIPPApprentice lastPresentedBy) {
		this.lastPresentedBy = lastPresentedBy;
	}

	public String getLearningUnit() {
		return learningUnit;
	}

	public void setLearningUnit(String learningUnit) {
		this.learningUnit = learningUnit;
	}

	public VIPPPresentationCycle getPresentationCycle() {
		return presentationCycle;
	}

	public void setPresentationCycle(VIPPPresentationCycle presentationCycle) {
		this.presentationCycle = presentationCycle;
	}
	
    public VIPPSubject getSubject() {
		return subject;
	}

	public void setSubject(VIPPSubject subject) {
		this.subject = subject;
	}
}
