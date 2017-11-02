package com.vanderlande.viac.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The Class VIACConfiguration.
 */
@Entity
@Table(name = "VIAC_CONFIGURATION")
public class VIACConfiguration implements Serializable
{

    /**
     * Instantiates a new VIAC configuration.
     *
     * @param key
     *            the key
     * @param value
     *            the value
     */
    public VIACConfiguration(String key, String value)
    {
        this.key = key;
        this.value = value;
    }

    /**
     * Instantiates a new VIAC configuration.
     */
    public VIACConfiguration()
    {
    }

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5660191287580199691L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "config_id")
    private long id;

    /** The key. */
    @Column(name = "config_key", unique = true)
    private String key;

    /** The value. */
    @Column(name = "config_value", length = 8024)
    private String value;

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
     * Gets the key.
     *
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key
     *            the new key
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value
     *            the new value
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}
