package com.vanderlande.viac.roles.provider;

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
import com.vanderlande.viac.model.VIACRole;

/**
 * The Class SortableRoleDataProvider.
 * 
 * @author desczu
 * 
 *         Used to display every role.
 */
public class SortableRoleDataProvider extends SortableDataProvider<VIACRole, String>
    implements ReloadableDataProvider<VIACRole>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -7628896225883261973L;

    /** The role filter. */
    private VIACRole roleFilter = new VIACRole();

    /** The filter options. */
    private List<FilterOption> filterOptions;

    /** The roles found. */
    private List<VIACRole> rolesFound;

    /** The filter utils. */
    private FilterUtils<VIACRole> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable role data provider.
     *
     * @param filterOptions
     *            the filter options
     */
    public SortableRoleDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Load.
     */
    private void load()
    {
        rolesFound = new ArrayList<VIACRole>();
        rolesFound = DatabaseProvider.getInstance().getAll(VIACRole.class);

    }

    /* (non-Javadoc)
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long, long)
     */
    @Override
    public Iterator<VIACRole> iterator(long first, long count)
    {
        rolesFound = filterUtils.sort(rolesFound, getSort());
        return filterUtils
            .filter(rolesFound, filterOptions, roleFilter)
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
        return filterUtils.filter(rolesFound, filterOptions, roleFilter).size();
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
    public IModel<VIACRole> model(VIACRole object)
    {
        //        return new DetachableContactModel(object);
        return Model.of(object);
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator#getFilterState()
     */
    @Override
    public VIACRole getFilterState()
    {
        return roleFilter;
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(VIACRole state)
    {
        roleFilter = state;
    }

    /* (non-Javadoc)
     * @see com.vanderlande.util.ReloadableDataProvider#reload()
     */
    @Override
    public void reload()
    {
        load();
    }

    /**
     * Adds the role.
     *
     * @param role
     *            the role
     */
    public void addRole(VIACRole role)
    {
        DatabaseProvider.getInstance().create(role);
        load();
        rolesFound = filterUtils.sort(rolesFound, getSort());
    }

    /* (non-Javadoc)
     * @see com.vanderlande.util.ReloadableDataProvider#getFilterOptions()
     */
    @Override
    public List<FilterOption> getFilterOptions()
    {
        return filterOptions;
    }

    /* (non-Javadoc)
     * @see com.vanderlande.util.ReloadableDataProvider#addFilterOption(com.vanderlande.viac.filter.FilterOption)
     */
    @Override
    public void addFilterOption(FilterOption option)
    {
        filterOptions.add(option);
    }

}