var app = angular.module('articles', ['ngResource', 'ui.bootstrap']);

app.controller('newsController', function ($scope,saveNewsService, newsService,removeNewsService,
                                           loginDude,$timeout,logoutService, loginProc, $window, isAdminService) {

    $scope.isEditable = false;

    $scope.isLoged = false;

    $scope.isAdmin = false;

    $scope.articles = newsService.query();



    $scope.removeNews = function (id) {
        removeNewsService.delete({id: id}).$promise.then(
            function () {
                $scope.news = newsService.query();
            }
        );
    };


    $scope.editNews = function (id) {
        document.getElementById('context').value = null;
        document.getElementById('content').value = null;
        $scope.newsId = id;
        $scope.isEditable = true;
        document.getElementById('context').value = $scope.news[id-1].context;
        document.getElementById('content').value = $scope.news[id-1].content;
    };


    $scope.clearForm = function () {
        document.getElementById('topic').value = null;
        document.getElementById('context').value = null;
        $scope.isEditable = false;
    };

    $scope.confirmEdition = function () {
        $scope.topic = document.getElementById('topic').value;
        $scope.context = document.getElementById('context').value;
        if($scope.isEditable){
            saveNewsService.save({id: $scope.newsId,topic: $scope.topic}, $scope.context);
            $scope.$broadcast('newsSaved');
        }
        else{
            saveNewsService.save({id:null,topic:$scope.topic},$scope.context);
            $scope.$broadcast('newsEdited');
        }
        $scope.clearForm();
        $timeout(function(){$scope.news = newsService.query();},500);
    };

    $scope.$on('newsSaved', function () {
        $scope.alerts = [
            { type: 'success', msg: 'News saved successfully!' }
        ];
    });
    $scope.$on('newsEdited', function () {
        $scope.alerts = [
            { type: 'success', msg: 'News edited successfully!' }
        ];
    });

    $scope.logout = function(){
        logoutService.get();
        $window.location.reload();

    };



    $scope.login = function(){
        var loginT =  document.getElementById('login').value;
        var passwordT = document.getElementById('password').value;
        $scope.proceed = loginProc.save({login:loginT}, passwordT).$promise.then(
            function () {
                $scope.isAdm = isAdminService.save(loginDude.name);
                if($scope.isAdm){
                    $window.location.href='forms.html';
                }
                else $window.location.href='IntroUserPage.html';

            },
            function () {
                $scope.alerts = [
                    { type: 'success', msg: 'Sorry, you are not authorized!Check your login and password' }
                ];
            });
    };

    function loginFunc(){
        $scope.logedPerson = loginDude.get();
        if($scope.logedPerson.value != null) $scope.isLoged=true;
    }


    loginFunc();

    function checkAdmin(){

        if($scope.isAdm && document.getElementById('removeB')!=null){
            document.getElementById('removeB').style.display = 'none';
            document.getElementById('editB').style.display = 'none';
            document.getElementById('formsLi').style.display = 'none';
        }
    }
    $timeout(function(){checkAdmin();},500);
});
app.factory('isAdminService',function($resource){
    return $resource('resources/article/isAdmin/');
});
app.factory('logoutService',function($resource){
    return $resource('resources/article/logout/');
});
app.factory('loginProc', function($resource){
    return $resource('resources/article/login/:login')
});

app.factory('loginDude',function ($resource){
    return $resource('resources/article/logedIn');
});


app.factory('newsService', function ($resource) {
    return $resource('resources/article/list'
        , {},
        {
            'query': {
                method: 'GET', isArray: true
            }
        });
});
app.factory('saveNewsService', function($resource){
   return $resource('resources/article/save/:id/:topic');
});
app.factory('removeNewsService', function ($resource) {
    return $resource('resources/article/remove/:id');
});
