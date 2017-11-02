package com.vanderlande.vita.employmenttest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.NavigationToolbar;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.yui.calendar.DateTimeField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.DownloadLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

import com.vanderlande.viac.controls.VIACDownloadActionPanel;
import com.vanderlande.vita.VITABasePage;
import com.vanderlande.vita.employmenttest.provider.TestApplicantsDataProvider;
import com.vanderlande.vita.model.VITAApplicant;
import com.vanderlande.vita.model.VITADocument;
import com.vanderlande.vita.model.VITAEligoTest;

/**
 * The Class EmploymentTestSummaryPage.
 * 
 * @author desczu
 * 
 *         This class displays a summary of a finished/cancelled test.
 */
public class EmploymentTestSummaryPage extends VITABasePage
{
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 3244971788752408574L;

    /** The test group. */
    private VITAEligoTest testGroup;

    /** The test date. */
    private DateTimeField testDate;

    /** The test room. */
    private TextField<String> testRoom;

    /** The test name. */
    private TextField<String> testName;

    /** The test applicants data provider. */
    private TestApplicantsDataProvider testApplicantsDataProvider;

    /** The test applicants table. */
    private DataTable<VITAApplicant, String> testApplicantsTable;

    /** The back button. */
    private Link<String> backButton;

    /**
     * Instantiates a new employment test summary page.
     *
     * @param testGroup
     *            the test group
     */
    public EmploymentTestSummaryPage(VITAEligoTest testGroup)
    {
        this.testGroup = testGroup;
        init();
    }

    /**
     * Inits the EmploymentTestSummaryPage.
     */
    private void init()
    {
        testApplicantsDataProvider = new TestApplicantsDataProvider(testGroup);

        // initialize fields
        testName = new TextField<String>("testName", new PropertyModel<String>(testGroup, "name"));
        testRoom = new TextField<String>("testRoom", new PropertyModel<String>(testGroup, "room"));
        testDate = new DateTimeField("testDate", new PropertyModel<Date>(testGroup, "date"));

        // columns of applicant table
        List<IColumn<VITAApplicant, String>> testApplicantsColumns = new ArrayList<>();
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Vorname"), "firstname", "firstname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Nachname"), "lastname", "lastname"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Beworben am"), "appliedOn", "appliedOn"));
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Fachrichtung"), "career.name", "career.name"));

        testApplicantsColumns.add(new AbstractColumn<VITAApplicant, String>(new Model<>("Eligo-Testergebnis"))
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 4064882913812147729L;

            @Override
            public void populateItem(Item<ICellPopulator<VITAApplicant>> cellItem, String componentId,
                                     final IModel<VITAApplicant> rowModel)
            {
                // find the eligo-document of the applicant and - if found -
                // show a downloadlink and a delete-button
                // for this document
                Set<VITADocument> documents = rowModel.getObject().getDocuments();
                VITADocument eligoTestDocumentTemp = null;
                for (final VITADocument vitaDocument : documents)
                {
                    if (vitaDocument.isEligo())
                    {
                        eligoTestDocumentTemp = vitaDocument;
                    }
                }
                ;
                final VITADocument eligoTestDocument = eligoTestDocumentTemp;
                final DownloadLink downloadLink;
                if (eligoTestDocument != null)
                {
                    downloadLink = new DownloadLink("download", new LoadableDetachableModel<File>()
                    {
                        /** The Constant serialVersionUID. */
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected File load()
                        {
                            final File file = new File(eligoTestDocument.getFileName());
                            try
                            {
                                FileUtils.writeByteArrayToFile(
                                    new File(eligoTestDocument.getFileName()),
                                    eligoTestDocument.getData());
                                return file;
                            }
                            catch (IOException e)
                            {
                                return null;
                            }
                        }
                    });

                    Label fileName = new Label("navItemLabel", eligoTestDocument.getFileName());
                    downloadLink.add(fileName);

                    AjaxLink deleteLink = new AjaxLink("deleteLink")
                    {
                        @Override
                        public void onClick(AjaxRequestTarget target)
                        {
                        }
                    };
                    deleteLink.setVisible(false);
                    cellItem.add(new VIACDownloadActionPanel<>(componentId, rowModel, downloadLink, deleteLink));
                }
                else
                {
                    cellItem.add(new Label(componentId, ""));
                }
            }
        });
        testApplicantsColumns
            .add(new PropertyColumn<VITAApplicant, String>(new Model<>("Status"), "status.name", "status.name"));

        // applicants table
        testApplicantsTable = new DataTable<VITAApplicant, String>(
            "testApplicantsTable",
            testApplicantsColumns,
            testApplicantsDataProvider,
            10);

        testApplicantsTable.setOutputMarkupId(true);
        testApplicantsTable.addTopToolbar(new NavigationToolbar(testApplicantsTable));
        testApplicantsTable.addTopToolbar(new HeadersToolbar<>(testApplicantsTable, testApplicantsDataProvider));
        testApplicantsTable.addBottomToolbar(new NavigationToolbar(testApplicantsTable));

        // back button
        backButton = new Link<String>("back")
        {
            /** The Constant serialVersionUID. */
            private static final long serialVersionUID = 1L;

            @Override
            public void onClick()
            {
                setResponsePage(new EmploymentTestOverviewPage());
            }
        };
        testName.setEnabled(false);
        testDate.setEnabled(false);
        testRoom.setEnabled(false);

        // add all components to form
        add(testName);
        add(testRoom);
        add(testDate);
        add(testApplicantsTable);
        add(backButton);
    }
}
