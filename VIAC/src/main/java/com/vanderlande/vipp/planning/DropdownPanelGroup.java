package com.vanderlande.vipp.planning;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import com.vanderlande.vipp.model.VIPPApprentice;
import com.vanderlande.vipp.model.VIPPGroup;

public class DropdownPanelGroup extends Panel
{
	private static final long serialVersionUID = 6301905587101582089L;
	final List<VIPPGroup> groups;
	final DropDownChoice<VIPPGroup> groupChoice;
	private VIPPGroup selectedGroup;
	final VIPPGroup group;
	final VIPPApprentice apprentice;
	
    public DropdownPanelGroup(String id, List<VIPPGroup> groups, VIPPApprentice apprentice)
    {
        super(id);
        this.apprentice = apprentice;
        this.groups = groups;
        this.group = apprentice.getCurrentGroup();
		groupChoice = new DropDownChoice<VIPPGroup>("groups",new Model<VIPPGroup>(this.group), groups);
		add(groupChoice);
	}
    public VIPPGroup getDropdownChoice()
    {
    	return groupChoice.getConvertedInput();
    }
	public VIPPGroup getSelectedGroup() {
		return selectedGroup;
	}
	public void setSelectedGroup(VIPPGroup selectedGroup) {
		this.selectedGroup = selectedGroup;
	}
}
