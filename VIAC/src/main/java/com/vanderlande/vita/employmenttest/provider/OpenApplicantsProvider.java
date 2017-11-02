package com.vanderlande.vita.employmenttest.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.ReloadableDataProvider;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.FilterUtils;
import com.vanderlande.vita.employmenttest.EmploymentTestDetailsPage;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class OpenApplicantsProvider.
 * 
 * @author desczu
 * 
 *         This class is used in the {@link EmploymentTestDetailsPage} and is used to display all applicants with the
 *         status "Bewerbung eingegangen" that are not planned for the testgroup already
 */
public class OpenApplicantsProvider extends SortableDataProvider<VITAApplicant, String>
    implements ReloadableDataProvider<VITAApplicant>
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7628896225883261973L;

    /** The open applicants. */
    public List<VITAApplicant> openApplicants;

    /** The test group. */
    public VITAEligoTest testGroup;

    /** The applicant filter. */
    private VITAApplicant applicantFilter = new VITAApplicant();

    /** The filter options. */
    private List<FilterOption> filterOptions;

    /** The filter utils. */
    private FilterUtils<VITAApplicant> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new open applicants provider.
     *
     * @param testGroup
     *            the test group
     * @param filterOptions
     *            the filter options
     */
    public OpenApplicantsProvider(VITAEligoTest testGroup, List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        this.testGroup = testGroup;
        load();
        setSort("lastname", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        openApplicants = new ArrayList<VITAApplicant>();
        openApplicants = DatabaseProvider.getInstance().getAllOpenApplicants();
        for (VITAApplicant testApplicant : testGroup.getTestApplicants())
        {
            if (openApplicants.contains(testApplicant))
            {
                openApplicants.remove(testApplicant);
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
    public Iterator<VITAApplicant> iterator(long first, long count)
    {
        // return openApplicants.subList((int)first, (int)(first +
        // count)).iterator();

        openApplicants = filterUtils.sort(openApplicants, getSort());
        return filterUtils
            .filter(openApplicants, filterOptions, applicantFilter)
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
        return filterUtils.filter(openApplicants, filterOptions, applicantFilter).size();
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
        openApplicants.add(applicant);
    }

    /**
     * Removes the applicant.
     *
     * @param applicant
     *            the applicant
     */
    public void removeApplicant(VITAApplicant applicant)
    {
        openApplicants.remove(applicant);
    }

    /**
     * Gets the applicants.
     *
     * @return the applicants
     */
    public Set<VITAApplicant> getApplicants()
    {
        return new HashSet<VITAApplicant>(openApplicants);
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