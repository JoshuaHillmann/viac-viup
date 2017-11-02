package com.vanderlande.vipp.archive.provider;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.util.HibernateUtil;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPPresentation;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.model.VIPPPresentedOn;
import com.vanderlande.vipp.model.VIPPSubject;

public class ArchiveTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;
    
	private Session session;
	private SessionFactory factory = HibernateUtil.getSessionFactory();

    private String firstname;

    private String lastname;

    private int yearOfApprenticeship;

    @FilterField(name = "name")
    private VIPPApprenticeshipArea apprenticeshipArea;
    
    @FilterField(name = "name")
    private VIPPGroup group;
    
    @FilterField(name = "name")
    private VIPPSubject subject;
    
    private VIPPPresentation presentation;
    
    private VIPPApprentice apprentice;
    
    private VIPPPresentationCycle cycle;

    public ArchiveTableModel(VIPPApprentice apprentice, VIPPPresentationCycle cycle)
    {
        this.firstname = apprentice.getFirstname();
        this.lastname = apprentice.getLastname();
        this.yearOfApprenticeship = apprentice.getYearOfApprenticeship();
        this.apprenticeshipArea = apprentice.getApprenticeshipArea();
        this.apprentice = apprentice;
        this.cycle = cycle;
        getOtherValues(apprentice, cycle);
    }

    public ArchiveTableModel()
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
    
    public VIPPSubject getSubject()
    {
    	return subject;
    }
    
    public void setSubject(VIPPSubject subject)
    {
    	this.subject = subject;
    }
    
    public VIPPPresentation getPresentation()
    {
    	return presentation;
    }
    
    public void setPresentation(VIPPPresentation presentation)
    {
    	this.presentation = presentation;
    }
    
    public VIPPApprentice getApprentice()
    {
    	return apprentice;
    }
    
    public void setApprentice(VIPPApprentice apprentice)
    {
    	this.apprentice = apprentice;
    }
    
    private void getOtherValues(VIPPApprentice apprentice, VIPPPresentationCycle cycle)
    {
    	List<VIPPPresentation> presentations;
    	session = factory.openSession();
    	presentations = session.createQuery("from VIPPPresentation WHERE presentationCycle_presentationCycle_id = " + cycle.getId()
    	+ "AND apprentice_apprentice_id = " + apprentice.getId(), VIPPPresentation.class)
    			.getResultList();
    	session.close();
    	if(presentations.size() != 0)
    	{
    		this.presentation = presentations.get(0);    		
    	}
    	else
    	{
    		this.presentation = null;
    	}
    	
    	List<VIPPGroup> groups;
    	session = factory.openSession();
    	groups = session.createQuery("from VIPPGroup WHERE cycle_presentationCycle_id = " + cycle.getId(), VIPPGroup.class)
    			.getResultList();
    	session.close();
    	
    	for(VIPPGroup vippGroup : groups)
    	{
    		if(vippGroup.getPersons().contains(apprentice))
    		{
    			this.group = vippGroup;
    		}
    	}
    	
    	List<VIPPPresentedOn> presentedList;
    	session = factory.openSession();
    	presentedList = session.createQuery("from VIPPPresentedOn WHERE cycle_presentationCycle_id = " + cycle.getId()
    	+ " AND apprentice_apprentice_id = " + apprentice.getId(), VIPPPresentedOn.class)
    			.getResultList();
    	session.close();
    	
    	if(presentedList.size() != 0)
    	{
    		this.subject = presentedList.get(0).getSubject();
    	}
    	else
    	{
    		this.subject = null;
    	}
    }
}