package com.vanderlande.viac.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The Class MailTemplate.
 */
@Entity
@Table(name = "MAIL_TEMPLATE")
public class MailTemplate implements Serializable
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -2659938417487186235L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_template_id")
    private long id;

    /** The key. */
    @Column(name = "mail_template_key")
    private String key;

    /** The mail text. */
    @Column(name = "mail_text")
    @Lob
    private String mailText;

    /** The carbon copy. */
    @Column(name = "carbon_copy")
    private String carbonCopy;

    /** The blind carbon copy. */
    @Column(name = "blind_carbon_copy")
    private String blindCarbonCopy;

    /** The documents. */
    @OneToMany(mappedBy = "template", fetch = FetchType.EAGER)
    private Set<MailDocument> documents = new HashSet<MailDocument>();

    /**
     * Instantiates a new mail template.
     */
    public MailTemplate()
    {

    }

    /**
     * Instantiates a new mail template.
     *
     * @param key the key
     * @param text the text
     */
    public MailTemplate(String key, String text)
    {
        this.key = key;
        this.mailText = text;
    }

    /**
     * Gets the id.
     *
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Sets the id.
     *
     * @param id the new id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Gets the key.
     *
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * Sets the key.
     *
     * @param key the new key
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * Gets the mail text.
     *
     * @return the mail text
     */
    public String getMailText()
    {
        return mailText;
    }

    /**
     * Sets the mail text.
     *
     * @param mailText the new mail text
     */
    public void setMailText(String mailText)
    {
        this.mailText = mailText;
    }

    /**
     * Gets the carbon copy.
     *
     * @return the carbon copy
     */
    public String getCarbonCopy()
    {
        return carbonCopy;
    }

    /**
     * Sets the carbon copy.
     *
     * @param carbonCopy the new carbon copy
     */
    public void setCarbonCopy(String carbonCopy)
    {
        this.carbonCopy = carbonCopy;
    }

    /**
     * Gets the blind carbon copy.
     *
     * @return the blind carbon copy
     */
    public String getBlindCarbonCopy()
    {
        return blindCarbonCopy;
    }

    /**
     * Sets the blind carbon copy.
     *
     * @param blindCarbonCopy the new blind carbon copy
     */
    public void setBlindCarbonCopy(String blindCarbonCopy)
    {
        this.blindCarbonCopy = blindCarbonCopy;
    }

    /**
     * Gets the documents.
     *
     * @return the documents
     */
    public Set<MailDocument> getDocuments()
    {
        return documents;
    }

    /**
     * Sets the documents.
     *
     * @param documents the new documents
     */
    public void setDocuments(Set<MailDocument> documents)
    {
        this.documents = documents;
    }
}
