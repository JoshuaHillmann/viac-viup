package com.vanderlande;

import org.apache.wicket.markup.html.WebPage;

import com.vanderlande.viac.LoginPage;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;

/**
 * The Class VIACWebPage.
 * 
 * @author dekscha
 * 
 *         Extend this class if you want to create a BasePage for a module
 */
public abstract class VIACWebPage extends WebPage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2944729477468237762L;

    /**
     * Instantiates a new VIAC web page. If the user does not have a valid session, the page will not be initialized
     */
    public VIACWebPage()
    {
        if (!isInvalidSession())
        {
            initialize();
        }
    }

    /**
     * Override this method to initialize your components.
     */
    protected abstract void initialize();

    /**
     * Checks if the user is logged in and not banned and if he is, he will be redirected to the LoginPage
     *
     * @return true, if the session is invalid
     */
    protected boolean isInvalidSession()
    {
        VIACUser user = VIACSession.getInstance().getUserModel();
        if (user == null)
        {
            setResponsePage(LoginPage.class);
        }
        else if (user.isBanned())
        {
            setResponsePage(LoginPage.class);
        }
        else
        {
            return false;
        }

        return true;
    }
}
