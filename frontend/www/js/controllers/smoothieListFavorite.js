'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.controller:MySmoothieListCtrl
 * @description
 * # MySmoothieListCtrl
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .controller('FavSmoothieListCtrl', ['$scope', 'restFactory', function ($scope, restFactory) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];

    restFactory.getMyFavorites(0).success(function(data) {
      $scope.favSmoothies = data;
    });

    $scope.showSmoothieDetails = function(id) {
      window.location = '#/smoothieDetail';
    };
  }]);
