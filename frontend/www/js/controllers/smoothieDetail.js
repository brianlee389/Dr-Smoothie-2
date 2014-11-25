'use strict';

/**
 * @ngdoc function
 * @name comdrsmoothieappApp.controller:MySmoothieListCtrl
 * @description
 * # MySmoothieListCtrl
 * Controller of the comdrsmoothieappApp
 */
angular.module('comdrsmoothieappApp')
  .controller('SmoothieDetailCtrl', ['$scope', 'restFactory', 'OpenFB', function ($scope, restFactory, openFB) {
    
console.log(openFB);
    $scope.share = function() {

      // publish the object
      var url="https://graph.facebook.com/me/objects/com-drexel-smoothie:smoothie";

      $.post(
          url,
          {
            access_token: openFB.getAccessToken(),
            object: JSON.stringify({
                app_id:openFB.getAppID(),
                type:"com-drexel-smoothie:smoothie",
                title:"Banana Smoothie"
            })
          },
          function (data) {
            var objectID = data.id;

            // open up the activity dialog

            var url = "https://www.facebook.com/dialog/share_open_graph?"+
            "app_id="+openFB.getAppID()+
            "&display=popup"+
            "&action_type=com-drexel-smoothie:prepare"+
            "&action_properties=%7B%22smoothie%22%3A"+objectID+"%7D"+
            "&redirect_uri=https%3A%2F%2Fdr-smoothie.appspot.com%2FfbRedirect";

            var shareWindow = window.open(url, "_blank", 'location=no,toolbar=no,clearsessioncache=yes');

            shareWindow.addEventListener("loadstart", function (e) {
                var loc = e.url;
                    //alert("loadstart url " + loc);
                if (loc.indexOf("dr-smoothie.appspot.com/fbRedirect") >= 0) {
                    //alert("closing loadstart " + loc);
                    shareWindow.close();
                    if (loc.indexOf("error") >= 0) alert("Sharing failed... :(");
                    if (loc.indexOf("post_id") >= 0) alert("Smoothie shared successfully! :)");
                }
            });
            shareWindow.addEventListener("loadstop", function (e) {
                var loc = e.url;
                    //alert("loadstop url " + loc);
                if (loc.indexOf("dr-smoothie.appspot.com/fbRedirect") >= 0) {
                    //alert("closing loadstop " + loc);
                    shareWindow.close();
                    if (loc.indexOf("error") >= 0) alert("Sharing failed... :(");
                    if (loc.indexOf("post_id") >= 0) alert("Smoothie shared successfully! :)");
                }
            });
          }
      );
  }

  }]);
