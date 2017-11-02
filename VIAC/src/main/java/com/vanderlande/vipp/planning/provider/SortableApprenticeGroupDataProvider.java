package com.vanderlande.vipp.planning.provider;

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
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.planning.data.ApprenticeGroupTableModel;

/**
 * The Class SortableApprenticeDataProvider.
 * 
 * @author dermic
 * 
 *         used to display every apprentice and their assigned group.
 */
public class SortableApprenticeGroupDataProvider extends SortableDataProvider<ApprenticeGroupTableModel, String>
    implements ReloadableDataProvider<ApprenticeGroupTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The apprentice filter. */
    private ApprenticeGroupTableModel apprenticeFilter = new ApprenticeGroupTableModel();

    /** The apprentice found. */
    List<ApprenticeGroupTableModel> apprenticesFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<ApprenticeGroupTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable apprentice data provider.
     */
    public SortableApprenticeGroupDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("group", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPApprentices from the database and encapsulates them in ApprenticeGroupTableModels.
     */
    private void load()
    {
        apprenticesFound = new ArrayList<ApprenticeGroupTableModel>();
        for (VIPPApprentice apprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
        {
        	if(apprentice.getCurrentGroup() != null)
        	{
        		apprenticesFound.add(new ApprenticeGroupTableModel(apprentice));        		
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
    public Iterator<ApprenticeGroupTableModel> iterator(long first, long count)
    {
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
    public IModel<ApprenticeGroupTableModel> model(ApprenticeGroupTableModel object)
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
    public ApprenticeGroupTableModel getFilterState()
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
    public void setFilterState(ApprenticeGroupTableModel state)
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
     * Adds the apprentice.
     *
     * @param apprenticeGroupTableModel
     *            the apprentice group table model
     */
    public void addApprentice(ApprenticeGroupTableModel apprenticeTableModel)
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