package com.vanderlande.vita.applicant.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.ReloadableDataProvider;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITAHistoricStatus;

/**
 * The Class SortableStatusHistoryDataProvider.
 * 
 * @author denmuj
 * 
 *         Used to display every status that an applicant had (also the current one)
 */
public class SortableStatusHistoryDataProvider extends SortableDataProvider<VITAHistoricStatus, String>
    implements ReloadableDataProvider<VITAHistoricStatus>
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7628896225883261973L;

    /** The status history filter. */
    private VITAHistoricStatus statusHistoryFilter = new VITAHistoricStatus();

    /** The applicant. */
    private IModel<VITAApplicant> applicantModel;

    /** The status history found. */
    private List<VITAHistoricStatus> statusHistoryFound;

    /**
     * Instantiates a new sortable status history data provider.
     *
     * @param applicantModel
     *            the applicant
     */
    public SortableStatusHistoryDataProvider(IModel<VITAApplicant> applicantModel)
    {
        this.applicantModel = applicantModel;
        load();
        setSort("changedOn", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        statusHistoryFound = new ArrayList<VITAHistoricStatus>(applicantModel.getObject().getHistoricStatuses());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<VITAHistoricStatus> iterator(long first, long count)
    {
        sort();
        return filterStatus(statusHistoryFound).subList((int)first, (int)(first + count)).iterator();
    }

    /**
     * Sort.
     */
    private void sort()
    {
        if (getSort().getProperty().equalsIgnoreCase("NAME"))
        {
            Collections.sort(statusHistoryFound, new Comparator<VITAHistoricStatus>()
            {
                @Override
                public int compare(VITAHistoricStatus o1, VITAHistoricStatus o2)
                {
                    int result = o1.getName().toString().compareTo(o2.getName().toString());
                    if (!getSort().isAscending())
                        result *= -1;
                    return result;
                }
            });
        }
        else if (getSort().getProperty().equalsIgnoreCase("CHANGEDON"))
        {
            Collections.sort(statusHistoryFound, new Comparator<VITAHistoricStatus>()
            {
                @Override
                public int compare(VITAHistoricStatus o1, VITAHistoricStatus o2)
                {
                    int result =
                        (o1.getChangedOn() == o2.getChangedOn()) ? 0
                                                                 : (o1.getChangedOn().before(o2.getChangedOn())) ? -1
                                                                                                                 : 1;
                    if (!getSort().isAscending())
                        result *= -1;
                    return result;
                }
            });
        }
    }

    /**
     * Filter status.
     *
     * @param namesFound
     *            the names found
     * @return the list
     */
    private List<VITAHistoricStatus> filterStatus(List<VITAHistoricStatus> namesFound)
    {
        ArrayList<VITAHistoricStatus> result = new ArrayList<VITAHistoricStatus>();
        VITAApplicantStatus name = statusHistoryFilter.getName();
        if (name == null)
            return statusHistoryFound;
        for (VITAHistoricStatus c : statusHistoryFound)
        {
            if (c.getName().toString().toUpperCase().contains(name.toString().toUpperCase()))
            {
                result.add(c);
            }
        }
        return result;
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
        return filterStatus(statusHistoryFound).size();
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
    public IModel<VITAHistoricStatus> model(VITAHistoricStatus object)
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
     * Adds the status history.
     *
     * @param statusHistory
     *            the status history
     */
    public void addStatusHistory(VITAHistoricStatus statusHistory)
    {
        DatabaseProvider.getInstance().create(statusHistory);
        load();
        sort();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#getFilterState()
     */
    @Override
    public VITAHistoricStatus getFilterState()
    {
        return statusHistoryFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(VITAHistoricStatus state)
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