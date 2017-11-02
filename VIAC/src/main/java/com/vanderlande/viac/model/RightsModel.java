package com.vanderlande.viac.model;

import java.io.Serializable;

/**
 * The Class RightsModel.
 */
public class RightsModel implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8271040254102259763L;

    /** The access right. */
    private String accessRight;

    /** The modul name. */
    private String modulName;

    /**
     * Gets the access right.
     *
     * @return the access right
     */
    public String getAccessRight()
    {
        return accessRight;
    }

    /**
     * Gets the modul name.
     *
     * @return the modul name
     */
    public String getModulName()
    {
        return modulName;
    }

    /**
     * Sets the modul name.
     *
     * @param modulName the new modul name
     */
    public void setModulName(String modulName)
    {
        this.modulName = modulName;
    }
}
