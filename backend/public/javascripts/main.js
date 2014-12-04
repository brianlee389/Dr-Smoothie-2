/*
 * Author: Brian Lee
 */
var app = angular.module("app", ["ngResource"])
	.constant("apiUrl", "http://localhost:9000\:9000/api") // to tell AngularJS that 9000 is not a dynamic parameter
	.config(["$routeProvider", function($routeProvider) {
		return $routeProvider.when("/", {
			templateUrl: "/views/main",
			controller: "ListCtrl"
		}).when("/create", {
			templateUrl: "/views/detail",
			controller: "CreateIngredientCtrl"
	    })/*.when("/edit/:id", {
			templateUrl: "/views/detail",
			controller: "EditCtrl"
	    })*/.otherwise({
			redirectTo: "/"
		});
	}
	]).config([
	"$locationProvider", function($locationProvider) {
		return $locationProvider.html5Mode(true).hashPrefix("!"); // enable the new HTML5 routing and histoty API
	}
]);

// the global controller
app.controller("AppCtrl", ["$scope", "$location", function($scope, $location) {
	// the very sweet go function is inherited to all other controllers
	$scope.go = function (path) {
		$location.path(path);
	};
}]);

// the list controller
/*app.controller("ListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
	var Celebrities = $resource(apiUrl + "/celebrities"); // a RESTful-capable resource object
	$scope.celebrities = Celebrities.query(); // for the list of celebrities in public/html/main.html
}]);*/
app.controller("ListCtrl", ["$scope", "$resource", "apiUrl", function($scope, $resource, apiUrl) {
	var ingredients = $resource(apiUrl + "/ingredients"); // a RESTful-capable resource object
	$scope.ingredients = ingredients.query(); // for the list of celebrities in public/html/main.html
}]);

// the create controller
app.controller("CreateIngredientCtrl", ["$scope", "$resource", "$timeout", "apiUrl", 
	function($scope, $resource, $timeout, apiUrl) {
	// to create an ingredient
	$scope.create = function() {
		var createdIngredient = $resource(apiUrl + "/ingredients/create"); // a RESTful-capable resource object
		createdIngredient.save($scope.ingredient);
		$timeout(function() { $scope.go('/'); }); // go back to public/html/main.html
	};
}]);

