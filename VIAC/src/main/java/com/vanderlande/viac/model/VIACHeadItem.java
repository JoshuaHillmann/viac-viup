package com.vanderlande.viac.model;

import java.io.Serializable;

/**
 * The Class VIACHeadItem.
 */
public class VIACHeadItem implements Serializable
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The name. */
    private String name;

    /** The page. */
    private Class page;

    /**
     * Instantiates a new VIAC head item.
     */
    public VIACHeadItem()
    {

    }

    /**
     * Instantiates a new VIAC head item.
     *
     * @param name the name
     * @param page the page
     */
    public VIACHeadItem(String name, Class page)
    {
        this.name = name;
        this.page = page;
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
     * @param name the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Gets the page.
     *
     * @return the page
     */
    public Class getPage()
    {
        return page;
    }

    /**
     * Sets the page.
     *
     * @param page the new page
     */
    public void setPage(Class page)
    {
        this.page = page;
    }
}
