'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .controller('MainCtrl', ['$scope', 'restFactory', 'OpenFB', function ($scope, restFactory, openFB) {


    var homeURL = "#/smoothieListPersonal";
    var fbToken;
    $("#btnLogin").hide();
    $("#userInfo").hide();

    $scope.userFirstName = '';
    $scope.userID='';


    // login
    $scope.login = function() {
    alert("blah");
        openFB.login('email,publish_stream',
            function(data) {
                alert(openFB.getAccessToken());
                redirectToHomePage(2000);

                console.log(data);
                restFactory.addUser(openFB.getAccessToken());
            },
            function() {
                alert(openFB.getAccessToken());
                location.reload();
            });
    }


    // display the user name and profile picture in on the home screen
    function showUserInfo(callback) {
    console.log("loading user data");
        openFB.api({
            path: '/me',
            error: errorHandler
        }).success(function(data) {
               $scope.userFirstName = data.first_name;
               $scope.userID = data.id;
               callback(data);
           });
    }

    // change the page after login
    function redirectToHomePage(duration) {
        setTimeout( function () {
            window.location.href=homeURL;
        }, duration);
    }

    // error handler
    function errorHandler(error) {
    console.log("error");
        console.log(error);
    }


    // check if the user is already logged into Facebook
    openFB.getLoginStatus (function (param) {
    console.log(param);
        if (param.status == "connected") {
            showUserInfo(function (data) {
                $("#userInfo").show();
                redirectToHomePage(3000);
            });
        } else if (param.status == 'expired') {
            $scope.login();
        }

        if (param.status == "unknown") {
            $("#btnLogin").show().animate({}, 1000);
        }
    });

  }]);
