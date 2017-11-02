package com.vanderlande.vipp.planning;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.RangeValidator;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.HibernateUtil;
import com.vanderlande.viac.controls.VIACActionPanel;
import com.vanderlande.viac.controls.VIACButton;
import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPPresentedOn;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.model.VIPPSubjectArea;

/**
 * The Class SubjectAssignment.
 * 
 * @author dermic
 *       
 */
public class SubjectAssignmentPage extends VIPPBasePage implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1605204959720209033L;

	/** The session. */
	private Session session;
	
	/** The session factory. */
	private SessionFactory factory = HibernateUtil.getSessionFactory();
    
    /** The authorizations. */
    List<String> authorizations;

    /** The action panel. */
    VIACActionPanel actionPanel;
    
    VIPPPresentedOn presentedOn;
    
    /** The add subject panel. */
    private WebMarkupContainer subjectsPanel;
    
    List<VIPPSubject> subjectsAvailableAll = new ArrayList<VIPPSubject>();
	
	/** The area choice. */
	private DropDownChoice<VIPPSubjectArea> areaChoice;
	
    int cycles = 1;
    
    IModel<Integer> cycleModel = new Model<Integer>();
    
    CheckGroup<Integer> year;
    
    List<VIPPGroup> groupList; 
    
    private NumberTextField<Integer> extraCriteriaNumberInput;
    
    boolean extragroup = false;
    
    int extra = 0;
    
    ModalWindow infoWindow;
    
    List<VIPPApprentice> apprentices;
    
    private NotificationDialog notificationDialog;
    
    IModel<Integer> criteriaNumber = new Model<Integer>();

    /**
     * Instantiates a new subject assignment page.
     */
    public SubjectAssignmentPage()
    {
    	session = factory.openSession();
    	groupList = session
				.createQuery("from VIPPGroup where cycle_presentationCycle_id = (SELECT max(cycle_presentationCycle_id) FROM VIPPPresentationCycle)", VIPPGroup.class)
				.getResultList();
		session.close();
        init();
    }
    
    public SubjectAssignmentPage(List<VIPPGroup> groupList) {
    	this.groupList = groupList;
		init();
	}

	/**
     * Inits the.
     */
    private void init()
    {
    	infoWindow = new ModalWindow("info");
    	infoWindow.setInitialWidth(500);
    	infoWindow.setInitialHeight(500);
    	infoWindow.setResizable(false);
    	Label info = new Label("infoLabel","");
    	infoWindow.add(info);
    	
        notificationDialog = new NotificationDialog("notificationDialog");
        notificationDialog.setCloseButtonCallback(new ModalWindow.CloseButtonCallback()
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 5613838020934203498L;

            @Override
            public boolean onCloseButtonClicked(AjaxRequestTarget target)
            {
                target.add(SubjectAssignmentPage.this);
                return true;
            }
        });
        add(notificationDialog);
    	
    	step = 2;
        Form<Object> subjectsForm = new Form<Object>("subjectsForm");
        Label presentationCyclesLabel = new Label("presentationCyclesLabel", new Model<String>("Anzahl Durchgänge:")); 
        VIACButton cyclesInfoButton = new VIACButton(VIACButton.Type.DETAILS)
        {
            private static final long serialVersionUID = 8842648163086760741L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	Label info = new Label("infoLabel","Gibt die Anzahl Präsentationsdurchläufe an, die vergangen sein soll, bis ein Thema in der Planung wieder vergeben wird.");
            	infoWindow.add(info);
            	infoWindow.setVisible(true);
            }
        };
        final NumberTextField<Integer> presentationCycleInput =
                new NumberTextField<Integer>("presentationCyclesInput", cycleModel, Integer.class);
        presentationCycleInput.add(new RangeValidator<Integer>(1, 9999));
        presentationCycleInput.add(new OnChangeAjaxBehavior() {
			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				if(presentationCycleInput.getConvertedInput() != null)
				{
					cycles = presentationCycleInput.getConvertedInput().intValue();					
				}
			}
		});
        
        final VIPPSubject subject = new VIPPSubject();
        Label extraCriteriaLabel = new Label("extraCriteriaLabel", new Model<String>("Extra Themengebiet:"));
        VIACButton criteriaInfoButton = new VIACButton(VIACButton.Type.DETAILS)
        {
            private static final long serialVersionUID = 8842648163086760741L;

            @Override
            public void onClick(AjaxRequestTarget target)
            {
            	Label info = new Label("infoLabel","Bei Auswahl eines Themengebiets wird (sofern möglich) die unten angegebene Anzahl an Themen aus diesem Themengebiet vergeben.");
            	infoWindow.add(info);
            	infoWindow.setVisible(true);
            }
        };
        // get all subjectAreas
		List<VIPPSubjectArea> subjects;
		subjects = new ArrayList<VIPPSubjectArea>();
		subjects = DatabaseProvider.getInstance().getAll(VIPPSubjectArea.class);
		// areas
		areaChoice = new DropDownChoice<VIPPSubjectArea>("subjects", new PropertyModel<VIPPSubjectArea>(subject, "subjectArea"),
				subjects);        
        extraCriteriaNumberInput =
                new NumberTextField<Integer>("extraCriteriaNumberInput", criteriaNumber, Integer.class);
        extraCriteriaNumberInput.add(new RangeValidator<Integer>(1, 9999));
        
        subjectsPanel = new WebMarkupContainer("subjectsPanel");
        subjectsPanel.setOutputMarkupId(true);
        IndicatingAjaxButton submitButton = new IndicatingAjaxButton("submit")
        {
            @Override
            public void onSubmit(AjaxRequestTarget target, Form<?> form)
            {
            	if(extraCriteriaNumberInput.getConvertedInput() == null)
            	{
            		assignSubjects(cycles, groupList, extra, null);            		            		
            	}
            	else
            	{
            		extra = extraCriteriaNumberInput.getConvertedInput();     
            		assignSubjects(cycles, groupList, extra, areaChoice.getConvertedInput());
            	}
            	setResponsePage(new EditSubjectAssignmentPage());
            }
            @Override
            public String getAjaxIndicatorMarkupId() {
                         return "indicator";
            }
        };
                          
         subjectsForm.add(presentationCyclesLabel);
//         subjectsForm.add(criteriaInfoButton);
         subjectsForm.add(presentationCycleInput);
         
         subjectsForm.add(extraCriteriaLabel);
//         subjectsForm.add(criteriaInfoButton);
         subjectsForm.add(areaChoice);
         subjectsForm.add(extraCriteriaNumberInput);
         subjectsForm.add(infoWindow);
                  
         subjectsForm.add(submitButton);
         subjectsPanel.add(subjectsForm);
         add(subjectsPanel);
     }

    public void assignSubjects(int cycles, List<VIPPGroup> groupList, int extra, VIPPSubjectArea vippSubjectArea) {
    	subjectsAvailableAll = DatabaseProvider.getInstance().getAllAvailableSubjects(cycles);
    	//the list of apprentices that are in a group (excludes those who are not active)
    	apprentices = new ArrayList<VIPPApprentice>();
    	List<VIPPPresentedOn> presentedOn = DatabaseProvider.getInstance().getAll(VIPPPresentedOn.class);
    	
    	HashMap<VIPPApprentice, VIPPSubjectArea> lastSubjectAreaMap = new HashMap<VIPPApprentice, VIPPSubjectArea>();
    	for(VIPPApprentice apprentice : apprentices)
    	{
    		VIPPSubjectArea lastArea = DatabaseProvider.getInstance().getLastSubjectArea(apprentice, presentedOn);
    		lastSubjectAreaMap.put(apprentice, lastArea);
    	}
    	
    	HashMap<VIPPGroup, List<VIPPSubject>> groupSubjectsMap = new HashMap<VIPPGroup, List<VIPPSubject>>();
    	
    	for(VIPPGroup group : groupList)
    	{
    		apprentices.addAll(group.getPersons());
    		List<VIPPSubject> subjectsForGroup = getSubjectsForGroup(group, subjectsAvailableAll);
    		groupSubjectsMap.put(group, subjectsForGroup);
    	}
    	
    	HashMap<VIPPApprentice, List<VIPPSubject>> apprenticeSubjectsMap = new HashMap<VIPPApprentice, List<VIPPSubject>>();
    	for(VIPPApprentice apprentice : apprentices)
    	{
    		List<VIPPSubject> subjectsAvailableApprentice = getSubjectsForApprentice(apprentice, groupSubjectsMap.get(apprentice.getCurrentGroup()), lastSubjectAreaMap.get(apprentice));
    		apprenticeSubjectsMap.put(apprentice, subjectsAvailableApprentice);
    	}
    	
    	VIPPSubjectArea extraSubjectArea = areaChoice.getConvertedInput();
    	int extraSubjectAreaNumber;
    	int extraSubjectAreaAssigned = 0;
    	if(extraCriteriaNumberInput.getConvertedInput() != null)
    	{
    		extraSubjectAreaNumber = extraCriteriaNumberInput.getConvertedInput();    		
    	}
    	else
    	{
    		extraSubjectAreaNumber = 0;
    	}
    	
    	List<VIPPApprentice> apprenticesForExtraSubjectArea = new ArrayList<VIPPApprentice>();
    	List<VIPPSubject> subjectsFromExtraSubjectArea = new ArrayList<VIPPSubject>();
    	
    	if(extraSubjectArea != null)
    	{
    		for(VIPPApprentice apprentice : apprentices)
    		{
    			if(lastSubjectAreaMap.get(apprentice) != extraSubjectArea)
    			{
    				apprenticesForExtraSubjectArea.add(apprentice);
    			}
    		}
    		for(VIPPSubject subject : subjectsAvailableAll)
    		{
    			if(subject.getSubjectArea().equals(extraSubjectArea))
    			{
    				subjectsFromExtraSubjectArea.add(subject);
    			}
    		}
    	}    	
    	
    	while(apprentices.isEmpty() != true)
    	{
    		// the apprentice that has the least assignable subject will be the first to get a subject assigned 
    		VIPPApprentice apprenticeLeastAvailableSubjects = shortestAmountSubjects(apprenticeSubjectsMap);
    		VIPPSubject assignedSubject = new VIPPSubject();
    		
    		if(apprenticeSubjectsMap.get(apprenticeLeastAvailableSubjects).size() == 0)
    		{
    			apprenticeLeastAvailableSubjects.setAssignedSubject(null);
    			apprentices.remove(apprenticeLeastAvailableSubjects);
    			apprenticeSubjectsMap.keySet().remove(apprenticeLeastAvailableSubjects);
    		}
    		else if(apprenticeLeastAvailableSubjects != null)
    		{
    			int random = (int)(Math.random() * apprenticeSubjectsMap.get(apprenticeLeastAvailableSubjects).size());
    			while(random == apprenticeSubjectsMap.get(apprenticeLeastAvailableSubjects).size())
    			{
    				random = (int)(Math.random() * apprenticeSubjectsMap.get(apprenticeLeastAvailableSubjects).size());    			
    			} 
    			assignedSubject = apprenticeSubjectsMap.get(apprenticeLeastAvailableSubjects).get(random);
    			apprenticeLeastAvailableSubjects.setAssignedSubject(assignedSubject);
    			if(extraSubjectArea != null)
    			{
    				if(assignedSubject.getSubjectArea().equals(extraSubjectArea))
    				{
    					extraSubjectAreaAssigned++;
    					if(subjectsFromExtraSubjectArea.contains(assignedSubject))
    					{
    						subjectsFromExtraSubjectArea.remove(assignedSubject);
    					}
    				}
    			}
    			DatabaseProvider.getInstance().merge(apprenticeLeastAvailableSubjects);
    			apprentices.remove(apprenticeLeastAvailableSubjects);
    			apprenticeSubjectsMap.keySet().remove(apprenticeLeastAvailableSubjects);
    		}
    		for(VIPPApprentice apprentice : apprentices)
    		{
    			if(assignedSubject != null)
    			{
    				if(apprenticeSubjectsMap.get(apprentice).contains(assignedSubject))
    				{
    					apprenticeSubjectsMap.get(apprentice).remove(assignedSubject);
    					List<VIPPSubject> updatedSubjectsForApprentice = apprenticeSubjectsMap.get(apprentice);
    					apprenticeSubjectsMap.put(apprentice, updatedSubjectsForApprentice);
    				}
    				if(apprenticeSubjectsMap.get(apprentice).size() == 0)
    				{
    					apprenticeSubjectsMap.keySet().remove(apprenticeLeastAvailableSubjects);
    				}
    			}
    		}
    	}
    	if(extraSubjectAreaAssigned < extraSubjectAreaNumber)
    	{
    		while(extraSubjectAreaAssigned < extraSubjectAreaNumber && apprenticesForExtraSubjectArea.size() > 0 && subjectsFromExtraSubjectArea.size() > 0)
    		{
    			int randomApprentice = (int)(Math.random() * apprenticesForExtraSubjectArea.size());
    			VIPPApprentice extraAreaApprentice = apprenticesForExtraSubjectArea.get(randomApprentice);
    			List<VIPPSubject> apprenticeExtraAreaSubjects = subjectsFromExtraSubjectArea;
    			List<VIPPSubject> irrelevantSubjects = new ArrayList<VIPPSubject>();
    			for(VIPPSubject subject : apprenticeExtraAreaSubjects)
    			{
    				if(!subject.getApprenticeshipArea().contains(extraAreaApprentice.getApprenticeshipArea()))
    				{
    					irrelevantSubjects.add(subject);
    				}
    			}
    			apprenticeExtraAreaSubjects.removeAll(irrelevantSubjects);
    			if(apprenticeExtraAreaSubjects.size() != 0)
    			{
    				int randomSubject = (int)(Math.random() * apprenticeExtraAreaSubjects.size());
    				extraAreaApprentice.setAssignedSubject(subjectsFromExtraSubjectArea.get(randomSubject));
    				DatabaseProvider.getInstance().merge(apprenticesForExtraSubjectArea.get(randomApprentice));
    				apprenticesForExtraSubjectArea.remove(randomApprentice);
    				subjectsFromExtraSubjectArea.remove(randomSubject);
    			}
    			else
    			{
    				apprenticesForExtraSubjectArea.remove(randomApprentice);
    			}
    		}
    	}
    	if(apprenticesForExtraSubjectArea.size() == 0 || subjectsFromExtraSubjectArea.size() == 0)
    	{
    		notificationDialog.showMessage(
    				"Es konnte nicht die gewünschte Anzahl an Themen des angegebenen Themengebiets zugewiesen werden.",
    				getRequestCycle().find(AjaxRequestTarget.class));
    	}
	}
    
    private VIPPApprentice shortestAmountSubjects(HashMap<VIPPApprentice, List<VIPPSubject>> apprenticeSubjectsMap)
    {
    	VIPPApprentice shortestApprentice = new VIPPApprentice();
    	int shortest = 9999;
    	for(VIPPApprentice apprentice : apprentices)
    	{
    		if(apprenticeSubjectsMap.get(apprentice).size() < shortest)
    		{
    			shortestApprentice = apprentice;
    			shortest = apprenticeSubjectsMap.get(apprentice).size();
    		}
    	}
    	return shortestApprentice;
    }
    
    private List<VIPPSubject> getSubjectsForGroup(VIPPGroup group, List<VIPPSubject> subjectsAvailableAll)
    {
    	List<VIPPApprentice> apprenticesInGroup = group.getPersons();
    	List<VIPPSubject> subjectsForGroup = subjectsAvailableAll;
    	List<VIPPSubject> subjectsPresentedInGroup = new ArrayList<VIPPSubject>();
    	
    	for(VIPPApprentice apprentice : apprenticesInGroup)
    	{
    		subjectsPresentedInGroup.addAll(apprentice.getPerformedPresentations());
    	}
    	subjectsForGroup.removeAll(subjectsPresentedInGroup);
    	return subjectsForGroup;
    }
    
    private List<VIPPSubject> getSubjectsForApprentice(VIPPApprentice apprentice, List<VIPPSubject> subjectsForGroup, VIPPSubjectArea lastArea)
    {
    	List<VIPPSubject> subjectsForApprentice = subjectsForGroup;
    	List<VIPPSubject> unfittingSubjects = new ArrayList<VIPPSubject>();
    	
    	for(VIPPSubject subject : subjectsForGroup)
    	{
    		if(lastArea != null)
    		{
    			if(subject.getSubjectArea().equals(lastArea))
    			{
    				unfittingSubjects.add(subject);
    				break;
    			}
    		}
    		if(!subject.getApprenticeshipArea().contains(apprentice.getApprenticeshipArea()))
    		{
    			unfittingSubjects.add(subject);
    			break;
    		}
    	}
    	subjectsForApprentice.removeAll(unfittingSubjects);
    	return subjectsForApprentice;
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