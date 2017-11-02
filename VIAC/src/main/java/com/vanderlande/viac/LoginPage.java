package com.vanderlande.viac;

import javax.persistence.NoResultException;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;

/**
 * The Class LoginPage.
 * 
 * @author dedhor
 * 
 *         First Page of the application. The User can log in and gets a notification whether his login-informations
 *         were correct or not. If the session is timed out the user is redirected to this page.
 */
public class LoginPage extends WebPage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2367669516742672127L;

    /** The password. */
    private String username, password;

    /** The error banned. */
    private WebMarkupContainer errorPassword;

    /** The error banned. */
    private WebMarkupContainer errorBanned;

    /** The form. */
    private Form<Object> form;

    /**
     * Instantiates a new login page.
     */
    public LoginPage()
    {
        init();
    }

    /**
     * Inits the.
     */
    private void init()
    {
        this.setOutputMarkupId(true);

        errorPassword = new WebMarkupContainer("errorPassword");
        errorPassword.setVisible(false);

        errorBanned = new WebMarkupContainer("errorBanned");
        errorBanned.setVisible(false);

        form = new Form<Object>("form");

        TextField<String> userName = new TextField<String>("loginName", new PropertyModel<String>(this, "username"));
        PasswordTextField userPassword =
            new PasswordTextField("loginPassword", new PropertyModel<String>(this, "password"));

        Button loginButton = new Button("loginButton")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit()
            {
                super.onSubmit();
                //reset errormessage
                errorPassword.setVisible(false);
                errorBanned.setVisible(false);

                //check the logininformations
                try
                {
                    if (checkLogin(username, password))
                    {
                        VIACUser nUser = DatabaseProvider.getInstance().getUserByName(username);
                        if (nUser.isBanned())
                            errorBanned.setVisible(true);
                        else
                        {
                            VIACSession.getInstance().setUserModel(nUser);
                            setResponsePage(VIACHomepage.class);
                        }
                        return;
                    }
                }
                catch (NoResultException e)
                {
                    error("NUTZER NICHT GEFUNDEN");
                }

                form.clearInput();
                errorPassword.setVisible(true);
            }

        };

        add(errorPassword);
        add(errorBanned);

        form.add(new Link("requestNewPassword")
        {
            @Override
            public void onClick()
            {
                setResponsePage(VIACPasswordResetPage.class);
            }
        });

        form.add(userName);
        userName.setRequired(true);

        form.add(userPassword);
        userPassword.setRequired(true);

        form.add(loginButton);
        this.add(form);
    }

    /**
     * Check login.
     *
     * @param username
     *            the username
     * @param attemptPassword
     *            the attempt password
     * @return the boolean
     */
    private Boolean checkLogin(String username, String attemptPassword)
    {
        VIACUser user = DatabaseProvider.getInstance().getUserByName(username);
        if (user == null)
        {
            return false;
        }

        return user.isRightPassword(attemptPassword);
    }
}
