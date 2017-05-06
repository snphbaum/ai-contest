var myApp = angular.module('myApp', []);
myApp.config(function ($locationProvider) {
    $locationProvider.html5Mode(true);
});
myApp.controller('myCtrl', function ($scope, $http, $location) {
    taskId = $location.search().taskId;
    $scope.taskId = taskId;
    details1 = $location.search().details1;
    $scope.details1 = details1;

    // If not all needed task details can be handed over as URL parameter (e.g. too big), they can be retrieved from EA with the taskID
    $http.get('restredirect/rest/target/task/'+taskId).then(function(response){
        $scope.task = response.data;
    });
    }
);
