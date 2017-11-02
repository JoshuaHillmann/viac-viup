package com.vanderlande.vipp.attendees;

import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPSubject;

/**
 * The Class ApprenticeInfoWindowContent.
 * 
 * @author dermic
 * 
 * Used in {@link NotificationDialog}. Don't use it yourself.
 */
public class ApprenticeInfoWindowContent extends Panel
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5692237700953217611L;

    /**
     * Instantiates a new notification dialog content.
     *
     * @param contentId the content id
     * @param message the message
     */
    public ApprenticeInfoWindowContent(String contentId, VIPPApprentice apprentice)
    {
        super(contentId);
        
        List<VIPPSubject> subjects = apprentice.getPerformedPresentations();
        
        Label apprenticeLabel = new Label("apprenticeLabel", new Model<String>("Folgende Präsentationen hat " + apprentice.getFirstname() + 
        		" " + apprentice.getLastname() + " bereits gehalten:"));

        ListView subjectView = new ListView("subjectView", subjects){
			@Override
			protected void populateItem(org.apache.wicket.markup.html.list.ListItem item) {
				item.add(new Label("label", item.getModel()));
			}
        };
        
        add(apprenticeLabel);
        add(subjectView);
    }
}
