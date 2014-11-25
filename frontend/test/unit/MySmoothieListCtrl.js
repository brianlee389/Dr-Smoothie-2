'use strict';

describe('Controller: MySmoothieListCtrl', function () {

  // load the controller's module
  beforeEach(angular.mock.module('comdrsmoothieappApp'));

  var MainCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(angular.mock.inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    MySmoothieListCtrl = $controller('MySmoothieListCtrl', {
      $scope: scope
    });
  }));

  it('should attach a list of mySmoothies to the scope', function () {
    expect(scope.mySmoothies.length).toBe(3);
  });
});
