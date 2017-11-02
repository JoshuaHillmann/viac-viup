package com.vanderlande.vita.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.vanderlande.viac.model.VIACUser;

/**
 * The Class VITADocument.
 */
@Entity
@Table(name = "VITA_DOCUMENT")
public class VITADocument implements Serializable
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

    /** The data. */
    @Column(name = "document_data")
    @Lob
    private byte[] data;

    /** The created by. */
    @ManyToOne
    private VIACUser createdBy;

    /** The created on. */
    @Column(name = "created_on")
    private Date createdOn = new Date();

    /** The changed by. */
    @ManyToOne
    private VIACUser changedBy;

    /** The changed on. */
    @Column(name = "changed_on")
    private Date changedOn;

    /** The applicant. */
    @ManyToOne
    private VITAApplicant applicant;

    /** The isEligo. */
    @Column(name = "is_eligo")
    private boolean isEligo;

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
     * Gets the changed by.
     *
     * @return the changed by
     */
    public VIACUser getChangedBy()
    {
        return changedBy;
    }

    /**
     * Sets the changed by.
     *
     * @param changedBy
     *            the new changed by
     */
    public void setChangedBy(VIACUser changedBy)
    {
        this.changedBy = changedBy;
    }

    /**
     * Gets the changed on.
     *
     * @return the changed on
     */
    public Date getChangedOn()
    {
        return changedOn;
    }

    /**
     * Sets the changed on.
     *
     * @param changedOn
     *            the new changed on
     */
    public void setChangedOn(Date changedOn)
    {
        this.changedOn = changedOn;
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
     * Gets the data.
     *
     * @return the data
     */
    public byte[] getData()
    {
        return data;
    }

    /**
     * Sets the data.
     *
     * @param data
     *            the new data
     */
    public void setData(byte[] data)
    {
        this.data = data;
    }

    /**
     * Gets the created by.
     *
     * @return the created by
     */
    public VIACUser getCreatedBy()
    {
        return createdBy;
    }

    /**
     * Sets the created by.
     *
     * @param createdBy
     *            the new created by
     */
    public void setCreatedBy(VIACUser createdBy)
    {
        this.createdBy = createdBy;
    }

    /**
     * Gets the created on.
     *
     * @return the created on
     */
    public Date getCreatedOn()
    {
        return createdOn;
    }

    /**
     * Sets the created on.
     *
     * @param createdOn
     *            the new created on
     */
    public void setCreatedOn(Date createdOn)
    {
        this.createdOn = createdOn;
    }

    /**
     * Gets the applicant.
     *
     * @return the applicant
     */
    public VITAApplicant getApplicant()
    {
        return applicant;
    }

    /**
     * Sets the applicant.
     *
     * @param applicant
     *            the new applicant
     */
    public void setApplicant(VITAApplicant applicant)
    {
        this.applicant = applicant;
    }

    /**
     * Checks if is eligo.
     *
     * @return true, if is eligo
     */
    public boolean isEligo()
    {
        return isEligo;
    }

    /**
     * Sets the eligo.
     *
     * @param isEligo
     *            the new eligo
     */
    public void setEligo(boolean isEligo)
    {
        this.isEligo = isEligo;
    }
}
