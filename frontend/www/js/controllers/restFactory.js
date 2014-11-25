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

  var urlBase = 'http://localhost:8000';
  // var urlBase = 'https://dr-smoothie.appspot.com';
  var restFactory = {};

  restFactory.getRecipeDetails = function(id) {
  	return $http.get(urlBase + '/GetRecipe?id=' + id);
  };

  restFactory.getTopRecipes = function() {
  	return $http.get(urlBase + '/TopRecipes?start=0&end=10');
  };

  restFactory.getMyRecipes = function(userId) {
  	return $http.get(urlBase + '/RecipesByUser?userkey=' + userId);
  };

  restFactory.getMyFavorites = function(userId) {
  	return $http.get(urlBase + '/FavoriteRecipes?name=' + userId);
  };

  restFactory.addRecipe = function(recipe) {
  	return null; //$http.get(urlBase + '/AddRecipe?name=2&', recipe);
  };

  restFactory.addUser = function(facebookID){
  	return $http.post(urlBase + '/AddUser', {fbID: facebookID}).success(function (data) {console.log(data)});
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

  restFactory.getIngredients = function(type, successCallback){
    // if(type == undefined) {
    //   type = "NONE"
    // }
    // return $http.get(urlBase + '/Ingredients?type='+type).success(successCallback);
    return null;
  };

  return restFactory;
  }]);
