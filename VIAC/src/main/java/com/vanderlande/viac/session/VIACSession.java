package com.vanderlande.viac.session;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Session;
import org.apache.wicket.protocol.http.WebSession;
import org.apache.wicket.request.Request;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.model.VIACAuthorization;
import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;

/**
 * The Class VIACSession.
 * 
 *          Stores all informations that you need in the whole application (e.g. who is logged in?)
 */
public class VIACSession extends WebSession
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 7581989785455359674L;

    /** The user model. */
    private VIACUser userModel;

    /** The user authorizations. */
    private List<String> userAuthorizations;

    /**
     * Gets the user authorizations.
     *
     * @return the user authorizations
     */
    public List<String> getUserAuthorizations()
    {
        return userAuthorizations;
    }

    /**
     * Sets the user authorizations.
     *
     * @param userAuthorizations
     *            the new user authorizations
     */
    public void setUserAuthorizations(List<String> userAuthorizations)
    {
        this.userAuthorizations = userAuthorizations;
    }

    /** The user roles. */
    private List<String> userRoles;

    /** The debug mode. */
    private boolean debugMode;

    /**
     * Instantiates a new VIAC session.
     *
     * @param request
     *            the request
     */
    public VIACSession(Request request)
    {
        super(request);
    }

    /**
     * Gets the single instance of VIACSession.
     *
     * @return single instance of VIACSession
     */
    public static VIACSession getInstance()
    {
        return (VIACSession)Session.get();
    }

    /**
     * Gets the user model.
     *
     * @return the user model
     */
    public VIACUser getUserModel()
    {
        return userModel;
    }

    /**
     * Sets the user model.
     *
     * @param userModel
     *            the new user model
     */
    public void setUserModel(VIACUser userModel)
    {
        this.debugMode = DatabaseProvider.getInstance().getConfigByKey("debug").getValue().equals("true");

        this.userModel = userModel;
        this.userAuthorizations = new ArrayList<String>();
        this.userRoles = new ArrayList<String>();
        for (VIACRole r : userModel.getRoles())
        {
            this.userRoles.add(r.getName());
            for (VIACAuthorization a : r.getAuthorizations())
            {
                this.userAuthorizations.add(a.getAuthCheckName());
            }
        }
    }

    /**
     * Checks if is allowed.
     *
     * @param auth
     *            the auth
     * @return true, if is allowed
     */
    public boolean isAllowed(String auth)
    {
        // User has no authorizazion
        if (this.userAuthorizations == null)
        {
            return false;
        }

        // User has the correct authorization
        if (this.userAuthorizations.contains(auth))
        {
            return true;
        }

        // User has the admin authorization
        if (this.userAuthorizations.contains("viac_admin"))
        {
            return true;
        }

        // User does not have the correct authorization
        return false;
    }

    /**
     * Checks if is debug.
     *
     * @return true, if is debug
     */
    public boolean isDebug()
    {
        return this.debugMode;
    }

    /**
     * Gets the user roles.
     *
     * @return the user roles
     */
    public List<String> getUserRoles()
    {
        return userRoles;
    }

    /**
     * Sets the user roles.
     *
     * @param userRoles
     *            the new user roles
     */
    public void setUserRoles(List<String> userRoles)
    {
        this.userRoles = userRoles;
    }
}
