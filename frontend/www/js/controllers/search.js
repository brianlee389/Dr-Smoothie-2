'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .controller('SearchListCtrl',['$scope', 'restFactory', function ($scope, restFactory) {
  	restFactory.getTopRecipes().success(function(data) {
      $scope.allSmoothies = data;
    });


  }]);

angular.module('comdrsmoothieappApp')
  .controller('IngrSearchListCtrl',['$scope', 'restFactory', function ($scope, restFactory) {
  	$scope.ingrsearch = function (actual, expected) {
	    for(var i in actual["ingredient"]) {
	    	console.log(actual["ingredient"][i]);
	    	if(angular.equals(actual["ingredient"][i].name, expected)) {
	    		return true;
	    	}
	    }
    	return false;
	};

  	restFactory.getTopRecipes().success(function(data) {
      $scope.allSmoothies = data;
    });


  }]);