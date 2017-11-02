package com.vanderlande.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.*;
import javax.mail.*;
import javax.mail.Message.*;
import javax.mail.internet.*;

import com.vanderlande.viac.model.MailDocument;
import com.vanderlande.viac.model.MailTemplate;

/**
 * @author dedhor
 * Mail HandlerSMTP: Allows sending mails via smtp protocol.
 */
public class MailHandlerSMTP {
	private static MailHandlerSMTP instance;
	private String mailFrom, mailHost;
	private boolean isDisabled = true;
	private Session session;
	
	
	/**
	 * Singelton implementation
	 * @return active MailHandlerSMTP
	 */
	public static MailHandlerSMTP getInstance() {
		if (instance == null)
			instance = new MailHandlerSMTP();
		return instance;
	}	
	
	/**
	 * Load needed informations from database and provide session with default properties.
	 */
	private MailHandlerSMTP() {
		mailFrom = DatabaseProvider.getInstance().getConfigByKey("mail_username").getValue();
		mailHost = DatabaseProvider.getInstance().getConfigByKey("mail_host").getValue();
		isDisabled = DatabaseProvider.getInstance().getConfigByKey("mail_disabled").getValue().equals("true");
		
		if (mailFrom.length() <= 0 || mailHost.length() <= 0 || isDisabled){
			isDisabled = true;
			return;
		}
		
		Properties properties = System.getProperties();
	    properties.setProperty("mail.smtp.host", mailHost);
		session = Session.getDefaultInstance(properties);
	}
	
	/**
	 * Send an email via smtp.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 * @param attachments attachments for mail
	 * @param send_cc mail cc receiver
	 * @param send_bcc mail bcc receiver
	 */
	public void sendMail(String sendTo, String Header, String Text, List<MailAttachment> attachments, List<String> send_cc, List<String> send_bcc){	
		if (isDisabled){
			System.out.println(sendTo);
			System.out.println(Header);
			System.out.println(Text);
			System.out.println("Der SMTP Service ist nicht gestartet, deshalb wurde nur diese Info ausgegeben!");
		}else{
			try{				
				MimeMessage message = new MimeMessage(session);
				message.setFrom(mailFrom);
				
				message.addRecipient(RecipientType.TO, new InternetAddress(sendTo));
				
				for(String email: send_cc){
					message.addRecipient(RecipientType.CC, new InternetAddress(email));
				}
				
				for (String email: send_bcc){
					message.addRecipient(RecipientType.BCC, new InternetAddress(email));
				}
				
				message.setSubject(Header);
				
				//Multipart will contain all the content html as well as attachments
				Multipart multipart = new MimeMultipart();
				//messageBodyPart will either contain the html content or an attachment
				BodyPart messageBodyPart = new MimeBodyPart();
				
				
				//set the content
				messageBodyPart.setContent(Text, "text/html");
				multipart.addBodyPart(messageBodyPart);				
				
				//add the attachments
				for (MailAttachment att: attachments){
					messageBodyPart = new MimeBodyPart();
			        messageBodyPart.setDataHandler(new DataHandler(new FileDataSource(att.getName())));			        
			        messageBodyPart.setFileName(att.getName());
			        multipart.addBodyPart(messageBodyPart);
				}
				
				message.setContent(multipart);
				
				Transport.send(message);				
				
				System.out.println("Message send: " + Text);
			}catch(Exception e){
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	 * Send an email via smtp without attachments and additional receivers.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 */
	public void sendMail(String sendTo, String Header, String Text){
		ArrayList<MailAttachment> al = new ArrayList<>();
		ArrayList<String> al2 = new ArrayList<>();
		ArrayList<String> al3 = new ArrayList<>();
		sendMail(sendTo, Header, Text, al, al2, al3);
	}

	/**
	 * Send an email via smtp without attachments.
	 * @param sendTo mail receiver
	 * @param Header mail subject
	 * @param Text html mail text
	 * @param send_cc mail cc receiver
	 * @param send_bcc mail bcc receiver
	 */
	public void sendMail(String sendTo, String Header, String Text, List<String> send_bcc){
		ArrayList<MailAttachment> al = new ArrayList<>();
		ArrayList<String> al2 = new ArrayList<>();
		sendMail(sendTo, Header, Text, al, al2, send_bcc);
	}

	/**
	 * In order to send a message based on a template this function will read the attachments,
	 * cc and bcc from the given input and call the actual send mail function.
	 * @param String send to mail
	 * @param MailTemplate mail subject
	 * @param MailTemplate mail text
	 * @param String 
	 */
	public void sendMail(String sendTo, MailTemplate headerTemplate, MailTemplate textTemplate, String mailText){
		String headerText = headerTemplate.getMailText();
		List<MailAttachment> attachments = new ArrayList<>();
		
		for(MailDocument md : textTemplate.getDocuments()){
			attachments.add(new MailAttachment(md.getFileName(), md.getData()));
		}
		
		List<String> ccList = new ArrayList<>();        
        if(textTemplate.getCarbonCopy() != null){
        	String[] ccArray = textTemplate.getCarbonCopy().split(";");
            for(int i = 0; i < ccArray.length; i++){
            	String s = ccArray[i];
            	ccList.add(s);
            }
        }
        
        List<String> bccList = new ArrayList<>();        
        if(textTemplate.getBlindCarbonCopy() != null){
        	String[] ccArray = textTemplate.getBlindCarbonCopy().split(";");
            for(int i = 0; i < ccArray.length; i++){
            	String s = ccArray[i];
            	bccList.add(s);
            }
        }
        
        sendMail(sendTo, headerText, mailText, attachments, ccList, bccList);
	}
}
