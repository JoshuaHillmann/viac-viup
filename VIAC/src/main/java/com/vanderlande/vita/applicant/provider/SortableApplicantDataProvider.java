package com.vanderlande.vita.applicant.provider;

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
import com.vanderlande.vita.model.VITAApplicant;

/**
 * The Class SortableApplicantDataProvider.
 * 
 * @author denmuj
 * 
 *         Used to display all applicants.
 */
public class SortableApplicantDataProvider extends SortableDataProvider<VITAApplicant, String>
    implements ReloadableDataProvider<VITAApplicant>
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5723997668857783653L;

    /** The applicant filter. */
    private VITAApplicant applicantFilter = new VITAApplicant();

    /** The applicants found. */
    private List<VITAApplicant> applicantsFound;

    /** The filter options. */
    private List<FilterOption> filterOptions;

    /** The filter utils. */
    private FilterUtils<VITAApplicant> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable applicant data provider.
     *
     * @param filterProperties
     *            the filter properties
     */
    public SortableApplicantDataProvider(List<FilterOption> filterProperties)
    {
        this.filterOptions = filterProperties;
        load();
        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        applicantsFound = new ArrayList<VITAApplicant>();
        applicantsFound = DatabaseProvider.getInstance().getAll(VITAApplicant.class);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<VITAApplicant> iterator(long first, long count)
    {
        applicantsFound = filterUtils.sort(applicantsFound, getSort());
        return filterUtils
            .filter(applicantsFound, filterOptions, applicantFilter)
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
        return filterUtils.filter(applicantsFound, filterOptions, applicantFilter).size();
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
    public IModel<VITAApplicant> model(VITAApplicant object)
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
     * Adds the applicant.
     *
     * @param applicant
     *            the applicant
     */
    public void addApplicant(VITAApplicant applicant)
    {
        DatabaseProvider.getInstance().create(applicant);
        load();
        applicantsFound = filterUtils.sort(applicantsFound, getSort());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#getFilterState()
     */
    @Override
    public VITAApplicant getFilterState()
    {
        return applicantFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(VITAApplicant state)
    {
        applicantFilter = state;
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
}
