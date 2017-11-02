package com.vanderlande.viac;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.vanderlande.VIACWebPage;
import com.vanderlande.viac.model.VIACHeadItem;
import com.vanderlande.viac.roles.RoleAdministrationPage;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.template.TemplateAdminPage;
import com.vanderlande.viac.userManagement.UserManagementPage;

/**
 * The Class VIACBasePage.
 * 
 * @author desczu
 * 
 *         The BasePage for every VIAC-Page. The header (containing the navigation) is instantiated here. Extend this
 *         page to create a new viac-page.
 */
public class VIACBasePage extends VIACWebPage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 4302397953772740775L;

    /** The header. */
    private VIACHeader header;

    /** The nav points. */
    private ArrayList<VIACHeadItem> navPoints;

    /** The authorizations. */
    private List<String> authorizations;

    /**
     * Instantiates a new VIAC base page.
     */
    public VIACBasePage()
    {
        super();

        // Debug Info-Field
        FeedbackPanel fbp = new FeedbackPanel("feedback");
        if (!VIACSession.getInstance().isDebug())
        {
            fbp.setVisible(false);
        }
        add(fbp);
    }

    /* (non-Javadoc)
     * @see org.apache.wicket.Component#renderHead(org.apache.wicket.markup.head.IHeaderResponse)
     */
    @Override
    public void renderHead(IHeaderResponse response)
    {
        response.render(
            CssReferenceHeaderItem.forReference(new PackageResourceReference(VIACBasePage.class, "styles/styles.css")));
    }

    /* (non-Javadoc)
     * @see com.vanderlande.VIACWebPage#initialize()
     */
    @Override
    protected void initialize()
    {
        navPoints = new ArrayList<VIACHeadItem>();
        authorizations = new ArrayList<>();
        authorizations = VIACSession.getInstance().getUserAuthorizations();

        checkAuthorizations();
        header = new VIACHeader("header", navPoints);

        add(header);
    }

    /**
     * Check authorizations.
     */
    private void checkAuthorizations()
    {
        if (this.authorizations.contains("viac_roleAdministration_tab"))
        {
            navPoints.add(new VIACHeadItem("Rollenverwaltung", RoleAdministrationPage.class));
        }
        if (this.authorizations.contains("viac_userAdministration_tab"))
        {
            navPoints.add(new VIACHeadItem("Nutzerverwaltung", UserManagementPage.class));
        }
        if (this.authorizations.contains("viac_templateAdministration_tab"))
        {
            navPoints.add(new VIACHeadItem("Templateverwaltung", TemplateAdminPage.class));
        }
    }
}
