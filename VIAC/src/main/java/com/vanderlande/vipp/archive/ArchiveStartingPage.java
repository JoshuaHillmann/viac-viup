package com.vanderlande.vipp.archive;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.util.DatabaseProvider;
import com.vanderlande.vipp.VIPPBasePage;
import com.vanderlande.vipp.attendees.PersonAdministrationPage;
import com.vanderlande.vipp.model.VIPPPresentationCycle;

public class ArchiveStartingPage extends VIPPBasePage implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3930686100957311346L;
	public ArchiveStartingPage()
	{
		init();
	}
	private void init()
	{
		Form<String> form = new Form<String>("presentationCycleForm");
		VIPPPresentationCycle presentationCycle = new VIPPPresentationCycle();
		List<VIPPPresentationCycle> presentationCycles = DatabaseProvider.getInstance().getAll(VIPPPresentationCycle.class);
		
		Label presentationCycleLabel = new Label("presentationCycleLabel", new Model<String>("Präsentationsdurchlauf wählen:"));
		final DropDownChoice<VIPPPresentationCycle> presentationCylceChoice = new DropDownChoice<VIPPPresentationCycle>("presentationCylces", new PropertyModel<VIPPPresentationCycle>(presentationCycle, "name"),
				presentationCycles);
		
		Button submitButton = new Button("submit")
		{
			@Override
		    public void onSubmit()
		    {
		        super.onSubmit();
		
		        setResponsePage(new ArchivePage(presentationCylceChoice.getConvertedInput()));
		    }
		};
		
		form.add(presentationCycleLabel);
		form.add(presentationCylceChoice);
		form.add(submitButton);
		add(form);
	}
}
