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
import com.vanderlande.vipp.planning.data.ApprenticeSubjectTableModel;

/**
 * The Class SortableApprenticeSubjectDataProvider.
 * 
 * @author dermic
 * 
 *         used to display every apprentice and their assigned subject.
 */
public class SortableApprenticeSubjectDataProvider extends SortableDataProvider<ApprenticeSubjectTableModel, String>
    implements ReloadableDataProvider<ApprenticeSubjectTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The apprentice filter. */
    private ApprenticeSubjectTableModel apprenticeFilter = new ApprenticeSubjectTableModel();

    /** The apprentices found. */
    List<ApprenticeSubjectTableModel> apprenticesFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<ApprenticeSubjectTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable apprentice subject data provider.
     */
    public SortableApprenticeSubjectDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("subject", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPApprentices from the database and encapsulates them in ApprenticeSubjectTableModels.
     */
    private void load()
    {
        apprenticesFound = new ArrayList<ApprenticeSubjectTableModel>();
        for (VIPPApprentice apprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
        {
        	if(apprentice.getCurrentGroup() != null)
        	{
        		apprenticesFound.add(new ApprenticeSubjectTableModel(apprentice));        		
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
    public Iterator<ApprenticeSubjectTableModel> iterator(long first, long count)
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
    public IModel<ApprenticeSubjectTableModel> model(ApprenticeSubjectTableModel object)
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
    public ApprenticeSubjectTableModel getFilterState()
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
    public void setFilterState(ApprenticeSubjectTableModel state)
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
     * @param apprenticeSubjectTableModel
     *            the apprentice subject table model
     */
    public void addApprentice(ApprenticeSubjectTableModel apprenticeTableModel)
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