package com.vanderlande.viac.template.provider;

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
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.template.model.TemplateTableModel;

/**
 * The class TemplateDataProvider
 * 
 * @author dedhor
 * 
 *         Implementation of SortableDataProvider and ReloadableDataProvider for wicket display of the mailTemplates in
 *         a table.
 */
public class TemplateDataProvider extends SortableDataProvider<TemplateTableModel, String>
    implements ReloadableDataProvider
{
    private static final long serialVersionUID = 1L;

    List<TemplateTableModel> templatesFound;

    /**
     * Load templates and sort by id.
     * 
     * @return
     */
    public TemplateDataProvider()
    {
        load();
        // set default sort
        setSort("id", SortOrder.ASCENDING);
    }

    /**
     * Override default iterator and add sort.
     * 
     * @param first
     * @param count
     * @return template iterator
     */
    @Override
    public Iterator<? extends TemplateTableModel> iterator(long first, long count)
    {
        sort();
        return templatesFound.subList((int)first, (int)(first + count)).iterator();
    }

    /**
     * Override default size function with list.size.
     * 
     * @return count templates
     */
    @Override
    public long size()
    {
        if (templatesFound == null)
        {
            return 0;
        }
        return templatesFound.size();
    }

    /**
     * Override default model with TemplateTableModel.
     * 
     * @param TemplateTableModel
     *            object
     * @return TemplateTableModel
     */
    @Override
    public IModel<TemplateTableModel> model(TemplateTableModel object)
    {
        return Model.of(object);
    }

    /**
     * Override reload function with load().
     */
    @Override
    public void reload()
    {
        this.load();
    }

    /**
     * Custom sort function for ID and TEXT parameter.
     */
    private void sort()
    {
        if (getSort().getProperty().equalsIgnoreCase("ID"))
        {
            Collections.sort(templatesFound, new Comparator<TemplateTableModel>()
            {
                @Override
                public int compare(TemplateTableModel arg0, TemplateTableModel arg1)
                {
                    int result = arg0.getId().toUpperCase().compareTo(arg0.getId().toUpperCase());
                    if (!getSort().isAscending())
                        result *= -1;
                    return result;
                }
            });
        }
        else if (getSort().getProperty().equalsIgnoreCase("TEXT"))
        {
            Collections.sort(templatesFound, new Comparator<TemplateTableModel>()
            {
                @Override
                public int compare(TemplateTableModel arg0, TemplateTableModel arg1)
                {
                    int result = arg0.getText().toUpperCase().compareTo(arg0.getText().toUpperCase());
                    if (!getSort().isAscending())
                        result *= -1;
                    return result;
                }
            });
        }
    }

    /**
     * Custom load function to load all mail templates from database into variable.
     */
    private void load()
    {
        templatesFound = new ArrayList<TemplateTableModel>();
        for (MailTemplate template : DatabaseProvider.getInstance().getAll(MailTemplate.class))
        {
            templatesFound.add(new TemplateTableModel(template));
        }
    }

    /**
     * Implementation of required funtion.
     * 
     * @return null
     */
    @Override
    public Object getFilterState()
    {
        return null;
    }

    /**
     * Implementation of required funtion.
     * 
     * @param
     */
    @Override
    public void setFilterState(Object state)
    {

    }

    /**
     * Implementation of required funtion.
     * 
     * @return
     */
    @Override
    public List getFilterOptions()
    {
        return null;
    }

    /**
     * Implementation of required funtion.
     * 
     * @param
     */
    @Override
    public void addFilterOption(FilterOption option)
    {

    }
}