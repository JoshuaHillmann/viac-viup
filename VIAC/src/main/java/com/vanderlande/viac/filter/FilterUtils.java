package com.vanderlande.viac.filter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.util.SortParam;

/**
 * The Class FilterUtils.
 *
 * @author dekscha
 * @param <T> the generic type
 * 
 *            Provides methods to filter and sort lists
 */
public class FilterUtils<T> implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3912182452122958537L;

    /**
     * Instantiates a new filter utils.
     */
    public FilterUtils()
    {

    }

    /**
     * Filters the list with the given filterOptions and filterBean
     *
     * @param listToFilter
     *            the list to filter
     * @param filterOptions
     *            the filter options
     * @param filterBean
     *            the filter bean
     * @return the list
     */
    public List<T> filter(List<T> listToFilter, List<FilterOption> filterOptions, T filterBean)
    {
        ArrayList<T> result = new ArrayList<T>();
        for (FilterOption filterProperty : filterOptions)
        {
            for (T entity : listToFilter)
            {
                try
                {
                    PropertyDescriptor descriptor =
                        new PropertyDescriptor(filterProperty.getPropertyField(), filterBean.getClass());
                    Object filterPropertyState = descriptor.getReadMethod().invoke(filterBean);
                    Object property = descriptor.getReadMethod().invoke(entity);
                    if (filterPropertyState != null)
                    {
                        if (!(String.valueOf(property))
                            .toUpperCase()
                            .contains((String.valueOf(filterPropertyState)).toUpperCase()))
                        {
                            result.add(entity);
                        }
                    }
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | IntrospectionException e)
                {
                    e.printStackTrace();
                }
            }
            listToFilter.removeAll(result);
        }
        return listToFilter;
    }

    /**
     * Sorts the list with the given parameters.
     *
     * @param listToSort
     *            the list to sort
     * @param sortParam
     *            the sort param
     * @return the list
     */
    public List<T> sort(List<T> listToSort, SortParam<String> sortParam)
    {
        final SortParam<String> fSortParam = sortParam;
        Collections.sort(listToSort, new Comparator<T>()
        {
            @Override
            public int compare(T o1, T o2)
            {
                try
                {
                    PropertyDescriptor descriptor = new PropertyDescriptor(fSortParam.getProperty(), o1.getClass());
                    int result;
                    result = (String.valueOf(descriptor.getReadMethod().invoke(o1)).toUpperCase().compareTo(
                        (String.valueOf(descriptor.getReadMethod().invoke(o2)).toUpperCase())));
                    if (!fSortParam.isAscending())
                        result *= -1;
                    return result;
                }
                catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                    | IntrospectionException e)
                {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return listToSort;
    }
}
