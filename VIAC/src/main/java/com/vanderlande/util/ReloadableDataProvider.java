package com.vanderlande.util;

import java.util.List;

import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.IFilterStateLocator;

import com.vanderlande.viac.filter.FilterOption;

/**
 * The Interface ReloadableDataProvider.
 *
 *@author desczu
 * @param <T> the generic type
 */
public interface ReloadableDataProvider<T> extends IFilterStateLocator<T> {
	
	/**
	 * Reload.
	 */
	public void reload();

	/**
	 * Gets the filter options.
	 *
	 * @return the filter options
	 */
	public List<FilterOption> getFilterOptions();

	/**
	 * Adds the filter option.
	 *
	 * @param option the option
	 */
	public void addFilterOption(FilterOption option);
}
