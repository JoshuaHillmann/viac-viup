package com.vanderlande.vipp.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.ajax.form.AjaxFormChoiceComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.HibernateUtil;
import com.vanderlande.viac.command.Command;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.dialog.ConfirmDeleteDialog;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.subject.provider.SortableSubjectDataProvider;

/**
 * The Class PlanningPage.
 * 
 * @author dermic
 * 
 */
public class PlanningPage extends VIPPBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

	/** The session. */
	private Session session;
	
	/** The session factory. */
	private SessionFactory factory = HibernateUtil.getSessionFactory();
    
    /** The confirm delete dialog. */
    private ConfirmDeleteDialog confirmDeleteDialog;

    /** The authorizations. */
    List<String> authorizations;

    /** The action panel. */
    VIACActionPanel actionPanel;
    
    /** The add subject panel. */
    private WebMarkupContainer buildGroupsPanel;
	
	/** The yesNo choice*/
	private final IModel<Boolean> yesNo = new Model<Boolean>();
		
	Calendar c = new GregorianCalendar();
	
    int groups = 1;
        
    IModel<Integer> groupModel = new Model<Integer>();
    
    Model<String> personAmountModel = Model.of("Jede Gruppe besteht aus 0 Auszubildenden");
    
    Label yearOfApprenticeshipLabel = new Label("yearOfApprenticeshipLabel", new Model<String>("Extra Gruppe für:"));
    
    Label personAmount = new Label("personAmount", personAmountModel);
    
    CheckGroup<Integer> year;
    
    private RadioChoice<Integer> yearGroup;
    private Label groupLabel;
    
    boolean extragroup = false;
    
    int extragroupNumber = 0;
    
	List<VIPPGroup> groupList = new ArrayList<VIPPGroup>();
        
    public List<String> groupBy = Arrays.asList(new String[] {"Gemischt", "Ausbildungsjahr", "Ausbildungsberuf"});
    
    private String selectedGroupBy = "Gemischt";
        
    private DropDownChoice<String> groupByChoice;

    /**
     * Instantiates a new subject administration page.
     */
    public PlanningPage()
    {
        init();
    }
    
    public PlanningPage(int groups, boolean extragroup, String selectedGroupBy, int extragroupNumber)
    {
    	this.groups = groups;
    	this.extragroup = extragroup;
    	this.selectedGroupBy = selectedGroupBy;
    	this.extragroupNumber = extragroupNumber;
    	init();
    }

	public PlanningPage(boolean extragroup, String selectedGroupBy, int extragroupNumber, int groups)
    {
    	this.extragroup = extragroup;
    	this.selectedGroupBy = selectedGroupBy;
    	this.extragroupNumber = extragroupNumber;
    	this.groups = groups;
    	init();
    }
	
	public PlanningPage(String selectedGroupBy, int extragroupNumber, int groups)
	{
		this.groups = groups;
		this.extragroupNumber = extragroupNumber;
		this.selectedGroupBy = selectedGroupBy;
		init();
	}
    
    /**
     * Inits the.
     */
    private void init()
    {
    	step = 0;
		personAmount.setOutputMarkupId(true);
    	buildGroupsPanel = new WebMarkupContainer("buildGroupsPanel");
        this.setOutputMarkupId(true);
		final int persons = DatabaseProvider.getInstance().getAll(VIPPApprentice.class).size();
        int max = 0;
        final Form<Object> buildGroupsForm = new Form<Object>("buildGroupsForm");
        final Label groupCountLabel = new Label("groupCountLabel", new Model<String>("Anzahl Gruppen:")); 
        final NumberTextField<Integer> groupCountInput =
                new NumberTextField<Integer>("groupCountInput", groupModel, Integer.class);
        if(selectedGroupBy == groupBy.get(2))
        {
        	max = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class).size();
        }
        else
        {
        	max = DatabaseProvider.getInstance().getAll(VIPPApprentice.class).size();
        }
        groupCountInput.setMinimum(1);
        groupCountInput.setMaximum(max);
        groupCountInput.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(groupCountInput.getConvertedInput() != null)
				{
					groups = groupCountInput.getConvertedInput().intValue();					
					if(selectedGroupBy != groupBy.get(2))
					{
						if(persons%groups != 0)
						{
							personAmountModel.setObject("Jede Gruppe besteht aus " + (int)(persons/groups) + " oder " + (int)(persons/groups+1) + " Auszubildenden.");
							target.add(personAmount);
						}
						else
						{
							personAmountModel.setObject("Jede Gruppe besteht aus " + (int)(persons/groups) + " Auszubildenden");
							target.add(personAmount);		    		
						} 
					}
					else
					{
						personAmountModel.setObject(getGroupSizes(groups));
						target.add(personAmount);
					}
				}
			}
		});
        Label groupByLabel = new Label("groupByLabel", new Model<String>("Gruppen einteilen nach:"));
        groupByChoice = new DropDownChoice<String>("groupBy", new PropertyModel<String>(this, "selectedGroupBy"), groupBy);
        groupByChoice.add(new OnChangeAjaxBehavior() {
			
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				selectedGroupBy = groupByChoice.getConvertedInput();
				setResponsePage(new PlanningPage(selectedGroupBy, extragroupNumber, groups));
			}
		});
        groupByChoice.setRequired(true);
		
		Label needSpecialGroupLabel = new Label("needSpecialGroupLabel", new Model<String>("Extra Gruppe für ein Ausbildungsjahr:"));
		final RadioGroup<Boolean> yesNoGroup = new RadioGroup<Boolean>("yesNoGroup", yesNo);
		Radio<Boolean> yes = new Radio<Boolean>("yes", Model.of(Boolean.TRUE));
		Radio<Boolean> no = new Radio<Boolean>("no", Model.of(Boolean.FALSE));
		yesNoGroup.setModelObject(false);
		yesNoGroup.add(yes);
		yesNoGroup.add(no);
		
		yes.add(new AjaxEventBehavior("click"){
			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				yesNoGroup.setModelObject(true);
				groupLabel.setVisible(true);
				groupLabel.setOutputMarkupId(true);
				yearGroup.setVisible(true);
				yearGroup.setOutputMarkupId(true);
				target.add(buildGroupsForm);
				extragroup = true;
			}
		});
		no.add(new AjaxEventBehavior("click"){
			@Override
			protected void onEvent(AjaxRequestTarget target)
			{
				yesNoGroup.setModelObject(false);
				groupLabel.setVisible(false);
				groupLabel.setOutputMarkupId(true);
				yearGroup.setVisible(false);
				target.add(buildGroupsForm);
				extragroup = false;
				extragroupNumber = 0;
			}
		});		
		
		if(selectedGroupBy == groupBy.get(1))
		{
			groupCountLabel.setVisible(false);
			groupCountInput.setVisible(false);
			personAmount.setVisible(false);
			needSpecialGroupLabel.setVisible(false);
			yesNoGroup.setVisible(false);
		}
        
		groupLabel = new Label("groupLabel", new Model<String>("Ausbildungsjahr:"));
		final List<Integer> years = Arrays.asList(new Integer[] {1, 2, 3});
		yearGroup = new RadioChoice<Integer>("yearGroup", new PropertyModel<Integer>(this, "extragroupNumber"), years);
		yearGroup.add(new AjaxFormChoiceComponentUpdatingBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(extragroup)
				{
					extragroupNumber = yearGroup.getConvertedInput();					
				}
			}
		});

		if(!extragroup)
		{
			yearGroup.setVisible(false);
			groupLabel.setVisible(false);
		}
		
		IndicatingAjaxButton submitButton = new IndicatingAjaxButton("submit")
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	int option = 0;
            	for (int i = 0; i < groupBy.size(); i++)
            	{
            		if(groupBy.get(i) == groupByChoice.getConvertedInput())
            		{
            			option = i;
            		}
            	}
            	if(option == 2)
            	{
            		setResponsePage(new GroupingPage(groupList, extragroupNumber));
            	}
            	else
            	{
            		setResponsePage(new GroupingPage(groups, option, extragroupNumber));            		
            	}
            }
            @Override
            public String getAjaxIndicatorMarkupId() {
                         return "indicator";
            }
        };
         
		buildGroupsPanel.add(personAmount);
		 
		buildGroupsForm.add(groupCountLabel);
		buildGroupsForm.add(groupCountInput);
		 
		buildGroupsForm.add(groupByLabel);
		buildGroupsForm.add(groupByChoice);
		
		buildGroupsForm.add(needSpecialGroupLabel);
		buildGroupsForm.add(yesNoGroup);
		 
		buildGroupsForm.add(groupLabel);
		buildGroupsForm.add(yearGroup);
		 
		buildGroupsForm.add(submitButton);
		buildGroupsPanel.add(buildGroupsForm);
		add(buildGroupsPanel);
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
    public Command onConfirm(final VIPPSubject rowModel, final SortableSubjectDataProvider dataProvider)
    {
        return new Command()
        {
            @Override
            public void execute()
            {
                AjaxRequestTarget ajaxTarget = getRequestCycle().find(AjaxRequestTarget.class);
                rowModel.setIsActive(Boolean.FALSE);
                DatabaseProvider.getInstance().merge(rowModel);
                dataProvider.reload();
                confirmDeleteDialog.close(ajaxTarget);
                ajaxTarget.add(PlanningPage.this);
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
    private String getGroupSizes(int groupAmount)
    {
    	int max;
    	List<VIPPApprentice> apprenticesExtraYear = new ArrayList<VIPPApprentice>();
    	int apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class).size();

    	if(extragroupNumber != 0)
    	{
    		max = groupAmount + 1;
    		int year = c.get(Calendar.YEAR) - extragroupNumber;
    		if(c.get(Calendar.MONTH) > 8)
    		{
    			year = year + 1;
    		}
    		session = factory.openSession();
    		apprenticesExtraYear = session.
    				createQuery("from VIPPApprentice WHERE apprentice_yearOfApprenticeship = " + year, VIPPApprentice.class)
    				.getResultList();
    		session.close();
    		apprentices = apprentices - apprenticesExtraYear.size();
    	}
    	else
    	{
    		max = groupAmount;
    	}
    	
    	int[] groupSizes = new int[max];
    	List<VIPPApprentice> apprenticesArea = new ArrayList<VIPPApprentice>();
    	List<VIPPApprenticeshipArea> apprenticeshipAreas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
    	List<List<VIPPApprentice>> apprenticesList = new ArrayList<List<VIPPApprentice>>(); 
    	int[] areaNumber = new int[apprenticeshipAreas.size()];
    	String sizes = "Die Gruppen bestehen aus";
    	
    	for(int i = 0; i < apprenticeshipAreas.size(); i++)
    	{
    		session = factory.openSession();
    		apprenticesArea = session
    				.createQuery("from VIPPApprentice WHERE apprenticeshipArea_apprenticeshipArea_id = " + (i+1), VIPPApprentice.class)
    				.getResultList();
    		session.close();
    		apprenticesArea.removeAll(apprenticesExtraYear);
    		areaNumber[i] = apprenticesArea.size();
    		apprenticesList.add(apprenticesArea);
    	}
    	for(int i = 0; i < groupAmount; i++)
    	{
    		int maxArea = 0;
    		int index = 0;
    		for(int j = 0; j < areaNumber.length; j++)
    		{
    			if(areaNumber[j] > maxArea)
    			{
    				maxArea = areaNumber[j];
    				index = j;
    			}
    		}
    		groupSizes[i] = groupSizes[i] + maxArea;
    		areaNumber[index] = 0;
    		apprentices = apprentices - maxArea;
    		groupList.add(new VIPPGroup());
    		groupList.get(i).getPersons().addAll(apprenticesList.get(index));
    	}
    	while(apprentices > 0)
    	{
			for(int j = 0; j < areaNumber.length; j++)
			{
				if(areaNumber[j] > 0)
				{
					int min = 100;
					int index = 0;
					for(int i = 0; i < groupSizes.length; i++)
					{
						if(groupSizes[i] <= min && groupSizes[i] != 0)
						{
							min = groupSizes[i];
							index = i;
						}
					}
					groupSizes[index] = groupSizes[index] + areaNumber[j];
					apprentices = apprentices - areaNumber[j];
					groupList.get(index).getPersons().addAll(apprenticesList.get(j));
				}
			}
    	}
    	if(extragroupNumber != 0)
    	{
    		VIPPGroup extra = new VIPPGroup(apprenticesExtraYear);
    		groupList.add(extra);
    	}
    	for(int i = 0; i < groupSizes.length; i++)
    	{
    		if(extragroupNumber == 0)
    		{
    			if(i == 0)
    			{
    				sizes = sizes + " " + groupSizes[i];
    			}
    			else if(i < groupSizes.length-1)
    			{
    				sizes = sizes + ", " + groupSizes[i];
    			}
    			else
    			{
    				sizes = sizes + " und " + groupSizes[i] + " Personen";
    			}    			
    		}
    		else
    		{
    			if(i == 0)
    			{
    				sizes = sizes + " " + groupSizes[i];
    			}
    			else if(i < groupSizes.length-1)
    			{
    				sizes = sizes + ", " + groupSizes[i];
    			}
    			else
    			{
    				sizes = sizes + "Personen und die extra Gruppe aus " + groupSizes[i] + " Personen";
    			}    
    		}
    	}
    	return sizes;
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
