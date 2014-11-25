// Ionic Starter App

// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
/*angular.module('starter', ['ionic'])

.run(function($ionicPlatform) {
  $ionicPlatform.ready(function() {
    // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
    // for form inputs)
    if(window.cordova && window.cordova.plugins.Keyboard) {
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) {
      StatusBar.styleDefault();
    }
  });
})*/

'use strict';

/**
 * @ngdoc overview
 * @name comdrsmoothieappApp
 * @description
 * # comdrsmoothieappApp
 *
 * Main module of the application.
 */
angular
  .module('comdrsmoothieappApp', [
    'ngAnimate',
    'ngCookies',
    'ngResource',
    'ngRoute',
    'ngSanitize',
    'ngTouch'
//    'openfb',
//    'socialModule'
  ]).run(function (OpenFB) {

    console.log("run time");
    console.log(OpenFB);

    // initialize the openFB object and store the token
    OpenFB.init('1447784508842506', window.localStorage);
  })
//    .run(function ($rootScope, $state, $ionicPlatform, $window, OpenFB) {

//        OpenFB.init('1447784508842506');
//
//        $ionicPlatform.ready(function () {
//            if (window.StatusBar) {
//                StatusBar.styleDefault();
//            }
//        });
//
//        $rootScope.$on('$stateChangeStart', function(event, toState) {
//            if (toState.name !== "app.login" && toState.name !== "app.logout" && !$window.sessionStorage['fbtoken']) {
//                $state.go('app.login');
//                event.preventDefault();
//            }
//        });
//
//        $rootScope.$on('OAuthException', function() {
//            $state.go('app.login');
//        });
//})
  .config(function ($routeProvider) {
    $routeProvider
//            .state('app', {
//                url: "/app",
//                abstract: true,
//                templateUrl: "templates/menu.html",
//                controller: "AppCtrl"
//            })
//
//            .state('app.login', {
//                url: "/login",
//                views: {
//                    'menuContent': {
//                        templateUrl: "templates/login.html",
//                        controller: "LoginCtrl"
//                    }
//                }
//            })
//
//            .state('app.logout', {
//                url: "/logout",
//                views: {
//                    'menuContent': {
//                        templateUrl: "templates/logout.html",
//                        controller: "LogoutCtrl"
//                    }
//                }
//            })
      .when('/', {
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      })
      .when('/smoothieListPersonal', {
        templateUrl: 'views/recipes.html',
        controller: 'MySmoothieListCtrl'
      })
      .when('/smoothieDetail', {
        templateUrl: 'views/smoothieDetail.html',
        controller: 'SmoothieDetailCtrl'
      })
      .when('/create', {
        templateUrl: 'views/createSmoothie.html',
        controller: 'CreateSmoothieCtrl'
      })
      .when('/search', {
        templateUrl: 'views/search.html',
        controller: 'SearchListCtrl'
      })
      .when('/searchByIngredient', {
        templateUrl: 'views/search.html',
        controller: 'SearchListCtrl'
      })
      .when('/searchByName', {
        templateUrl: 'views/search.html',
        controller: 'SearchListCtrl'
      })
      .when('/searchByNutrient', {
        templateUrl: 'views/search.html',
        controller: 'SearchListCtrl'
      })
      .otherwise({
        templateUrl: 'views/main.html',
        controller: 'MainCtrl'
      });
  });
