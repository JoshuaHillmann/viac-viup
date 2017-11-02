package com.vanderlande.viac.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class VIACUserPasswordReset.
 */
@Entity
@Table(name = "VIAC_USER_PASSWORD_RESET")
public class VIACUserPasswordReset implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9083620773192277084L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reset_id")
    private long id;

    /** The user. */
    @ManyToOne
    private VIACUser user;

    /** The token. */
    private String token;

    /** The created on. */
    @Column(name = "created_on")
    private Date createdOn;

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
     * Gets the token.
     *
     * @return the token
     */
    public String getToken()
    {
        return token;
    }

    /**
     * Sets the token.
     *
     * @param token
     *            the new token
     */
    public void setToken(String token)
    {
        this.token = token;
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
}
