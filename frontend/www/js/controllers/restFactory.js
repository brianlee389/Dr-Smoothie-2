'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.factory.restFactory
 * @description
 * # restfactory
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .factory('restFactory', ['$http', function ($http) {

  var urlBase = 'http://localhost:9000/api';
//  var urlBase = 'https://dr-smoothie.appspot.com';
  var restFactory = {};

  restFactory.getRecipeDetails = function(id) {
  	return $http.get(urlBase + '/GetRecipe?id=' + id);
  };

  restFactory.getTopRecipes = function() {
  	return $http.get(urlBase + '/recipes');
  };

  restFactory.getMyRecipes = function(userId) {
  	return $http.get(urlBase + '/RecipesByUser?userkey=' + userId);
  };

  restFactory.getMyFavorites = function(userId) {
  	return $http.get(urlBase + '/FavoriteRecipes?name=' + userId);
  };

  restFactory.addRecipe = function(recipe) {
   return $http({
          url: urlBase + '/AddRecipe',
          method: "POST",
          data: recipe
      }).success(function (data, status, headers, config) {
        console.log(data);
      });
  	//return $http.post(urlBase + '/AddRecipe', recipe);
  };

  restFactory.addUser = function(facebookID){


    return $http({
        method: "POST",
        url: urlBase + '/users/create',
        data: JSON.stringify({"key": facebookID}),
        headers: {
            'Content-Type': 'application/json; charset=utf-8'
        }
    }).success(function (data, status, headers, config) {console.log(data);});
  	//return $http.post(urlBase + '/users/create', {"key": facebookID}).success(function (data) {console.log(data)});
  };

  //delete doesnot take a body
  //we need to pass recipeid as well as userid
  restFactory.removeRecipe = function(recipe){
  	return $http.delete(urlBase + '/recipe/' + recipe.id);
  };

  restFactory.searchByName = function(name){
  	return $http.get(urlBase + '/Search/Recipe?name=' + name);
  };

  restFactory.searchByIngredient = function(ing){
  	return $http.get(urlBase + '/Search/Ingredient?name=' + ing);
  };

  restFactory.searhByNutrient = function(nut){
  	return $http.get(urlBase + '/Search/Nutrient?name=' + nut);
  };

  restFactory.getIngredients = function(type, successCallback) {
    var typeNo;
    if(type == "vegetables") typeNo = 1;
    if(type == "fruits") typeNo = 2;
    if(type == "nuts") typeNo = 3;
    if(type == "other") typeNo = 4;
    return $http.get(urlBase + '/ingredients/'+typeNo).success(successCallback);
  };

  return restFactory;
  }]);
