package com.vanderlande.vita.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.viac.model.VIACUser;

/**
 * The Class VITAEligoTest.
 */
@Entity
@Table(name = "VITA_ELIGO_TEST")
public class VITAEligoTest implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5487604628893189831L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private long id;

    /** The room. */
    private String room;

    /** The name. */
    @Column(name = "test_name")
    private String name;

    /** The date. */
    @Column(name = "test_date")
    private Date date;

    /** The status. */
    @ManyToOne
    @FilterField(name = "name")
    private VITATestStatus status;

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

    /** The testApplicants. */
    @OneToMany(mappedBy = "eligoTest", fetch = FetchType.EAGER)
    private Set<VITAApplicant> testApplicants = new HashSet();

    /** The isCertificatePrinted *. */
    private boolean isCertificatePrinted;

    /** The isLoginInformationPrinted *. */
    private boolean isLoginInformationPrinted;

    /** The isEmailSent *. */
    private boolean isEmailSent;

    /** The isCateringOrganized *. */
    private boolean isCateringOrganized;

    /** The comment *. */
    private String comment;

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
     * Gets the room.
     *
     * @return the room
     */
    public String getRoom()
    {
        return room;
    }

    /**
     * Sets the room.
     *
     * @param room
     *            the new room
     */
    public void setRoom(String room)
    {
        this.room = room;
    }

    /**
     * Gets the date.
     *
     * @return the date
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * Sets the date.
     *
     * @param date
     *            the new date
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public VITATestStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(VITATestStatus status)
    {
        this.status = status;
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
     * Gets the test applicants.
     *
     * @return the test applicants
     */
    public Set<VITAApplicant> getTestApplicants()
    {
        return testApplicants;
    }

    /**
     * Sets the test applicants.
     *
     * @param testApplicants
     *            the new test applicants
     */
    public void setTestApplicants(Set<VITAApplicant> testApplicants)
    {
        this.testApplicants = testApplicants;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name
     *            the new name
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Checks if is certificate printed.
     *
     * @return true, if is certificate printed
     */
    public boolean isCertificatePrinted()
    {
        return isCertificatePrinted;
    }

    /**
     * Sets the certificate printed.
     *
     * @param isCertificatePrinted
     *            the new certificate printed
     */
    public void setCertificatePrinted(boolean isCertificatePrinted)
    {
        this.isCertificatePrinted = isCertificatePrinted;
    }

    /**
     * Checks if is login information printed.
     *
     * @return true, if is login information printed
     */
    public boolean isLoginInformationPrinted()
    {
        return isLoginInformationPrinted;
    }

    /**
     * Sets the login information printed.
     *
     * @param isLoginInformationPrinted
     *            the new login information printed
     */
    public void setLoginInformationPrinted(boolean isLoginInformationPrinted)
    {
        this.isLoginInformationPrinted = isLoginInformationPrinted;
    }

    /**
     * Checks if is email sent.
     *
     * @return true, if is email sent
     */
    public boolean isEmailSent()
    {
        return isEmailSent;
    }

    /**
     * Sets the email sent.
     *
     * @param isEmailSent
     *            the new email sent
     */
    public void setEmailSent(boolean isEmailSent)
    {
        this.isEmailSent = isEmailSent;
    }

    /**
     * Checks if is catering organized.
     *
     * @return true, if is catering organized
     */
    public boolean isCateringOrganized()
    {
        return isCateringOrganized;
    }

    /**
     * Sets the catering organized.
     *
     * @param isCateringOrganized
     *            the new catering organized
     */
    public void setCateringOrganized(boolean isCateringOrganized)
    {
        this.isCateringOrganized = isCateringOrganized;
    }

    /**
     * Gets the comment.
     *
     * @return the comment
     */
    public String getComment()
    {
        return comment;
    }

    /**
     * Sets the comment.
     *
     * @param comment
     *            the new comment
     */
    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
