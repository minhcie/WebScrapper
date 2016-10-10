package src.main.java;

import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.WebElement;

import org.apache.log4j.Logger;

public class AccessReport extends CommOSExportAbstract {
    private static final Logger log = Logger.getLogger(AccessReport.class);

    public AccessReport() {
		super();
    }

	/**
	 * Select report template.
	 * 
	 * @throws Exception
	 */
	public void step1() throws Exception {
		log.info("Step 1: select report template");
		WebElement jobname_editbox = super.driver.findElement(By.id("jobname"));
		Select template_dropdown = new Select(super.driver.findElement(By.id("template_agency")));
		WebElement save_checkbox = super.driver.findElement(By.id("saved"));
		WebElement continue_button = super.driver.findElement(By.xpath("//input[@value='Continue']"));

		jobname_editbox.sendKeys("Access");
		template_dropdown.selectByVisibleText("ACCESS");
		if (save_checkbox.isSelected()) {
			save_checkbox.click(); // Uncheck.
		}
		continue_button.click();
		super.fluentWait(By.xpath("//input[@value='Next Step']"));
	}

	/**
	 * Select report fields.
	 * 
	 * @throws Exception
	 */
	public void step2() throws Exception {
		log.info("Step 2: select report fields");
		WebElement next_button = super.driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		super.fluentWait(By.xpath("//input[@value='Next Step']"));
	}

	/**
	 * Columns order.
	 * 
	 * @throws Exception
	 */
	public void step3() throws Exception {
		log.info("Step 3: columns order");
		WebElement next_button = super.driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		super.fluentWait(By.id("criteria.field.1.1"));
	}

	/**
	 * Set report criterias
	 * 
	 * @throws Exception
	 */
	public void step4(String rptDate) throws Exception {
		log.info("Step 4: set report criterias");
		Select field_1_dropdown = new Select(super.driver.findElement(By.id("criteria.field.1.1")));
		field_1_dropdown.selectByVisibleText("Contact: Create Date/Time");
		Select criteria_1_dropdown = new Select(super.driver.findElement(By.id("criteria.criteria.1.1")));
		criteria_1_dropdown.selectByVisibleText("equal to");
		WebElement value_1_editbox = super.driver.findElement(By.id("criteria.value.1.1"));
		value_1_editbox.clear();
		value_1_editbox.sendKeys(rptDate);

		Select field_2_dropdown = new Select(super.driver.findElement(By.id("criteria.field.1.2")));
		field_2_dropdown.selectByVisibleText("Portal Restrictions");
		Select criteria_2_dropdown = new Select(super.driver.findElement(By.id("criteria.criteria.1.2")));
		criteria_2_dropdown.selectByVisibleText("equals");
		WebElement value_2_editbox = super.driver.findElement(By.id("criteria.value.1.2"));
		value_2_editbox.sendKeys("211 access");
		//Select value_2_dropdown = new Select(super.driver.findElement(By.id("criteria.value.1.2")));
		//value_2_dropdown.selectByVisibleText("211 access");

		Select field_3_dropdown = new Select(super.driver.findElement(By.id("criteria.field.1.3")));
		field_3_dropdown.selectByVisibleText("-");
		WebElement next_button = super.driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		super.fluentWait(By.xpath("//input[@value='Export']"));
	}

	/**
	 * Other report options.
	 * 
	 * @throws Exception
	 */
	public void step5() throws Exception {
		log.info("Step 5: other report options");
		WebElement export_button = super.driver.findElement(By.xpath("//input[@value='Export']"));
		export_button.click();
		super.fluentWait(By.xpath("//input[@value='Export Options']"));
	}

	/**
	 * Download and save report zip file.
	 * 
	 * @throws Exception
	 */
	public void downloadReport(String rptDate) throws Exception {
		String rptName = "access_" + rptDate.replaceAll("/", "") + ".zip";
		super.downloadWait(rptName);
	}

	/**
	 * Convert CSV file to Excel file.
	 * 
	 * @throws Exception
	 */
	public void convertCsv2Excel() throws Exception {
		String excelName = "access.xlsx";
		super.csv2Excel(excelName);
	}

	/**
	 * Copy report (Excel) data to remote server.
	 * 
	 * @throws Exception
	 */
	public void copyData() throws Exception {
		String excelName = "access.xlsx";
		super.copy2Server(excelName);
	}

	/**
	 * Import report data into SQL server.
	 * 
	 * @throws Exception
	 */
	public void importData() throws Exception {
		String scriptName = "import_access.sql";
		super.import2SqlServer(scriptName);
	}
}