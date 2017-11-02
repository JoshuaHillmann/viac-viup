package com.vanderlande.viac.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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

/**
 * The Class VIACRole.
 */
@Entity
@Table(name = "VIAC_ROLE")
public class VIACRole implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 2474584764578338805L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private long id;

    /** The name. */
    @Column(name = "role_name")
    private String name;

    /** The description. */
    @Column(name = "role_description")
    private String description;

    /** The created by. */
    @ManyToOne
    private VIACUser createdBy;

    /** The created on. */
    @Column(name = "created_on")
    private Date createdOn = new Date();

    /** The changed by. */
    @ManyToOne
    private VIACUser changedBy;

    /** The changed on. */
    @Column(name = "changed_on")
    private Date changedOn;

    /** The authorizations. */
    @ManyToMany(fetch = FetchType.EAGER, cascade =
    {
      CascadeType.MERGE, CascadeType.PERSIST
    })
    private Set<VIACAuthorization> authorizations;

    /** The users. */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private Set<VIACUser> users;

    /**
     * Instantiates a new VIAC role.
     */
    public VIACRole()
    {
    }

    /**
     * Instantiates a new VIAC role.
     *
     * @param name
     *            the name
     * @param description
     *            the description
     */
    public VIACRole(String name, String description)
    {
        this.name = name;
        this.description = description;
    }

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
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description
     *            the new description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public VIACUser getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy
     *            the new created by
     */
    public void setCreatedBy(VIACUser createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Gets the created on.
     *
     * @return the created on
     */
    public Date getCreatedOn()
    {
        return createdOn;
    }

    /**
     * Sets the created on.
     *
     * @param createdOn
     *            the new created on
     */
    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    /**
     * Gets the changed by.
     *
     * @return the changed by
     */
    public VIACUser getChangedBy()
    {
        return changedBy;
    }

    /**
     * Sets the changed by.
     *
     * @param changedBy
     *            the new changed by
     */
    public void setChangedBy(VIACUser changedBy)
    {
        this.changedBy = changedBy;
    }

    /**
     * Gets the changed on.
     *
     * @return the changed on
     */
    public Date getChangedOn()
    {
        return changedOn;
    }

    /**
     * Sets the changed on.
     *
     * @param changedOn
     *            the new changed on
     */
    public void setChangedOn(Date changedOn)
    {
        this.changedOn = changedOn;
    }

    /**
     * Gets the authorizations.
     *
     * @return the authorizations
     */
    public Set<VIACAuthorization> getAuthorizations()
    {
        return authorizations;
    }

    /**
     * Sets the authorizations.
     *
     * @param selectedAccessRights
     *            the new authorizations
     */
    public void setAuthorizations(Set<VIACAuthorization> selectedAccessRights)
    {
        this.authorizations = selectedAccessRights;
    }

    /**
     * Gets the users.
     *
     * @return the users
     */
    public Set<VIACUser> getUsers()
    {
        return users;
    }

    /**
     * Sets the users.
     *
     * @param users
     *            the new users
     */
    public void setUsers(Set<VIACUser> users)
    {
        this.users = users;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIACRole)
            return this.id == ((VIACRole)obj).getId();
        return false;
    }

}
