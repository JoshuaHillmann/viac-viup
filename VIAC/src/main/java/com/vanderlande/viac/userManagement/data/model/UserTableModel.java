package com.vanderlande.viac.userManagement.data.model;

import java.io.Serializable;

import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;

/**
 * The Class UserTableModel.
 * 
 * @author dekscha
 * 
 *         The UserTableModel encapsulates a VIACUser and converts his roles to a String to use it in a DataTable.
 */
public class UserTableModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6911087940003007831L;

    /** The roles. */
    private String roles;

    /** The name. */
    private String name;

    /** The firstname. */
    private String firstname;

    /** The lastname. */
    private String lastname;

    /** The user. */
    private VIACUser user;

    /** The id. */
    private long id;

    /**
     * Instantiates a new user table model. If the user has VIACRoles, the roles field of the UserTableModel is set to
     * the role names as a comma separated String
     *
     * @param user
     *            the user
     */
    public UserTableModel(VIACUser user)
    {
        this.id = user.getId();
        this.name = user.getName();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.roles = "";
        this.user = user;
        for (VIACRole vr : user.getRoles())
        {
            roles += vr.getName();
            roles += ", ";
        }
        if (!user.getRoles().isEmpty())
            roles = roles.substring(0, roles.length() - 2);
    }

    public UserTableModel()
    {
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public String getRoles()
    {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles
     *            the new roles
     */
    public void setRoles(String roles)
    {
        this.roles = roles;
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
     * Gets the user.
     *
     * @return the user
     */
    public VIACUser getUser()
    {
        return user;
    }

    /**
     * Sets the user.
     *
     * @param user
     *            the new user
     */
    public void setUser(VIACUser user)
    {
        this.user = user;
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
}
