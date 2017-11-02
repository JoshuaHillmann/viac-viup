package com.vanderlande.vipp;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.vanderlande.VIACWebPage;
import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.VIACHeader;
import com.vanderlande.viac.model.VIACHeadItem;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vipp.administration.AdministrationPage;
import com.vanderlande.vipp.administration.ApprenticeshipAreaAdministrationPage;
import com.vanderlande.vipp.archive.ArchiveStartingPage;
import com.vanderlande.vipp.attendees.PersonAdministrationPage;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.planning.EditSubjectAssignmentPage;
import com.vanderlande.vipp.planning.GroupingPage;
import com.vanderlande.vipp.planning.PlanningPage;
import com.vanderlande.vipp.subject.SubjectAdministrationPage;

/**
 * The Class VIPPBasePage.
 * 
 * @author dermic
 * 
 *         Basepage of every VIPP-Page
 */
public class VIPPBasePage extends VIACWebPage
{
	public int step;
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8089986204147558930L;

    /** The header. */
    private VIACHeader header;

    /** The nav points. */
    private ArrayList<VIACHeadItem> navPoints;

    /** The authorizations. */
//    private List<String> authorizations;

    /**
     * Instantiates a new VIPP base page.
     */
    public VIPPBasePage()
    {
        // Debug Info-Feld
        FeedbackPanel fbp = new FeedbackPanel("feedback");
        if (!VIACSession.getInstance().isDebug())
        {
            fbp.setVisible(false);
        }
        add(fbp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.wicket.Component#renderHead(org.apache.wicket.markup.head.
     * IHeaderResponse)
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.render(
            CssReferenceHeaderItem.forReference(new PackageResourceReference(VIACBasePage.class, "styles/styles.css")));
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vanderlande.VIACWebPage#initialize()
     */
    @Override
    protected void initialize()
    {
        navPoints = new ArrayList<VIACHeadItem>();

        checkAuthorizations();
        header = new VIACHeader("header", navPoints);
        header.setBackgroundColor("#b02727");
        add(header);
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    public void checkAuthorizations()
    {
    	navPoints.add(new VIACHeadItem("Administration", AdministrationPage.class));
    	navPoints.add(new VIACHeadItem("Personenverwaltung", PersonAdministrationPage.class));
    	navPoints.add(new VIACHeadItem("Berufsverwaltung", ApprenticeshipAreaAdministrationPage.class));
    	List<VIPPApprentice> apprentices = DatabaseProvider.getInstance().getAll(VIPPApprentice.class);
    	if(apprentices.size() > 0)
    	{
    		VIPPApprentice apprentice = DatabaseProvider.getInstance().getAll(VIPPApprentice.class).get(0);	
    		if(apprentice.getCurrentGroup() == null)
    		{
    			navPoints.add(new VIACHeadItem("Planung", PlanningPage.class));
    		}
    		else if(apprentice.getAssignedSubject() == null)
    		{
    			navPoints.add(new VIACHeadItem("Planung", GroupingPage.class));    		
    		}
    		else
    		{
    			navPoints.add(new VIACHeadItem("Planung", EditSubjectAssignmentPage.class));
    		}
    	}
    	else
    	{
    		navPoints.add(new VIACHeadItem("Planung", PlanningPage.class));
    	}
    	navPoints.add(new VIACHeadItem("Themenverwaltung", SubjectAdministrationPage.class));
    	navPoints.add(new VIACHeadItem("Archiv", ArchiveStartingPage.class));
    }

    /**
     * You can use this method to toggle the visibility of the navigation at the top of the page. Useful if you want to
     * show a page within a dialog
     *
     * @param shouldBeVisible
     *            the new header visibility
     */
    public void setHeaderVisibility(Boolean shouldBeVisible)
    {
        header.setVisible(shouldBeVisible);
    }
}
