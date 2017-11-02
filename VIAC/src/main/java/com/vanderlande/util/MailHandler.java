package com.vanderlande.util;


import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import microsoft.exchange.webservices.data.autodiscover.IAutodiscoverRedirectionUrl;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.FileAttachment;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

/**
 * @author dedhor
 * 
 * Mail Handler: Allows sending mails and creating appointments via Exchange.
 */
public class MailHandler implements Serializable{
    private static final long serialVersionUID = 1L;
    private static MailHandler instance;
	private static ExchangeService service;
	private static boolean service_is_started = false;

	/**
	 * Singelton implementation
	 * @return active MailHandler
	 */
	public static MailHandler getInstance() {
		if (instance == null)
			instance = new MailHandler();
		return instance;
	}

	
	/**
	 * Load needed informations from database and create a new session for late use.
	 */
	private MailHandler() {
		String username = DatabaseProvider.getInstance().getConfigByKey("mail_username").getValue();
		String password = DatabaseProvider.getInstance().getConfigByKey("mail_password").getValue();
		String exchangeURL = DatabaseProvider.getInstance().getConfigByKey("mail_exchange_url").getValue();
		boolean disabled = DatabaseProvider.getInstance().getConfigByKey("mail_disabled").getValue().equals("true");
		
		if (username.length() <= 0 || password.length() <= 0 || exchangeURL.length() <= 0 || disabled){
			service_is_started = false;
			return;
		}
		
		service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
		ExchangeCredentials credentials = new WebCredentials(username, password);
		service.setCredentials(credentials);
		
		try{
			service.setUrl(new URI(exchangeURL));
			service_is_started = true;
		}catch (Exception e){
			service_is_started = false;
			e.printStackTrace(System.out);
		}		
	}
	
	
	/**
	 * @author dedhor
	 * Overwrites the default domain search for exchange, this allows only https.
	 */
	static class RedirectionUrlCallback implements IAutodiscoverRedirectionUrl {
        public boolean autodiscoverRedirectionUrlValidationCallback(
                String redirectionUrl) {
            return redirectionUrl.toLowerCase().startsWith("https://");
        }
    }
		
	/**
	 * Send an email via exchange.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 * @param attachments attachments for mail
	 * @param send_cc mail cc receiver
	 */
	public void sendMail(String sendTo, String Header, String Text, List<MailAttachment> attachments, List<String> send_cc){	
		if (!service_is_started){
			System.out.println(sendTo);
			System.out.println(Header);
			System.out.println(Text);
			System.out.println("Der Exchange Service ist nicht gestartet, deshalb wurde nur diese Info ausgegeben!");
		}else{
			try{				
				EmailMessage message = new EmailMessage(service);				
				message.getToRecipients().add(sendTo);
				
				for (String cc: send_cc){
					message.getBccRecipients().add(cc);
				}
				
				message.setSubject(Header);
				message.setBody(new MessageBody(Text));
				
				for (MailAttachment attachment : attachments){
					FileAttachment fa = message.getAttachments().addFileAttachment(attachment.getName(), attachment.getContent());
					fa.setIsInline(attachment.isInline());
					fa.setContentType(attachment.getMimeType());
					fa.setContentId(attachment.getName());
				}
				
				message.sendAndSaveCopy();
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}
	
	
	/**
	 * Send an email via exchange without attachments or additional receivers.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 */
	public void sendMail(String sendTo, String Header, String Text){
		ArrayList<MailAttachment> al = new ArrayList<>();
		ArrayList<String> al2 = new ArrayList<>();
		sendMail(sendTo, Header, Text, al, al2);
	}
	
	/**
	 * Send an email via exchange without attachments.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 * @param send_cc mail cc receiver
	 */
	public void sendMail(String sendTo, String Header, String Text, List<String> sendCC){
		ArrayList<MailAttachment> al = new ArrayList<>();
		sendMail(sendTo, Header, Text, al, sendCC);
	}

	/**
	 * Send an appointment via exchange without additional receivers.
	 * @param AppointmentSubject appointment subject
	 * @param ApointmentText appointment text
	 * @param startDate appointment start date
	 * @param endDate appointment end date
	 * @return boolean was successfull
	 */
	public boolean createAppointment(String AppointmentSubject, String ApointmentText, Date startDate, Date endDate){
		ArrayList<String> tn = new ArrayList<String>();
		return createAppointment(AppointmentSubject, ApointmentText, startDate, endDate, tn);
	}
	
	/**
	 * Send an appointment via exchange.
	 * @param AppointmentSubject appointment subject
	 * @param ApointmentText appointment text
	 * @param startDate appointment start date
	 * @param endDate appointment end date
	 * @param Participants appointment participants
	 * @return boolean was successfull
	 */
	public boolean createAppointment(String AppointmentSubject, String AppointmentText, Date startDate, Date endDate, List<String> Participants){
		try{
			Appointment appointment = new  Appointment(service);
			appointment.setSubject(AppointmentSubject);
			appointment.setBody(MessageBody.getMessageBodyFromText(AppointmentText));
			appointment.setStart(startDate);
			appointment.setEnd(endDate);
			
			for (String TeilnehmerE : Participants){
				appointment.getRequiredAttendees().add(TeilnehmerE);
			}
			
			appointment.save();
		}catch(Exception e){
			e.printStackTrace(System.out);
			return false;
		}
		return true;
	}
}
