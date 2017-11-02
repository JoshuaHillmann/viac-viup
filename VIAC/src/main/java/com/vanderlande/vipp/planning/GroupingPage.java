package com.vanderlande.vipp.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
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
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.HibernateUtil;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.filter.FilterOption;
import com.vanderlande.viac.filter.VIACFilterPanel;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.planning.data.ApprenticeGroupTableModel;
import com.vanderlande.vipp.planning.provider.SortableApprenticeGroupDataProvider;

/**
 * The Class GroupingPage.
 * 
 * @author dermic
 * 
 */
public class GroupingPage extends VIPPBasePage implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

	/** The session. */
	private Session session;
	
	/** The session factory. */
	private SessionFactory factory = HibernateUtil.getSessionFactory();
    
    /** The authorizations. */
    List<String> authorizations;

    /** The data provider. */
    private SortableApprenticeGroupDataProvider dataProvider;
    
    private int amountGroups = 0;
    
	/** The create new applicant button. */
    public IndicatingAjaxButton submitButton;
    
    public Link<String> backButton;
        
    private boolean extragroup = false;
    
    /** 0 = Gemischt, 1 = Ausbildungsjahr, 2 = Ausbildungsberuf*/
    private int option = 0;
    
    /** The add subject panel. */
    private WebMarkupContainer subjectPanel;
    
    
    VIPPGroup selectedGroup = new VIPPGroup();
    
    DataTable<ApprenticeGroupTableModel, String> tableWithFilterForm;
    
    /** The buttons. */
    List<VIACButton> buttons;
    
    List<VIPPGroup> groupListArea = new ArrayList<VIPPGroup>();
    
    List<VIPPGroup> groupList;
    
    Calendar c = new GregorianCalendar();
    String presentationCycleName = c.get(Calendar.YEAR) + "_" + (c.get(Calendar.MONTH + 1)/4);

    VIPPPresentationCycle cycle = new VIPPPresentationCycle(presentationCycleName);
    String cycleName = presentationCycleName;
    /**
     * Instantiates a new subject administration page.
     */
    public GroupingPage()
    {
    	List<VIPPPresentationCycle> cycles;
    	session = factory.openSession();
    	cycles = session
				.createQuery("from VIPPPresentationCycle where presentationCycle_id = (SELECT max(id) FROM VIPPPresentationCycle)", VIPPPresentationCycle.class)
				.getResultList();
		session.close();
		cycle = cycles.get(cycles.size()-1);
		
    	session = factory.openSession();
    	groupList = session
				.createQuery("from VIPPGroup where cycle_presentationCycle_id = " + cycle.getId(), VIPPGroup.class)
				.getResultList();
		session.close();
        init();
    }
    
    public GroupingPage(int amountGroups, int option, int extragroupNumber)
    {
    	DatabaseProvider.getInstance().create(cycle);
    	
    	this.amountGroups = amountGroups;
    	this.option = option;
    	
    	if(option == 0)
    	{
	    	createGroupsMixed(amountGroups, extragroupNumber);
    	}
    	if(option == 1)
    	{
    		createGroupsYear();
    	}
    	session = factory.openSession();
    	groupList = session
				.createQuery("from VIPPGroup where cycle_presentationCycle_id = " + cycle.getId(), VIPPGroup.class)
				.getResultList();
		session.close();
    	init();
    }
    
    public GroupingPage(List<VIPPGroup> groupList)
    {
    	this.groupList = groupList;
    	init();
    }
    
    public GroupingPage(List<VIPPGroup> groupListArea, int extragroupNumber) { 
    	DatabaseProvider.getInstance().create(cycle);
    	this.groupListArea = groupListArea;
		createGroupsArea(groupListArea, extragroupNumber);
    	session = factory.openSession();
    	groupList = session
				.createQuery("from VIPPGroup where cycle_presentationCycle_id = " + cycle.getId(), VIPPGroup.class)
				.getResultList();
		session.close();
		init();
	}

	/**
     * Inits the.
     */
    private void init()
    {
    	final Form<Object> changeGroupsForm = new Form<Object>("changeGroupsForm");
        subjectPanel = new WebMarkupContainer("subjectsPanel");
        subjectPanel.setOutputMarkupId(true);        
        submitButton = new IndicatingAjaxButton("submit")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	Iterator<? extends ApprenticeGroupTableModel> iterator = tableWithFilterForm.getDataProvider().iterator(0, tableWithFilterForm.getDataProvider().size());
            	while(iterator.hasNext())
                {
                	VIPPApprentice apprentice = iterator.next().getApprentice();  
                	DatabaseProvider.getInstance().merge(apprentice);
                }
            	
            	setResponsePage(new SubjectAssignmentPage(groupList));
            }
            @Override
            public String getAjaxIndicatorMarkupId() {
                         return "indicator";
            }
        };
        
        List<FilterOption> filters = new ArrayList<>();
        filters.add(new FilterOption("Vorname", "firstname"));
        filters.add(new FilterOption("Nachname", "lastname"));
        filters.add(new FilterOption("Ausbildungsjahr", "yearOfApprenticeship"));
        filters.add(new FilterOption("Ausbildungsberuf", "apprenticeshipArea"));
        filters.add(new FilterOption("Gruppe", "group"));

        dataProvider = new SortableApprenticeGroupDataProvider(filters);

        tableWithFilterForm = this.initDataTable(filters);

        FilterForm<ApprenticeGroupTableModel> apprenticeFilterForm = new FilterForm<ApprenticeGroupTableModel>("filterForm", dataProvider);

        FilterToolbar filterToolbar = new FilterToolbar(tableWithFilterForm, apprenticeFilterForm);
        tableWithFilterForm.addTopToolbar(filterToolbar);
        tableWithFilterForm.addTopToolbar(new HeadersToolbar<>(tableWithFilterForm, dataProvider));
        tableWithFilterForm.addBottomToolbar(new NavigationToolbar(tableWithFilterForm));
        
        apprenticeFilterForm.add(tableWithFilterForm);

        VIACFilterPanel<ApprenticeGroupTableModel> filterPanel =
            new VIACFilterPanel<>("filterPanel", dataProvider, tableWithFilterForm);
        apprenticeFilterForm.add(filterPanel);
        
        
        backButton = new Link<String>("back")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = -4037454663308029262L;

            @Override
            public void onClick()
            {
            	for(VIPPApprentice vippApprentice : DatabaseProvider.getInstance().getAll(VIPPApprentice.class))
            	{
            		vippApprentice.setCurrentGroup(null);
            		DatabaseProvider.getInstance().merge(vippApprentice);
            	}
            	for(VIPPGroup vippGroup : groupList)
            	{
            		vippGroup.getPersons().clear();
            		DatabaseProvider.getInstance().delete(vippGroup);
            	}
            	session = factory.openSession();
            	VIPPPresentationCycle cycle;
            	cycle = session
        				.createQuery("from VIPPPresentationCycle where presentationCycle_id = (SELECT max(id) FROM VIPPPresentationCycle)", VIPPPresentationCycle.class)
        				.getSingleResult();
        		session.close();
        		DatabaseProvider.getInstance().delete(cycle);
            	setResponsePage(new PlanningPage());
            }
        };
        
        changeGroupsForm.add(apprenticeFilterForm);
        changeGroupsForm.add(submitButton);
        changeGroupsForm.add(backButton);
        add(changeGroupsForm);
    }

    private void createGroupsMixed(int groupAmount, int extragroupNumber) {
    	List<VIPPGroup> groups = new ArrayList<VIPPGroup>();
    	List<VIPPApprentice> leftApprentices = new ArrayList<VIPPApprentice>();
    	List<VIPPApprentice> areaApprentices = new ArrayList<VIPPApprentice>();
    	List<VIPPApprentice> apprenticesExtraYear = new ArrayList<VIPPApprentice>();
    	int random;
    	int max;
    	if(extragroupNumber != 0)
    	{
    		max = groupAmount - 1;
    	}
    	else
    	{
    		max = groupAmount;
    	}

		List<VIPPApprenticeshipArea> apprenticeshipAreas = DatabaseProvider.getInstance().getAll(VIPPApprenticeshipArea.class);
		
		if(extragroupNumber != 0)
		{
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
		}
		
    	for(int i = 0; i < max; i++)
    	{
    		groups.add(new VIPPGroup());
    	}
		
		for(int i = 1; i <= apprenticeshipAreas.size(); i++)
		{
			// gets all the apprentices of one apprenticeshipArea
	    	session = factory.openSession();
			areaApprentices = session
					.createQuery("from VIPPApprentice WHERE"
							+ " apprenticeshipArea_apprenticeshipArea_id = "+i,
							VIPPApprentice.class)
					.getResultList();
			session.close();
			// removes all the apprentices that are already assigned to the extragroup
			areaApprentices.removeAll(apprenticesExtraYear);
			// the amount of persons of the current apprenticeshipArea that has to be added to each group
			int persons = areaApprentices.size()/max;
			// adds random apprentices of the current area to each group until the amount of persons is reached
			for(int j = 0; j < groups.size(); j++)
			{
				groups.get(j).setName("Gruppe " + (j+1));
				for(int k = 0; k < persons; k++)
				{
					if(areaApprentices.size() > 0)
					{
						random = (int)(Math.random()*areaApprentices.size());
						groups.get(j).getPersons().add(areaApprentices.get(random));
						areaApprentices.get(random).setCurrentGroup(groups.get(j));
						areaApprentices.remove(random);						
					}
				}
			}
			// if there are still apprentices left from this area they are added to a the leftApprentices list
			if(!areaApprentices.isEmpty())
			{
				for (VIPPApprentice vippApprentice : areaApprentices) {						
					leftApprentices.add(vippApprentice);
				}
			}
		}
		/* if there are persons in leftApprentices each group gets a random person of this group until the list is empty 
		 * this procedure is to ensure that the groups have the same size (+/- 1)*/
		if(!leftApprentices.isEmpty())
		{
			while(leftApprentices.size() > 0)
			{
				for(int j = 0; j < groups.size(); j++)
				{
					random = (int)(Math.random()*leftApprentices.size());
					groups.get(j).getPersons().add(leftApprentices.get(random));
					leftApprentices.get(random).setCurrentGroup(groups.get(j));
					leftApprentices.remove(random);
					if(leftApprentices.size() == 0)
					{
						break;
					}
				}
			}
		}
		for (VIPPGroup vippGroup : groups) {
			vippGroup.setCycle(cycle);
			DatabaseProvider.getInstance().create(vippGroup);
			for (VIPPApprentice vippApprentice : vippGroup.getPersons()) {
				DatabaseProvider.getInstance().merge(vippApprentice);				
			}
		}
		if(extragroupNumber != 0)
		{
			VIPPGroup extra = new VIPPGroup(apprenticesExtraYear);
			extra.setCycle(cycle);
			extra.setName(extragroupNumber+". Ausbildungsjahr");
			DatabaseProvider.getInstance().create(extra);
			for(VIPPApprentice vippApprentice : extra.getPersons())
			{
				vippApprentice.setCurrentGroup(extra);
				DatabaseProvider.getInstance().merge(vippApprentice);
			}
		}
	}
    
    private void createGroupsYear()
    {
    	List<VIPPGroup> groups = new ArrayList<>();
    	List<VIPPApprentice> apprenticesYear = new ArrayList<VIPPApprentice>();
    	List<VIPPApprentice> highestYear = new ArrayList<VIPPApprentice>();
    	List<Integer> years = new ArrayList<Integer>();
    	session = factory.openSession();
		highestYear = session
				.createQuery("from VIPPApprentice WHERE apprentice_yearOfApprenticeship = (SELECT max(yearOfApprenticeship) FROM VIPPApprentice)", VIPPApprentice.class)
				.getResultList();
		session.close();
		for(int i = 0; i < 3; i++)
		{
			years.add(highestYear.get(0).getYearOfApprenticeship() - i);
		}
    	for(int i = 0; i < years.size(); i++)
    	{
    		groups.add(new VIPPGroup());
    	}
    	
    	for(int i = 0; i < groups.size(); i++)
    	{
    		groups.get(i).setName("Gruppe " + (i+1));
    		session = factory.openSession();
    		apprenticesYear = session
    				.createQuery("from VIPPApprentice WHERE apprentice_yearOfApprenticeship = " + years.get(i) + "", VIPPApprentice.class)
    				.getResultList();
    		session.close();
    		for (VIPPApprentice vippApprentice : apprenticesYear) {
				groups.get(i).getPersons().add(vippApprentice);
				vippApprentice.setCurrentGroup(groups.get(i));
			}
    	}
    	
    	for (VIPPGroup vippGroup : groups) {
    		vippGroup.setCycle(cycle);
			DatabaseProvider.getInstance().create(vippGroup);
			for (VIPPApprentice vippApprentice : vippGroup.getPersons()) {
				DatabaseProvider.getInstance().merge(vippApprentice);
			}
		}
    	amountGroups = groups.size();
    }
    
    private void createGroupsArea(List<VIPPGroup> grouplistArea, int extragroupNumber)
    {
    	for(int i = 0; i < grouplistArea.size(); i++)
    	{
    		/* the apprentices have already been separated into groups in the PlanningPage since that assignment already had to be done to calculate
    		 * the group sizes and now  they only get their group assigned*/
    		VIPPGroup group = grouplistArea.get(i);
    		if(extragroupNumber != 0 && i == grouplistArea.size() - 1)
    		{
    			group.setName(extragroupNumber + ". Ausbildungsjahr");
    		}
    		else
    		{
    			group.setName("Gruppe " + (i+1));    			
    		}
    		group.setCycle(cycle);
    		DatabaseProvider.getInstance().create(group);
    		for(VIPPApprentice vippApprentice : group.getPersons())
    		{
    			vippApprentice.setCurrentGroup(group);
    			DatabaseProvider.getInstance().merge(vippApprentice);
    		}
    	}
    }
    
    private DataTable<ApprenticeGroupTableModel, String> initDataTable(List<FilterOption> filters)
    {
        List<IColumn<ApprenticeGroupTableModel, String>> columns = new ArrayList<>();

        columns.add(new PropertyColumn<ApprenticeGroupTableModel, String>(new Model<>("Vorname"), "firstname", "firstname"));
        columns.add(new PropertyColumn<ApprenticeGroupTableModel, String>(new Model<>("Nachname"), "lastname", "lastname"));
        columns.add(new PropertyColumn<ApprenticeGroupTableModel, String>(new Model<>("Ausbildungsjahr"), "yearOfApprenticeship", "yearOfApprenticeship"));
        columns.add(new PropertyColumn<ApprenticeGroupTableModel, String>(new Model<>("Ausbildungsberuf"), "apprenticeshipArea", "apprenticeshipArea"));
        columns.add(new PropertyColumn<ApprenticeGroupTableModel, String>(new Model<>("Gruppe"), "group", "group"));
        columns.add(new AbstractColumn<ApprenticeGroupTableModel, String>(new Model<>("Gruppe ändern"))
        {
			@Override
			public void populateItem(Item<ICellPopulator<ApprenticeGroupTableModel>> cellItem, String componentId,
					final IModel<ApprenticeGroupTableModel> rowModel) {
				
				final VIPPApprentice apprentice = rowModel.getObject().getApprentice();
			    final DropdownPanelGroup panel = new DropdownPanelGroup(componentId, groupList, apprentice);
				panel.groupChoice.add(new OnChangeAjaxBehavior() {

					private static final long serialVersionUID = 1L;

					@Override
					protected void onUpdate(AjaxRequestTarget target) {
						apprentice.setCurrentGroup(panel.groupChoice.getModelObject());
					}
				});
				cellItem.add(panel);
			}
        });
        

        DataTable<ApprenticeGroupTableModel, String> dataTable =
            new DataTable<ApprenticeGroupTableModel, String>("tableWithFilterForm", columns, dataProvider, 100);
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