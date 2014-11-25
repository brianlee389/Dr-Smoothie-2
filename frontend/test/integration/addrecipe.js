beforeEach(function () {
browser.ignoreSynchronization = true;



});




describe('add recipe', function() {
  it('should add a recipe', function() {
  	
    browser.get('http://localhost:8100/#/create');
    browser.sleep(2000);

    ing1 = element.all(by.css('.glyphicon-plus')).get(2);
    ing1.getText().then(function(text){
    	console.log(text);
    })

    ing1.click();
    browser.sleep(1000);

    selected = element.all(by.repeater('ingredient in selectedIngredients'));
    expect(selected.count()).toEqual(1);

    createBtn = element(by.id('btnHomeCreateSmootie'));
    createBtn.click();

  });
});