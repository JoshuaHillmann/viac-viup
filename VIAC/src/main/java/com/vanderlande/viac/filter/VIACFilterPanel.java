package com.vanderlande.viac.filter;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.util.ReloadableDataProvider;

/**
 * The Class VIACFilterPanel.
 *
 * @author dekscha
 *
 * @param <T> the generic type
 * 
 *            Use this if you want to add a filter to a table. You simply need to add a <div wicket:id="[FILTERNAME]">
 *            to your markup-file and pass this wicket:id, the DataProvider for the table that you want to filter and
 *            the table itself. You define the FilterOptions in the dataprovider.
 * 
 */
public class VIACFilterPanel<T> extends Panel
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8678658576207968030L;

    /** The filters. */
    private List<FilterOption> filters;

    /** The filter bean. */
    private T filterBean;

    /** The choosen filter. */
    private Model<FilterOption> choosenFilter = new Model<>();

    /** The filter word. */
    private Model<String> filterWord = new Model<>();

    /** The filter class. */
    private Class<T> filterClass;

    /** The parent. */
    private Panel parent;

    /** The data provider. */
    private ReloadableDataProvider<T> dataProvider;

    /**
     * Instantiates a new VIAC filter panel.
     *
     * @param id
     *            the id
     * @param dataProvider
     *            the data provider
     * @param parent
     *            the parent
     */
    public VIACFilterPanel(String id, ReloadableDataProvider<T> dataProvider, Panel parent)
    {
        super(id);
        this.dataProvider = dataProvider;
        this.filters = this.dataProvider.getFilterOptions();
        this.filterBean = this.dataProvider.getFilterState();
        this.filterClass = (Class<T>)filterBean.getClass();
        this.parent = parent;
        this.initialize();
        this.setOutputMarkupId(true);
    }

    /**
     * Initialize.
     */
    private void initialize()
    {
        Form<?> filterForm = new Form<>("filterForm");
        add(filterForm);

        List<String> filterNames = new ArrayList<String>();
        for (FilterOption filterProperty : filters)
        {
            filterNames.add(filterProperty.getFilterName());
        }
        if (!filters.isEmpty())
            choosenFilter.setObject(filters.get(0));
        DropDownChoice<FilterOption> filterDropDown =
            new DropDownChoice<FilterOption>("filterDropDown", choosenFilter, new ListModel<>(filters));
        filterForm.add(filterDropDown);

        TextField<String> filterText = new TextField<>("filterText", filterWord);
        filterForm.add(filterText);

        AjaxButton submitButton = new AjaxButton("submit")
        {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                super.onSubmit(target, form);
                if (choosenFilter.getObject() != null)
                {
                    emptyFilterBean();
                    changeFilterState();
                    dataProvider.reload();
                    target.add(parent);
                }
            }
        };
        filterForm.add(submitButton);
    }

    /**
     * Changes the filter state.
     */
    private void changeFilterState()
    {
        try
        {
            new PropertyDescriptor(choosenFilter.getObject().getPropertyField(), filterClass)
                .getWriteMethod()
                .invoke(filterBean, filterWord.getObject());
        }
        catch (IllegalArgumentException e)
        {
            try
            {
                String fieldName = "";
                Field filterClassField = filterClass.getDeclaredField(choosenFilter.getObject().getPropertyField());
                if (filterClassField.isAnnotationPresent(FilterField.class))
                {
                    fieldName = filterClassField.getAnnotation(FilterField.class).name();
                }

                Object obj = new PropertyDescriptor(choosenFilter.getObject().getPropertyField(), filterClass)
                    .getPropertyType()
                    .newInstance();

                new PropertyDescriptor(fieldName, obj.getClass()).getWriteMethod().invoke(obj, filterWord.getObject());
                new PropertyDescriptor(choosenFilter.getObject().getPropertyField(), filterClass)
                    .getWriteMethod()
                    .invoke(filterBean, obj);

            }
            catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException
                | InvocationTargetException | IntrospectionException | InstantiationException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (IllegalAccessException | InvocationTargetException | IntrospectionException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Empties the filter bean.
     */
    private void emptyFilterBean()
    {
        for (Field f : filterBean.getClass().getDeclaredFields())
        {
            f.setAccessible(true);
            try
            {
                f.set(filterBean, null);
            }
            catch (IllegalArgumentException | IllegalAccessException e)
            {
                System.out.println("VIACFilterPanel.java: Trying to set Long/int field to null");
                //					e.printStackTrace();
            }
        }
    }
}
