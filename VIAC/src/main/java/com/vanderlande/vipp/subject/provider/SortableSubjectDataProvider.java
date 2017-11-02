package com.vanderlande.vipp.subject.provider;

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
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.subject.data.SubjectTableModel;

/**
 * The Class SortableSubjectDataProvider.
 * 
 * @author dermic
 * 
 *         used to display every subject.
 */
public class SortableSubjectDataProvider extends SortableDataProvider<SubjectTableModel, String>
    implements ReloadableDataProvider<SubjectTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The subject filter. */
    private SubjectTableModel subjectFilter = new SubjectTableModel();

    /** The subject found. */
    List<SubjectTableModel> subjectsFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<SubjectTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable subject data provider.
     */
    public SortableSubjectDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("name", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIPPSubjects from the database and encapsulates them in SubjectTableModels.
     */
    private void load()
    {
        subjectsFound = new ArrayList<SubjectTableModel>();
        for (VIPPSubject subject : DatabaseProvider.getInstance().getAll(VIPPSubject.class))
        {
        	if(subject.isIsActive())
        	{
        		subjectsFound.add(new SubjectTableModel(subject));        		
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
    public Iterator<SubjectTableModel> iterator(long first, long count)
    {
        subjectsFound = filterUtils.sort(subjectsFound, getSort());
        return filterUtils
            .filter(subjectsFound, filterOptions, subjectFilter)
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
        return filterUtils.filter(subjectsFound, filterOptions, subjectFilter).size();
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
    public IModel<SubjectTableModel> model(SubjectTableModel object)
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
    public SubjectTableModel getFilterState()
    {
        return subjectFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(SubjectTableModel state)
    {
        subjectFilter = state;
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
     * Adds the subject.
     *
     * @param SubjectTableModel
     *            the subject table model
     */
    public void addSubject(SubjectTableModel subjectTableModel)
    {
        subjectsFound.add(subjectTableModel);
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