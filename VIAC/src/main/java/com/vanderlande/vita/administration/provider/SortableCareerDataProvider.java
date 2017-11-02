package com.vanderlande.vita.administration.provider;

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
import com.vanderlande.vita.administration.VITAAdministrationPage;
import com.vanderlande.vita.model.VITACareer;

/**
 * The Class SortableCareerDataProvider.
 * 
 * @author dermic, denmuj
 * 
 *         Used in {@link VITAAdministrationPage}. Displays all careers.
 */
public class SortableCareerDataProvider extends SortableDataProvider<VITACareer, String>
		implements ReloadableDataProvider<VITACareer> {
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -7489295680946422986L;

	/** The career filter. */
	private VITACareer careerFilter = new VITACareer();

	/** The found careers. */
	public List<VITACareer> careersFound;

	/** Instantiates a new sortable career data provider. */
	public SortableCareerDataProvider() {
		load();
		setSort("id", SortOrder.ASCENDING);
	}

	/**
	 * Load.
	 */
	private void load() {
		careersFound = new ArrayList<VITACareer>();
		careersFound = DatabaseProvider.getInstance().getAll(VITACareer.class);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.markup.repeater.data.IDataProvider#iterator(long,
	 * long)
	 */
	@Override
	public Iterator<VITACareer> iterator(long first, long count) {
		sort();
		return filterCareers(careersFound).subList((int) first, (int) (first + count)).iterator();
	}

	/**
	 * Sorts the careers by name or id.
	 */
	private void sort() {
		if (getSort().getProperty().equalsIgnoreCase("NAME")) {
			Collections.sort(careersFound, new Comparator<VITACareer>() {
				@Override
				public int compare(VITACareer o1, VITACareer o2) {
					int result = o1.getName().compareTo(o2.getName());
					if (!getSort().isAscending())
						result *= -1;
					return result;
				}
			});
		} else if (getSort().getProperty().equalsIgnoreCase("ID")) {
			Collections.sort(careersFound, new Comparator<VITACareer>() {
				@Override
				public int compare(VITACareer o1, VITACareer o2) {
					int result = (o1.getId() == o2.getId()) ? 0 : (o1.getId() < o2.getId()) ? -1 : 1;
					if (!getSort().isAscending())
						result *= -1;
					return result;
				}
			});
		}
	}

	/**
	 * Filters the careers
	 * 
	 * @param list
	 *            of careers
	 * @return list of careers
	 */
	private List<VITACareer> filterCareers(List<VITACareer> namesFound) {
		ArrayList<VITACareer> result = new ArrayList<VITACareer>();
		String name = careerFilter.getName();
		if (name == null)
			return careersFound;
		for (VITACareer c : careersFound) {
			if (c.getName().toUpperCase().contains(name.toUpperCase())) {
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
	public long size() {
		return filterCareers(careersFound).size();
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
	public IModel<VITACareer> model(VITACareer object) {
		return Model.of(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vanderlande.util.ReloadableDataProvider#reload()
	 */
	@Override
	public void reload() {
		load();
	}

	/**
	 * Adds the career.
	 *
	 * @param career
	 *            the career
	 */
	public void addCareer(VITACareer career) {
		DatabaseProvider.getInstance().create(career);
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
	public VITACareer getFilterState() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.wicket.extensions.markup.html.repeater.data.table.filter.
	 * IFilterStateLocator#setFilterState(java.lang.Object)
	 */
	@Override
	public void setFilterState(VITACareer state) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vanderlande.util.ReloadableDataProvider#getFilterOptions()
	 */
	@Override
	public List<FilterOption> getFilterOptions() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.vanderlande.util.ReloadableDataProvider#addFilterOption(com.
	 * vanderlande.viac.filter.FilterOption)
	 */
	@Override
	public void addFilterOption(FilterOption option) {
	}
}