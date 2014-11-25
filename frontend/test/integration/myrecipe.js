beforeEach(function () {
browser.ignoreSynchronization = true;



});




describe('my recipe', function() {
  it('should count 3', function() {
  	browser.sleep(2000);
    mySmoothies = element.all(by.binding('mySmoothies'))
    expect(mySmoothies.count()).toEqual(0);

  });
});