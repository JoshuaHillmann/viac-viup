package com.vanderlande.vita;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.vanderlande.VIACWebPage;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.VIACHeader;
import com.vanderlande.viac.model.VIACHeadItem;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.vita.administration.VITAAdministrationPage;
import com.vanderlande.vita.applicant.ApplicantManagementPage;
import com.vanderlande.vita.employmenttest.EmploymentTestOverviewPage;

/**
 * The Class VITABasePage.
 * 
 * @author desczu
 * 
 *         Basepage of every VITA-Page
 */
public class VITABasePage extends VIACWebPage
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 219401363050208755L;

    /** The header. */
    private VIACHeader header;

    /** The nav points. */
    private ArrayList<VIACHeadItem> navPoints;

    /** The authorizations. */
    private List<String> authorizations;

    /**
     * Instantiates a new VITA base page.
     */
    public VITABasePage()
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
        header.setBackgroundColor("#598ecc");
        add(header);
    }

    /**
     * Disables/hides all components that should not be visible if user does not have the required authorizations.
     */
    public void checkAuthorizations()
    {
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        // authorized to see administration?
        if (this.authorizations.contains("vita_administration_tab"))
        {
            navPoints.add(new VIACHeadItem("Administration", VITAAdministrationPage.class));
        }
        // authorized to see applicant administration?
        if (this.authorizations.contains("vita_applicantAdministration_tab"))
        {
            navPoints.add(new VIACHeadItem("Bewerberverwaltung", ApplicantManagementPage.class));
        }
        // authorized to see test administration?
        if (this.authorizations.contains("vita_testAdministration_tab"))
        {
            navPoints.add(new VIACHeadItem("Testplanung", EmploymentTestOverviewPage.class));
        }
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
