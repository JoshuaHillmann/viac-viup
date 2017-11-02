package com.vanderlande.vipp.administration.data;

import java.io.Serializable;

import com.vanderlande.vipp.model.VIPPApprenticeshipArea;

/**
 * The Class ApprenticeshipAreaTableModel.
 * 
 * @author dermic
 * 
 *         the table model for the apprenticeship areas.
 */
public class ApprenticeshipAreaTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;

    private String name;

    private String active;
    
    private VIPPApprenticeshipArea apprenticeshipArea;

    /**
     * Instantiates a new apprenticeshipArea table model.
     *
     * @param apprenticeshipArea
     *            the apprenticeshipArea
     */
    public ApprenticeshipAreaTableModel(VIPPApprenticeshipArea apprenticeshipArea)
    {
        this.name = apprenticeshipArea.getName();
        if(apprenticeshipArea.isActive())
        {
        	this.active = "ja";
        }
        else
        {
        	this.active = "nein";
        }
        this.apprenticeshipArea = apprenticeshipArea;
    }

    public ApprenticeshipAreaTableModel()
    {
    }

 
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String isActive()
    {
        return active;
    }

    public void setActive(String isActive)
    {
        this.active = isActive;
    }
    
    public VIPPApprenticeshipArea getApprenticeshipArea()
    {
    	return apprenticeshipArea;
    }
    
    public void setApprenticeshipArea(VIPPApprenticeshipArea apprenticeshipArea)
    {
    	this.apprenticeshipArea = apprenticeshipArea;
    }
}
