package com.vanderlande.vipp.subject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.vanderlande.viac.dialog.NotificationDialog;
import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPSubject;

/**
 * The Class SubjectInfoWindowContent.
 * 
 * @author dermic
 * 
 * Used in {@link NotificationDialog}. Don't use it yourself.
 */
public class SubjectInfoWindowContent extends Panel
{
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -5692237700953217611L;

    /**
     * Instantiates a new notification dialog content.
     *
     * @param contentId the content id
     * @param message the message
     */
    public SubjectInfoWindowContent(String contentId, VIPPSubject subject)
    {
        super(contentId);
        
        Set<VIPPApprentice> apprentices = subject.getApprentices();
        List<VIPPApprentice> apprenticesList = new ArrayList<VIPPApprentice>();
        for(VIPPApprentice apprentice : apprentices)
        {
        	apprenticesList.add(apprentice);
        }
        
        Label subjectLabel = new Label("subjectLabel", new Model<String>("Folgende Azubis haben " + subject.getName() + " bereits gehalten:"));

        ListView apprenticeView = new ListView("apprenticesView", apprenticesList){
			@Override
			protected void populateItem(org.apache.wicket.markup.html.list.ListItem item) {
				item.add(new Label("label", item.getModel()));
			}
        };
        
        add(subjectLabel);
        add(apprenticeView);
    }
}
