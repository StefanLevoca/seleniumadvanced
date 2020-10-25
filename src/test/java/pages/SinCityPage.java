package pages;

import base.WebDriverSingleton;
import enumerators.SinType;
import models.Sin;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.util.List;

import static base.TestBase.BASE_URL;

public class SinCityPage {

    private WebDriver driver;

    private static final String PAGE_URL = "sincity.php";

    @FindBy(name = "title")
    private WebElement titleInput;

    @FindBy(name = "author")
    private WebElement authorInput;

    @FindBy(name = "message")
    private WebElement messageInput;

    @FindBy(xpath = "//button[@type='submit']")
    private WebElement confessButton;

    @FindBy(css = "div.sinsListArea")
    private WebElement sinListSection;

    public SinCityPage() throws MalformedURLException {
        driver = WebDriverSingleton.getInstance().getDriver();
        PageFactory.initElements(driver, this);
    }

    public void openPage() throws InterruptedException {
        driver.get(BASE_URL + PAGE_URL);
        Thread.sleep(2000);
    }

    public void fillSinInformation(Sin sin) throws InterruptedException {
        titleInput.sendKeys(sin.getTitle());
        Thread.sleep(2000);
        authorInput.sendKeys(sin.getAuthor());
        Thread.sleep(2000);
        messageInput.sendKeys(sin.getMessage());
    }

    public void markTag(List<SinType> tags) throws InterruptedException {
        for (SinType tag : tags) {
            Thread.sleep(2000);
            driver.findElement(By.xpath("//input[@value='" + tag.getXpathValue() + "']")).click();
        }
    }

    public void confessSin() {
        confessButton.click();
    }

    public void checkSinStatus(Sin spiderSin) {
        //1.chcem najst element hriechu
        WebElement actualSin = getSinFromListElement(spiderSin);
        //2.chcem z neho vytiahnut text
        String actualText = actualSin.findElement(By.cssSelector("p")).getText().trim();
        //3.porovnat ocakavane a skutocne
        if (spiderSin.isForgiven()) {
            Assert.assertEquals("forgiven", actualText);
        } else {
            Assert.assertEquals("pending", actualText);
        }
    }

    private WebElement getSinFromListElement(Sin spiderSin) {
        WebElement listOfSins = driver.findElement(By.cssSelector("ul.list-of-sins"));
        return listOfSins.findElement(By.xpath("./li[contains(text(),'" + spiderSin.getTitle() + "')]"));
    }

    public void openDetail(Sin spiderSin) throws InterruptedException {
        getSinFromListElement(spiderSin).click();
        Thread.sleep(2000);
    }

    public void checkDetail(Sin spiderSin) {
        //1.najst element detailu
        WebElement sinDetail = driver.findElement(By.cssSelector("div.detail"));
        //2.pockat na hriech
        new WebDriverWait(driver, 10)
                .until(ExpectedConditions.textToBePresentInElement(
                        sinDetail.findElement(By.cssSelector("p")),
                        spiderSin.getMessage())
                );
        //3.overit message, author, title
        String actualTitle = sinDetail.findElement(By.cssSelector("h4")).getText();
        Assert.assertTrue(actualTitle.contains(spiderSin.getTitle()));
        Assert.assertTrue(actualTitle.contains(spiderSin.getAuthor()));
    }
}
