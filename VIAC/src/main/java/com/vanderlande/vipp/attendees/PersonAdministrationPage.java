package com.vanderlande.vipp.attendees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.attendees.data.ApprenticeTableModel;
import com.vanderlande.vipp.attendees.provider.SortablePersonDataProvider;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vita.applicant.ApplicantDetailPage;
import com.vanderlande.vita.applicant.ApplicantManagementPage;

/**
 * The Class PersonAdministrationPage.
 * 
 * @author dermic
 * 
 *         Displays all apprentices. Click on an apprentice to edit/delete it. You can also create a new apprentice.
 */
public class PersonAdministrationPage extends VIPPBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;
        
    private ApprenticeInfoWindow infoWindow;

    /** The action panel. */
    VIACActionPanel actionPanel;
    
    Calendar c = new GregorianCalendar();

    /** The buttons. */
    List<VIACButton> buttons;
    
    /** The add subject panel. */
    private WebMarkupContainer addApprenticePanel;
    
	/** The career choice. */
	private DropDownChoice<VIPPApprenticeshipArea> apprenticeshipAreaChoice;
	
	/** The career choice. */
	private DropDownChoice<Integer> yearOfApprenticeshipChoice;
	
	private IModel<Integer> yearModel = new Model<Integer>();
	
	private VIPPApprentice editApprentice;
	
	private TextField<String> apprenticeFirstnameInput;
	
    private DataTable<ApprenticeTableModel, String> tableWithFilterForm;
	
	private TextField<String> apprenticeLastnameInput;
	
	private SortablePersonDataProvider dataProvider;
	
	final VIPPApprentice apprentice = new VIPPApprentice();
	
	VIPPApprentice doubledApprentice = new VIPPApprentice();
	
	public Link<String> cancelButton;
	
	private static final List<Integer> years = Arrays.asList(new Integer[]{new Integer(1), new Integer(2), new Integer(3)});
	    
    /**
     * Instantiates a new apprentice administration page.
     */
    public PersonAdministrationPage()
    {
        init();
    }

    /**
     * Inits the.
     */
    private void init()
    {
    	addApprenticePanel = new WebMarkupContainer("addApprenticePanel");

        buttons = new ArrayList<>();
        
        infoWindow = new ApprenticeInfoWindow("infoWindow");
        add(infoWindow);

        this.setOutputMarkupId(true);

        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Vorname", "firstname"));
        filters.add(new FilterOption("Nachname", "lastname"));
        filters.add(new FilterOption("Ausbildungsgang", "apprenticeshipArea"));
        filters.add(new FilterOption("Ausbildungsjahr", "yearOfApprenticeship"));

        dataProvider = new SortablePersonDataProvider(filters);

        tableWithFilterForm = this.initDataTable(filters);
        
        FilterForm<ApprenticeTableModel> apprenticeFilterForm = new FilterForm<ApprenticeTableModel>("filterForm", dataProvider);
        
        FilterToolbar filterTooblbar = new FilterToolbar(tableWithFilterForm, apprenticeFilterForm);
        tableWithFilterForm.addTopToolbar(filterTooblbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        
        apprenticeFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<ApprenticeTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        apprenticeFilterForm.add(filterPanel);

        add(apprenticeFilterForm);
        
        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);
        
        final ModalWindow confirmDialog;
		add(confirmDialog = new ModalWindow("notificationDialog"));
		confirmDialog.showUnloadConfirmation(false);
		confirmDialog.setInitialWidth(ConfirmCreateApprenticeContent.DEFAULT_WIDTH);
		confirmDialog.setInitialHeight(ConfirmCreateApprenticeContent.DEFAULT_HEIGHT);

		confirmDialog.setResizable(false);
		confirmDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				return true;
			}
		});

        // Form to add a new person
        Form<String> addApprenticeForm = new Form<String>("addApprenticeForm");
        
        // for the person data
        Label firstnameLabel = new Label("firstnameLabel", new Model<String>("Vorname:"));
        apprenticeFirstnameInput =
            new TextField<String>("apprenticeFirstnameInput", new PropertyModel<String>(apprentice, "firstname"));
        apprenticeFirstnameInput.setRequired(true);
        apprenticeFirstnameInput.setOutputMarkupId(true);
        
        Label lastnameLabel = new Label("lastnameLabel", new Model<String>("Nachname:"));
        apprenticeLastnameInput =
            new TextField<String>("apprenticeLastnameInput", new PropertyModel<String>(apprentice, "lastname"));
        apprenticeLastnameInput.setRequired(true);
        apprenticeLastnameInput.setOutputMarkupId(true);
        
		// get all apprenticeshipAreas
		List<VIPPApprenticeshipArea> apprenticeshipAreas;
		apprenticeshipAreas = new ArrayList<VIPPApprenticeshipArea>();
		apprenticeshipAreas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
//		if (!apprenticeshipAreas.contains(apprentice.getApprenticeshipArea()) && apprentice.getApprenticeshipArea() != null) {
//			apprenticeshipAreas.add(apprentice.getApprenticeshipArea());
//		}
		List<VIPPApprenticeshipArea> deletedApprenticeshipAreas = new ArrayList<VIPPApprenticeshipArea>();
		for(VIPPApprenticeshipArea area : apprenticeshipAreas)
		{
			if(!area.isActive())
			{
				deletedApprenticeshipAreas.add(area);
			}
		}
		apprenticeshipAreas.removeAll(deletedApprenticeshipAreas);

		// apprenticeshipArea
		Label apprenticeshipAreaLabel = new Label("apprenticeshipAreaLabel", new Model<String>("Ausbildungsberuf:"));
		apprenticeshipAreaChoice = new DropDownChoice<VIPPApprenticeshipArea>("apprenticeshipAreas", new PropertyModel<VIPPApprenticeshipArea>(apprentice, "apprenticeshipArea"),
				apprenticeshipAreas);
		apprenticeshipAreaChoice.setRequired(true);
		apprenticeshipAreaChoice.setOutputMarkupId(true);
		Label yearOfApprenticeshipLabel = new Label("yearOfApprenticeshipLabel", new Model<String>("Ausbildungsjahr:"));
		yearOfApprenticeshipChoice = new DropDownChoice<Integer>("yearOfApprenticeshipChoice", yearModel, years); 
		yearOfApprenticeshipChoice.setRequired(true);
		yearOfApprenticeshipChoice.setOutputMarkupId(true);
        
		AjaxButton submitButton = new AjaxButton("submit")
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                super.onSubmit(target, form);
                final int yearOfApprenticeship;
                int currentYear = c.get(Calendar.YEAR);
            	int year = yearOfApprenticeshipChoice.getConvertedInput();
            	// a the new apprentices start in september so it makes a difference at which date you add the apprentices 
            	if(c.get(Calendar.MONTH) >= 8)
            	{
            		yearOfApprenticeship = currentYear + 1 - year;
            	}
            	else
            	{
            		yearOfApprenticeship = currentYear - year;
            	} 	
                apprentice.setYearOfApprenticeship(yearOfApprenticeship);
                boolean warning = false;
                if(editApprentice != null)
                {
                	editApprentice.setFirstname(apprenticeFirstnameInput.getConvertedInput());
                	editApprentice.setLastname(apprenticeLastnameInput.getConvertedInput());
                	editApprentice.setApprenticeshipArea(apprenticeshipAreaChoice.getConvertedInput());
                	editApprentice.setYearOfApprenticeship(yearOfApprenticeship);
                	DatabaseProvider.getInstance().merge(editApprentice);
                	setResponsePage(new PersonAdministrationPage());
                }
                else
                {
                	List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
                	for(VIPPApprentice vippApprentice : apprentices)
                	{
                		if(vippApprentice.getFirstname().equals(apprenticeFirstnameInput.getConvertedInput()) && vippApprentice.getLastname().equals(apprenticeLastnameInput.getConvertedInput()))
                		{
                			warning = true;
                			doubledApprentice = vippApprentice;
                			break;
                		}
                	}
                	if(warning)
                	{
                		if(doubledApprentice.isActive())
                		{
                			confirmDialog.setContent(new ConfirmCreateApprenticeContent(confirmDialog.getContentId(), PersonAdministrationPage.this) {
                				/** The Constant serialVersionUID. */
                				private static final long serialVersionUID = 1L;
                				
                				@Override
                				public void onConfirm() {
                					createApprentice(); 
                					setResponsePage(PersonAdministrationPage.class);
                				}
                				
                				@Override
                				public void onCancel() {
                					AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                					confirmDialog.close(ajaxTarget);
                				}
                			});
                			confirmDialog.show(target);               	
                		}
                		else
                		{
                			confirmDialog.setContent(new ConfirmReactivateApprenticeContent(confirmDialog.getContentId(), PersonAdministrationPage.this)
                			{
                  				@Override
                				public void onConfirm() {
                					doubledApprentice.setActive(true);
                					doubledApprentice.setApprenticeshipArea(apprenticeshipAreaChoice.getModelObject());
                					doubledApprentice.setYearOfApprenticeship(yearOfApprenticeship);
                					DatabaseProvider.getInstance().merge(doubledApprentice);
                					setResponsePage(PersonAdministrationPage.class);
                				}
                				@Override
                				public void onCreate() {
                					createApprentice(); 
                					setResponsePage(PersonAdministrationPage.class);
                				}
                				@Override
                				public void onCancel() {
                					AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                					confirmDialog.close(ajaxTarget);
                				}
                			});
                			confirmDialog.show(target);        
                		}
                	}
                	else
                	{
                		createApprentice();
	                	setResponsePage(new PersonAdministrationPage());
                	}
                }
            }
        };
        cancelButton = new Link<String>("cancel")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onClick()
            {
                setResponsePage(new PersonAdministrationPage());
            }
        };
        addApprenticeForm.add(firstnameLabel);
        addApprenticeForm.add(apprenticeFirstnameInput);
        
        addApprenticeForm.add(lastnameLabel);
        addApprenticeForm.add(apprenticeLastnameInput);
        
        addApprenticeForm.add(apprenticeshipAreaLabel);
        addApprenticeForm.add(apprenticeshipAreaChoice);
        
        addApprenticeForm.add(yearOfApprenticeshipLabel);
        addApprenticeForm.add(yearOfApprenticeshipChoice);
        
        addApprenticeForm.add(submitButton);
        addApprenticeForm.add(cancelButton);
        addApprenticePanel.add(addApprenticeForm);
        add(addApprenticePanel);
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
    public Command onConfirm(final VIPPApprentice rowModel, final SortablePersonDataProvider dataProvider)
    {
        return new Command()
        {
            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                rowModel.setActive(false);
                DatabaseProvider.getInstance().merge(rowModel);
                setResponsePage(new PersonAdministrationPage());
                
                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(PersonAdministrationPage.this);
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
    
    private DataTable<ApprenticeTableModel, String> initDataTable(List<FilterOption> filters)
    {
        List<IColumn<ApprenticeTableModel, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ApprenticeTableModel, String>(new Model<>("Vorname"), "firstname", "firstname"));
        columns.add(new PropertyColumn<ApprenticeTableModel, String>(new Model<>("Nachname"), "lastname", "lastname"));
        columns.add(new PropertyColumn<ApprenticeTableModel, String>(new Model<>("Ausbildungsjahr"), "yearOfApprenticeship", "yearOfApprenticeship"));
        columns.add(new PropertyColumn<ApprenticeTableModel, String>(new Model<>("Ausbildungsberuf"), "apprenticeshipArea", "apprenticeshipArea"));
	    columns.add(new AbstractColumn<ApprenticeTableModel, String>(new Model<>("Aktionen"))
	    {
	        @Override
	        public void populateItem(Item<ICellPopulator<ApprenticeTableModel>> cellItem, String componentId,
	                                 final IModel<ApprenticeTableModel> rowModel)
	        {
	            VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
	            {
	                @Override
	                public void onClick(final AjaxRequestTarget target)
	                {
	                    editApprentice = rowModel.getObject().getApprentice();
	                    apprenticeFirstnameInput.setModel(new PropertyModel<String>(editApprentice, "firstname"));
	                    apprenticeFirstnameInput.setOutputMarkupId(true);
	                    apprenticeLastnameInput.setModel(new PropertyModel<String>(editApprentice, "lastname"));
	                    apprenticeLastnameInput.setOutputMarkupId(true);
	                    apprenticeshipAreaChoice.setModel(new PropertyModel<VIPPApprenticeshipArea>(editApprentice, "apprenticeshipArea"));
	                    apprenticeshipAreaChoice.setOutputMarkupId(true);
	                    yearOfApprenticeshipChoice.setModel(new PropertyModel<Integer>(editApprentice, "yearOfApprenticeship"));
	                    yearOfApprenticeshipChoice.setOutputMarkupId(true);
	                    target.add(apprenticeFirstnameInput);
	                    target.add(apprenticeLastnameInput);
	                    target.add(apprenticeshipAreaChoice);
	                    target.add(yearOfApprenticeshipChoice);
	                }
	            };
	            VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
	            {
	                @Override
	                public void onClick(final AjaxRequestTarget target)
	                {
	                    String lastname = rowModel.getObject().getLastname();
	                    String firstname = rowModel.getObject().getFirstname();
	                    confirmDeleteDialog
	                        .showMessage(firstname + " " + lastname, onConfirm(rowModel.getObject().getApprentice(), dataProvider), onCancel(), target);
	                }
	            };
                VIACButton infoButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    private static final long serialVersionUID = 8842648163086760741L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                    	infoWindow.showMessage(rowModel.getObject().getApprentice(), target);
                    }
                };
	            actionPanel = new VIACActionPanel(componentId, editButton, deleteButton, infoButton);
	            cellItem.add(actionPanel);
	            buttons = actionPanel.getPanelButtons();
	            verifyPage();
	        }
	
	    });
        

        DataTable<ApprenticeTableModel, String> dataTable =
            new DataTable<ApprenticeTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
        dataTable.setOutputMarkupId(true);

        return dataTable;
    }

    private void createApprentice()
    {
    	DatabaseProvider.getInstance().create(apprentice);       
    }
}
