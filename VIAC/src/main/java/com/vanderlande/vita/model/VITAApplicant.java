package com.vanderlande.vita.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.vanderlande.annotations.FilterField;
import com.vanderlande.util.Address;
import com.vanderlande.viac.model.VIACUser;

/**
 * The Class VITAApplicant.
 */
@Entity
@Table(name = "VITA_APPLICANT")
public class VITAApplicant implements Serializable
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1744170184788412003L;

    /**
     * Instantiates a new VITA applicant.
     */
    public VITAApplicant()
    {
    }

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private long id;

    /** The address. */
    private Address address;

    /** The firstname. */
    private String firstname;

    /** The lastname. */
    private String lastname;

    /** The mail. */
    private String mail;

    /** The phone. */
    private String phone;

    /** The birthday. */
    @Temporal(TemporalType.DATE)
    private Date birthday;

    /** The status. */
    @ManyToOne
    @FilterField(name = "name")
    private VITAApplicantStatus status;

    /** The career. */
    @ManyToOne
    @FilterField(name = "name")
    private VITACareer career;

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

    /** The applied on. */
    @Column(name = "applied_on")
    @Temporal(TemporalType.DATE)
    private Date appliedOn;

    /** The gender. */
    @Column(name = "gender")
    private String gender;

    /** The historic statuses. */
    @OneToMany(mappedBy = "changedFrom", fetch = FetchType.EAGER)
    private Set<VITAHistoricStatus> historicStatuses = new HashSet<VITAHistoricStatus>();

    /** The eligo test. */
    @ManyToOne(cascade =
    {
      CascadeType.MERGE, CascadeType.PERSIST
    })
    private VITAEligoTest eligoTest;

    /** The documents. */
    @OneToMany(mappedBy = "applicant", fetch = FetchType.EAGER)
    private Set<VITADocument> documents = new HashSet<VITADocument>();

    /** The eligo firstname. */
    @Column(name = "eligo_firstname")
    private String eligoFirstname;

    /** The eligo lastname. */
    @Column(name = "eligo_lastname")
    private String eligoLastname;

    /** The eligo password. */
    @Column(name = "eligo_password")
    private String eligoPassword;

    /** The eligo username. */
    @Column(name = "eligo_username")
    private String eligoUsername;

    /** The invitation date. */
    @Column(name = "invitation_date")
    private Date invitationDate;

    /** The comment. */
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
     * Gets the address.
     *
     * @return the address
     */
    public Address getAddress()
    {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address
     *            the new address
     */
    public void setAddress(Address address)
    {
        this.address = address;
    }

    /**
     * Gets the firstname.
     *
     * @return the firstname
     */
    public String getFirstname()
    {
        return firstname;
    }

    /**
     * Sets the firstname.
     *
     * @param firstname
     *            the new firstname
     */
    public void setFirstname(String firstname)
    {
        this.firstname = firstname;
    }

    /**
     * Gets the lastname.
     *
     * @return the lastname
     */
    public String getLastname()
    {
        return lastname;
    }

    /**
     * Sets the lastname.
     *
     * @param lastname
     *            the new lastname
     */
    public void setLastname(String lastname)
    {
        this.lastname = lastname;
    }

    /**
     * Gets the birthday.
     *
     * @return the birthday
     */
    public Date getBirthday()
    {
        return birthday;
    }

    /**
     * Sets the birthday.
     *
     * @param birthday
     *            the new birthday
     */
    public void setBirthday(Date birthday)
    {
        this.birthday = birthday;
    }

    /**
     * Gets the status.
     *
     * @return the status
     */
    public VITAApplicantStatus getStatus()
    {
        return status;
    }

    /**
     * Sets the status.
     *
     * @param status
     *            the new status
     */
    public void setStatus(VITAApplicantStatus status)
    {
        this.status = status;
    }

    /**
     * Gets the career.
     *
     * @return the career
     */
    public VITACareer getCareer()
    {
        return career;
    }

    /**
     * Sets the career.
     *
     * @param career
     *            the new career
     */
    public void setCareer(VITACareer career)
    {
        this.career = career;
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
     * Gets the eligo test.
     *
     * @return the eligo test
     */
    public VITAEligoTest getEligoTest()
    {
        return eligoTest;
    }

    /**
     * Sets the eligo test.
     *
     * @param eligoTest
     *            the new eligo test
     */
    public void setEligoTest(VITAEligoTest eligoTest)
    {
        this.eligoTest = eligoTest;
    }

    /**
     * Gets the documents.
     *
     * @return the documents
     */
    public Set<VITADocument> getDocuments()
    {
        return documents;
    }

    /**
     * Sets the documents.
     *
     * @param documents
     *            the new documents
     */
    public void setDocuments(Set<VITADocument> documents)
    {
        this.documents = documents;
    }

    /**
     * Gets the historic statuses.
     *
     * @return the historic statuses
     */
    public Set<VITAHistoricStatus> getHistoricStatuses()
    {
        return historicStatuses;
    }

    /**
     * Sets the historic statuses.
     *
     * @param historicStatuses
     *            the new historic statuses
     */
    public void setHistoricStatuses(Set<VITAHistoricStatus> historicStatuses)
    {
        this.historicStatuses = historicStatuses;
    }

    /**
     * Gets the eligo firstname.
     *
     * @return the eligo firstname
     */
    public String getEligoFirstname()
    {
        return eligoFirstname;
    }

    /**
     * Sets the eligo firstname.
     *
     * @param eligoFirstname
     *            the new eligo firstname
     */
    public void setEligoFirstname(String eligoFirstname)
    {
        this.eligoFirstname = eligoFirstname;
    }

    /**
     * Gets the eligo lastname.
     *
     * @return the eligo lastname
     */
    public String getEligoLastname()
    {
        return eligoLastname;
    }

    /**
     * Sets the eligo lastname.
     *
     * @param eligoLastname
     *            the new eligo lastname
     */
    public void setEligoLastname(String eligoLastname)
    {
        this.eligoLastname = eligoLastname;
    }

    /**
     * Gets the eligo password.
     *
     * @return the eligo password
     */
    public String getEligoPassword()
    {
        return eligoPassword;
    }

    /**
     * Sets the eligo password.
     *
     * @param eligoPassword
     *            the new eligo password
     */
    public void setEligoPassword(String eligoPassword)
    {
        this.eligoPassword = eligoPassword;
    }

    /**
     * Gets the eligo username.
     *
     * @return the eligo username
     */
    public String getEligoUsername()
    {
        return eligoUsername;
    }

    /**
     * Sets the eligo username.
     *
     * @param eligoUsername
     *            the new eligo username
     */
    public void setEligoUsername(String eligoUsername)
    {
        this.eligoUsername = eligoUsername;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object o)
    {
        return id == ((VITAApplicant)o).getId();
    }

    /**
     * Gets the mail.
     *
     * @return the mail
     */
    public String getMail()
    {
        return mail;
    }

    /**
     * Sets the mail.
     *
     * @param mail
     *            the new mail
     */
    public void setMail(String mail)
    {
        this.mail = mail;
    }

    /**
     * Gets the phone.
     *
     * @return the phone
     */
    public String getPhone()
    {
        return phone;
    }

    /**
     * Sets the phone.
     *
     * @param phone
     *            the new phone
     */
    public void setPhone(String phone)
    {
        this.phone = phone;
    }

    /**
     * Gets the applied on.
     *
     * @return the applied on
     */
    public Date getAppliedOn()
    {
        return appliedOn;
    }

    /**
     * Sets the applied on.
     *
     * @param appliedOn
     *            the new applied on
     */
    public void setAppliedOn(Date appliedOn)
    {
        this.appliedOn = appliedOn;
    }

    /**
     * Gets the gender.
     *
     * @return the gender
     */
    public String getGender()
    {
        return gender;
    }

    /**
     * Sets the gender.
     *
     * @param gender
     *            the new gender
     */
    public void setGender(String gender)
    {
        this.gender = gender;
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
     * Gets the invitation date.
     *
     * @return the invitation date
     */
    public Date getInvitationDate()
    {
        return invitationDate;
    }

    /**
     * Sets the invitation date.
     *
     * @param invitationDate
     *            the new invitation date
     */
    public void setInvitationDate(Date invitationDate)
    {
        this.invitationDate = invitationDate;
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
