package com.vanderlande.viac.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.vanderlande.viac.userManagement.password.PasswordHandler;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class VIACUser.
 */
@Entity
@Table(name = "VIAC_USER")
public class VIACUser implements Serializable
{

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 5610120966792338317L;

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    /** The name. */
    @Column(unique = true, name = "user_name")
    private String name;

    /** The password. */
    private byte[] password;

    /** The salt. */
    private byte[] salt;

    /** The firstname. */
    private String firstname;

    /** The lastname. */
    private String lastname;

    /** The day of birth. */
    @Column(name = "day_of_birth")
    private Date dayOfBirth;

    /** The roles. */
    @ManyToMany(fetch = FetchType.EAGER, cascade =
    {
      CascadeType.MERGE, CascadeType.PERSIST
    })
    private Set<VIACRole> roles;

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

    /** The created users. */
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<VIACUser> createdUsers;

    /** The changed users. */
    @OneToMany(mappedBy = "changedBy", fetch = FetchType.EAGER)
    private Set<VIACUser> changedUsers;

    /** The created roles. */
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<VIACRole> createdRoles;

    /** The changed roles. */
    @OneToMany(mappedBy = "changedBy", fetch = FetchType.EAGER)
    private Set<VIACRole> changedRoles;

    /** The created applicants. */
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<VITAApplicant> createdApplicants;

    /** The changed applicants. */
    @OneToMany(mappedBy = "changedBy", fetch = FetchType.EAGER)
    private Set<VITAApplicant> changedApplicants;

    /** The created documents. */
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<VITADocument> createdDocuments;

    /** The changed documents. */
    @OneToMany(mappedBy = "changedBy", fetch = FetchType.EAGER)
    private Set<VITADocument> changedDocuments;

    /** The created eligo tests. */
    @OneToMany(mappedBy = "createdBy", fetch = FetchType.EAGER)
    private Set<VITAEligoTest> createdEligoTests;

    /** The changed eligo tests. */
    @OneToMany(mappedBy = "changedBy", fetch = FetchType.EAGER)
    private Set<VITAEligoTest> changedEligoTests;

    /** The password resets. */
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private Set<VIACUserPasswordReset> passwordResets;

    /** The banned. */
    private boolean banned = false;

    /**
     * Instantiates a new VIAC user.
     */
    public VIACUser()
    {

    }

    /**
     * Instantiates a new VIAC user.
     *
     * @param name
     *            the name
     * @param password
     *            the password
     * @param firstname
     *            the firstname
     * @param lastname
     *            the lastname
     * @param dayOfBirth
     *            the day of birth
     * @param createdBy
     *            the created by
     */
    public VIACUser(String name,
                    String password,
                    String firstname,
                    String lastname,
                    Date dayOfBirth,
                    VIACUser createdBy)
    {
        this.name = name;
        this.firstname = firstname;
        this.lastname = lastname;
        this.dayOfBirth = dayOfBirth;
        this.createdBy = createdBy;
        this.changedBy = createdBy;
        this.changedOn = new Date();

        try
        {
            this.salt = PasswordHandler.getInstance().generateSalt();
            this.password = PasswordHandler.getInstance().getEncryptedPassword(password, this.salt);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the roles.
     *
     * @return the roles
     */
    public Set<VIACRole> getRoles()
    {
        return roles;
    }

    /**
     * Sets the roles.
     *
     * @param roles
     *            the new roles
     */
    public void setRoles(Set<VIACRole> roles)
    {
        this.roles = roles;
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
     * Gets the password.
     *
     * @return the password
     */
    public byte[] getPassword()
    {
        return password;
    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(byte[] password)
    {
        this.password = password;
    }

    /**
     * Gets the salt.
     *
     * @return the salt
     */
    public byte[] getSalt()
    {
        return salt;
    }

    /**
     * Sets the salt.
     *
     * @param salt
     *            the new salt
     */
    public void setSalt(byte[] salt)
    {
        this.salt = salt;
    }

    /**
     * Gets the created applicants.
     *
     * @return the created applicants
     */
    public Set<VITAApplicant> getCreatedApplicants()
    {
        return createdApplicants;
    }

    /**
     * Sets the created applicants.
     *
     * @param createdApplicants
     *            the new created applicants
     */
    public void setCreatedApplicants(Set<VITAApplicant> createdApplicants)
    {
        this.createdApplicants = createdApplicants;
    }

    /**
     * Gets the changed applicants.
     *
     * @return the changed applicants
     */
    public Set<VITAApplicant> getChangedApplicants()
    {
        return changedApplicants;
    }

    /**
     * Sets the changed applicants.
     *
     * @param changedApplicants
     *            the new changed applicants
     */
    public void setChangedApplicants(Set<VITAApplicant> changedApplicants)
    {
        this.changedApplicants = changedApplicants;
    }

    /**
     * Gets the created documents.
     *
     * @return the created documents
     */
    public Set<VITADocument> getCreatedDocuments()
    {
        return createdDocuments;
    }

    /**
     * Sets the created documents.
     *
     * @param createdDocuments
     *            the new created documents
     */
    public void setCreatedDocuments(Set<VITADocument> createdDocuments)
    {
        this.createdDocuments = createdDocuments;
    }

    /**
     * Gets the changed documents.
     *
     * @return the changed documents
     */
    public Set<VITADocument> getChangedDocuments()
    {
        return changedDocuments;
    }

    /**
     * Sets the changed documents.
     *
     * @param changedDocuments
     *            the new changed documents
     */
    public void setChangedDocuments(Set<VITADocument> changedDocuments)
    {
        this.changedDocuments = changedDocuments;
    }

    /**
     * Gets the created eligo tests.
     *
     * @return the created eligo tests
     */
    public Set<VITAEligoTest> getCreatedEligoTests()
    {
        return createdEligoTests;
    }

    /**
     * Sets the created eligo tests.
     *
     * @param createdEligoTests
     *            the new created eligo tests
     */
    public void setCreatedEligoTests(Set<VITAEligoTest> createdEligoTests)
    {
        this.createdEligoTests = createdEligoTests;
    }

    /**
     * Gets the changed eligo tests.
     *
     * @return the changed eligo tests
     */
    public Set<VITAEligoTest> getChangedEligoTests()
    {
        return changedEligoTests;
    }

    /**
     * Sets the changed eligo tests.
     *
     * @param changedEligoTests
     *            the new changed eligo tests
     */
    public void setChangedEligoTests(Set<VITAEligoTest> changedEligoTests)
    {
        this.changedEligoTests = changedEligoTests;
    }

    /**
     * Checks if is right password.
     *
     * @param attemptedPassword
     *            the attempted password
     * @return true, if is right password
     */
    public boolean isRightPassword(String attemptedPassword)
    {
        try
        {
            return PasswordHandler.getInstance().isValidPassword(attemptedPassword, this.password, this.salt);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return false;
        }

    }

    /**
     * Sets the password.
     *
     * @param password
     *            the new password
     */
    public void setPassword(String password)
    {
        try
        {
            this.salt = PasswordHandler.getInstance().generateSalt();
            this.password = PasswordHandler.getInstance().getEncryptedPassword(password, this.salt);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
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
     * Gets the day of birth.
     *
     * @return the day of birth
     */
    public Date getDayOfBirth()
    {
        return dayOfBirth;
    }

    /**
     * Sets the day of birth.
     *
     * @param dayOfBirth
     *            the new day of birth
     */
    public void setDayOfBirth(Date dayOfBirth)
    {
        this.dayOfBirth = dayOfBirth;
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
     * Gets the id.
     *
     * @return the id
     */
    public long getId()
    {
        return id;
    }

    /**
     * Gets the password resets.
     *
     * @return the password resets
     */
    public Set<VIACUserPasswordReset> getPasswordResets()
    {
        return passwordResets;
    }

    /**
     * Sets the password resets.
     *
     * @param passwordResets
     *            the new password resets
     */
    public void setPasswordResets(Set<VIACUserPasswordReset> passwordResets)
    {
        this.passwordResets = passwordResets;
    }

    /**
     * Gets the created roles.
     *
     * @return the created roles
     */
    public Set<VIACRole> getCreatedRoles()
    {
        return createdRoles;
    }

    /**
     * Sets the created roles.
     *
     * @param createdRoles
     *            the new created roles
     */
    public void setCreatedRoles(Set<VIACRole> createdRoles)
    {
        this.createdRoles = createdRoles;
    }

    /**
     * Gets the changed roles.
     *
     * @return the changed roles
     */
    public Set<VIACRole> getChangedRoles()
    {
        return changedRoles;
    }

    /**
     * Sets the changed roles.
     *
     * @param changedRoles
     *            the new changed roles
     */
    public void setChangedRoles(Set<VIACRole> changedRoles)
    {
        this.changedRoles = changedRoles;
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
     * Gets the created users.
     *
     * @return the created users
     */
    public Set<VIACUser> getCreatedUsers()
    {
        return createdUsers;
    }

    /**
     * Sets the created users.
     *
     * @param createdUsers
     *            the new created users
     */
    public void setCreatedUsers(Set<VIACUser> createdUsers)
    {
        this.createdUsers = createdUsers;
    }

    /**
     * Gets the changed users.
     *
     * @return the changed users
     */
    public Set<VIACUser> getChangedUsers()
    {
        return changedUsers;
    }

    /**
     * Sets the changed users.
     *
     * @param changedUsers
     *            the new changed users
     */
    public void setChangedUsers(Set<VIACUser> changedUsers)
    {
        this.changedUsers = changedUsers;
    }

    /**
     * Checks if is banned.
     *
     * @return true, if is banned
     */
    public boolean isBanned()
    {
        return banned;
    }

    /**
     * Sets the banned.
     *
     * @param banned
     *            the new banned
     */
    public void setBanned(boolean banned)
    {
        this.banned = banned;
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof VIACUser)
        {
            return ((VIACUser)o).getId() == this.getId();
        }
        else
        {
            return false;
        }

    }
}