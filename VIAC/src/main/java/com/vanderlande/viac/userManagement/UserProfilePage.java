package com.vanderlande.viac.userManagement;

import java.util.Date;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.RequiredTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;

/**
 * The Class UserProfilePage.
 * 
 * @author dedhor
 * 
 *         The user profile page. The user can change his name (must be an email) and his password.
 */
public class UserProfilePage extends VIACBasePage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8093804776788134968L;

    /** The user model. */
    private VIACUser userModel;

    /** The last page. */
    private WebPage lastPage;

    /** The repeat password. */
    private String password, repeatPassword;

    /** The success change. */
    private WebMarkupContainer errorPassword;

    /** The error change. */
    private WebMarkupContainer errorChange;

    /** The error password compare. */
    private WebMarkupContainer errorPasswordCompare;

    /** The success change. */
    private WebMarkupContainer successChange;

    /**
     * Instantiates a new user profile page.
     *
     * @param user
     *            the user
     */
    public UserProfilePage(VIACUser user)
    {
        userModel = user;
        init();
    }

    /**
     * Instantiates a new user profile page.
     *
     * @param user
     *            the user
     * @param lastPage
     *            the last page
     */
    public UserProfilePage(VIACUser user, WebPage lastPage)
    {
        this(user);
        this.lastPage = lastPage;
    }

    /**
     * Inits the.
     */
    private void init()
    {
        this.setOutputMarkupId(true);

        Form<?> form = new Form<>("form");

        errorPassword = new WebMarkupContainer("errorPassword");
        errorPassword.setVisible(false);

        errorPasswordCompare = new WebMarkupContainer("errorPasswordCompare");
        errorPasswordCompare.setVisible(false);

        errorChange = new WebMarkupContainer("errorChange");
        errorChange.setVisible(false);

        successChange = new WebMarkupContainer("successChange");
        successChange.setVisible(false);

        Label usernameLabel = new Label("usernameLabel", new Model<String>("Neuer Benutzername:"));
        TextField<String> usernameField =
            new RequiredTextField<>("username", new PropertyModel<String>(userModel, "name"));
        usernameField.add(EmailAddressValidator.getInstance());

        Label firstnameLabel = new Label("firstnameLabel", new Model<String>(userModel.getFirstname()));

        Label lastnameLabel = new Label("lastnameLabel", new Model<String>(userModel.getLastname()));

        Label passwordLabel = new Label("passwordLabel", new Model<String>("Neues Passwort:"));
        PasswordTextField passwordField =
            new PasswordTextField("password", new PropertyModel<String>(this, "password"));
        passwordField.setRequired(false);

        Label repeatPasswordLabel = new Label("repeatPasswordLabel", new Model<String>("Neues Passwort wiederholen:"));
        PasswordTextField repeatPasswordField =
            new PasswordTextField("repeatPassword", new PropertyModel<String>(this, "repeatPassword"));
        repeatPasswordField.setRequired(false);

        form.add(errorPassword);
        form.add(errorChange);
        form.add(errorPasswordCompare);
        form.add(successChange);
        form.add(usernameLabel);
        form.add(usernameField);
        form.add(firstnameLabel);
        form.add(lastnameLabel);
        form.add(passwordLabel);
        form.add(passwordField);
        form.add(repeatPasswordLabel);
        form.add(repeatPasswordField);

        Button submitButton = new Button("submit")
        {
            private static final long serialVersionUID = -6945656790341817258L;

            @Override
            public void onSubmit()
            {
                errorPassword.setVisible(false);
                errorChange.setVisible(false);
                errorPasswordCompare.setVisible(false);
                successChange.setVisible(false);
                changeUser();
            }
        };

        form.add(submitButton);

        Link<String> backButton = new Link<String>("back")
        {
            /**
             * 
             */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(lastPage);
            }
        };
        form.add(backButton);

        add(form);
    }

    /**
     * Change user.
     */
    private void changeUser()
    {
        // set changed values
        userModel.setChangedBy(VIACSession.getInstance().getUserModel());
        userModel.setChangedOn(new Date());

        // set password
        if (password == null)
        {
            password = "";
        }
        if (password.length() >= 8)
        {
            if (password.equals(repeatPassword))
            {
                userModel.setPassword(password);
            }
            else
            {
                errorPasswordCompare.setVisible(true);
                return;
            }
        }
        else if (password.length() < 8 && password.length() > 0)
        {
            errorPassword.setVisible(true);
            return;
        }

        // save
        try
        {
            DatabaseProvider.getInstance().mergeUser(userModel);
            //setResponsePage(VIACHomepage.class);
        }
        catch (Exception ex)
        {
            errorChange.setVisible(true);
            return;
        }
        successChange.setVisible(true);
    }
}
