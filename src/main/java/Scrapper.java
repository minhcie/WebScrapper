package src.main.java;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

public class Scrapper {
    private static final Logger log = Logger.getLogger(Scrapper.class);

    static void usage() {
        System.err.println("");
        System.err.println("usage: java -jar Scrapper.jar <auto or report_date (mm/dd/yyyy)>");
        System.err.println("");
        System.exit(-1);
    }

	public static void main(String[] args) {
        if (args.length == 0 || args.length < 1) {
            usage();
        }

		try {
			String rptDate = null;
			if (args[0].equalsIgnoreCase("auto")) {
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
				rptDate = args[0];
			}

			AccessReport rpt = new AccessReport();
			rpt.openCommOSWebsite();
			rpt.login();
			rpt.exportWizard(rptDate);
			rpt.closeCommOSWebsite();
		}
		catch (Exception e) {
			log.error(e.getMessage());
			e.printStackTrace();
		}
	}
}