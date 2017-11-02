package com.vanderlande.vipp.planning;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPSubject;

public class DropdownPanelSubject extends Panel
{
	private static final long serialVersionUID = -6281537122167168057L;
	final List<VIPPSubject> subjectsAvailableAll;
	final DropDownChoice<VIPPSubject> subjectChoice;
	VIPPApprentice apprentice;
	
    public DropdownPanelSubject(String id, List<VIPPSubject> subjectsAvailableAll, VIPPApprentice apprentice)
    {
        super(id);
        this.subjectsAvailableAll = subjectsAvailableAll;
        this.apprentice = apprentice;
		subjectChoice = new DropDownChoice<VIPPSubject>("subjects",new Model<VIPPSubject>(this.apprentice.getAssignedSubject()), subjectsAvailableAll);
		add(subjectChoice);
	}
    public VIPPSubject getDropdownChoice()
    {
    	return subjectChoice.getConvertedInput();
    }
}
