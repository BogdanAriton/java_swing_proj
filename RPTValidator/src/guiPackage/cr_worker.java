package guiPackage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

import org.apache.log4j.Logger;

import com.crystaldecisions.sdk.occa.report.application.ISubreportClientDocument;
import com.crystaldecisions.sdk.occa.report.application.OpenReportOptions;
import com.crystaldecisions.sdk.occa.report.application.ReportClientDocument;
import com.crystaldecisions.sdk.occa.report.data.FieldValueType;
import com.crystaldecisions.sdk.occa.report.data.ParameterField;
import com.crystaldecisions.sdk.occa.report.data.Tables;
import com.crystaldecisions.sdk.occa.report.definition.ISubreportLink;
import com.crystaldecisions.sdk.occa.report.definition.SubreportLinks;
import com.crystaldecisions.sdk.occa.report.lib.IStrings;
import com.crystaldecisions.sdk.occa.report.lib.PropertyBag;
import com.crystaldecisions.sdk.occa.report.lib.ReportSDKException;

import generateReportPackage.ErrorHandle;
import generateReportPackage.GenerateReportImpl;

public class cr_worker {

	static final Logger logger = Logger.getLogger(cr_tester.class);
	/**
	 * Currently to get stuff for RPTValidator
	 * 
	 * @param reportFileName
	 * @param logggingLocation
	 * @return String[]
	 */
	public String[] SwingWorker(String reportFileName, String logggingLocation) {
		String[] localstatus = new String[5];
		localstatus[0] = ""; // Unmmaped params
		localstatus[1] = "0"; // out fail or success (1/0)
		localstatus[2] = ""; // Provider types
		localstatus[3] = ""; // non-string type params
		localstatus[4] = ""; // sp names
		int unmapped = 0;
		ReportClientDocument reportClientDoc = new ReportClientDocument();
		GenerateReportImpl generateReport = new GenerateReportImpl();

		try {
			// ===========================================================================
			// Set logger
        	CreateLogs(logggingLocation+ "/oms-reporting.log");
        	CreateLogs(logggingLocation+ "/sap-reporting.log");
			generateReport.setLogger(logggingLocation + "/oms-reporting.log", logggingLocation + "/sap-reporting.log", "DEBUG",
					"ERROR");
			// ===========================================================================
			// Open the report
			logger.info("Opening report: " + reportFileName);
			reportClientDoc.open(reportFileName, OpenReportOptions._discardSavedData);
			// logDatabaseInfo(reportClientDoc.getDatabaseController());
			Tables tables = reportClientDoc.getDatabaseController().getDatabase().getTables();
			for (int ii = 0; ii < tables.size(); ii++) {
				PropertyBag bag = tables.get(ii).getConnectionInfo().getAttributes();
				localstatus[2] = localstatus[2] + "mainReport-" + reportClientDoc.displayName() + ": "
						+ bag.get("Server Type") + ", Provider: " + bag.get("Provider") + ";";
			}
			
			if (!reportClientDoc.getDatabaseController().getDatabase().getTables().isEmpty()) {
				logger.debug("Getting main sp name: ");
				localstatus[4] = localstatus[4] + "main SP: " + reportClientDoc.getDatabaseController().getDatabase().getTables().get(0).getName();	
				localstatus[4] = localstatus[4] + "; ";
			}

			logger.info("Report Opened");
			IStrings subreportNames = reportClientDoc.getSubreportController().getSubreportNames();
			// Set the data source for all the sub reports.
			logger.debug("Subreports:");
			for (int i = 0; i < subreportNames.size(); i++) {
				logger.debug("Subreport: " + subreportNames.getString(i));
				ISubreportClientDocument subreportClientDoc = reportClientDoc.getSubreportController()
						.getSubreport(subreportNames.getString(i));
				
				// Adding Sub Reports SP Name
				if (!subreportClientDoc.getDatabaseController().getDatabase().getTables().isEmpty()) {
					localstatus[4] = localstatus[4] + "Subreport (" + subreportNames.getString(i) + ") SP: "
							+ subreportClientDoc.getDatabaseController().getDatabase().getTables().get(0).getName();
					localstatus[4] = localstatus[4] + "; ";
				}
				
				SubreportLinks links = reportClientDoc.getSubreportController()
						.getSubreportLinks(subreportNames.getString(i));
				for (Object field : subreportClientDoc.getDataDefController().getDataDefinition()
						.getParameterFields()) {
					ParameterField fd = (ParameterField) field;
					boolean found = false;
					for (ISubreportLink link : links) {
						if (fd.getName().contentEquals(link.getLinkedParameterName())) {
							logger.debug(
									"Parameter: " + fd.getName() + " is linked to: " + link.getMainReportFieldName());
							// we found the parameter in the links so we can
							// brake out of this iteration
							// we should add it to the list of parameters linked
							found = true;
						}
					}
					if (found == false) { // we couldn't find the parameter in
											// the links list
						unmapped = unmapped + 1;
						localstatus[0] = localstatus[0] + "; subreport: " + subreportNames.getString(i) + ", param: "
								+ fd.getName();
						logger.debug("Parameter: " + fd.getName() + " is not linked.");
					}
				}

				tables = subreportClientDoc.getDatabaseController().getDatabase().getTables();
				for (int ii = 0; ii < tables.size(); ii++) {
					PropertyBag bag = tables.get(ii).getConnectionInfo().getAttributes();
					localstatus[2] = localstatus[2] + " subReport-" + subreportNames.getString(i) + ": "
							+ bag.get("Server Type") + ", Provider: " + bag.get("Provider") + ";";
				}
			}
						
			localstatus[0] = "count: " + unmapped + localstatus[0];
			localstatus[1] = "0";

			//=======================
			// Get DB Param Types
			String parameterName = "";
			for (Object field : reportClientDoc.getDataDefController().getDataDefinition().getParameterFields()) {
				ParameterField fd = (ParameterField) field;
				FieldValueType fdType = fd.getType(); //xsd:string anything but
				parameterName = fd.getShortName(Locale.ENGLISH).substring(1);
				if (!fdType.toString().equalsIgnoreCase("xsd:string")){
					localstatus[3] = localstatus[3] + "ParamName=[" + parameterName + "] Type=[" + fdType.toString() + "]; ";
				}
			}
			
			
			// ===========================================================================
		} catch (ReportSDKException rse) {
			localstatus = ErrorHandle.checkForKnownIssues(rse);
		} catch (Exception e) {
			// Catch random error
			localstatus[0] = "Unknown exception found: " + e.getMessage();
			localstatus[1] = Integer.toString(-1);
			logger.error(localstatus[0]);
		} finally {
			try {
				if (reportClientDoc.isOpen()) {
					logger.info("Closing report: " + reportFileName);
					reportClientDoc.close();
				}
			} catch (ReportSDKException e) {
				localstatus = ErrorHandle.checkForKnownIssues(e);
			}
			logger.info("Report was closed: " + reportFileName);
		}
		return localstatus;
	}	
	
	public static void CreateLogs(String location) {
		try {
			new File(location).createNewFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
