package com.vanderlande.vita.employmenttest.provider;

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
import com.vanderlande.vita.employmenttest.EmploymentTestOverviewPage;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class SortableGroupDataProvider.
 * 
 * @author desczu, dermic
 * 
 *         Used in {@link EmploymentTestOverviewPage}. Displays all testgroups.
 */
public class EmploymentTestGroupDataProvider extends SortableDataProvider<VITAEligoTest, String>
    implements ReloadableDataProvider<VITAEligoTest>
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7628896225883261973L;

    /** The groups found. */
    public List<VITAEligoTest> groupsFound;

    /** The test filter. */
    private VITAEligoTest testFilter = new VITAEligoTest();

    /** The filter options. */
    private List<FilterOption> filterOptions;

    /** The filter utils. */
    private FilterUtils<VITAEligoTest> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable group data provider.
     *
     * @param filterOptions
     *            the filter options
     */
    public EmploymentTestGroupDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        groupsFound = new ArrayList<VITAEligoTest>();
        groupsFound = DatabaseProvider.getInstance().getAll(VITAEligoTest.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<VITAEligoTest> iterator(long first, long count)
    {
        groupsFound = filterUtils.sort(groupsFound, getSort());
        return filterUtils
            .filter(groupsFound, filterOptions, testFilter)
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
        return filterUtils.filter(groupsFound, filterOptions, testFilter).size();
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
    public IModel<VITAEligoTest> model(VITAEligoTest object)
    {
        return Model.of(object);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.util.ReloadableDataProvider#reload()
     */
    @Override
    public void reload()
    {
        load();
    }

    /**
     * Adds the test.
     *
     * @param test
     *            the test
     */
    public void addTest(VITAEligoTest test)
    {
        DatabaseProvider.getInstance().create(test);
        load();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.util.ReloadableDataProvider#getFilterOptions()
     */
    @Override
    public List<FilterOption> getFilterOptions()
    {
        return filterOptions;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.util.ReloadableDataProvider#addFilterOption(com.
     * vanderlande.viac.filter.FilterOption)
     */
    @Override
    public void addFilterOption(FilterOption option)
    {
        filterOptions.add(option);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#getFilterState()
     */
    @Override
    public VITAEligoTest getFilterState()
    {
        return testFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(VITAEligoTest state)
    {
        testFilter = state;
    }
}