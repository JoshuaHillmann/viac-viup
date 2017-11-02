package com.vanderlande.vipp.administration.provider;

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
import com.vanderlande.vipp.administration.data.ApprenticeshipAreaTableModel;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;

/**
 * The Class SortableApprenticeshipAreaDataProvider.
 * 
 * @author dermic
 * 
 *         used to display every apprenticehipArea.
 */
public class SortableApprenticeshipAreaDataProvider extends SortableDataProvider<ApprenticeshipAreaTableModel, String>
    implements ReloadableDataProvider<ApprenticeshipAreaTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The apprenticeshipArea filter. */
    private ApprenticeshipAreaTableModel apprenticeshipAreaFilter = new ApprenticeshipAreaTableModel();

    /** The apprenticeshipAreas found. */
    List<ApprenticeshipAreaTableModel> apprenticeshipAreaFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<ApprenticeshipAreaTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable apprenticeship data provider.
     */
    public SortableApprenticeshipAreaDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("name", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPApprenticeshipAreas from the database and encapsulates them in ApprenticeshipAreaTableModels.
     */
    private void load()
    {
        apprenticeshipAreaFound = new ArrayList<ApprenticeshipAreaTableModel>();
        for (VIPPApprenticeshipArea apprenticeshipArea : DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class))
        {
    		apprenticeshipAreaFound.add(new ApprenticeshipAreaTableModel(apprenticeshipArea));        		
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<ApprenticeshipAreaTableModel> iterator(long first, long count)
    {
        apprenticeshipAreaFound = filterUtils.sort(apprenticeshipAreaFound, getSort());

        return filterUtils
            .filter(apprenticeshipAreaFound, filterOptions, apprenticeshipAreaFilter)
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
        return filterUtils.filter(apprenticeshipAreaFound, filterOptions, apprenticeshipAreaFilter).size();
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
    public IModel<ApprenticeshipAreaTableModel> model(ApprenticeshipAreaTableModel object)
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
    public ApprenticeshipAreaTableModel getFilterState()
    {
        return apprenticeshipAreaFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(ApprenticeshipAreaTableModel state)
    {
        apprenticeshipAreaFilter = state;
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
     * Adds the apprenticeshipArea.
     *
     * @param apprenticeshipAreaTableModel
     *            the apprenticeshipArea table model
     */
    public void addApprentice(ApprenticeshipAreaTableModel apprenticeshipAreaTableModel)
    {
        apprenticeshipAreaFound.add(apprenticeshipAreaTableModel);
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