package com.vanderlande.vipp.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vanderlande.annotations.FilterField;

/**
 * The Class VIPPPresentation.
 */
@Entity
@Table(name = "VIPP_PRESENTATION")
public class VIPPPresentation implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1342462054663515424L;

    /**
     * Instantiates a new VIPP presentation.
     */
    public VIPPPresentation()
    {
    }

    /**
     * Instantiates a new VIPP presentation.
     *
     * @param presentation_name
     *            the presentation name
     */
    public VIPPPresentation(String fileName)
    {
        this.fileName = fileName;
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "presentation_id")
    private long id;
    
    /** The file name. */
    @Column(name = "file_name")
    private String fileName;

    /** The file type. */
    @Column(name = "file_type")
    private String fileType;

    /** The data. */
    @Column(name = "document_data")
    @Lob
    private byte[] data;
    
    /** The apprentice. */
    @ManyToOne
    @FilterField(name = "presentation_apprentice")
    private VIPPApprentice apprentice;
    
    /** The presentationCycle. */
    @ManyToOne
    @FilterField(name = "presentation_presentationCycle")
    private VIPPPresentationCycle presentationCycle;
    
    /** The subject. */
    @ManyToOne
    @FilterField(name = "presentation_subject")
    private VIPPSubject subject;

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
     * @param id
     *            the new id
     */
    public void setId(long id)
    {
        this.id = id;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }
    
    public String getFileType()
    {
    	return fileType;
    }
    
    public void setFileType(String fileType)
    {
    	this.fileType = fileType;
    }
    
    public byte[] getData()
    {
    	return data;
    }
    
    public void setData(byte[] data)
    {
    	this.data = data;
    }
    
    /**
     * Gets the apprentice.
     *
     * @return the apprentice
     */
    public VIPPApprentice getApprentice()
    {
        return apprentice;
    }

    /**
     * Sets the apprentice.
     *
     * @param apprentice
     *            the new apprentice
     */
    public void setApprentice(VIPPApprentice apprentice)
    {
        this.apprentice = apprentice;
    }
    
    /**
     * Gets the presentationCycle.
     *
     * @return the presentationCycle
     */
    public VIPPPresentationCycle getPresentationCycle()
    {
        return presentationCycle;
    }

    /**
     * Sets the presentationCycle.
     *
     * @param presentationCycle
     *            the new presentationCycle
     */
    public void setPresentationCycle(VIPPPresentationCycle presentationCycle)
    {
        this.presentationCycle = presentationCycle;
    } 

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof VIPPPresentation)
        {
        	VIPPPresentation temp = (VIPPPresentation)obj;
            return fileName.equals(temp.getFileName());
        }
        else
        {
            return super.equals(obj);
        }
    }
}
