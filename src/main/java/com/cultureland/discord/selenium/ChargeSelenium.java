package com.cultureland.discord.selenium;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class ChargeSelenium {

    //WebDriver
    private WebDriver driver;
    private WebElement webElement;
    private WebDriverWait wait;
    private WebDriverWait dowait;

    //Properties
    public final String WEB_DRIVER_ID = "webdriver.chrome.driver";
    public final String WEB_DRIVER_PATH = "/usr/local/bin/chromedriver";

    //크롤링 할 URL
    private final String LOGIN_URL = "https://m.cultureland.co.kr/mmb/loginMain.do";
    private final String CHARGE_URL = "https://m.cultureland.co.kr/csh/cshGiftCard.do";

    private final String CHARGE_ID = "wnsgur65";
    private final String CHARGE_PW = "sksqkrwns65@";


     private ChargeSelenium() {
        //System Property SetUp
        System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

        //Driver SetUp
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 10);
        dowait = new WebDriverWait(driver, 3);
    }



    public static int chargeMain(String pin){
        ChargeSelenium chargeSelenium = new ChargeSelenium();
        if(chargeSelenium.login()){
            String[] pinNum = chargeSelenium.pinReplace(pin);
            if(pinNum == null){
                return -1;
            }
            return chargeSelenium.charge(pinNum);
        };
        return -404;
    }


    private String[] pinReplace(String pin){
        System.out.println(pin);
        if (pin.contains("-")) {
            return pin.split("-", 4);
        }else if (pin.contains(" ")) {
            return pin.split(" ", 4);
        }
        try {
            String[] pins = new String[4];
            pins[0] = pin.substring(0,3);
            pins[1] = pin.substring(4,7);
            pins[2] = pin.substring(8,11);
            pins[3] = pin.substring(12,17);
            return pins;
        }catch (Exception e){
            e.printStackTrace();
            driver.close();
            return null;
        }
    }


    private int charge(String[] pinNum){
        try {
            driver.get(CHARGE_URL);

            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtScr11")));
            webElement.click();
            webElement.sendKeys(pinNum[0]);
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtScr12")));
            webElement.click();
            webElement.sendKeys(pinNum[1]);
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtScr13")));
            webElement.click();
            webElement.sendKeys(pinNum[2]);
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtScr14")));
            webElement.click();
            for (String target : pinNum[3].split("")) {
                checkKeyboard(target);
            }


            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnCshFrom")));
            webElement.click();


            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("inSafeSub")));

            String result = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='wrap']/div/section/dl/dd"))).getAttribute("innerHTML");
            if(result.contains(",")){
                result = result.replace(",", "");
            }
            int resultMoney = Integer.parseInt(result.replace("원", ""));
            driver.close();
            return resultMoney;
        }catch (Exception e){
            e.printStackTrace();
            driver.close();
            return -1;
        }
    }

    private boolean login() {
        try {
            driver.get(LOGIN_URL);
            //iframe 내부에서 id 필드 탐색
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("txtUserId")));
            webElement.sendKeys(CHARGE_ID);

            //iframe 내부에서 pw 필드 탐색
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("passwd")));
            webElement.click();
            String[] pw = CHARGE_PW.split("");
            for (String target : pw) {
                checkKeyboard(target);
            }
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("btnLogin")));
            webElement.click();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            driver.close();
            return false;
        }
    }

    private void checkKeyboard(String target){
        String specialString = "[!@#$%^&*(),.?\":{}|<>]";
        if (Pattern.matches(specialString, target)) {
            System.out.println();
            System.out.println(target);
            webElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@alt='특수키']")));
            webElement.click();
            webElement =  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@alt='#']".replace("#",specialStringSet(target)))));
            webElement.click();
        } else {
            System.out.println(target);
            webElement =  wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@alt='#']".replace("#", target))));
            webElement.click();
        }
    }

    private String specialStringSet(String target){
        if(target.equals("@")){
            return "골뱅이";
        }
        return  "입력완료";
    }

}