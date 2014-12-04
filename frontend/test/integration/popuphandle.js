beforeEach(function () {
browser.ignoreSynchronization = true;
});




describe('login', function() {
  it('should bring a popup', function() {
    browser.driver.get('http://localhost:8100/#');

    browser.driver.findElement(By.className('btn')).click();
    var mainHandle;
    browser.driver.getAllWindowHandles().then(function(handles){
      //switching to login page
      browser.driver.switchTo().window(handles[1]);
      mainHandle = handles[0];
    })
    browser.sleep(1000);
    email = browser.driver.findElement(By.id('email'));
    email.sendKeys('test_njxdgos_user@tfbnw.net');

    pass = browser.driver.findElement(By.id('pass'));
    pass.sendKeys('testuser');

    submit = element(by.id('u_0_1'));
    submit.click();

    browser.sleep(1000);
    browser.driver.getAllWindowHandles().then(function(handles){
      //switching back to main page
      browser.driver.switchTo().window(handles[0]);
    });

  });
});