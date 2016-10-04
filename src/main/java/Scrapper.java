package src.main.java;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import org.openqa.selenium.By;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Scrapper {

    private static final Logger LOG = Logger.getLogger(Scrapper.class);
	private static WebDriver driver = null;

	public static void main(String[] args) {
		try {
			// Initialize WebDriver for Chrome.
			String workingDir = System.getProperty("user.dir");
			System.setProperty("webdriver.chrome.driver", workingDir + "/chromedriver.exe");

			// Set default download folder for Chrome.
			String downloadFilepath = "C:\\Downloads";
			HashMap<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put("profile.default_content_settings.popups", 0);
			chromePrefs.put("download.default_directory", downloadFilepath);
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("prefs", chromePrefs);
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
			cap.setCapability(ChromeOptions.CAPABILITY, options);
			driver = new ChromeDriver(cap);

			// Execute Community OS export wizard.
			openCommOSWebsite();
			login("mtran@211sandiego.org", "M1nh@2112");
			exportWizard();
			closeCommOSWebsite();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Open Community OS website.
	 * 
	 * @throws Exception
	 */
	private static void openCommOSWebsite() throws Exception {
		driver.navigate().to("https://211sandiego.communityos.org/zf/auth/logout");
	}

	/**
	 * Logins into the website, by entering provided username and password.
	 * 
	 * @param username
	 * @param Password
	 * 
	 * @throws Exception
	 */
	private static void login(String username, String password) throws Exception {
		WebElement userName_editbox = driver.findElement(By.id("username"));
		WebElement password_editbox = driver.findElement(By.id("password"));
		WebElement submit_button = driver.findElement(By.xpath("//input[@value='Login']"));

		userName_editbox.sendKeys(username);
		password_editbox.sendKeys(password);
		submit_button.click();
	}

	/**
	 * Export wizard.
	 * 
	 * @throws Exception
	 */
	private static void exportWizard() throws Exception {
		driver.navigate().to("https://211sandiego.communityos.org/zf/exportwizard");
		fluentWait(By.id("jobname"));

		// Step 1.
		WebElement jobname_editbox = driver.findElement(By.id("jobname"));
		Select template_dropdown = new Select(driver.findElement(By.id("template_agency")));
		WebElement save_checkbox = driver.findElement(By.id("saved"));
		WebElement continue_button = driver.findElement(By.xpath("//input[@value='Continue']"));

		jobname_editbox.sendKeys("Access");
		template_dropdown.selectByVisibleText("ACCESS");
		if (save_checkbox.isSelected()) {
			save_checkbox.click(); // Uncheck.
		}
		continue_button.click();
		fluentWait(By.xpath("//input[@value='Next Step']"));

		// Step 2 - select fields.
		WebElement next_button = driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		fluentWait(By.xpath("//input[@value='Next Step']"));

		// Step 3 - columns order.
		next_button = driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		fluentWait(By.id("criteria.field.1.1"));

		// Step 4 - report criteria.
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -1); // Yesterday.
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

		Select field_1_dropdown = new Select(driver.findElement(By.id("criteria.field.1.1")));
		field_1_dropdown.selectByVisibleText("Contact: Create Date/Time");
		Select criteria_1_dropdown = new Select(driver.findElement(By.id("criteria.criteria.1.1")));
		criteria_1_dropdown.selectByVisibleText("equal to");
		WebElement value_1_editbox = driver.findElement(By.id("criteria.value.1.1"));
		value_1_editbox.clear();
		value_1_editbox.sendKeys(sdf.format(cal.getTime()));

		Select field_2_dropdown = new Select(driver.findElement(By.id("criteria.field.1.2")));
		field_2_dropdown.selectByVisibleText("Portal Restrictions");
		Select criteria_2_dropdown = new Select(driver.findElement(By.id("criteria.criteria.1.2")));
		criteria_2_dropdown.selectByVisibleText("equals");
		WebElement value_2_editbox = driver.findElement(By.id("criteria.value.1.2"));
		value_2_editbox.sendKeys("211 access");
		//Select value_2_dropdown = new Select(driver.findElement(By.id("criteria.value.1.2")));
		//value_2_dropdown.selectByVisibleText("211 access");

		Select field_3_dropdown = new Select(driver.findElement(By.id("criteria.field.1.3")));
		field_3_dropdown.selectByVisibleText("-");
		next_button = driver.findElement(By.xpath("//input[@value='Next Step']"));
		next_button.click();
		fluentWait(By.xpath("//input[@value='Export']"));

		// Step 5 - report options.
		WebElement export_button = driver.findElement(By.xpath("//input[@value='Export']"));
		export_button.click();
		fluentWait(By.xpath("//input[@value='Export Options']"));

		// Wait for report data to generate.
		String url = driver.getCurrentUrl();

		boolean rptFound = false;
		WebElement downloadLink = null;
		driver.get(url);
		while (!rptFound) {
			List<WebElement> elements = driver.findElements(By.tagName("a"));
			if (elements != null) {
				for (WebElement ele : elements) {
					String str = ele.getText();
					if (str.equalsIgnoreCase("fb_export.csv.zip")) {
						rptFound = true;
						downloadLink = ele;
						break;
					}
				}
			}

			if (!rptFound) {
				Thread.sleep(15000); // 15 seconds.
				driver.navigate().refresh();
			}
		}

		// Download report data.
		if (rptFound && downloadLink != null) {
			downloadLink.click();

			// Wait until download completed.
			File f = new File("C:\\Downloads\\fb_export.csv.zip");
			while (!f.exists()) {
				Thread.sleep(3000);
			}
		}
	}

	/**
	 * Wait 15 seconds for an element to be present on the page, check for its
	 * present once every 3 seconds.
	 */
	private static WebElement fluentWait(final By locator) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(15, TimeUnit.SECONDS)
																.pollingEvery(3, TimeUnit.SECONDS)
																.ignoring(NoSuchElementException.class);

		WebElement ele = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		});
		return ele;
	}; 

	private static void closeCommOSWebsite() throws Exception {
		driver.close();
	}

	/**
	 * Grabs the status text and saves that into status.txt file.
	 * 
	 * @throws IOException
	 */
	private static void getText() throws IOException {
		String text = driver.findElement(By.xpath("//div[@id='case_login']/h3")).getText();
		Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("status.txt"), "utf-8"));
		writer.write(text);
		writer.close();
	}

	/**
	 * Saves the screenshot.
	 * 
	 * @throws IOException
	 */
	private static void saveScreenshot(String fileName) throws IOException {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(fileName));
	}
}