package com.vanderlande;

import org.apache.wicket.Session;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.Request;
import org.apache.wicket.request.Response;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.LoginPage;
import com.vanderlande.viac.VIACHomepage;
import com.vanderlande.viac.session.VIACSession;

/**
 * Application object for your web application. If you want to run this application without deploying, run the Start
 * class.
 * 
 * @see com.vanderlande.Start#main(String[])
 */
public class WicketApplication extends WebApplication
{
    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    @Override
    public Class<? extends WebPage> getHomePage()
    {
        if (VIACSession.exists())
            return VIACHomepage.class;
        else
            return LoginPage.class;
    }

    public Session newSession(Request request, Response response)
    {
        return new VIACSession(request);
    }

    /**
     * @see org.apache.wicket.Application#init()
     */
    @Override
    public void init()
    {
        super.init();

        // add your configuration here
        DatabaseProvider.getInstance().databaseInit();
    }
}
