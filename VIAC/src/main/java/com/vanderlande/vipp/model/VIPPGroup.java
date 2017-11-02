package com.vanderlande.vipp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPGroup.
 */
@Entity
@Table(name = "VIPP_GROUP")
public class VIPPGroup implements Serializable
{	
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP group.
     */
    public VIPPGroup()
    {
    }

    /**
     * Instantiates a new VIPP group.
     *
     * @param group_name
     *            the group name
     */
    public VIPPGroup(String group_name)
    {
        this.name = group_name;
    }

    public VIPPGroup(List<VIPPApprentice> groupMembers) {
    	System.out.println(groupMembers);
    	this.apprentice = groupMembers;
	}

	/** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private long id;

    /** The name. */
    @Column(name = "group_name")
    private String name;
    
    /** The person. */
    @ManyToMany(fetch = FetchType.EAGER, cascade =
    {
      CascadeType.MERGE, CascadeType.PERSIST
    })
    private List<VIPPApprentice> apprentice = new ArrayList<VIPPApprentice>();
    
    /** The cycle. */
    @ManyToOne
    @FilterField(name = "group_presentationCycle")
    private VIPPPresentationCycle cycle;
    

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
     * Gets the person.
     *
     * @return the person
     */
    public List<VIPPApprentice> getPersons()
    {
        return apprentice;
    }

    /**
     * Sets the person.
     *
     * @param person
     *            the new person
     */
    public void setPersons(List<VIPPApprentice> apprentice)
    {
        this.apprentice = apprentice;
    }
    
    /**
     * Gets the cycle.
     *
     * @return the cycle
     */
    public VIPPPresentationCycle getCycle()
    {
        return cycle;
    }

    /**
     * Sets the cycle.
     *
     * @param cycle
     *            the new cycle
     */
    public void setCycle(VIPPPresentationCycle cycle)
    {
        this.cycle = cycle;
    }
    
    public String toString()
    {
    	return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPGroup)
        {
        	VIPPGroup temp = (VIPPGroup)obj;
            return name.equals(temp.getName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
