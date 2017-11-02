package com.vanderlande.viac;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.NoResultException;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.EmailTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.PasswordTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.util.MailHandlerSMTP;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.model.VIACUserPasswordReset;
import com.vanderlande.viac.template.model.TemplateHelper;
import com.vanderlande.viac.template.model.TemplateVariable;
import com.vanderlande.viac.userManagement.password.PasswordHandler;

/**
 * The Class VIACPasswordResetPage.
 * 
 * @author dedhor
 * 
 *         On this page the user can reset his password if has forgotten his old one. The user will get an email with a
 *         link. If he clicks on it he can enter a new password.
 */
public class VIACPasswordResetPage extends WebPage
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 9130506006308317277L;

    /** The form set. */
    private Form<Object> formRequest;

    /** The form set. */
    private Form<Object> formSet;

    /** The error password repeat. */
    private WebMarkupContainer errorEmail;

    /** The error password. */
    private WebMarkupContainer errorPassword;

    /** The error token. */
    private WebMarkupContainer errorToken;

    /** The error unknown. */
    private WebMarkupContainer errorUnknown;

    /** The error password repeat. */
    private WebMarkupContainer errorPasswordRepeat;

    /** The password repeat. */
    private String email;

    /** The password. */
    private String password;

    /** The password repeat. */
    private String passwordRepeat;

    /**
     * Instantiates a new VIAC password reset.
     *
     * @param params
     *            the params
     */
    public VIACPasswordResetPage(PageParameters params)
    {
        init(params.get("reqKey").toString(""));
    }

    /**
     * Inits the.
     *
     * @param requestKey
     *            the request key
     */
    private void init(final String requestKey)
    {
        this.setOutputMarkupId(true);

        ///REQUEST
        // Fehlermeldung
        errorEmail = new WebMarkupContainer("errorEmail");
        errorEmail.setVisible(false);

        // Eingabe Formular
        formRequest = new Form<Object>("formRequest");

        // Email Feld
        TextField<String> emailField = new EmailTextField("email", new PropertyModel<String>(this, "email"));

        // Anfrage senden Button
        Button resetButton = new Button("resetButton")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit()
            {
                super.onSubmit();
                resetPassword();
            }

        };

        this.add(errorEmail);
        formRequest.add(emailField);
        formRequest.add(resetButton);
        this.add(formRequest);

        ///SET
        // Fehlermeldung
        errorPassword = new WebMarkupContainer("errorPassword");
        errorPassword.setVisible(false);

        errorToken = new WebMarkupContainer("errorToken");
        errorToken.setVisible(false);

        errorUnknown = new WebMarkupContainer("errorUnknown");
        errorUnknown.setVisible(false);

        errorPasswordRepeat = new WebMarkupContainer("errorPasswordRepeat");
        errorPasswordRepeat.setVisible(false);

        // Eingabe Formular
        formSet = new Form<Object>("formSet");

        // Passwörter Felder
        PasswordTextField passwordField =
            new PasswordTextField("password", new PropertyModel<String>(this, "password"));
        PasswordTextField passwordRepeatField =
            new PasswordTextField("passwordRepeat", new PropertyModel<String>(this, "passwordRepeat"));

        // Anfrage senden Button
        Button sendButton = new Button("sendButton")
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void onSubmit()
            {
                super.onSubmit();
                errorEmail.setVisible(false);
                errorPassword.setVisible(false);
                errorToken.setVisible(false);
                errorUnknown.setVisible(false);
                errorPasswordRepeat.setVisible(false);

                if (password == null || passwordRepeat == null)
                {
                    errorPassword.setVisible(true);
                    return;
                }
                if (password.length() < 8 || passwordRepeat.length() < 8)
                {
                    errorPassword.setVisible(true);
                    return;
                }
                if (!password.equals(passwordRepeat))
                {
                    errorPasswordRepeat.setVisible(true);
                    return;
                }

                VIACUserPasswordReset reset;
                try
                {
                    reset = DatabaseProvider.getInstance().getPasswordResetByToken(requestKey);
                }
                catch (Exception ex)
                {
                    errorToken.setVisible(true);
                    return;
                }

                try
                {
                    reset.getUser().setPassword(password);
                    DatabaseProvider.getInstance().mergeUser(reset.getUser());
                    DatabaseProvider.getInstance().deletePasswordReset(reset);
                }
                catch (Exception ex)
                {
                    errorUnknown.setVisible(true);
                    return;
                }

                setResponsePage(VIACHomepage.class);
            }

        };

        this.add(errorPassword);
        this.add(errorToken);
        this.add(errorUnknown);
        this.add(errorPasswordRepeat);

        formSet.add(passwordField);
        formSet.add(passwordRepeatField);
        formSet.add(sendButton);
        this.add(formSet);

        if (requestKey.length() <= 0)
        {
            formSet.setVisible(false);
            formRequest.setVisible(true);
        }
        else
        {
            formRequest.setVisible(false);
            formSet.setVisible(true);
        }
    }

    /**
     * Reset password.
     */
    private void resetPassword()
    {
        errorEmail.setVisible(false);
        errorPassword.setVisible(false);
        errorToken.setVisible(false);
        errorUnknown.setVisible(false);
        errorPasswordRepeat.setVisible(false);

        try
        {
            VIACUser user = DatabaseProvider.getInstance().getUserByName(email);

            Set<VIACUserPasswordReset> resets = new HashSet<VIACUserPasswordReset>();
            user.setPasswordResets(resets);

            VIACUserPasswordReset reset = new VIACUserPasswordReset();
            reset.setCreatedOn(new Date());
            reset.setUser(user);
            reset.setToken(PasswordHandler.getInstance().getRandomPassword(30));

            resets.add(reset);

            DatabaseProvider.getInstance().create(reset);

            //Generiere Reset URL
            PageParameters ppK = new PageParameters();
            ppK.add("reqKey", reset.getToken());
            String url = RequestCycle
                .get()
                .getUrlRenderer()
                .renderFullUrl(Url.parse(urlFor(VIACPasswordResetPage.class, ppK).toString()));

            MailTemplate textTemplate = TemplateHelper.getInstance().getTemplate("mail_template_password_forgot");
            String mail_text = textTemplate.getMailText();
            ArrayList<TemplateVariable> al = new ArrayList<>();
            al.add(new TemplateVariable("#register_key", "", url));
            mail_text = TemplateHelper.getInstance().useTemplate(mail_text, user, al);
            MailHandlerSMTP.getInstance().sendMail(
                user.getName(),
                TemplateHelper.getInstance().getTemplate("mail_template_password_forgot_header"),
                textTemplate,
                mail_text);

            setResponsePage(VIACHomepage.class);
        }
        catch (NoResultException e)
        {
            error("NUTZER NICHT GEFUNDEN");

            errorEmail.setVisible(true);
        }
    }

}