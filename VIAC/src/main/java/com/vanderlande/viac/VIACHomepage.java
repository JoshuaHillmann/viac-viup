package com.vanderlande.viac;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.link.Link;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.model.VIACHeadItem;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.administration.AdministrationPage;
import com.vanderlande.vipp.attendees.PersonAdministrationPage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.planning.EditSubjectAssignmentPage;
import com.vanderlande.vipp.planning.GroupingPage;
import com.vanderlande.vipp.planning.PlanningPage;
import com.vanderlande.vita.administration.VITAAdministrationPage;
import com.vanderlande.vita.applicant.ApplicantManagementPage;
import com.vanderlande.vita.employmenttest.EmploymentTestOverviewPage;

/**
 * The Class VIACHomepage.
 * 
 * @author desczu
 * 
 *         The startpage of the VIAC-Application after the user has logged in. Every module is displayed here with a
 *         square containing the module-name. The squares have the color of the module.
 */
public class VIACHomepage extends VIACBasePage
{

    /** The authorizations. */
    List<String> authorizations = new ArrayList<>();

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 6457645598270402664L;

    /**
     * Instantiates a new VIAC homepage.
     */
    public VIACHomepage()
    {
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        WebMarkupContainer vita = new WebMarkupContainer("vita");
        vita.setOutputMarkupId(true);
        vita.add(new AjaxEventBehavior("onclick")
        {

            @Override
            protected void onEvent(AjaxRequestTarget target)
            {
                if (authorizations.contains("vita_applicantAdministration_tab"))
                {
                    setResponsePage(ApplicantManagementPage.class);
                }
                else if (authorizations.contains("vita_testAdministration_tab"))
                {
                    setResponsePage(EmploymentTestOverviewPage.class);
                }
                else if (authorizations.contains("vita_administration_tab"))
                {
                    setResponsePage(VITAAdministrationPage.class);
                }
                else
                {
                    setResponsePage(VIACHomepage.class);
                }
            }
        });
        add(vita);
        if (!authorizations.contains("vita_applicantAdministration_tab") &&
            !authorizations.contains("vita_testAdministration_tab") &&
            !authorizations.contains("vita_administration_tab"))
        {
            vita.setVisible(false);
        }
        
        Link button = new Link("testDataButton")
        {
            @Override
            public void onClick()
            {
                DatabaseProvider.getInstance().generateTestData();
            }
        };
        button.setOutputMarkupId(true);

        add(button);

        if (!VIACSession.getInstance().isDebug())
            button.setVisible(false);
        
        WebMarkupContainer vipp = new WebMarkupContainer("vipp");
        vipp.setOutputMarkupId(true);
        vipp.add(new AjaxEventBehavior("onclick")
        {

            @Override
            protected void onEvent(AjaxRequestTarget target)
            {
            	List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
            	List<VIPPGroup> groups = new ArrayList<VIPPGroup>();
            	if(apprentices.size() > 0)
            	{
            		VIPPApprentice apprentice = DatabaseProvider.getInstance().getAll(VIPPApprentice.class).get(0);
            		
            		if(apprentice.getCurrentGroup() == null)
            		{
            			setResponsePage(PlanningPage.class);
            		}
            		else if(apprentice.getAssignedSubject() == null)
            		{
            			setResponsePage(GroupingPage.class);    		
            		}
            		else
            		{
            			setResponsePage(EditSubjectAssignmentPage.class);
            		}
            	}
            	else
            	{
            		setResponsePage(AdministrationPage.class);
            	}
            }
        });
        add(vipp);
        if (!authorizations.contains("vipp_all"))
            {
                vipp.setVisible(false);
            }
    }

}
