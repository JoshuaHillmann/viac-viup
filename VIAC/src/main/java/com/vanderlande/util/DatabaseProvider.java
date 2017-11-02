package com.vanderlande.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.wicket.markup.html.media.video.Video;
import org.hibernate.HibernateError;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

import com.vanderlande.exceptions.UserAlreadyExistsException;
import com.vanderlande.viac.model.MailTemplate;
import com.vanderlande.viac.model.VIACAuthorization;
import com.vanderlande.viac.model.VIACConfiguration;
import com.vanderlande.viac.model.VIACRole;
import com.vanderlande.viac.model.VIACUser;
import com.vanderlande.viac.model.VIACUserPasswordReset;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPApprenticeshipArea;
import com.vanderlande.vipp.model.VIPPGroup;
import com.vanderlande.vipp.model.VIPPLearningUnit;
import com.vanderlande.vipp.model.VIPPPerson;
import com.vanderlande.vipp.model.VIPPPresentationCycle;
import com.vanderlande.vipp.model.VIPPPresentedOn;
import com.vanderlande.vipp.model.VIPPSubject;
import com.vanderlande.vipp.model.VIPPSubjectArea;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITAApplicantStatus;
import com.vanderlande.vita.model.VITACareer;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAEligoTest;
import com.vanderlande.vita.model.VITAHistoricStatus;
import com.vanderlande.vita.model.VITATestStatus;

/**
 * The Class VIACDataProvider.
 * 
 * @author dekscha, dedhor, desczu, denmuj, dermic
 * 
 * VIACDataProvider is a Singleton that provides all VIAC-data needed from the
 * database
 */
public class DatabaseProvider {
	
	/** The instance. */
	private static DatabaseProvider instance;

	/** The session. */
	private Session session;

	/** The session factory. */
	private SessionFactory factory;

	/**
	 * Instantiates a new VIAC data provider.
	 */
	private DatabaseProvider() {
		factory = HibernateUtil.getSessionFactory();
	}

	/**
	 * Gets the single instance of VIACDataProvider.
	 *
	 * @return single instance of VIACDataProvider
	 */
	public static DatabaseProvider getInstance() {
		if (instance == null)
			instance = new DatabaseProvider();
		return instance;
	}

	// GETTERS

	/**
	 * Gets the entity.
	 *
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the class
	 * @param id
	 *            the id
	 * @return the entity
	 */
	public <T> T get(Class<T> clazz, Serializable id) {
		session = factory.openSession();
		return session.get(clazz, id);
	}

	/**
	 * Gets all entities of the given class.
	 *
	 * @param <T>
	 *            the generic type
	 * @param clazz
	 *            the class
	 * @return list with all entities
	 */
	public <T> List<T> getAll(Class<T> clazz) {
		session = factory.openSession();
		CriteriaQuery<T> query = session.getCriteriaBuilder().createQuery(clazz);
		query.select(query.from(clazz));
		Query<T> q = session.createQuery(query);
		List<T> users = q.getResultList();
		session.close();
		return users;
	}
	
	/**
	 * Gets the user by name.
	 *
	 * @param name
	 *            the name
	 * @return the user with the given name
	 */
	public VIACUser getUserByName(String name) {
		session = factory.openSession();
		VIACUser user = session.createQuery("from VIACUser where name = '" + name + "'", VIACUser.class)
				.getSingleResult();
		session.close();
		return user;
	}

	/**
	 * Gets the config by key.
	 *
	 * @param key
	 *            the key
	 * @return the config by key
	 */
	public VIACConfiguration getConfigByKey(String key) {
		session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<VIACConfiguration> q = cb.createQuery(VIACConfiguration.class);
		Root<VIACConfiguration> c = q.from(VIACConfiguration.class);
		q.select(c).where(cb.equal(c.get("key"), key));
		TypedQuery<VIACConfiguration> typedQuery = session.createQuery(q);
		VIACConfiguration configuration = null;
		try {
			configuration = typedQuery.getSingleResult();
		} catch (NoResultException e) {
			configuration = new VIACConfiguration(key, "");
		}
		return configuration;
	}
	
	public MailTemplate getTemplateByKey(String key){
		session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<MailTemplate> q = cb.createQuery(MailTemplate.class);
		Root<MailTemplate> c = q.from(MailTemplate.class);
		q.select(c).where(cb.equal(c.get("key"), key));
		TypedQuery<MailTemplate> typedQuery = session.createQuery(q);
		MailTemplate template;
		template = typedQuery.getSingleResult();
		return template;
	}

	/**
	 * Gets the passwordReset by token.
	 *
	 * @param token
	 *            the token
	 * @return the passwordReset with the given token
	 */
	public VIACUserPasswordReset getPasswordResetByToken(String token) {
		session = factory.openSession();
		VIACUserPasswordReset reset = session
				.createQuery("from VIACUserPasswordReset where token = :token", VIACUserPasswordReset.class)
				.setParameter("token", token).getSingleResult();

		Date now = new Date();
		if (now.getTime() - reset.getCreatedOn().getTime() >= 60 * 60 * 1000) {
			throw new HibernateError("not valid");
		}
		session.close();
		return reset;
	}

	/**
	 * Gets the templates.
	 *
	 * @return the templates
	 */
	public List<VIACConfiguration> getTemplates() {
		List<VIACConfiguration> al = new ArrayList<>();

		session = factory.openSession();
		CriteriaBuilder cb = session.getCriteriaBuilder();

		CriteriaQuery<VIACConfiguration> q = cb.createQuery(VIACConfiguration.class);
		Root<VIACConfiguration> c = q.from(VIACConfiguration.class);
		q.select(c).where(cb.like(c.<String>get("key"), "mail_template_%"));
		TypedQuery<VIACConfiguration> typedQuery = session.createQuery(q);

		try {
			al = typedQuery.getResultList();
		} catch (NoResultException e) {
			al = new ArrayList<>();
		}
		return al;
	}

	/**
	 * Gets the all open applicants.
	 *
	 * @return all open applicants
	 */
	public List<VITAApplicant> getAllOpenApplicants() {
		session = factory.openSession();
		List<VITAApplicant> applicants = session
				.createQuery("from VITAApplicant where status_applicant_status_id=1", VITAApplicant.class)
				.getResultList();
		session.close();
		return applicants;
	}

	/**
	 * Gets the all active careers.
	 *
	 * @return all active careers
	 */
	public List<VITACareer> getAllActiveCareers() {
		session = factory.openSession();
		List<VITACareer> careers = session
				.createQuery("from VITACareer where is_active=" + "true", VITACareer.class)
				.getResultList();
		session.close();
		return careers;
	}

	/**
	 * Gets the all possible test delegates.
	 *
	 * @return all possible test delegates
	 */
	public List<VIACUser> getAllPossibleTestDelegates() {
		session = factory.openSession();
		List<VIACUser> delegates = new ArrayList<VIACUser>();
		for (VIACUser delegate : getAll(VIACUser.class)) {
			for (VIACRole role : delegate.getRoles()) {
				for (VIACAuthorization auth : role.getAuthorizations()) {
					if (auth.getAuthCheckName().equalsIgnoreCase("vita_delegate_test")) {
						if (!delegates.contains(delegate)) {
							delegates.add(delegate);
						}
						continue;
					}
				}
			}
		}
		session.close();
		return delegates;
	}
	
	/**
	 * Gets the all 
	 *
	 * @return all 
	 */
	public List<VIPPSubject> getAllAvailableSubjects(int cycles) {
		session = factory.openSession();
		List<VIPPSubject> subjects = session
				.createQuery("from VIPPSubject where presentationCycle_presentationCycle_id is null or presentationCycle_presentationCycle_id < ((select max(id) from VIPPPresentationCycle) - " + (cycles) + ")", VIPPSubject.class)
				.getResultList();
		session.close();
		return subjects;
	}
	
	/**
	 * Gets the all 
	 *
	 * @return all 
	 */
	public List<VIPPSubject> getAllSubjectsFromArea(VIPPSubjectArea bla) {
		session = factory.openSession();
		List<VIPPSubject> subjects = session
				.createQuery("from VIPPSubject where subjectArea_subjectArea_id = (select id from VIPPSubjectArea where subjectArea_name = '" + bla.getName()+ "')", VIPPSubject.class)
				.getResultList();
		session.close();
		return subjects;
	}
	
	public VIPPApprenticeshipArea getApprenticeshipArea(String stringCellValue) {
		session = factory.openSession();
		VIPPApprenticeshipArea area = session
				.createQuery("from VIPPApprenticeshipArea where apprenticeshipArea_name = '" + stringCellValue + "'", VIPPApprenticeshipArea.class)
				.getSingleResult();
		session.close();
		return area;
	}
	
	public VIPPSubject getSubject(String stringCellValue) {
//		session = factory.openSession();
//		VIPPSubject subject = session
//				.createQuery("from VIPPSubject where subject_name LIKE '%" + stringCellValue + "%'", VIPPSubject.class)
//				.getSingleResult();
//		session.close();
		List<VIPPSubject> subjects = this.getAll(VIPPSubject.class);
		if(stringCellValue != "")
		{
			for(VIPPSubject vippSubject : subjects)
			{
				if(vippSubject.getName().contains(stringCellValue))
				{
					return vippSubject;
				}
			}			
		}		
		return null;
	}
	
	public VIPPSubjectArea getLastSubjectArea(VIPPApprentice apprentice, List<VIPPPresentedOn> presentedOn)
	{
		VIPPSubjectArea lastArea;
		if(apprentice.getPerformedPresentations().size() != 0 && presentedOn.size() > 0)
		{
			VIPPSubject subject;
			session = factory.openSession();
			subject = session
					.createQuery("from VIPPPresentedOn where apprentice_apprentice_id = " + apprentice.getId() +" and cycle_presentationCycle_id = (SELECT max(cycle.id) FROM VIPPPresentedOn)", VIPPPresentedOn.class)
					.getSingleResult().getSubject();
			session.close();
			lastArea = subject.getSubjectArea();
		}
		else
		{
			lastArea = null;
		}
		return lastArea;
	}

	// END GETTERS

	// CREATERS

	/**
	 * Persists the entity.
	 *
	 * @param entity
	 *            the entity
	 */
	public void create(Object entity) {
		session = factory.openSession();
		session.beginTransaction();
		session.save(entity);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Persists the entities.
	 *
	 * @param entities
	 *            the entities
	 */
	public void create(List<Object> entities) {
		session = factory.openSession();
		session.beginTransaction();
		for (Object entity : entities) {
			session.save(entity);
		}
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Persists the user.
	 *
	 * @param user
	 *            the user
	 * @throws UserAlreadyExistsException
	 *             the user already exists exception is thrown when a unique
	 *             value is violated
	 */
	public void createUser(VIACUser user) throws UserAlreadyExistsException {
		session = factory.openSession();
		try {
			session.beginTransaction();
			session.save(user);
			session.getTransaction().commit();
		} catch (ConstraintViolationException e) {
			throw new UserAlreadyExistsException(e);
		} finally {
			session.close();
		}
	}

	// END CREATERS

	// MERGERS

	/**
	 * Merges the user.
	 *
	 * @param user
	 *            the user
	 * @throws UserAlreadyExistsException
	 *             the user already exists exception
	 */
	public void mergeUser(VIACUser user) throws UserAlreadyExistsException {
		session = factory.openSession();
		try {
			session.beginTransaction();
			session.merge(user);
			session.getTransaction().commit();
		} catch (ConstraintViolationException e) {
			throw new UserAlreadyExistsException(e);
		} finally {
			session.close();
		}
	}

	/**
	 * Merges the object.
	 *
	 * @param object
	 *            the object
	 */
	public void merge(Object object) {
		session = factory.openSession();
		session.beginTransaction();
		session.merge(object);
		session.getTransaction().commit();
		session.close();
	}

	// END MERGERS

	// DELETERS

	/**
	 * Deletes a User from the database
	 * 
	 * DB-Session 1: Removes all references from
	 * 
	 * (1) users (2) roles (3) applicants (4) eligo tests (5) documents
	 * 
	 * created or changed by the user
	 * 
	 * DB-Session 2: (6) Deletes the user from the database.
	 *
	 * @param user
	 *            the user
	 */
	public void deleteUser(VIACUser user) {

		session = factory.openSession();
		session.beginTransaction();

		Set<VIACUser> usersCreatedBy = user.getCreatedUsers();
		Set<VIACUser> usersChangedBy = user.getChangedUsers();
		Set<VIACRole> rolesCreatedBy = user.getCreatedRoles();
		Set<VIACRole> rolesChangedBy = user.getChangedRoles();
		Set<VITAApplicant> applicantsCreatedBy = user.getCreatedApplicants();
		Set<VITAApplicant> applicantsChangedBy = user.getChangedApplicants();
		Set<VITAEligoTest> eligoTestsCreatedBy = user.getCreatedEligoTests();
		Set<VITAEligoTest> eligoTestsChangedBy = user.getChangedEligoTests();
		Set<VITADocument> documentsCreatedBy = user.getCreatedDocuments();
		Set<VITADocument> documentsChangedBy = user.getChangedDocuments();

		// (1)
		for (VIACUser viacUser : usersCreatedBy) {
			viacUser.setCreatedBy(null);
			session.merge(viacUser);
		}

		for (VIACUser viacUser : usersChangedBy) {
			viacUser.setChangedBy(null);
			session.merge(viacUser);
		}

		// (2)
		for (VIACRole viacRole : rolesCreatedBy) {
			viacRole.setCreatedBy(null);
			session.merge(viacRole);
		}

		for (VIACRole viacRole : rolesChangedBy) {
			viacRole.setChangedBy(null);
			session.merge(viacRole);
		}

		// (3)
		for (VITAApplicant vitaApplicant : applicantsCreatedBy) {
			vitaApplicant.setCreatedBy(null);
			session.merge(vitaApplicant);
		}

		for (VITAApplicant vitaApplicant : applicantsChangedBy) {
			vitaApplicant.setChangedBy(null);
			session.merge(vitaApplicant);
		}

		// (4)
		for (VITAEligoTest eligoTest : eligoTestsCreatedBy) {
			eligoTest.setCreatedBy(null);
			session.merge(eligoTest);
		}

		for (VITAEligoTest eligoTest : eligoTestsChangedBy) {
			eligoTest.setChangedBy(null);
			session.merge(eligoTest);
		}

		// (5)
		for (VITADocument document : documentsCreatedBy) {
			document.setCreatedBy(null);
			session.merge(document);
		}

		for (VITADocument document : documentsChangedBy) {
			document.setChangedBy(null);
			session.merge(document);
		}

		session.getTransaction().commit();
		session.close();

		// (6)
		session = factory.openSession();
		session.beginTransaction();
		session.delete(user);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Delete a test from the database.
	 *
	 * @param test
	 *            the test
	 */
	public void deleteTest(VITAEligoTest test) {
		Set<VITAApplicant> applicants = test.getTestApplicants();
		session = factory.openSession();
		session.beginTransaction();
		for (VITAApplicant applicant : applicants) {
			applicant.setEligoTest(null);
			session.merge(applicant);
		}
		session.getTransaction().commit();
		session.close();

		session = factory.openSession();
		session.beginTransaction();
		session.delete(test);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Deletes the role from the database.
	 *
	 * @param role
	 *            the role
	 */
	public void deleteRole(VIACRole role) {
		Set<VIACUser> users = role.getUsers();
		session = factory.openSession();
		session.beginTransaction();
		for (VIACUser user : users) {
			user.getRoles().remove(role);
			session.merge(user);
		}
		session.getTransaction().commit();
		session.close();

		session = factory.openSession();
		session.beginTransaction();
		role.setAuthorizations(null);
		session.merge(role);
		session.getTransaction().commit();
		session.close();

		session = factory.openSession();
		session.beginTransaction();
		session.delete(role);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Deletes the applicant from the database.
	 * (1) Status history and (2) documents need to be removed beforehand.
	 * 
	 * @param applicant the applicant
	 */
	public void deleteApplicant(VITAApplicant applicant) {
		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		//(1)
		Set<VITAHistoricStatus> historicStatus = applicant.getHistoricStatuses();

		for (VITAHistoricStatus vitaHistoricStatus : historicStatus) {
			session.delete(vitaHistoricStatus);
		}
		applicant.setHistoricStatuses(null);

		session.getTransaction().commit();
		session.close();

		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();

		//(2)
		Set<VITADocument> vitaDocuments = applicant.getDocuments();

		for (VITADocument vitaDocument : vitaDocuments) {
			session.delete(vitaDocument);
		}
		applicant.setDocuments(null);

		session.getTransaction().commit();
		session.close();

		session = HibernateUtil.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(applicant);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Deletes a passwordReset form the database.
	 *
	 * @param reset
	 *            the reset
	 */
	public void deletePasswordReset(VIACUserPasswordReset reset) {
		reset.setToken(new Date().toString());

		session = factory.openSession();
		session.beginTransaction();
		session.merge(reset);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Deletes the object from the database.
	 *
	 * @param object
	 *            the object
	 */
	public void delete(Object object) {
		session = factory.openSession();
		session.beginTransaction();
		session.delete(object);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Deletes the document from the database.
	 *
	 * @param document
	 *            the document
	 */
	public void deleteDocument(VITADocument document) {

		session = factory.openSession();
		session.beginTransaction();

		VITAApplicant applicant = document.getApplicant();

		Set<VITADocument> documents = applicant.getDocuments();

		documents.remove(document);
		session.delete(document);

		session.getTransaction().commit();
		session.close();
	}
	
	public void deleteApprentice(VIPPApprentice apprentice)
	{
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPSubject> subjects = apprentice.getPerformedPresentations();
		for (VIPPSubject vippSubject : subjects) {
			if(vippSubject.getLastPresentedBy() == apprentice)
			{
				vippSubject.setLastPresentedBy(null);
				session.merge(vippSubject);
			}
		}
		session.getTransaction().commit();
		session.close();
		
		session = factory.openSession();
		session.beginTransaction();
		List<VIPPGroup> groups = getAll(VIPPGroup.class);
		for (VIPPGroup vippGroup : groups) {
			for (VIPPApprentice vippApprentice : vippGroup.getPersons())
			{
				if(vippApprentice == apprentice)
				{
					vippGroup.getPersons().remove(vippApprentice);
					session.merge(vippApprentice);
				}
			}
		}
		session.getTransaction().commit();
		session.close();
		
		session = factory.openSession();
		session.beginTransaction();		
		session.delete(apprentice);		
		session.getTransaction().commit();
		session.close();
	}
	
	public void deleteSubject(VIPPSubject subject)
	{
		session = factory.openSession();
		session.beginTransaction();
		subject.setIsActive(false);
		session.merge(subject);
		session.close();
	}

	// END DELETER

	/**
	 * Database init.
	 */
	public void databaseInit() {
		initConfigurations();
		initMailTemplates();
		initApplicantStatus();
		initCareers();
		initTestStatus();
		initAuthorizationsAndRoles();
		initAdminData();
		initLearningUnits();
		initSubjectAreas();
		initApprenticeshipAreas();
		initSubjects();
//		initApprentices();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the needed roles and authorizations, which can be change later if needed.
	 */
	private void initAuthorizationsAndRoles() {
		session = factory.openSession();
		session.beginTransaction();

		Set<VIACRole> roles = new HashSet<VIACRole>();
		Set<VIACAuthorization> authorizations = new HashSet<VIACAuthorization>();
		
		//VITA Authorizations
		
		//Administration
		VIACAuthorization administration_tab = new VIACAuthorization("vita_administration_tab", "Administration Tab", "Administration Tab");
		authorizations.add(administration_tab);
		VIACAuthorization edit_career = new VIACAuthorization("vita_edit_career", "Karriere bearbeiten", "Karriere bearbeiten");
		authorizations.add(edit_career);
		VIACAuthorization new_career = new VIACAuthorization("vita_new_career", "Karriere anlegen", "Karriere anlegen");
		authorizations.add(new_career);
		
		//Test Administration
		VIACAuthorization testAdministration_tab = new VIACAuthorization("vita_testAdministration_tab", "Test Administration Tab", " Test Administration Tab");
		authorizations.add(testAdministration_tab);
		VIACAuthorization new_test = new VIACAuthorization("vita_new_test", "Test anlegen", "Test anlegen");
		authorizations.add(new_test);
		VIACAuthorization delegate_test = new VIACAuthorization("vita_delegate_test", "Planung delegieren", "Planung delegieren");
		authorizations.add(delegate_test);
		VIACAuthorization edit_test = new VIACAuthorization("vita_edit_test", "Test bearbeiten", "Test bearbeiten");
		authorizations.add(edit_test);
		VIACAuthorization delete_test = new VIACAuthorization("vita_delete_test", "Test löschen", "Test löschen");
		authorizations.add(delete_test);
		VIACAuthorization close_test = new VIACAuthorization("vita_close_test", "Test abschließen", "Test abschließen");
		authorizations.add(close_test);
		VIACAuthorization cancel_test = new VIACAuthorization("vita_cancel_test", "Test absagen", "Test absagen");
		authorizations.add(cancel_test);
		VIACAuthorization eligo_administration = new VIACAuthorization("vita_eligo_administration", "Eligo-Verwaltung", "Eligo-Verwaltung");
		authorizations.add(eligo_administration);
		VIACAuthorization send_mail = new VIACAuthorization("vita_send_mail", "Einladung versenden", "Einladung versenden");
		authorizations.add(send_mail);
		
		//Applicant Management
		VIACAuthorization applicantAdministration_tab = new VIACAuthorization("vita_applicantAdministration_tab", "Bewerberverwaltung Tab", "Bewerberverwaltung Tab");
		authorizations.add(applicantAdministration_tab);
		VIACAuthorization new_applicant = new VIACAuthorization("vita_new_applicant", "Bewerber anlegen", "Bewerber anlegen");
		authorizations.add(new_applicant);
		VIACAuthorization edit_applicant = new VIACAuthorization("vita_edit_applicant", "Bewerber bearbeiten", "Bewerber bearbeiten");
		authorizations.add(edit_applicant);
		VIACAuthorization delete_applicant = new VIACAuthorization("vita_delete_applicant", "Bewerber löschen", "Bewerber löschen");
		authorizations.add(delete_applicant);
		VIACAuthorization detail_applicant = new VIACAuthorization("vita_detail_applicant", "Bewerber Detail", "Bewerber Detail");
		authorizations.add(detail_applicant);
		VIACAuthorization accept_applicant = new VIACAuthorization("vita_accept_applicant", "Bewerber annehmen", "Bewerber annehmen");
		authorizations.add(accept_applicant);
		VIACAuthorization reject_applicant = new VIACAuthorization("vita_reject_applicant", "Bewerber ablehnen", "Bewerber ablehnen");
		authorizations.add(reject_applicant);
		
		//VIAC
		
		//Role Management
		VIACAuthorization roleAdministration_tab = new VIACAuthorization("viac_roleAdministration_tab", "Rollenverwaltung Tab", "Rollenverwaltung Tab");
		authorizations.add(roleAdministration_tab);
		VIACAuthorization edit_role = new VIACAuthorization("viac_edit_role", "Rolle bearbeiten", "Rolle bearbeiten");
		authorizations.add(edit_role);
		VIACAuthorization new_role = new VIACAuthorization("viac_new_role", "Rolle anlegen", "Rolle anlegen");
		authorizations.add(new_role);
		VIACAuthorization delete_role = new VIACAuthorization("viac_delete_role", "Rolle löschen", "Rolle löschen");
		authorizations.add(delete_role);
		
		//User Management
		VIACAuthorization delete_user = new VIACAuthorization("viac_delete_user", "Nutzer löschen", "Nutzer löschen");
		authorizations.add(delete_user);
		VIACAuthorization new_user = new VIACAuthorization("viac_new_user", "Nutzer anlegen", "Nutzer anlegen");
		authorizations.add(new_user);
		VIACAuthorization edit_user = new VIACAuthorization("viac_edit_user", "Nutzer bearbeiten", "Nutzer bearbeiten");
		authorizations.add(edit_user);
		VIACAuthorization userAdministration_tab = new VIACAuthorization("viac_userAdministration_tab", "Nutzerverwaltung Tab", "Nutzerverwaltung Tab");
		authorizations.add(userAdministration_tab);
		VIACAuthorization viac_block_user = new VIACAuthorization("viac_block_user", "Nutzer sperren", "Nutzer sperren");
		authorizations.add(viac_block_user);
		
		//Template Management
		VIACAuthorization templateAdministration_tab = new VIACAuthorization("viac_templateAdministration_tab", "Templateverwaltung Tab", "Templateverwaltung Tab");
		authorizations.add(templateAdministration_tab);
		
		//VIPP
		VIACAuthorization vipp_all = new VIACAuthorization("vipp_all", "Alles in VIPP", "Alles in VIPP");
		authorizations.add(vipp_all);
		
		//Roles
		VIACRole roleRoleAdministration = new VIACRole("VIAC Rollenverwaltung", "VIAC Rollenverwaltung");
		Set<VIACAuthorization> roleRoleAdministrationRights = new HashSet<VIACAuthorization>();
		roleRoleAdministrationRights.add(roleAdministration_tab);
		roleRoleAdministrationRights.add(edit_role);
		roleRoleAdministrationRights.add(new_role);
		roleRoleAdministrationRights.add(delete_role);
		roleRoleAdministration.setAuthorizations(roleRoleAdministrationRights);
		roles.add(roleRoleAdministration);
		
		VIACRole roleUserAdministrartion = new VIACRole("VIAC Nutzerverwaltung", "VIAC Nutzerverwaltung");
		Set<VIACAuthorization> roleUserAdministrartionRights = new HashSet<VIACAuthorization>();
		roleUserAdministrartionRights.add(delete_user);
		roleUserAdministrartionRights.add(new_user);
		roleUserAdministrartionRights.add(edit_user);
		roleUserAdministrartionRights.add(viac_block_user);
		roleUserAdministrartionRights.add(userAdministration_tab);
		roleUserAdministrartion.setAuthorizations(roleUserAdministrartionRights);
		roles.add(roleUserAdministrartion);
		
		VIACRole roleTemplateAdministration = new VIACRole("VIAC Templateverwaltung", "VIAC Templateverwaltung");
		Set<VIACAuthorization> roleTemplateAdministrationRights = new HashSet<VIACAuthorization>();
		roleTemplateAdministrationRights.add(templateAdministration_tab);
		roleTemplateAdministration.setAuthorizations(roleTemplateAdministrationRights);
		roles.add(roleTemplateAdministration);
		
		VIACRole roleApplicantAdministration = new VIACRole("VITA Bewerberverwaltung", "VITA Bewerberverwaltung");
		Set<VIACAuthorization> roleApplicantAdministrationRights = new HashSet<VIACAuthorization>();
		roleApplicantAdministrationRights.add(applicantAdministration_tab);
		roleApplicantAdministrationRights.add(new_applicant);
		roleApplicantAdministrationRights.add(edit_applicant);
		roleApplicantAdministrationRights.add(delete_applicant);
		roleApplicantAdministrationRights.add(detail_applicant);
		roleApplicantAdministrationRights.add(accept_applicant);
		roleApplicantAdministrationRights.add(reject_applicant);
		roleApplicantAdministration.setAuthorizations(roleApplicantAdministrationRights);
		roles.add(roleApplicantAdministration);
		
		VIACRole roleTestOrganisation = new VIACRole("VITA Testplanung", "VITA Testplanung");
		Set<VIACAuthorization> roleTestOrganisationRights = new HashSet<VIACAuthorization>();
		roleTestOrganisationRights.add(testAdministration_tab);
		roleTestOrganisationRights.add(new_test);
		roleTestOrganisationRights.add(delegate_test);
		roleTestOrganisationRights.add(edit_test);
		roleTestOrganisationRights.add(delete_test);
		roleTestOrganisationRights.add(close_test);
		roleTestOrganisationRights.add(cancel_test);
		roleTestOrganisationRights.add(eligo_administration);
		roleTestOrganisationRights.add(send_mail);
		roleTestOrganisation.setAuthorizations(roleTestOrganisationRights);
		roles.add(roleTestOrganisation);
		
		VIACRole roleAdministration = new VIACRole("VITA Administration", "VITA Administration");
		Set<VIACAuthorization> roleAdministrationRights = new HashSet<VIACAuthorization>();
		roleAdministrationRights.add(administration_tab);
		roleAdministrationRights.add(edit_career);
		roleAdministrationRights.add(new_career);
		roleAdministration.setAuthorizations(roleAdministrationRights);
		roles.add(roleAdministration);
		
		VIACRole roleDelegatedTest = new VIACRole("VITA nur Testersteller", "VITA nur Testersteller");
		Set<VIACAuthorization> roleDelegatedTestRights = new HashSet<VIACAuthorization>();
		roleDelegatedTestRights.add(testAdministration_tab);
		roleDelegatedTestRights.add(new_test);
		roleDelegatedTestRights.add(edit_test);
		roleDelegatedTestRights.add(send_mail);	
		roleDelegatedTestRights.add(applicantAdministration_tab);
		roleDelegatedTestRights.add(new_applicant);
		roleDelegatedTestRights.add(edit_applicant);
		roleDelegatedTestRights.add(detail_applicant);
		roleDelegatedTest.setAuthorizations(roleDelegatedTestRights);
		roles.add(roleDelegatedTest);
		
		VIACRole roleVitaAdmin = new VIACRole("VITA Admin", "VITA Vollzugriff");
		Set<VIACAuthorization> roleVitaAdminRights = new HashSet<VIACAuthorization>();
		roleVitaAdminRights.add(applicantAdministration_tab);
		roleVitaAdminRights.add(new_applicant);
		roleVitaAdminRights.add(edit_applicant);
		roleVitaAdminRights.add(delete_applicant);
		roleVitaAdminRights.add(detail_applicant);
		roleVitaAdminRights.add(accept_applicant);
		roleVitaAdminRights.add(reject_applicant);
		roleVitaAdminRights.add(testAdministration_tab);
		roleVitaAdminRights.add(new_test);
		roleVitaAdminRights.add(delegate_test);
		roleVitaAdminRights.add(edit_test);
		roleVitaAdminRights.add(delete_test);
		roleVitaAdminRights.add(close_test);
		roleVitaAdminRights.add(cancel_test);
		roleVitaAdminRights.add(eligo_administration);
		roleVitaAdminRights.add(send_mail);
		roleVitaAdminRights.add(administration_tab);
		roleVitaAdminRights.add(edit_career);
		roleVitaAdminRights.add(new_career);
		roleVitaAdmin.setAuthorizations(roleVitaAdminRights);
		roles.add(roleVitaAdmin);
		
		VIACRole roleViacAdmin = new VIACRole("VIAC Admin", "Vollzugriff");
		Set<VIACAuthorization> roleViacAdminRights = new HashSet<VIACAuthorization>();
		for(VIACAuthorization a : roleVitaAdminRights){
			roleViacAdminRights.add(a);
		}
		roleViacAdminRights.add(roleAdministration_tab);
		roleViacAdminRights.add(edit_role);
		roleViacAdminRights.add(new_role);
		roleViacAdminRights.add(delete_role);
		roleViacAdminRights.add(delete_user);
		roleViacAdminRights.add(new_user);
		roleViacAdminRights.add(edit_user);
		roleViacAdminRights.add(userAdministration_tab);
		roleViacAdminRights.add(viac_block_user);
		roleViacAdminRights.add(templateAdministration_tab);
		roleViacAdminRights.add(vipp_all);
		roleViacAdmin.setAuthorizations(roleViacAdminRights);
		roles.add(roleViacAdmin);		
		
		//Roles
		VIACRole roleVippTrainer = new VIACRole("VIPP Ausbilder", "VIPP Ausbilder");
		Set<VIACAuthorization> roleVippTrainerRights = new HashSet<VIACAuthorization>();
		roleVippTrainerRights.add(vipp_all);
		roleVippTrainer.setAuthorizations(roleVippTrainerRights);
		roles.add(roleVippTrainer);
		
		createRolesIfMissing(roles, session);
		createAuthIfMissing(authorizations, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the default admin with full access rights. Username and password are static.
	 */
	private void initAdminData() {
		session = factory.openSession();
		session.beginTransaction();
		
		Query q = session.createQuery("from VIACUser where name = :name").setParameter("name", "admin@example.com");
		if (q.getResultList().isEmpty()) {
			VIACUser user = new VIACUser("admin@example.com", "passwort", "The", "Admin", new Date(), null);
			
			Query q1 = session.createQuery("from VIACRole where name = :name").setParameter("name", "VIAC Admin");
			VIACRole role = (VIACRole) q1.uniqueResult();
			Set<VIACRole> roles = new HashSet<>();
			roles.add(role);
			user.setRoles(roles);
			session.save(user);
		}
		
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the pre defined test status.
	 */
	private void initTestStatus() {
		session = factory.openSession();
		session.beginTransaction();
		List<VITATestStatus> testStatusList = new ArrayList<>();

		testStatusList.add(new VITATestStatus("TEST GEPLANT"));
		testStatusList.add(new VITATestStatus("TEST DURCHGEFÜHRT"));
		testStatusList.add(new VITATestStatus("TEST ABGESAGT"));
		testStatusList.add(new VITATestStatus("TEST ABGESCHLOSSEN"));

		createTestStatusIfMissing(testStatusList, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the pre defined careers, additional entrys can be added with the ui.
	 */
	private void initCareers() {
		session = factory.openSession();
		session.beginTransaction();
		List<VITACareer> careers = new ArrayList<>();
		careers.add(new VITACareer("Fachinformatiker Anwendungsentwicklung"));
		careers.add(new VITACareer("Fachinformatiker Systemintegration"));

		createCareersIfMissing(careers, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the applicant status.
	 */
	private void initApplicantStatus() {
		session = factory.openSession();
		session.beginTransaction();
		List<VITAApplicantStatus> applicantStatusList = new ArrayList<>();
		// ApplicantStatus
		applicantStatusList.add(new VITAApplicantStatus("Bewerbung eingegangen"));
		applicantStatusList.add(new VITAApplicantStatus("Bewerbung abgelehnt"));
		applicantStatusList.add(new VITAApplicantStatus("Zum Online-Test eingeladen"));
		applicantStatusList.add(new VITAApplicantStatus("Online-Test durchgeführt"));
		applicantStatusList.add(new VITAApplicantStatus("Nicht zum Online-Test erschienen"));
		applicantStatusList.add(new VITAApplicantStatus("Abgelehnt nach Online-Test"));
		applicantStatusList.add(new VITAApplicantStatus("Zum Vorstellungsgespräch einladen"));
		applicantStatusList.add(new VITAApplicantStatus("Zum Vorstellungsgespräch eingeladen"));
		applicantStatusList.add(new VITAApplicantStatus("Vorstellungsgespräch durchgeführt"));
		applicantStatusList.add(new VITAApplicantStatus("Nach Vorstellungsgespräch abgelehnt"));
		applicantStatusList.add(new VITAApplicantStatus("Vertrag angeboten"));
		applicantStatusList.add(new VITAApplicantStatus("Vertrag angenommen"));
		applicantStatusList.add(new VITAApplicantStatus("Vertrag abgelehnt"));
		applicantStatusList.add(new VITAApplicantStatus("Bewerber hat abgelehnt"));

		createApplicantStatusIfMissing(applicantStatusList, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates mail templates, they can be changed via the ui. 
	 * It is not possible to add additional templates with the ui!
	 *
	 * The mail subject has the same as the text, with the suffix "_header".
	 */
	private void initMailTemplates() {
		session = factory.openSession();
		session.beginTransaction();
		
		List<MailTemplate> templates = new ArrayList<>();
		
		// Mail templates
		templates.add(new MailTemplate("mail_template_test_invitation_change",
				"#gender_hello #lastname,\n\nes gab Änderungen beim Einstellungstest. Der Test findet am #test_date im Raum #test_room statt.\n\n\nMit freundlichen Grüßen\n\nVanderlande Industries"));
		templates.add(
				new MailTemplate("mail_template_test_invitation_change_header", "Änderung beim Einstellungstest"));
		templates.add(new MailTemplate("mail_template_password_forgot",
				"Hallo #firstname #lastname,\n\n<a href=\"#register_key\">bitte klicke hier um jetzt dein neues Passwort festzulegen.</a>\nDieser Link verfällt innerhalb von einer Stunde.\n\n\nMit freundlichen Grüßen\n\ndein VIAC-Team"));
		templates.add(new MailTemplate("mail_template_password_forgot_header", "VIAC Passwort vergessen"));
		templates.add(new MailTemplate("mail_template_user_password_change",
				"Hallo #firstname #lastname,\n\ndein Passwort wurde geändert.\nDein neues Passwort lautet: #password.\nBitte ändere dieses Passwort so schnell wie möglich.\n\n\nMit freundlichen Grüßen\n\ndein VIAC-Team"));
		templates.add(new MailTemplate("mail_template_user_password_change_header", "VIAC Passwort verändert"));
		templates.add(new MailTemplate("mail_template_user_created",
				"Hallo #firstname #lastname,\n\ndein VIAC-Account wurde gerade erstellt.\nDu kannst dich <a href=\"#register_key\">hier einloggen</a>.\nMit den folgenden Daten:\nNutzername: #username\nPasswort: #password\nBitte ändere dieses Passwort so schnell wie möglich.\n\n\nMit freundlichen Grüßen\n\ndein VIAC-Team"));
		templates.add(new MailTemplate("mail_template_user_created_header", "VIAC Account erstellt"));
		templates.add(new MailTemplate("mail_template_deligate_test",
				"Hallo #firstname #lastname,\n\ndir wurde gerade der Test #testgroup zugewiesen.\n\n\nMit freundlichen Grüßen\n\ndein VIAC-Team"));
		templates.add(new MailTemplate("mail_template_deligate_test_header", "VITA Test deligiert"));
		templates.add(new MailTemplate("mail_template_test_invitation",
				"#gender_hello #lastname,\n\nhiermit sind Sie herzlichst zu einem Einstellungstest bei Vanderlande Industries eingeladen.\nDer Test wird am #test_date stattfinden im Raum #test_room.\nBitte melden Sie sich rechzeitig ab, falls Sie nicht teilnehmen können.\n\n\nMit freundlichen Grüßen\n\nVanderlande Industries"));
		templates.add(new MailTemplate("mail_template_test_invitation_header", "Einladung zum Einstellungstest"));
		templates.add(new MailTemplate("mail_template_men", "Sehr geehrter Herr"));
		templates.add(new MailTemplate("mail_template_women", "Sehr geehrte Frau"));
		templates.add(new MailTemplate("mail_template_cancel_test", "Der Test wurde abgesagt."));
		templates.add(new MailTemplate("mail_template_cancel_test_header", "Test abgesagt"));
		
		createTemplateIfMissing(templates, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * Part of the database init process. 
	 * This part creates the basic configurations for the system.
	 *
	 * debug mode, mail informations
	 */
	private void initConfigurations() {
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIACConfiguration> configurations = new ArrayList<>();
		// Debug Mode
		configurations.add(new VIACConfiguration("debug", "true"));

		// Email configuration
		configurations.add(new VIACConfiguration("mail_disabled", "true"));
		configurations.add(new VIACConfiguration("mail_username", "sopra@vanderlande.com"));
		configurations.add(new VIACConfiguration("mail_password", "DO NOT USE"));
		configurations.add(new VIACConfiguration("mail_exchange_url", "https://outlook.office365.com/EWS/Exchange.asmx"));
		configurations.add(new VIACConfiguration("mail_host", "mailserver.vi.corp"));
		createConfigsIfMissing(configurations, session);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Part of the database init process. 
	 * This part creates the learning units.
	 */
	private void initLearningUnits(){
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPLearningUnit> units = new ArrayList<>();
		
		units.add(new VIPPLearningUnit("01", "Der Betrieb und sein Umfeld"));
		units.add(new VIPPLearningUnit("02", "Geschäftsprozesse und Betriebliche Organisation"));
		units.add(new VIPPLearningUnit("03", "Informationsquellen und Arbeitsmethoden"));
		units.add(new VIPPLearningUnit("04", "Einfache IT-Systeme"));
		units.add(new VIPPLearningUnit("05", "Fachliches Englisch"));
		units.add(new VIPPLearningUnit("06", "Entwickeln und bereitstellen von Themen"));
		units.add(new VIPPLearningUnit("07", "Vernetzte IT-Systeme"));
		units.add(new VIPPLearningUnit("08", "Markt- und Kundenbeziehungen"));
		units.add(new VIPPLearningUnit("09", "Öffentliche Netze, Dienste"));
		units.add(new VIPPLearningUnit("10", "Betreuen von IT-Systemen"));
		units.add(new VIPPLearningUnit("11", "Rechnungswesen und Controlling"));
		createUnitsIfMissing(units, session);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Part of the database init process. 
	 * This part creates the subject areas.
	 */
	private void initSubjectAreas(){
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPSubjectArea> areas = new ArrayList<>();
		
		areas.add(new VIPPSubjectArea("Netzwerk"));
		areas.add(new VIPPSubjectArea("Biografie"));
		areas.add(new VIPPSubjectArea("BWL"));
		areas.add(new VIPPSubjectArea("Vorgehensmodelle"));
		areas.add(new VIPPSubjectArea("Hardware"));
		areas.add(new VIPPSubjectArea("Programmierung"));
		areas.add(new VIPPSubjectArea("Datensicherheit"));
		areas.add(new VIPPSubjectArea("Datenbanken"));
		areas.add(new VIPPSubjectArea("Projektmanagement"));
		areas.add(new VIPPSubjectArea("Software"));
		areas.add(new VIPPSubjectArea("Theoretische Informatik"));
		
		createAreasIfMissing(areas, session);
		session.getTransaction().commit();
		session.close();
	}
	
	/**
	 * Part of the database init process. 
	 * This part creates the learning units.
	 */
	private void initSubjects(){
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPSubject> subjects = new ArrayList<>();		
		VIPPSubject subject;
		
		subjects.add(new VIPPSubject(false, "Freie Themenwahl", null, null, null, null));
//		subjects.add(new VIPPSubject("Freie Themenwahl"));
		
		createSubjectsIfMissing(subjects, session);
		session.getTransaction().commit();
		session.close();
	}
	
	private void initApprenticeshipAreas(){
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPApprenticeshipArea> areas = new ArrayList<>();
		
		areas.add(new VIPPApprenticeshipArea("FISI"));
		areas.add(new VIPPApprenticeshipArea("AE"));
		areas.add(new VIPPApprenticeshipArea("IT-SK"));
		areas.add(new VIPPApprenticeshipArea("Kaufl."));
		areas.add(new VIPPApprenticeshipArea("Dual"));
		
		createApprenticeshipAreasIfMissing(areas, session);
		session.getTransaction().commit();
		session.close();
	}
	
	private void initApprentices(){
		session = factory.openSession();
		session.beginTransaction();
		
		List<VIPPApprentice> apprentices = new ArrayList<>();
		VIPPApprentice apprentice;

		List<VIPPApprenticeshipArea> areas = session
				.createQuery("from VIPPApprenticeshipArea", VIPPApprenticeshipArea.class)
				.getResultList();
		
		List<VIPPSubject> subjects = session
				.createQuery("from VIPPSubject", VIPPSubject.class)
				.getResultList();
		
		List<VIPPSubject> subject;
		
		VIPPApprenticeshipArea fisi = areas.get(0);
		VIPPApprenticeshipArea ae = areas.get(1);
		VIPPApprenticeshipArea it = areas.get(2);
		VIPPApprenticeshipArea kauf = areas.get(3);
		VIPPApprenticeshipArea dual = areas.get(4);
		
		System.out.println(subjects.size());
		
//		apprentice = new VIPPApprentice("Gina Carina", "Dobry", dual, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(18)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Janine", "Zieher", dual, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(64)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Tom", "Meyer", dual, 2016);
//		subject = new ArrayList<VIPPSubject>();
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Ole", "Jankowski", dual, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(66)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Danny", "Vogt", ae, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(75)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Niklas", "Vielhaber", ae, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(68)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Maximilian", "Grobe", ae, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(72)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Joshua", "Marks", ae, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(78)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Lena", "Sturtz", kauf, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(6)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Moritz", "Gräwe", fisi, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(49)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Nikola", "Drehnhaus", fisi, 2016);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(45)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Daniel", "Buschmann", dual, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(51), subjects.get(22), subjects.get(23), subjects.get(36), subjects.get(32)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Johannes", "Heiderich", dual, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(47), subjects.get(76), subjects.get(1), subjects.get(42), subjects.get(80)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Niklas", "Sprenger", dual, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(59), subjects.get(20), subjects.get(11), subjects.get(21), subjects.get(72)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Pascal", "Michallik", dual, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(17), subjects.get(13), subjects.get(65), subjects.get(56), subjects.get(52)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Alex", "Kodat", ae, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(52), subjects.get(61), subjects.get(54), subjects.get(78), subjects.get(80)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Alex", "Rüttershoff", it, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(32), subjects.get(16), subjects.get(19), subjects.get(44), subjects.get(49)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Fabian", "Haasjes", ae, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(70), subjects.get(48), subjects.get(7), subjects.get(59), subjects.get(74)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Jennifer", "Mette", ae, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(15), subjects.get(9), subjects.get(2), subjects.get(31), subjects.get(64)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Lillebror", "Geißler", ae, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(60), subjects.get(4), subjects.get(35), subjects.get(55), subjects.get(25)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Tim", "Reichelt", fisi, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(25), subjects.get(0), subjects.get(41), subjects.get(73), subjects.get(60)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
//		
//		apprentice = new VIPPApprentice("Jamie", "Herzog", kauf, 2015);
//		subject = new ArrayList<VIPPSubject>(Arrays.asList(subjects.get(34), subjects.get(85), subjects.get(53), subjects.get(80), subjects.get(31)));
//		apprentice.setPerformedPresentations(subject);
//		apprentices.add(apprentice);
		
		createApprenticesIfMissing(apprentices, session);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create configuration if needed.
	 * @param configurations to add
	 * @param database session
	 */
	private void createConfigsIfMissing(List<VIACConfiguration> configurations, Session session) {
		for (VIACConfiguration config : configurations) {
			Query q = session.createQuery("from VIACConfiguration where key = :key").setParameter("key",
					config.getKey());
			if (q.getResultList().isEmpty()) {
				session.save(config);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create role if needed.
	 * @param roles to add
	 * @param database session
	 */
	private void createRolesIfMissing(Set<VIACRole> roles, Session session) {
		for (VIACRole r : roles) {
			Query q = session.createQuery("from VIACRole where name = :name").setParameter("name",
					r.getName());
			if (q.getResultList().isEmpty()) {
				session.save(r);
			}
		}
	}

	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create career if needed.
	 * @param careers to add
	 * @param database session
	 */
	private void createCareersIfMissing(List<VITACareer> careers, Session session) {
		for (VITACareer career : careers) {
			Query q = session.createQuery("from VITACareer where name = :name").setParameter("name", career.getName());
			if (q.getResultList().isEmpty()) {
				session.save(career);
			}
		}
	}

	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create applicant status if needed.
	 * @param applicant status to add
	 * @param database session
	 */
	private void createApplicantStatusIfMissing(List<VITAApplicantStatus> statusList, Session session) {
		for (VITAApplicantStatus status : statusList) {
			Query q = session.createQuery("from VITAApplicantStatus where name = :name").setParameter("name",
					status.getName());
			if (q.getResultList().isEmpty()) {
				session.save(status);
			}
		}
	}

	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create test status if needed.
	 * @param test status to add
	 * @param database session
	 */
	private void createTestStatusIfMissing(List<VITATestStatus> statusList, Session session) {
		for (VITATestStatus status : statusList) {
			Query q = session.createQuery("from VITATestStatus where name = :name").setParameter("name",
					status.getName());
			if (q.getResultList().isEmpty()) {
				session.save(status);
			}
		}
	}

	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create role authorization if needed.
	 * @param role authorization to add
	 * @param database session
	 */
	private void createAuthIfMissing(Set<VIACAuthorization> authorizations, Session session) {
		for (VIACAuthorization auth : authorizations) {
			Query q = session.createQuery("from VIACAuthorization where auth_name = :name").setParameter("name",
					auth.getName());
			if (q.getResultList().isEmpty()) {
				session.save(auth);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create template if needed.
	 * @param templates to add
	 * @param database session
	 */
	private void createTemplateIfMissing(List<MailTemplate> templates, Session session) {
		for (MailTemplate template : templates) {
			Query q = session.createQuery("from MailTemplate where mail_template_key = :key").setParameter("key",
					template.getKey());
			if (q.getResultList().isEmpty()) {
				session.save(template);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create units if needed.
	 * @param units to add
	 * @param database session
	 */
	private void createUnitsIfMissing(List<VIPPLearningUnit> units, Session session) {
		for (VIPPLearningUnit u : units) {
			Query q = session.createQuery("from VIPPLearningUnit where number = :number").setParameter("number",
					u.getNumber());
			if (q.getResultList().isEmpty()) {
				session.save(u);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create area if needed.
	 * @param areas to add
	 * @param database session
	 */
	private void createAreasIfMissing(List<VIPPSubjectArea> areas, Session session) {
		for (VIPPSubjectArea a : areas) {
			Query q = session.createQuery("from VIPPSubjectArea where name = :name").setParameter("name",
					a.getName());
			if (q.getResultList().isEmpty()) {
				session.save(a);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create subject if needed.
	 * @param subject to add
	 * @param database session
	 */
	private void createSubjectsIfMissing(List<VIPPSubject> subjects, Session session) {
		for (VIPPSubject s : subjects) {
			Query q = session.createQuery("from VIPPSubject where name = :name").setParameter("name",
					s.getName());
			if (q.getResultList().isEmpty()) {
				session.save(s);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create subject if needed.
	 * @param subject to add
	 * @param database session
	 */
	private void createPresentationCyclesIfMissing(List<VIPPPresentationCycle> presentationCycles, Session session) {
		for (VIPPPresentationCycle p : presentationCycles) {
			Query q = session.createQuery("from VIPPSubject where name = :name").setParameter("name",
					p.getName());
			if (q.getResultList().isEmpty()) {
				System.out.println("name: " + p.getName());
				session.save(p);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create subject if needed.
	 * @param subject to add
	 * @param database session
	 */
	private void createApprenticeshipAreasIfMissing(List<VIPPApprenticeshipArea> areas, Session session) {
		for (VIPPApprenticeshipArea a : areas) {
			Query q = session.createQuery("from VIPPApprenticeshipArea where name = :name").setParameter("name",
					a.getName());
			if (q.getResultList().isEmpty()) {
				session.save(a);
			}
		}
	}
	
	/**
	 * In order to create the needed entries in the database init is it necessary to add them only if not already existing.
	 *
	 * Create subject if needed.
	 * @param subject to add
	 * @param database session
	 */
	private void createApprenticesIfMissing(List<VIPPApprentice> areas, Session session) {
		for (VIPPApprentice a : areas) {
			Query q = session.createQuery("from VIPPApprentice where lastname = :lastname").setParameter("lastname",
					a.getLastname());
			if (q.getResultList().isEmpty()) {
				session.save(a);
			}
		}
	}

	/**
	 * In order to presend an already used system, check performance and have test data available 
	 * this function will provide the needed applicants and user.
	 */
	public void generateTestData() {
		int counter = 1;
		
		for (VITAApplicantStatus as : getAll(VITAApplicantStatus.class)) {
			for(int i = 0; i < 4; i++){
				String number;
				VITACareer career;
				String gender;
				
				if(counter < 10)
					number = "0" + counter;
				else
					number = String.valueOf(counter);
				
				if(i < 2)
					career = getAllActiveCareers().get(0);
				else
					career = getAllActiveCareers().get(1);
				
				if (i % 2 == 0)
					gender = "Männlich";
				else
					gender = "Weiblich";
				
				Address address = new Address();
				address.setCity("Testhausen");
				address.setPostalCode("1337" + counter);
				address.setStreet("Teststraße " + counter);
				
				VITAApplicant applicant = new VITAApplicant();
				applicant.setFirstname("Bewerber " + number);
				applicant.setLastname("Tester");
				applicant.setAddress(address);
				applicant.setAppliedOn(new Date());
				applicant.setBirthday(new Date());
				applicant.setCareer(career);
				applicant.setCreatedBy(getUserByName("admin@example.com"));
				applicant.setGender(gender);
				applicant.setMail("AppTest" + counter + "@example.com");
				applicant.setPhone("013371337" + counter);
				applicant.setStatus(as);
				
				create(applicant);
				
				counter++;
			}
		}
		
		for (int i = 0; i < 20; i++) {
			String number;
			if (i < 10) {
				number = "0" + i;
			} else {
				number = String.valueOf(i);
			}
			VIACUser user = new VIACUser("test" + number + "@example.com", "passwort", "Peter " + number,
					"Tester " + number, new Date(), getUserByName("admin@example.com"));
			try {
				createUser(user);
			} catch (UserAlreadyExistsException e) {
				System.out.println("User gibts schon!");
				continue;
			}
		}
	}

	/**
	 * Adds a new historie status for an applicant to the database.
	 * @param new Status
	 * @param applicant id
	 */
	public void createHistoricStatus(VITAApplicantStatus statusName, long id) {
	
		VITAHistoricStatus newHistoricStatus = new VITAHistoricStatus();
		VITAApplicant applicant = new VITAApplicant();
		applicant.setId(id);
		newHistoricStatus.setChangedOn(new Date());
		newHistoricStatus.setChangedFrom(applicant);
		newHistoricStatus.setName(statusName);

		DatabaseProvider.getInstance().create(newHistoricStatus);

	}
}
