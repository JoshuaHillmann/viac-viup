package com.vanderlande.vipp.archive.provider;

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
import com.vanderlande.vipp.model.VIPPPresentationCycle;

/**
 * The Class ArchiveDataProvider.
 * 
 * @author dermic
 * 
 *         the data provider for the archive page.
 */
public class ArchiveDataProvider extends SortableDataProvider<ArchiveTableModel, String>
    implements ReloadableDataProvider<ArchiveTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The apprentice filter. */
    private ArchiveTableModel apprenticeFilter = new ArchiveTableModel();

    /** The apprentices found. */
    List<ArchiveTableModel> apprenticesFound;
    
    VIPPPresentationCycle cycle;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<ArchiveTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable apprentice data provider.
     */
    public ArchiveDataProvider(List<FilterOption> filterOptions, VIPPPresentationCycle cycle)
    {
        this.filterOptions = filterOptions;
        this.cycle = cycle;
        load();
        // set default sort
        setSort("firstname", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPApprentices from the database and encapsulates them in ArchiveTableModels.
     */
    private void load()
    {
        apprenticesFound = new ArrayList<ArchiveTableModel>();
        for (VIPPApprentice apprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
        {
            apprenticesFound.add(new ArchiveTableModel(apprentice, cycle));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<ArchiveTableModel> iterator(long first, long count)
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
    public IModel<ArchiveTableModel> model(ArchiveTableModel object)
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
    public ArchiveTableModel getFilterState()
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
    public void setFilterState(ArchiveTableModel state)
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
     * @param archiveTableModel
     *            the archive table model
     */
    public void addApprentice(ArchiveTableModel archiveTableModel)
    {
        apprenticesFound.add(archiveTableModel);
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