package com.vanderlande.vipp.model;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The Class VIPPDocument.
 */
@Entity
@Table(name = "VIPP_DOCUMENT")
public class VIPPDocument implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5034667494854634385L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private long id;

    /** The file name. */
    @Column(name = "file_name")
    private String fileName;

    /** The file type. */
    @Column(name = "file_type")
    private String fileType;

    /** The apprentice. */
    @ManyToOne
    private VIPPApprentice apprentice;
    
    /** The file. */
    private File file;

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
     * Gets the file name.
     *
     * @return the file name
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * Sets the file name.
     *
     * @param fileName
     *            the new file name
     */
    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    /**
     * Gets the file type.
     *
     * @return the file type
     */
    public String getFileType()
    {
        return fileType;
    }

    /**
     * Sets the file type.
     *
     * @param fileType
     *            the new file type
     */
    public void setFileType(String fileType)
    {
        this.fileType = fileType;
    }

    /**
     * Gets the applicant.
     *
     * @return the applicant
     */
    public VIPPApprentice getApprentice()
    {
        return apprentice;
    }

    /**
     * Sets the applicant.
     *
     * @param applicant
     *            the new applicant
     */
    public void setApplicant(VIPPApprentice apprentice)
    {
        this.apprentice = apprentice;
    }
    
    public File getFile() {
 		return file;
 	}

 	public void setFile(File file) {
 		this.file = file;
 	}
}
