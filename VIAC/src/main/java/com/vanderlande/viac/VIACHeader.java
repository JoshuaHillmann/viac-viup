package com.vanderlande.viac;

import java.util.List;

import org.apache.wicket.StyleAttributeModifier;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.resource.PackageResourceReference;

import com.vanderlande.viac.model.VIACHeadItem;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.userManagement.UserProfilePage;

/**
 * The Class VIACHeader.
 * 
 * @author desczu
 * 
 *         The header of every page containing the navigation. You can change the color of the header by passing a color
 *         as a hex-string to setBackgroundColor() - every module has to have it's own colorscheme. For the navigation
 *         you have to pass a list of {@link VIACHeadItem}. These HeadItems map a display-name with a page.
 */
public class VIACHeader extends Panel
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 844003726206659513L;

    /** The navigation container. */
    RepeatingView navigationContainer;

    /** The background panel. */
    WebMarkupContainer backgroundPanel;

    /**
     * Instantiates a new VIAC header.
     *
     * @param id
     *            the id
     */
    public VIACHeader(String id)
    {
        super(id);
        init();
    }

    /**
     * Instantiates a new VIAC header.
     *
     * @param id
     *            the id
     * @param navPoints
     *            the nav points
     */
    public VIACHeader(String id, List<VIACHeadItem> navPoints)
    {
        super(id);
        init();
        initNavigation(navPoints);
    }

    /**
     * Inits the.
     */
    private void init()
    {
        backgroundPanel = new WebMarkupContainer("backgroundPanel");
        add(backgroundPanel);
        Link logoLink = new Link("logoLink")
        {

            private static final long serialVersionUID = -1251237456438250660L;

            @Override
            public void onClick()
            {
                setResponsePage(VIACHomepage.class);
            }
        };

        PackageResourceReference logoRef = new PackageResourceReference(getClass(), "ressources/logo.png");
        logoLink.add(new Image("logo", logoRef));

        backgroundPanel.add(logoLink);

        VIACUser userModel = VIACSession.getInstance().getUserModel();
        Link<String> usernameLink = new Link<String>("usernameLink")
        {
            private static final long serialVersionUID = 4934706498083314932L;

            @Override
            public void onClick()
            {
                setResponsePage(new UserProfilePage(VIACSession.getInstance().getUserModel(), getWebPage()));
            }

        };
        Label username = new Label("username", new Model<String>(userModel.getName()));
        usernameLink.add(username);
        backgroundPanel.add(usernameLink);

        Link<String> logoutLink = new Link<String>("logoutLink")
        {

            @Override
            public void onClick()
            {
                VIACSession.getInstance().invalidate();
                setResponsePage(LoginPage.class);
            }
        };
        backgroundPanel.add(logoutLink);

        navigationContainer = new RepeatingView("navContainer");
        backgroundPanel.add(navigationContainer);
    }

    /**
     * Inits the navigation.
     *
     * @param navPoints
     *            the nav points
     */
    private void initNavigation(List<VIACHeadItem> navPoints)
    {
        for (final VIACHeadItem item : navPoints)
        {
            Fragment fragment = new Fragment(navigationContainer.newChildId(), "fragment", this);
            Link link = new Link("navLink")
            {
                /**
                 * 
                 */
                private static final long serialVersionUID = -4287730434294946680L;

                @Override
                public void onClick()
                {
                    setResponsePage(item.getPage());
                }

            };
            Label linkName = new Label("navItemLabel", new Model<String>(item.getName()));
            link.add(linkName);
            fragment.add(link);

            navigationContainer.add(fragment);
        }
    }

    /**
     * Sets the background color.
     *
     * @param color
     *            the new background color
     */
    public void setBackgroundColor(String color)
    {
        backgroundPanel.add(
            StyleAttributeModifier.replace(
                "style",
                "background-image: none; background-color:" + color + "; padding-right: 10px; margin-bottom: 0px;"));
    }
}
