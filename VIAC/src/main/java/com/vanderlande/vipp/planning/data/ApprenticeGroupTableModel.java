package com.vanderlande.vipp.planning.data;

import java.io.Serializable;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;

public class ApprenticeGroupTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;

    private String firstname;

    private String lastname;

    private int yearOfApprenticeship;

    @FilterField(name = "name")
    private VIPPApprenticeshipArea apprenticeshipArea;
    
    @FilterField(name = "name")
    private VIPPGroup group;
    
    private VIPPApprentice apprentice;

    /**
     * Instantiates a new apprentice group table model.
     *
     * @param apprentice
     *            the apprentice
     */
    public ApprenticeGroupTableModel(VIPPApprentice apprentice)
    {
        this.firstname = apprentice.getFirstname();
        this.lastname = apprentice.getLastname();
        this.yearOfApprenticeship = apprentice.getYearOfApprenticeship();
        this.apprenticeshipArea = apprentice.getApprenticeshipArea();
        this.group = apprentice.getCurrentGroup();
        this.apprentice = apprentice;
    }

    public ApprenticeGroupTableModel()
    {
    }

 
    public String getFirstname()
    {
        return firstname;
    }

    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }
    
    public String getLastname()
    {
        return lastname;
    }

    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }
    
    public String getYearOfApprenticeship()
    {
        return Integer.toString(yearOfApprenticeship);
    }

    public void setYearOfApprenticeship(String yearOfApprenticeship)
    {
        this.yearOfApprenticeship = Integer.parseInt(yearOfApprenticeship);
    }
    
    public VIPPApprenticeshipArea getApprenticeshipArea()
    {
        return apprenticeshipArea;
    }

    public void setApprenticeshipArea(VIPPApprenticeshipArea apprenticeshipArea)
    {
        this.apprenticeshipArea = apprenticeshipArea;
    }
    
    public VIPPGroup getGroup()
    {
    	return group;
    }
    
    public void setGroup(VIPPGroup group)
    {
    	this.group = group;
    }
    
    public VIPPApprentice getApprentice()
    {
    	return apprentice;
    }
    
    public void setApprentice(VIPPApprentice apprentice)
    {
    	this.apprentice = apprentice;
    }
}
