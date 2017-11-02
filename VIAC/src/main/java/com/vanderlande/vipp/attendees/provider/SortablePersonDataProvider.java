package com.vanderlande.vipp.attendees.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.ReloadableDataProvider;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.FilterUtils;
import com.vanderlande.vipp.attendees.data.ApprenticeTableModel;
import com.vanderlande.vipp.model.VIPPApprentice;

/**
 * The Class SortablePersonDataProvider.
 * 
 * @author dermic
 * 
 *         used to display every apprentice.
 */
public class SortablePersonDataProvider extends SortableDataProvider<ApprenticeTableModel, String>
    implements ReloadableDataProvider<ApprenticeTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The apprentice filter. */
    private ApprenticeTableModel apprenticeFilter = new ApprenticeTableModel();

    /** The apprentices found. */
    List<ApprenticeTableModel> apprenticesFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<ApprenticeTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable apprentice data provider.
     */
    public SortablePersonDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("firstname", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPApprentices from the database and encapsulates them in ApprenticeTableModels.
     */
    private void load()
    {
        apprenticesFound = new ArrayList<ApprenticeTableModel>();
        for (VIPPApprentice apprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
        {
        	if(apprentice.isActive())
        	{
        		apprenticesFound.add(new ApprenticeTableModel(apprentice));        		
        	}
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<ApprenticeTableModel> iterator(long first, long count)
    {
        //		sort();
        apprenticesFound = filterUtils.sort(apprenticesFound, getSort());

        return filterUtils
            .filter(apprenticesFound, filterOptions, apprenticeFilter)
            .subList((int)first, (int)(first + count))
            .iterator();
    }

    /**
     * Size.
     *
     * @return the long
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public long size()
    {
        return filterUtils.filter(apprenticesFound, filterOptions, apprenticeFilter).size();
    }

    /**
     * Model.
     *
     * @param object
     *            the object
     * @return the i model
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#model(java.lang.Object)
     */
    @Override
    public IModel<ApprenticeTableModel> model(ApprenticeTableModel object)
    {
        return Model.of(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#getFilterState()
     */
    @Override
    public ApprenticeTableModel getFilterState()
    {
        return apprenticeFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(ApprenticeTableModel state)
    {
        apprenticeFilter = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.provider.ReloadableDataProvider#reload()
     */
    @Override
    public void reload()
    {
        this.load();
    }

    /**
     * Adds the user.
     *
     * @param apprenticeTableModel
     *            the apprentice table model
     */
    public void addApprentice(ApprenticeTableModel apprenticeTableModel)
    {
        apprenticesFound.add(apprenticeTableModel);
    }

    @Override
    public List<FilterOption> getFilterOptions()
    {
        return filterOptions;
    }

    @Override
    public void addFilterOption(FilterOption filterOption)
    {
        filterOptions.add(filterOption);
    }
}