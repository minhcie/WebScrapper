package src.main.java;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Function;

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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public abstract class CommOSExportAbstract {
    private static final Logger log = Logger.getLogger(CommOSExportAbstract.class);
    protected WebDriver driver = null;

    public CommOSExportAbstract() {
		// Initialize WebDriver for Chrome.
		log.info("Initializing WebDriver for Chrome");
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
		this.driver = new ChromeDriver(cap);
    }

	/**
	 * Open CommunityOS website.
	 * 
	 * @throws Exception
	 */
	public void openCommOSWebsite() throws Exception {
		log.info("Opening CommunityOS website");
		this.driver.navigate().to("https://211sandiego.communityos.org/zf/auth/logout");
	}

	/**
	 * Logins into the website, by entering provided username and password.
	 * 
	 * @throws Exception
	 */
	public void login() throws Exception {
		log.info("Signing in CommunityOS website");
		WebElement userName_editbox = this.driver.findElement(By.id("username"));
		WebElement password_editbox = this.driver.findElement(By.id("password"));
		WebElement submit_button = this.driver.findElement(By.xpath("//input[@value='Login']"));

		userName_editbox.sendKeys("mtran@211sandiego.org");
		password_editbox.sendKeys("M1nh@2112");
		submit_button.click();
	}

	/**
	 * Close CommunityOS website and kill webdriver instance.
	 * 
	 * @throws Exception
	 */
	public void closeCommOSWebsite() throws Exception {
		log.info("Closing CommunityOS website, kill Chrome WebDriver");
		this.driver.quit(); // close() doesn't kill the instance.
	}

	/**
	 * CommunityOS export wizard steps.
	 * 
	 * @throws Exception
	 */
	public void exportWizard(String rptDate) throws Exception {
		// Navigate to export wizard.
		log.info("Opening CommunityOS exeport wizard");
		this.driver.navigate().to("https://211sandiego.communityos.org/zf/exportwizard");
		fluentWait(By.id("jobname"));

		// Step 1 - select report template.
		step1();

		// Step 2 - select fields.
		step2();

		// Step 3 - columns order.
		step3();

		// Step 4 - report criterias.
		step4(rptDate);

		// Step 5 - report options.
		step5();

		// Download and save report zip file.
		downloadReport(rptDate);
	}

	/**
	 * CommunityOS export wizard abstract method for each step.
	 * 
	 * @throws Exception
	 */
	public abstract void step1() throws Exception;
	public abstract void step2() throws Exception;
	public abstract void step3() throws Exception;
	public abstract void step4(String rptDate) throws Exception;
	public abstract void step5() throws Exception;
	public abstract void downloadReport(String rptDate) throws Exception;

	/**
	 * Wait 15 seconds for an element to be present on the page, check for its
	 * present once every 3 seconds.
	 */
	protected WebElement fluentWait(final By locator) {
		Wait<WebDriver> wait = new FluentWait<WebDriver>(this.driver)
								   .withTimeout(15, TimeUnit.SECONDS)
								   .pollingEvery(3, TimeUnit.SECONDS)
								   .ignoring(NoSuchElementException.class);

		WebElement ele = wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(locator);
			}
		});
		return ele;
	};

	/**
	 * Grabs the status text and saves that into status.txt file.
	 * 
	 * @throws IOException
	 */
	protected void getText() throws IOException {
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
	protected void saveScreenshot(String fileName) throws IOException {
		File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
		FileUtils.copyFile(scrFile, new File(fileName));
	}

	protected void downloadWait(String rptName) throws Exception {
		// Wait for report data to generate.
		log.info("Waiting for report zip file");
		String url = this.driver.getCurrentUrl();

		boolean rptFound = false;
		WebElement downloadLink = null;
		this.driver.get(url);
		while (!rptFound) {
			List<WebElement> elements = this.driver.findElements(By.tagName("a"));
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
				this.driver.navigate().refresh();
			}
		}

		// Download report data.
		if (rptFound && downloadLink != null) {
			log.info("Downloading report zip file");
			downloadLink.click();

			// Wait until download completed.
			File f = new File("C:\\Downloads\\fb_export.csv.zip");
			while (!f.exists()) {
				Thread.sleep(3000);
			}

			// Save a copy in the archive folder.
			String workingDir = System.getProperty("user.dir");
			String archivePath = workingDir + "\\archive\\" + rptName;
			File archiveFile = new File(archivePath);

			FileInputStream inStream = new FileInputStream(f);
    	    FileOutputStream outStream = new FileOutputStream(archiveFile);

    	    // Copy the file content in bytes.
    	    byte[] buffer = new byte[1024];
    	    int length;
    	    while ((length = inStream.read(buffer)) > 0) {
    	    	outStream.write(buffer, 0, length);
    	    }

    	    inStream.close();
    	    outStream.close();
    	    log.info("Saving/archiving report zip file: " + rptName);
		}
	}
}