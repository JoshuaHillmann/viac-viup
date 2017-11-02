package com.vanderlande.vipp.subject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPLearningUnit;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.model.VIPPSubjectArea;
import com.vanderlande.vipp.subject.data.SubjectTableModel;
import com.vanderlande.vipp.subject.provider.SortableSubjectDataProvider;

/**
 * The Class SubjectAdministration.
 * 
 * @author dermic
 * 
 *         Displays all subjects. Click on a subjects to edit/delete it. You can also create a new subject.
 */
public class SubjectAdministrationPage extends VIPPBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The authorizations. */
    List<String> authorizations;

    /** The action panel. */
    VIACActionPanel actionPanel;

    /** The buttons. */
    List<VIACButton> buttons;
    
    private TextField<String> subjectNameInput;
    
    
    private Set<VIPPApprenticeshipArea> selectedApprenticeshipAreas;
    
    private RadioGroup<Boolean> yesNoGroup;
    
    /** The add subject panel. */
    private WebMarkupContainer addSubjectPanel;
    
	/** The area choice. */
	private DropDownChoice<VIPPSubjectArea> areaChoice;
	
	/** The unit choice. */
	private DropDownChoice<VIPPLearningUnit> unitChoice;
	
	private Form<String> addSubjectForm = new Form<String>("addSubjectForm");
	
	private List<VIPPLearningUnit> selectedUnitsList = new ArrayList<VIPPLearningUnit>();
	private ListView<VIPPLearningUnit> learningUnits;
	private final List<VIPPLearningUnit> units = DatabaseProvider.getInstance().getAll(VIPPLearningUnit.class);
	private CheckGroup<VIPPLearningUnit> group = new CheckGroup<VIPPLearningUnit>("group", selectedUnitsList);
	
	final VIPPSubject subject = new VIPPSubject();
	
	private DataTable<SubjectTableModel, String> tableWithFilterForm;
	private SortableSubjectDataProvider dataProvider;
	
	private SubjectInfoWindow infoWindow;
	
	final List<VIPPApprenticeshipArea> areas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
    CheckGroup<VIPPApprenticeshipArea> apprenticeshipAreasCheck;
    ListView<VIPPApprenticeshipArea> apprenticeshipAreas;
	
	/** The yesNo choice*/
	private final IModel<Boolean> yesNo = new Model<Boolean>();
	
	private VIPPSubject editSubject;

    /**
     * Instantiates a new subject administration page.
     */
    public SubjectAdministrationPage()
    {
        init();
    }

    /**
     * Inits the.
     */
    private void init()
    {
    	addSubjectPanel = new WebMarkupContainer("addSubjectPanel");
    	addSubjectPanel.setOutputMarkupId(true);
    	addSubjectForm.setOutputMarkupId(true);

        buttons = new ArrayList<>();

        this.setOutputMarkupId(true);
        
        infoWindow = new SubjectInfoWindow("infoWindow");
        add(infoWindow);

        List<FilterOption> filterProperties = new ArrayList<>();
        filterProperties.add(new FilterOption("Name", "name"));
        filterProperties.add(new FilterOption("Themengebiet", "subjectArea"));
        filterProperties.add(new FilterOption("Lerneinheit", "learningUnit"));
        filterProperties.add(new FilterOption("Relevant für", "apprenticeshipArea"));
        filterProperties.add(new FilterOption("Prüfungsrelevant", "isFinalExamRelevant"));
        
        dataProvider = new SortableSubjectDataProvider(filterProperties);
        
        tableWithFilterForm = this.initDataTable(filterProperties);
        
        FilterForm<SubjectTableModel> subjectFilterForm = new FilterForm<SubjectTableModel>("filterForm", dataProvider);
        
        FilterToolbar filterTooblbar = new FilterToolbar(tableWithFilterForm, subjectFilterForm);
        tableWithFilterForm.addTopToolbar(filterTooblbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        
        subjectFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<SubjectTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        subjectFilterForm.add(filterPanel);

        add(subjectFilterForm);

        confirmDeleteDialog = new ConfirmDeleteDialog("confirmDeleteDialog");
        add(confirmDeleteDialog);
        
        final ModalWindow confirmDialog;
		add(confirmDialog = new ModalWindow("notificationDialog"));
		confirmDialog.showUnloadConfirmation(false);
		confirmDialog.setInitialWidth(ConfirmCreateSubjectContent.DEFAULT_WIDTH);
		confirmDialog.setInitialHeight(ConfirmCreateSubjectContent.DEFAULT_HEIGHT);

		confirmDialog.setResizable(false);
		confirmDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback() {
			/** The Constant serialVersionUID. */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean onCloseButtonClicked(AjaxRequestTarget target) {
				return true;
			}
		});

        // Form to add a new subject
        Label nameLabel = new Label("nameLabel", new Model<String>("Name:"));
        subjectNameInput =
            new TextField<String>("subjectNameInput", new PropertyModel<String>(subject, "name"));
        subjectNameInput.setRequired(true);
        subjectNameInput.setOutputMarkupId(true);
        
        
		// get all subjectAreas
		List<VIPPSubjectArea> subjects;
		subjects = new ArrayList<VIPPSubjectArea>();
		subjects = DatabaseProvider.getInstance().getAll(VIPPSubjectArea.class);
		if (!subjects.contains(subject.getSubjectArea()) && subject.getSubjectArea() != null) {
			subjects.add(subject.getSubjectArea());
		}

		// areas
		Label subjectLabel = new Label("subjectLabel", new Model<String>("Themengebiet:"));
		areaChoice = new DropDownChoice<VIPPSubjectArea>("subjects", new PropertyModel<VIPPSubjectArea>(subject, "subjectArea"),
				subjects);
		areaChoice.setRequired(true);
		areaChoice.setOutputMarkupId(true);
		
		Label unitLabel = new Label("unitsLabel", new Model<String>("Lerneinheit:"));
        learningUnits = new ListView<VIPPLearningUnit>("learningUnits", units)
        {

            private static final long serialVersionUID = 8302133829188691631L;

            protected void populateItem(ListItem<VIPPLearningUnit> item)
            {
                item.add(new Check<VIPPLearningUnit>("checkbox", item.getModel()));
                item.add(new Label("unitName", item.getModel().getObject().getName()));
            }
        }.setReuseItems(true);
        learningUnits.setOutputMarkupId(true);
        group.add(learningUnits);
        
        selectedApprenticeshipAreas = new HashSet<VIPPApprenticeshipArea>();
        Label areasLabel = new Label("areasLabel", new Model<String>("Relevant für:"));
        apprenticeshipAreasCheck = new CheckGroup<VIPPApprenticeshipArea>("apprenticeshipAreasCheck", selectedApprenticeshipAreas);
        apprenticeshipAreas = new ListView<VIPPApprenticeshipArea>("apprenticeshipAreas", areas)
        {

            private static final long serialVersionUID = 8302133829188691631L;

            protected void populateItem(ListItem<VIPPApprenticeshipArea> item)
            {
                item.add(new Check<VIPPApprenticeshipArea>("checkbox", item.getModel()));
                item.add(new Label("areaName", item.getModel().getObject().getName()));
            }
        };
        apprenticeshipAreas.setOutputMarkupId(true);
        apprenticeshipAreasCheck.add(apprenticeshipAreas);
		
		Label isFinalExamRelevantLabel = new Label("isFinalExamRelevantLabel", new Model<String>("Prüfungsrelevant:"));
		yesNoGroup = new RadioGroup<Boolean>("yesNoGroup", yesNo);
		yesNoGroup.add(new Radio<Boolean>("yes", Model.of(Boolean.TRUE)));
		yesNoGroup.add(new Radio<Boolean>("no", Model.of(Boolean.FALSE)));
		
        AjaxButton submitButton = new AjaxButton("submit")
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
                super.onSubmit(target, form);
                subject.setLearningUnit(selectedUnitsList);
                subject.setApprenticeshipArea(selectedApprenticeshipAreas);
                subject.setIsFinalExamRelevant(yesNoGroup.getConvertedInput().booleanValue());
                boolean warning = false;
                if(editSubject != null)
                {
                	editSubject.setName(subjectNameInput.getConvertedInput());
                	editSubject.setSubjectArea(areaChoice.getConvertedInput());
                	List<VIPPLearningUnit> selectedUnits = new ArrayList<VIPPLearningUnit>(selectedUnitsList);
                	editSubject.setLearningUnit(selectedUnits);
                	Set<VIPPApprenticeshipArea> selectedAreas = new HashSet<VIPPApprenticeshipArea>(selectedApprenticeshipAreas);
                	editSubject.setApprenticeshipArea(selectedAreas);
                	editSubject.setIsFinalExamRelevant(yesNoGroup.getConvertedInput().booleanValue());
                	DatabaseProvider.getInstance().merge(editSubject);
                	setResponsePage(new SubjectAdministrationPage());
                }
                else
                {
					List<VIPPSubject> subjects = DatabaseProvider.getInstance().getAll(VIPPSubject.class);
					for (VIPPSubject vippSubject : subjects) {
						if(subjectNameInput.getConvertedInput().equals(vippSubject.getName()))
						{
							warning = true;
							break;
						}
					}
					if (!warning) {
						createSubject();
	                	setResponsePage(new SubjectAdministrationPage());
					} 
					else 
					{
						confirmDialog.setContent(new ConfirmCreateSubjectContent(confirmDialog.getContentId(), SubjectAdministrationPage.this) {
							/** The Constant serialVersionUID. */
							private static final long serialVersionUID = 1L;
	
							@Override
							public void onConfirm() {
								createSubject(); 
								setResponsePage(SubjectAdministrationPage.class);
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
            }
        };
        addSubjectForm.add(nameLabel);
        addSubjectForm.add(subjectNameInput);
        
        addSubjectForm.add(subjectLabel);
        addSubjectForm.add(areaChoice);
        
        addSubjectForm.add(unitLabel);
        addSubjectForm.add(group);
        
        addSubjectForm.add(areasLabel);
        addSubjectForm.add(apprenticeshipAreasCheck);
        
        addSubjectForm.add(isFinalExamRelevantLabel);
        addSubjectForm.add(yesNoGroup);
        
        addSubjectForm.add(submitButton);
        addSubjectPanel.add(addSubjectForm);
        add(addSubjectPanel);
    }

    /**
     * On confirm.
     *
     * @param subjectTableModel
     *            the row model
     * @param dataProvider
     *            the data provider
     * @return the command
     */
    public Command onConfirm(final SubjectTableModel subjectTableModel, final SortableSubjectDataProvider dataProvider)
    {
        return new Command()
        {
            @Override 
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                DatabaseProvider.getInstance().merge(subjectTableModel);
                dataProvider.reload();

                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(SubjectAdministrationPage.this);
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
    
    public void createSubject()
    {
    	DatabaseProvider.getInstance().create(subject);
    }
    
    private DataTable<SubjectTableModel, String> initDataTable(List<FilterOption> filters)
    {
        List<IColumn<SubjectTableModel, String>> columns = new ArrayList<>();
        
        columns.add(new PropertyColumn<SubjectTableModel, String>(new Model<>("Name"), "name", "name"));
        columns.add(new PropertyColumn<SubjectTableModel, String>(new Model<>("Themengebiet"), "subjectArea", "subjectArea"));
        columns.add(new PropertyColumn<SubjectTableModel, String>(new Model<>("Lerneinheit"), "learningUnit", "learningUnit"));
        columns.add(new PropertyColumn<SubjectTableModel, String>(new Model<>("Relevant für"), "apprenticeshipArea", "apprenticeshipArea"));
        columns.add(new PropertyColumn<SubjectTableModel, String>(new Model<>("Prüfungsrelevant"), "isFinalExamRelevant", "isFinalExamRelevant"));
        columns.add(new AbstractColumn<SubjectTableModel, String>(new Model<>("Aktionen"))
        {
            @Override
            public void populateItem(Item<ICellPopulator<SubjectTableModel>> cellItem, String componentId,
                                     final IModel<SubjectTableModel> rowModel)
            {
                VIACButton editButton = new VIACButton(VIACButton.Type.EDIT)
                {
                    @Override
                    public void onClick(final AjaxRequestTarget target)
                    {
                        editSubject = rowModel.getObject().getSubject();
                        
                        subjectNameInput.setModel(new PropertyModel<String>(editSubject, "name"));
                        subjectNameInput.setOutputMarkupId(true);
                        
                        areaChoice.setModel(new PropertyModel<VIPPSubjectArea>(editSubject, "subjectArea"));
                        areaChoice.setOutputMarkupId(true);
                      
                        group.getModelObject().clear();
                        for(VIPPLearningUnit unitToBeChecked : editSubject.getLearningUnit())
                        {
		                      for(VIPPLearningUnit unit : learningUnits.getModelObject())
		                      {
                        	      if(unitToBeChecked.getName().equals(unit.getName()))
                        	      {
                        	    	  group.getModelObject().add(unit);                        			
                        	      }
		                      }
                        }
                      
                        apprenticeshipAreasCheck.getModelObject().clear();
                        for(VIPPApprenticeshipArea areaToBeChecked : editSubject.getApprenticeshipArea())
                        {
                        	for(VIPPApprenticeshipArea area : apprenticeshipAreas.getModelObject())
                        	{
                        		if(areaToBeChecked.getName().equals(area.getName()))
                        		{
                        			apprenticeshipAreasCheck.getModelObject().add(area);
                        		}
                        	}
                        }

                        yesNoGroup.setModelObject(editSubject.getIsFinalExamRelevant());                        
                        target.add(addSubjectPanel);
                    }
                };
                VIACButton deleteButton = new VIACButton(VIACButton.Type.DELETE)
                {
                	@Override
                	public void onClick(final AjaxRequestTarget target)
                	{
                		String name = rowModel.getObject().getName();
                		confirmDeleteDialog
                		.showMessage(name, onConfirm(rowModel.getObject(), dataProvider), onCancel(), target);
                	}
                };
                VIACButton infoButton = new VIACButton(VIACButton.Type.DETAILS)
                {
                    private static final long serialVersionUID = 8842648163086760741L;

                    @Override
                    public void onClick(AjaxRequestTarget target)
                    {
                    	infoWindow.showMessage(rowModel.getObject().getSubject(), target);
                    }
                };
                actionPanel = new VIACActionPanel(componentId, editButton, deleteButton, infoButton);
                cellItem.add(actionPanel);
                buttons = actionPanel.getPanelButtons();
                verifyPage();
            }

        });        

        DataTable<SubjectTableModel, String> dataTable =
                new DataTable<SubjectTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
            dataTable.setOutputMarkupId(true);

        return dataTable;
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
}
