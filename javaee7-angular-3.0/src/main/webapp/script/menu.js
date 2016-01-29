
app.controller('navCtrl', ['$scope', '$location', function ($scope, $location) {
    $scope.navClass = function (page) {
        var currentRoute = $location.path().substring(1) || 'articles';
        return page === currentRoute ? 'active' : '';
    };

}]);

