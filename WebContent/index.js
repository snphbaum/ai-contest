angular.module('myApp', []).controller('myCtrl', function($scope, $http) {
    $scope.sendTask = function() {

        //Don't use redirect but redirect manually
        var tabWindowId = window.open('about:blank');

        $http.post('rest/target/task', $scope.task).then(function(response) {
            tabWindowId.location.href = response.headers('Location');
        });

    }
});