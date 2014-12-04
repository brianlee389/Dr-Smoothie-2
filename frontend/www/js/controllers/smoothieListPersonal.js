'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.controller:MySmoothieListCtrl
 * @description
 * # MySmoothieListCtrl
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .controller('MySmoothieListCtrl', ['$scope', 'restFactory', function ($scope, restFactory) {

    restFactory.getTopRecipes().success(function(data) {
      $scope.mySmoothies = data;
    });

    $scope.showSmoothieDetails = function(id) {
      window.location = '#/smoothieDetail';
    };

  }]);
