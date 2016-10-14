package src.main.java;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class Scrapper {
    private static final Logger log = Logger.getLogger(Scrapper.class);

    static void usage() {
        System.err.println("");
        System.err.println("usage: java -jar WebScrapper.jar <report name> <auto or report_date (mm/dd/yyyy)>");
        System.err.println("");
        System.exit(-1);
    }

	public static void main(String[] args) {
        if (args.length != 2) {
            usage();
        }

		try {
			String rptName = args[0].toLowerCase();
			String rptDate = null;
			if (args[1].equalsIgnoreCase("auto")) {
				// Default report date to yesterday.
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.DATE, -1);
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				rptDate = sdf.format(cal.getTime());
			}
			else {
				rptDate = args[1];
			}

			CommOSExportAbstract rpt = null;
			switch (rptName) {
				case "access":
					rpt = new AccessReport();
					break;
				default:
					break;
			}

			if (rpt != null) {
				rpt.openCommOSWebsite();
				rpt.login();
				rpt.exportWizard(rptDate);
				if (rpt.downloadReport(rptDate)) {
					rpt.extractData();
					rpt.convertCsv2Excel();
					rpt.copyData();
					rpt.importData();
				}
				rpt.closeCommOSWebsite();
			}
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}