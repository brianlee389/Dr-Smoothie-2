describe('angularjs homepage', function() {
  it('should have a title', function() {
    browser.driver.get('http://localhost:8100/#');

    expect(browser.driver.getTitle()).toEqual('http://localhost:8100/#/');
  });
});


