package com.vanderlande.viac.userManagement.data.provider;

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
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.userManagement.data.model.UserTableModel;

/**
 * The Class SortableUserDataProvider.
 * 
 * @author dekscha
 * 
 *         used to display every user.
 */
public class SortableUserDataProvider extends SortableDataProvider<UserTableModel, String>
    implements ReloadableDataProvider<UserTableModel>
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2501454151306544934L;

    /** The user filter. */
    private UserTableModel userFilter = new UserTableModel();

    /** The users found. */
    List<UserTableModel> usersFound;

    private List<FilterOption> filterOptions = new ArrayList<FilterOption>();

    private FilterUtils<UserTableModel> filterUtils = new FilterUtils<>();

    /**
     * Instantiates a new sortable user data provider.
     */
    public SortableUserDataProvider(List<FilterOption> filterOptions)
    {
        this.filterOptions = filterOptions;
        load();
        // set default sort
        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Loads all VIACUsers from the database and encapsulates them in UserTableModels.
     */
    private void load()
    {
        usersFound = new ArrayList<UserTableModel>();
        for (VIACUser user : DatabaseProvider.getInstance().getAll(VIACUser.class))
        {
            usersFound.add(new UserTableModel(user));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
     * long)
     */
    @Override
    public Iterator<UserTableModel> iterator(long first, long count)
    {
        //		sort();
        usersFound = filterUtils.sort(usersFound, getSort());

        return filterUtils
            .filter(usersFound, filterOptions, userFilter)
            .subList((int)first, (int)(first + count))
            .iterator();
    }

    //	/**
    //	 * Sorts the list of UserTableModels.
    //	 */
    //	private void sort() {
    //		if (getSort().getProperty().equalsIgnoreCase("NAME")) {
    //			Collections.sort(usersFound, new Comparator<UserTableModel>() {
    //				@Override
    //				public int compare(UserTableModel o1, UserTableModel o2) {
    //					int result = o1.getName().toUpperCase().compareTo(o2.getName().toUpperCase());
    //					if (!getSort().isAscending())
    //						result *= -1;
    //					return result;
    //				}
    //			});
    //		} else if (getSort().getProperty().equalsIgnoreCase("FIRSTNAME")) {
    //			Collections.sort(usersFound, new Comparator<UserTableModel>() {
    //				@Override
    //				public int compare(UserTableModel o1, UserTableModel o2) {
    //					int result = o1.getFirstname().toUpperCase().compareTo(o2.getFirstname().toUpperCase());
    //					if (!getSort().isAscending())
    //						result *= -1;
    //					return result;
    //				}
    //			});
    //		} else if (getSort().getProperty().equalsIgnoreCase("ROLES")) {
    //			Collections.sort(usersFound, new Comparator<UserTableModel>() {
    //				@Override
    //				public int compare(UserTableModel o1, UserTableModel o2) {
    //					int result = o1.getRoles().toUpperCase().compareTo(o2.getRoles().toUpperCase());
    //					if (!getSort().isAscending())
    //						result *= -1;
    //					return result;
    //				}
    //			});
    //		} else if (getSort().getProperty().equalsIgnoreCase("LASTNAME")) {
    //			Collections.sort(usersFound, new Comparator<UserTableModel>() {
    //				@Override
    //				public int compare(UserTableModel o1, UserTableModel o2) {
    //					int result = o1.getLastname().toUpperCase().compareTo(o2.getLastname().toUpperCase());
    //					if (!getSort().isAscending())
    //						result *= -1;
    //					return result;
    //				}
    //			});
    //		} else if (getSort().getProperty().equalsIgnoreCase("ID")) {
    //			Collections.sort(usersFound, new Comparator<UserTableModel>() {
    //				@Override
    //				public int compare(UserTableModel o1, UserTableModel o2) {
    //					int result = (o1.getId() == o2.getId()) ? 0 : (o1.getId() < o2.getId()) ? -1 : 1;
    //					if (!getSort().isAscending())
    //						result *= -1;
    //					return result;
    //				}
    //			});
    //		}
    //	}

    /**
     * Size.
     *
     * @return the long
     * @see org.apache.wicket.markup.repeater.data.IDataProvider#size()
     */
    @Override
    public long size()
    {
        return filterUtils.filter(usersFound, filterOptions, userFilter).size();
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
    public IModel<UserTableModel> model(UserTableModel object)
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
    public UserTableModel getFilterState()
    {
        return userFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
     * IFilterStateLocator#setFilterState(java.lang.Object)
     */
    @Override
    public void setFilterState(UserTableModel state)
    {
        userFilter = state;
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
     * Adds the user.
     *
     * @param userTableModel
     *            the user table model
     */
    public void addUser(UserTableModel userTableModel)
    {
        usersFound.add(userTableModel);
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