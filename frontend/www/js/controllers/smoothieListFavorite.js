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

    restFactory.getMyFavorites().success(function(data) {
      $scope.favSmoothies = data;
    });

    $scope.showSmoothieDetails = function(smoothie) {
      window.location = '#/smoothieDetail';
    };
  }]);
