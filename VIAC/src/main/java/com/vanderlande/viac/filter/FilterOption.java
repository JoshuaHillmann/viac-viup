package com.vanderlande.viac.filter;

import java.io.Serializable;

/**
 * The Class FilterOption.
 * 
 * @author dekscha
 * 
 *         You have to pass a list of FilterOptions to the VIACFilterPanel. Every FilterOption that is in the list will
 *         be available for filtering.
 */
public class FilterOption implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -6684866569101081542L;

    /** The filter name. */
    private String filterName;

    /** The property field. */
    private String propertyField;

    /**
     * Instantiates a new filter option.
     *
     * @param filterName
     *            the filter name
     * @param propertyField
     *            the property field
     */
    public FilterOption(String filterName, String propertyField)
    {
        this.setFilterName(filterName);
        this.setPropertyField(propertyField);
    }

    /**
     * Gets the filter name.
     *
     * @return the filter name
     */
    public String getFilterName()
    {
        return filterName;
    }

    /**
     * Sets the filter name.
     *
     * @param filterName
     *            the new filter name
     */
    public void setFilterName(String filterName)
    {
        this.filterName = filterName;
    }

    /**
     * Gets the property field.
     *
     * @return the property field
     */
    public String getPropertyField()
    {
        return propertyField;
    }

    /**
     * Sets the property field.
     *
     * @param propertyField
     *            the new property field
     */
    public void setPropertyField(String propertyField)
    {
        this.propertyField = propertyField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return this.filterName;
    }
}
