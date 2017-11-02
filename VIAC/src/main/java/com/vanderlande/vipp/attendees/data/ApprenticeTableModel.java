package com.vanderlande.vipp.attendees.data;

import java.io.Serializable;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;

public class ApprenticeTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;

    private String firstname;

    private String lastname;

    private int yearOfApprenticeship;

    @FilterField(name = "name")
    private VIPPApprenticeshipArea apprenticeshipArea;
    
    private VIPPApprentice apprentice;

    /**
     * Instantiates a new apprentice table model.
     *
     * @param apprentice
     *            the apprentice
     */
    public ApprenticeTableModel(VIPPApprentice apprentice)
    {
        this.firstname = apprentice.getFirstname();
        this.lastname = apprentice.getLastname();
        this.yearOfApprenticeship = apprentice.getYearOfApprenticeship();
        this.apprenticeshipArea = apprentice.getApprenticeshipArea();
        this.apprentice = apprentice;
    }

    public ApprenticeTableModel()
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
    
    public VIPPApprentice getApprentice()
    {
    	return apprentice;
    }
    
    public void setApprentice(VIPPApprentice apprentice)
    {
    	this.apprentice = apprentice;
    }
}
