package com.vanderlande.viac.template.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.vita.model.VITAApplicant;

/**
 * The Class TemplateHelper.
 * 
 * @author dedhor
 * 
 *         Helper class for the mailtemplates. Allows to set the available template marker and information.
 * 
 */
public class TemplateHelper implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 896160158798847450L;

    /** The instance. */
    private static TemplateHelper instance;

    /** The template. */
    private ArrayList<TemplateVariable> template = new ArrayList<>();

    /**
     * Gets the single instance of TemplateHelper.
     *
     * @return single instance of TemplateHelper
     */
    public static TemplateHelper getInstance()
    {
        if (instance == null)
            instance = new TemplateHelper();
        return instance;
    }

    /**
     * Instantiates a new template helper with the available template markers and descriptions.
     */
    public TemplateHelper()
    {
        template.add(new TemplateVariable("#firstname", "Vorname des Nutzers"));
        template.add(new TemplateVariable("#lastname", "Nachname des Nutzers"));
        template.add(new TemplateVariable("#mail", "E-Mail des Nutzers"));
        template.add(new TemplateVariable("#date", "Aktuelles Datum"));
        template.add(new TemplateVariable("#gender_hello", "Geschlechtspezifische Anrede"));
        template.add(
            new TemplateVariable("#register_key", "E-Mail Aktivierungsschlüssel (nur _password_reset/_user_created)"));
        template.add(new TemplateVariable("#username", "Nutzer Username (nur _user_created)"));
        template.add(new TemplateVariable("#password", "Nutzer Passwort (nur _user_created/_password_change)"));
        template.add(new TemplateVariable("#test_group", "VITA Testgruppe (nur _deligate_test)"));
        template.add(new TemplateVariable("#test_date", "VITA Testdatum (nur _test_invitation)"));
        template.add(new TemplateVariable("#test_room", "VITA Testraum (nur _test_invitation)"));
    }

    /**
     * Gets the template values.
     *
     * @return the template values
     */
    public List<TemplateVariable> getTemplateValues()
    {
        return this.template;
    }

    /**
     * Gets the required value for a key if asked for a VIACUser.
     *
     * @param key
     *            the key
     * @param user
     *            the user
     * @return the value for key
     */
    public String getValueForKey(String key, VIACUser user)
    {
        switch (key)
        {
            case ("#firstname"):
                return user.getFirstname();
            case ("#lastname"):
                return user.getLastname();
            case ("#mail"):
                return user.getName();
            case ("#gender_hello"):
                //TODO: implement GENDER for user?
                return getGenderSalutaion(true);
            case ("#date"):
                return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        }
        return "NaV";
    }

    /**
     * Gets the required value for a key if asked for a VITAApplicant.
     *
     * @param key
     *            the key
     * @param user
     *            the user
     * @return the value for key
     */
    public String getValueForKey(String key, VITAApplicant user)
    {
        switch (key)
        {
            case ("#firstname"):
                return user.getFirstname();
            case ("#lastname"):
                return user.getLastname();
            case ("#mail"):
                return user.getMail();
            case ("#gender_hello"):
                return getGenderSalutaion(user.getGender().toLowerCase().equals("männlich"));
            case ("#date"):
                return new SimpleDateFormat("dd.MM.yyyy").format(Calendar.getInstance().getTime());
        }
        return "NaV";
    }

    /**
     * Gets the gender salutaion. This is a special case in the template, as it is not a full mail template, doesn't has
     * a header and will not require another template marker check.
     *
     * @param isMen
     *            the is men
     * @return the gender salutaion
     */
    public String getGenderSalutaion(boolean isMen)
    {
        if (isMen)
            return getTemplate("mail_template_men").getMailText();
        return getTemplate("mail_template_women").getMailText();
    }

    /**
     * Loads the requested template from the database and fill it for a VIACUser.
     *
     * @param key
     *            the key
     * @param user
     *            the user
     * @return the string
     */
    public String loadTemplate(String key, VIACUser user)
    {
        return useTemplate(DatabaseProvider.getInstance().getConfigByKey(key).getValue(), user);
    }

    /**
     * Loads the requested template from the database and fill it for a VITAApplicant.
     *
     * @param key
     *            the key
     * @param user
     *            the user
     * @return the string
     */
    public String loadTemplate(String key, VITAApplicant user)
    {
        return useTemplate(DatabaseProvider.getInstance().getConfigByKey(key).getValue(), user);
    }

    /**
     * Use a template with a VIACUser and replace the markers with the required variables.
     *
     * @param input
     *            the input
     * @param user
     *            the user
     * @return the string
     */
    public String useTemplate(String input, VIACUser user)
    {
        input = input.replace("\n", "<br>");
        for (TemplateVariable tv : template)
        {
            input = input.replace(tv.getKey(), getValueForKey(tv.getKey(), user));
        }
        return input;
    }

    /**
     * Use a template with a VITAApplicant and replace the markers with the required variables.
     *
     * @param input
     *            the input
     * @param user
     *            the user
     * @return the string
     */
    public String useTemplate(String input, VITAApplicant user)
    {
        input = input.replace("\n", "<br>");
        for (TemplateVariable tv : template)
        {
            input = input.replaceAll(tv.getKey(), getValueForKey(tv.getKey(), user));
        }
        return input;
    }

    /**
     * Use a template with additional markers which will be replaced according to the given input.
     *
     * @param input
     *            the input
     * @param additional_keys
     *            the additional keys
     * @return the string
     */
    public String useTemplateAdditional(String input, List<TemplateVariable> additional_keys)
    {
        for (TemplateVariable tv : additional_keys)
        {
            input = input.replaceAll(tv.getKey(), tv.getValue());
        }
        return input;
    }

    /**
     * Use a template with a VIACUser and additional markers which will be replaced according to the given input.
     *
     * @param input
     *            the input
     * @param user
     *            the user
     * @param additional_keys
     *            the additional keys
     * @return the string
     */
    public String useTemplate(String input, VIACUser user, List<TemplateVariable> additional_keys)
    {
        input = useTemplateAdditional(input, additional_keys);
        input = useTemplate(input, user);
        return input;
    }

    /**
     * Use a template with a VITAApplicant and additional markers which will be replaced according to the given input.
     *
     * @param input
     *            the input
     * @param user
     *            the user
     * @param additional_keys
     *            the additional keys
     * @return the string
     */
    public String useTemplate(String input, VITAApplicant user, List<TemplateVariable> additional_keys)
    {
        input = useTemplateAdditional(input, additional_keys);
        input = useTemplate(input, user);
        return input;
    }

    /**
     * Load a existing template from the database based on the key.
     *
     * @param key
     *            the key
     * @return the template
     */
    public MailTemplate getTemplate(String key)
    {
        return DatabaseProvider.getInstance().getTemplateByKey(key);
    }
}