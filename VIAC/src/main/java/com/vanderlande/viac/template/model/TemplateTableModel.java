package com.vanderlande.viac.template.model;

import java.io.Serializable;

import com.vanderlande.viac.model.MailTemplate;

/**
 * Table model for display of a template.
 */
public class TemplateTableModel implements Serializable
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /** The text. */
    private String id;
    
    /** The text. */
    private String text;

    /** The template. */
    private MailTemplate template;

    /**
     * Constructer requires the template.
     *
     * @param template the template
     */
    public TemplateTableModel(MailTemplate template)
    {
        this.id = template.getKey();
        this.text = template.getMailText();
        this.template = template;
    }

    /**
     * Get template id.
     *
     * @return id
     */
    public String getId()
    {
        return id;
    }

    /**
     * Set template id.
     *
     * @param id the new id
     */
    public void setId(String id)
    {
        this.id = id;
    }

    /**
     * Get template text.
     *
     * @return text
     */
    public String getText()
    {
        return text;
    }

    /**
     * Set template text.
     *
     * @param text the new text
     */
    public void setText(String text)
    {
        this.text = text;
    }

    /**
     * Get template.
     *
     * @return mail template
     */
    public MailTemplate getTemplate()
    {
        return template;
    }

    /**
     * Set template.
     *
     * @param template the new template
     */
    public void setTemplate(MailTemplate template)
    {
        this.template = template;
    }

}
