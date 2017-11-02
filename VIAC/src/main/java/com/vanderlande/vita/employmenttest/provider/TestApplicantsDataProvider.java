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

import com.vanderlande.util.ReloadableDataProvider;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.FilterUtils;
import com.vanderlande.vita.employmenttest.EmploymentTestDetailsPage;
import com.vanderlande.vita.employmenttest.EmploymentTestEligoManagementPage;
import com.vanderlande.vita.employmenttest.EmploymentTestSummaryPage;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class TestApplicantsDataProvider.
 * 
 * @author desczu
 * 
 *         Used in {@link EmploymentTestDetailsPage}, {@link EmploymentTestEligoManagementPage} and
 *         {@link EmploymentTestSummaryPage}. Displays all applicants that are planned for the test.
 */
public class TestApplicantsDataProvider extends SortableDataProvider<VITAApplicant, String>
    implements ReloadableDataProvider<VITAApplicant>
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7628896225883261973L;

    /** The applicants found. */
    public List<VITAApplicant> applicantsFound;

    /** The test group. */
    public VITAEligoTest testGroup;

    /** The filter utils. */
    private FilterUtils<VITAApplicant> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new test applicants data provider.
     *
     * @param testGroup
     *            the test group
     */
    public TestApplicantsDataProvider(VITAEligoTest testGroup)
    {
        this.testGroup = testGroup;
        load();
        setSort("lastname", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        applicantsFound = new ArrayList<VITAApplicant>(testGroup.getTestApplicants());
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
        return applicantsFound.subList((int)first, (int)(first + count)).iterator();
    }

    /**
     * Adds the applicant.
     *
     * @param applicant
     *            the applicant
     */
    public void addApplicant(VITAApplicant applicant)
    {
        applicantsFound.add(applicant);
    }

    /**
     * Removes the applicant.
     *
     * @param applicant
     *            the applicant
     */
    public void removeApplicant(VITAApplicant applicant)
    {
        applicantsFound.remove(applicant);
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
        return applicantsFound.size();
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
     * Gets the applicants.
     *
     * @return the applicants
     */
    public Set<VITAApplicant> getApplicants()
    {
        return new HashSet<VITAApplicant>(applicantsFound);
    }

    /**
     * Sets the applicants.
     *
     * @param testApplicants
     *            the new applicants
     */
    public void setApplicants(Set<VITAApplicant> testApplicants)
    {
        this.applicantsFound = new ArrayList<VITAApplicant>(testApplicants);
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
        return null;
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
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.util.ReloadableDataProvider#getFilterOptions()
     */
    @Override
    public List<FilterOption> getFilterOptions()
    {
        return null;
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
    }
}