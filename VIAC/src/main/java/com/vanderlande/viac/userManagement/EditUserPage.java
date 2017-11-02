package com.vanderlande.viac.userManagement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Check;
import org.apache.wicket.markup.html.form.CheckGroup;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.validation.validator.EmailAddressValidator;

import com.vanderlande.exceptions.UserAlreadyExistsException;
import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MailHandlerSMTP;
import com.vanderlande.viac.VIACBasePage;
import com.vanderlande.viac.VIACHomepage;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.session.VIACSession;
import com.vanderlande.viac.template.model.TemplateHelper;
import com.vanderlande.viac.template.model.TemplateVariable;
import com.vanderlande.viac.userManagement.password.PasswordHandler;

/**
 * The Class EditUserPage.
 * 
 * @author dekscha
 * 
 *         On this page you can edit a user (or create a new one).
 */
public class EditUserPage extends VIACBasePage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -4987309174340681683L;

    /** The user model. */
    private VIACUser userModel;

    private String password;

    /** The is new user. */
    private boolean isNewUser;

    /**
     * Instantiates a new EditUserpage. This constructor is used when you need to create a new user
     */
    public EditUserPage()
    {
        userModel = new VIACUser();
        isNewUser = true;
        init();
    }

    /**
     * Instantiates a new edits the user page. This constructor is used when you need to edit an existing user
     *
     * @param user
     *            the user
     */
    public EditUserPage(VIACUser user)
    {
        userModel = user;
        isNewUser = false;
        init();
    }

    /**
     * Initializes the components.
     */
    private void init()
    {
        Form<?> form = new Form<>("form");

        Label usernameLabel = new Label("usernameLabel", new Model<String>("Benutzername(E-Mail):"));
        TextField<String> usernameField = new TextField<>("username", new PropertyModel<String>(userModel, "name"));
        usernameField.add(EmailAddressValidator.getInstance());

        Label firstnameLabel = new Label("firstnameLabel", new Model<String>("Vorname:"));
        TextField<String> firstnameField =
            new TextField<>("firstname", new PropertyModel<String>(userModel, "firstname"));

        Label lastnameLabel = new Label("lastnameLabel", new Model<String>("Nachname:"));
        TextField<String> lastnameField = new TextField<>("lastname", new PropertyModel<String>(userModel, "lastname"));

        Label passwordLabel = new Label("passwordLabel", new Model<String>("Passwort:"));
        PasswordTextField passwordField =
            new PasswordTextField("password", new PropertyModel<String>(this, "password"));
        passwordField.setRequired(false);

        form.add(usernameLabel);
        form.add(usernameField.setRequired(true));

        form.add(firstnameLabel);
        form.add(firstnameField.setRequired(true));

        form.add(lastnameLabel);
        form.add(lastnameField.setRequired(true));

        form.add(passwordLabel);
        form.add(passwordField);

        final List<VIACRole> selectedRolesList = new ArrayList<VIACRole>();
        if (userModel.getRoles() != null)
            selectedRolesList.addAll(userModel.getRoles());

        final List<VIACRole> rolesVita = new ArrayList<>();
        final List<VIACRole> rolesViac = new ArrayList<>();
        final List<VIACRole> rolesOther = new ArrayList<>();
        for (VIACRole r : selectedRolesList)
        {
            if (r.getName().toLowerCase().contains("vita"))
            {
                rolesVita.add(r);
            }
            else if (r.getName().toLowerCase().contains("viac"))
            {
                rolesViac.add(r);
            }
            else
            {
                rolesOther.add(r);
            }
        }

        final List<VIACRole> access = DatabaseProvider.getInstance().getAll(VIACRole.class);
        final List<VIACRole> accessRightsVita = new ArrayList<>();
        final List<VIACRole> accessRightsViac = new ArrayList<>();
        final List<VIACRole> accessRightsOther = new ArrayList<>();
        for (VIACRole a : access)
        {
            if (a.getName().toLowerCase().contains("vita"))
            {
                accessRightsVita.add(a);
            }
            else if (a.getName().toLowerCase().contains("viac"))
            {
                accessRightsViac.add(a);
            }
            else
            {
                accessRightsOther.add(a);
            }
        }

        CheckGroup<VIACRole> group = new CheckGroup<VIACRole>("group", rolesViac);
        form.add(group);

        ListView<VIACRole> roles = new ListView<VIACRole>("roles", accessRightsViac)
        {

            private static final long serialVersionUID = 8302133829188691631L;

            protected void populateItem(ListItem<VIACRole> item)
            {
                item.add(new Check<VIACRole>("checkbox", item.getModel()));
                item.add(new Label("roleName", item.getModel().getObject().getName()));
            }
        };
        roles.setReuseItems(true);
        group.add(roles);

        if (userModel.equals(VIACSession.getInstance().getUserModel()))
        {
            group.setEnabled(false);
        }

        CheckGroup<VIACRole> group2 = new CheckGroup<VIACRole>("group2", rolesVita);
        form.add(group2);

        ListView<VIACRole> roles2 = new ListView<VIACRole>("roles2", accessRightsVita)
        {

            private static final long serialVersionUID = 8302133829188691631L;

            protected void populateItem(ListItem<VIACRole> item)
            {
                item.add(new Check<VIACRole>("checkbox2", item.getModel()));
                item.add(new Label("roleName2", item.getModel().getObject().getName()));
            }
        };
        roles2.setReuseItems(true);
        group2.add(roles2);

        CheckGroup<VIACRole> group3 = new CheckGroup<VIACRole>("group3", rolesOther);
        form.add(group3);

        ListView<VIACRole> roles3 = new ListView<VIACRole>("roles3", accessRightsOther)
        {

            private static final long serialVersionUID = 8302133829188691631L;

            protected void populateItem(ListItem<VIACRole> item)
            {
                item.add(new Check<VIACRole>("checkbox3", item.getModel()));
                item.add(new Label("roleName3", item.getModel().getObject().getName()));
            }
        };
        roles3.setReuseItems(true);
        group3.add(roles3);

        Link<String> backButton = new Link<String>("back")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(UserManagementPage.class);
            }
        };
        form.add(backButton);

        Button submitButton = new Button("submit")
        {
            private static final long serialVersionUID = -6945656790341817258L;

            @Override
            public void onSubmit()
            {
                Set<VIACRole> selectedRolesSet = new HashSet<>();
                selectedRolesSet.addAll(rolesOther);
                selectedRolesSet.addAll(rolesVita);
                selectedRolesSet.addAll(rolesViac);
                userModel.setRoles(selectedRolesSet);
                createUser(selectedRolesSet);
            }
        };
        form.add(submitButton);

        add(form);
    }

    private void createUser(Set<VIACRole> selectedRolesList)
    {
        // set changed values
        userModel.setChangedBy(VIACSession.getInstance().getUserModel());
        userModel.setChangedOn(new Date());

        // set roles
        userModel.setRoles(selectedRolesList);

        // set password
        boolean passwordSet = false;
        if (password == null)
        {
            password = "";
        }
        if (password.length() > 0 || isNewUser)
        {
            if (password.length() < 8)
            {
                //if passwort nicht gesetzt oder ung�ltig, dann passwort reset link erstellen? und niergendwo das pw ausgeben??
                password = PasswordHandler.getInstance().getRandomPassword(16);
            }
            userModel.setPassword(password);
            passwordSet = true;
        }

        try
        {
            if (isNewUser)
            {
                userModel.setCreatedBy(VIACSession.getInstance().getUserModel());
                DatabaseProvider.getInstance().createUser(userModel);
            }
            else
            {
                DatabaseProvider.getInstance().mergeUser(userModel);
            }
            setResponsePage(UserManagementPage.class);
        }
        catch (UserAlreadyExistsException e)
        {
            error("Nutzer ist bereits vorhanden!");
        }

        if (passwordSet && !isNewUser)
        {
            MailTemplate textTemplate = TemplateHelper.getInstance().getTemplate("mail_template_user_password_change");
            String mail_text = textTemplate.getMailText();
            ArrayList<TemplateVariable> al = new ArrayList<>();
            al.add(new TemplateVariable("#password", "", password));
            mail_text = TemplateHelper.getInstance().useTemplate(mail_text, userModel, al);
            MailHandlerSMTP.getInstance().sendMail(
                userModel.getName(),
                TemplateHelper.getInstance().getTemplate("mail_template_user_password_change_header"),
                textTemplate,
                mail_text);
        }

        if (isNewUser)
        {
            String HomeURL = RequestCycle
                .get()
                .getUrlRenderer()
                .renderFullUrl(Url.parse(urlFor(VIACHomepage.class, null).toString()));

            MailTemplate textTemplate = TemplateHelper.getInstance().getTemplate("mail_template_user_created");
            String mail_text = textTemplate.getMailText();
            ArrayList<TemplateVariable> al = new ArrayList<>();
            al.add(new TemplateVariable("#password", "", password));
            al.add(new TemplateVariable("#username", "", userModel.getName()));
            al.add(new TemplateVariable("#register_key", "", HomeURL));
            mail_text = TemplateHelper.getInstance().useTemplate(mail_text, userModel, al);
            MailHandlerSMTP.getInstance().sendMail(
                userModel.getName(),
                TemplateHelper.getInstance().getTemplate("mail_template_user_created_header"),
                textTemplate,
                mail_text);
        }
    }
}
