package com.vanderlande.viac.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * The Class VIACAuthorization.
 */
@Entity
@Table(name = "VIAC_AUTHORIZATION")
public class VIACAuthorization implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 267484427812534279L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auth_id")
    private long id;

    /** The name. */
    @Column(name = "auth_name")
    private String name;

    /** The description. */
    @Column(name = "auth_description")
    private String description;

    /** The auth check name. */
    @Column(name = "auth_check_name")
    private String authCheckName;

    /** The roles. */
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "authorizations")
    private Set<VIACRole> roles;

    /**
     * Instantiates a new VIAC authorization.
     */
    public VIACAuthorization()
    {

    };

    /**
     * Instantiates a new VIAC authorization.
     *
     * @param authCheckName
     *            the auth check name
     * @param description
     *            the description
     * @param name
     *            the name
     */
    public VIACAuthorization(String authCheckName, String description, String name)
    {
        this.name = name;
        this.description = description;
        this.authCheckName = authCheckName;
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
     * Gets the auth check name.
     *
     * @return the auth check name
     */
    public String getAuthCheckName()
    {
        return authCheckName;
    }

    /**
     * Sets the auth check name.
     *
     * @param authCheckName
     *            the new auth check name
     */
    public void setAuthCheckName(String authCheckName)
    {
        this.authCheckName = authCheckName;
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public Set<VIACRole> getRoles()
    {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles
     *            the new roles
     */
    public void setRoles(Set<VIACRole> roles)
    {
        this.roles = roles;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof VIACAuthorization)
        {
            return ((VIACAuthorization)o).getId() == this.getId();
        }
        return true;

    }
}
