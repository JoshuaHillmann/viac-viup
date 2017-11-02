package com.vanderlande.vipp.administration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.administration.data.ApprenticeshipAreaTableModel;
import com.vanderlande.vipp.administration.provider.SortableApprenticeshipAreaDataProvider;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;

/**
 * The Class ApprenticeshipAreaAdministrationPage.
 * 
 * @author dermic
 * 
 *         Displays all apprenticeshipAreas. You can edit the name or delete/create an apprenticeshipArea.
 */
public class ApprenticeshipAreaAdministrationPage extends VIPPBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The action panel. */
    VIACActionPanel actionPanel;
    
    List<VIACButton> buttons;
    
    NotificationDialog notificationDialog;
    
    /** The add apprenticeshipArea panel. */
    private WebMarkupContainer addApprenticeshipAreaPanel;
    	
	private VIPPApprenticeshipArea editApprenticeshipArea;
	
	private TextField<String> apprenticeshipAreaNameInput;
	
    private DataTable<ApprenticeshipAreaTableModel, String> tableWithFilterForm;
	
	private SortableApprenticeshipAreaDataProvider dataProvider;
		    
	private AjaxButton activateButton;
	
    /**
     * Instantiates a new apprentice administration page.
     */
    public ApprenticeshipAreaAdministrationPage()
    {
        init();
    }

    /**
     * Inits the.
     */
    private void init()
    {
        notificationDialog = new NotificationDialog("notificationDialog");
        notificationDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(ApprenticeshipAreaAdministrationPage.this);
                return true;
            }
        });
        add(notificationDialog);
    	
    	addApprenticeshipAreaPanel = new WebMarkupContainer("addApprenticeshipAreaPanel");

        buttons = new ArrayList<>();

        this.setOutputMarkupId(true);

        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Name", "name"));
        filters.add(new FilterOption("Aktiv", "active"));

        dataProvider = new SortableApprenticeshipAreaDataProvider(filters);

        tableWithFilterForm = this.initDataTable(filters);
        
        FilterForm<ApprenticeshipAreaTableModel> apprenticeshipAreaFilterForm = new FilterForm<ApprenticeshipAreaTableModel>("filterForm", dataProvider);
        
        FilterToolbar filterTooblbar = new FilterToolbar(tableWithFilterForm, apprenticeshipAreaFilterForm);
        tableWithFilterForm.addTopToolbar(filterTooblbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        
        apprenticeshipAreaFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<ApprenticeshipAreaTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        apprenticeshipAreaFilterForm.add(filterPanel);

        add(apprenticeshipAreaFilterForm);
        
        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);

        // Form to add a new apprenticeshipArea
        final VIPPApprenticeshipArea apprenticeshipArea = new VIPPApprenticeshipArea();
        Form<String> addApprenticeshipAreaForm = new Form<String>("addApprenticeshipAreaForm");
        
        Label nameLabel = new Label("nameLabel", new Model<String>("Name:"));
        apprenticeshipAreaNameInput =
            new TextField<String>("apprenticeshipAreaNameInput", new PropertyModel<String>(apprenticeshipArea, "name"));
        apprenticeshipAreaNameInput.setRequired(true);
        apprenticeshipAreaNameInput.setOutputMarkupId(true);
        
        AjaxButton submitButton = new AjaxButton("submit")
        {
        	boolean create = true;
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	if(apprenticeshipAreaNameInput.getConvertedInput().isEmpty())
            	{
            		notificationDialog.showMessage(
            				"Der Name darf nicht leer sein!",
            				getRequestCycle().find(AjaxRequestTarget.class));
            	}
                super.onSubmit(target, form);
                if(editApprenticeshipArea == null)
                {
                	for(VIPPApprenticeshipArea area : DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class))
                	{
                		if(area.getName().equals(apprenticeshipAreaNameInput.getConvertedInput()) && area.isActive())
                		{
                			create = false;
                			break;
                		}
                	}
                	if(create)
                	{
            			apprenticeshipArea.setName(apprenticeshipAreaNameInput.getConvertedInput());
            			DatabaseProvider.getInstance().create(apprenticeshipArea); 
            			setResponsePage(new ApprenticeshipAreaAdministrationPage());                			
                	}
                	else
                	{
                		notificationDialog.showMessage(
                				"Es existiert bereits ein aktiver Ausbildungsberuf mit diesem Namen",
                				getRequestCycle().find(AjaxRequestTarget.class));
                		create = true;
                	}
                }
                else
                {
                	for(VIPPApprenticeshipArea area : DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class))
                	{
                		if(area.getName().equals(apprenticeshipAreaNameInput.getConvertedInput()) && area.isActive())
                		{
                			create = false;
                			break;
                		}
                	}
                	if(create)
                	{
                		apprenticeshipArea.setName(apprenticeshipAreaNameInput.getConvertedInput());
                		DatabaseProvider.getInstance().merge(editApprenticeshipArea);
                		setResponsePage(new ApprenticeshipAreaAdministrationPage());                		
                	}
                	else
                	{
                		notificationDialog.showMessage(
                				"Es existiert bereits ein aktiver Ausbildungsberuf mit diesem Namen",
                				getRequestCycle().find(AjaxRequestTarget.class));
                	}
                }
            }
        };
        activateButton = new AjaxButton("activate") {
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	editApprenticeshipArea.setActive(true);
            	DatabaseProvider.getInstance().merge(editApprenticeshipArea);
            	setResponsePage(new ApprenticeshipAreaAdministrationPage());
            }
		};
        addApprenticeshipAreaForm.add(nameLabel);
        addApprenticeshipAreaForm.add(apprenticeshipAreaNameInput);
        addApprenticeshipAreaForm.add(submitButton);
        activateButton.setOutputMarkupId(true);
        activateButton.setOutputMarkupPlaceholderTag(true);
        addApprenticeshipAreaForm.add(activateButton);
        addApprenticeshipAreaPanel.add(addApprenticeshipAreaForm);
        add(addApprenticeshipAreaPanel);
        
        if(editApprenticeshipArea == null)
        {
        	activateButton.setEnabled(false);
        	activateButton.setVisible(false);
        }
    }

    /**
     * On confirm.
     *
     * @param rowModel
     *            the row model
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    public Command onConfirm(final VIPPApprenticeshipArea rowModel, final SortableApprenticeshipAreaDataProvider dataProvider)
    {
        return new Command()
        {
            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                rowModel.setActive(false);
                DatabaseProvider.getInstance().merge(rowModel);
                dataProvider.reload();

                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(ApprenticeshipAreaAdministrationPage.this);
            }
        };
    }

    /**
     * On cancel.
     *
     * @return the command
     */
    public Command onCancel()
    {
        return new Command()
        {
            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                confirmDeleteDialog.close(ajaxTarget);
            }
        };
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    private void verifyPage()
    {
//        authorizations = new ArrayList<>();
//        authorizations = VIACSession.getInstance().getUserAuthorizations();
//
//        if (!authorizations.contains("vipp_new_subject"))
//        {
//            addSubjectPanel.setVisible(false);
//        }
//
//        for (VIACButton b : buttons)
//        {
//
//            if (b.getButtonType().toString().equals("DELETE"))
//            {
//                if (!authorizations.contains("vipp_delete_subject"))
//                {
//                    b.setVisible(false);
//                }
//            }
//
//            if (b.getButtonType().toString().equals("EDIT"))
//            {
//                if (!authorizations.contains("vipp_edit_subject"))
//                {
//                    b.setVisible(false);
//                }
//            }
//        }

    }
    
    private DataTable<ApprenticeshipAreaTableModel, String> initDataTable(List<FilterOption> filters)
    {
        List<IColumn<ApprenticeshipAreaTableModel, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ApprenticeshipAreaTableModel, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<ApprenticeshipAreaTableModel, String>(new Model<>("Aktuell"), "isActive", "isActive"));
	    columns.add(new AbstractColumn<ApprenticeshipAreaTableModel, String>(new Model<>("Aktionen"))
	    {
			private static final long serialVersionUID = 5965492182336971017L;

			@Override
	        public void populateItem(Item<ICellPopulator<ApprenticeshipAreaTableModel>> cellItem, String componentId,
	                                 final IModel<ApprenticeshipAreaTableModel> rowModel)
	        {
	            VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
	            {
	                @Override
	                public void onClick(final AjaxRequestTarget target)
	                {
	                    editApprenticeshipArea = rowModel.getObject().getApprenticeshipArea();
	                    apprenticeshipAreaNameInput.setModel(new PropertyModel<String>(editApprenticeshipArea, "name"));
	                    apprenticeshipAreaNameInput.setOutputMarkupId(true);
	                    target.add(apprenticeshipAreaNameInput);
	                    if(!editApprenticeshipArea.isActive())
	                    {
	                    	activateButton.setVisible(true);
	                    	activateButton.setEnabled(true);
	                    	activateButton.setOutputMarkupId(true);	  
	                    	target.add(activateButton);	                    	
	                    }
	                    else
	                    {
	                    	activateButton.setVisible(false);
	                    	activateButton.setEnabled(false);
	                    	activateButton.setOutputMarkupId(true);	  
	                    	target.add(activateButton);	
	                    }
	                }
	            };
	            VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
	            {
	                @Override
	                public void onClick(final AjaxRequestTarget target)
	                {
	                    String name = rowModel.getObject().getName();
	                    confirmDeleteDialog
	                        .showMessage(name, onConfirm(rowModel.getObject().getApprenticeshipArea(), dataProvider), onCancel(), target);
	                }
	            };
	            if(!rowModel.getObject().getApprenticeshipArea().isActive())
	            {
	            	deleteButton.setEnabled(false);
	            	deleteButton.setVisible(false);
	            }
	            actionPanel = new VIACActionPanel(componentId, editButton, deleteButton);
	            cellItem.add(actionPanel);
	            buttons = actionPanel.getPanelButtons();	            	
	            verifyPage();
	        }
	
	    });
        
        DataTable<ApprenticeshipAreaTableModel, String> dataTable =
            new DataTable<ApprenticeshipAreaTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
        dataTable.setOutputMarkupId(true);

        return dataTable;
    }
}
