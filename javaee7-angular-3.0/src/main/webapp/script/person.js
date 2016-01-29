var app = angular.module('persons', ['ngResource', 'ngGrid', 'ui.bootstrap']);


// Create a controller with name personsListController to bind to the grid section.
app.controller('personsListController', function (rolesService, $scope, $rootScope, personService) {
    // Initialize required information: sorting, the first page to show and the grid options.
    $scope.sortInfo = {fields: ['id'], directions: ['asc']};
    $scope.persons = {currentPage: 1};
    $scope.gridOptions = {
        data: 'persons.list',
        useExternalSorting: true,
        sortInfo: $scope.sortInfo,

        columnDefs: [
            { field: 'id', displayName: 'Id' },
            { field: 'name', displayName: 'Name'},
            { field: '', width: 30, cellTemplate: '<span class="glyphicon glyphicon-remove remove" ng-click="deleteRow(row)"></span>' }
        ],

        multiSelect: false,
        selectedItems: [],
        // Broadcasts an event when a row is selected, to signal the form that it needs to load the row data.
        afterSelectionChange: function (rowItem) {
            if (rowItem.selected) {
                $rootScope.$broadcast('personSelected', $scope.gridOptions.selectedItems[0].id);
                $rootScope.$broadcast('refreshRoles');
            }
        }
    };

    // Refresh the grid, calling the appropriate rest method.
    $scope.refreshGrid = function () {
        var listPersonsArgs = {
            page: $scope.persons.currentPage,
            sortFields: $scope.sortInfo.fields[0],
            sortDirections: $scope.sortInfo.directions[0]
        };

        personService.get(listPersonsArgs, function (data) {
            $scope.persons = data;
        })
    };
    // Broadcast an event when an element in the grid is deleted. No real deletion is perfomed at this point.
    $scope.deleteRow = function (row) {
        $rootScope.$broadcast('deletePerson', row.entity.id);
    };

    // Watch the sortInfo variable. If changes are detected than we need to refresh the grid.
    // This also works for the first page access, since we assign the initial sorting in the initialize section.
    $scope.$watch('sortInfo.fields[0]', function () {
        $scope.refreshGrid();
    }, true);



    // Do something when the grid is sorted.
    // The grid throws the ngGridEventSorted that gets picked up here and assigns the sortInfo to the scope.
    // This will allow to watch the sortInfo in the scope for changed and refresh the grid.
    $scope.$on('ngGridEventSorted', function (event, sortInfo) {
        $scope.sortInfo = sortInfo;
    });

    // Picks the event broadcasted when a person is saved or deleted to refresh the grid elements with the most
    // updated information.
    $scope.$on('refreshGrid', function () {
        $scope.refreshGrid();
    });

    // Picks the event broadcasted when the form is cleared to also clear the grid selection.
    $scope.$on('clear', function () {
        $scope.gridOptions.selectAll(false);
    });
});


// Create a controller with name personsFormController to bind to the form section.
app.controller('personsFormController', function ($scope, $rootScope, personService) {

    // Clears the form. Either by clicking the 'Clear' button in the form, or when a successfull save is performed.
    $scope.clearForm = function () {
        $scope.person = null;
        // For some reason, I was unable to clear field values with type 'url' if the value is invalid.
        // This is a workaroud. Needs proper investigation.
        document.getElementById('imageUrl').value = null;
        // Resets the form validation state.
        $scope.personForm.$setPristine();
        // Broadcast the event to also clear the grid selection.
        $rootScope.$broadcast('clear');
    };

    // Calls the rest method to save a person.
    $scope.updatePerson = function () {
        personService.save($scope.person).$promise.then(
            function () {
                // Broadcast the event to refresh the grid.
                $rootScope.$broadcast('refreshGrid');
                // Broadcast the event to display a save message.
                $rootScope.$broadcast('personSaved');
                $scope.clearForm();
            },
            function () {
                // Broadcast the event for a server error.
                $rootScope.$broadcast('error');
            });
    };

    // Picks up the event broadcasted when the person is selected from the grid and perform the person load by calling
    // the appropiate rest service.
    $scope.$on('personSelected', function (event, id) {
        $scope.person = personService.get({id: id});
    });

    // Picks us the event broadcasted when the person is deleted from the grid and perform the actual person delete by
    // calling the appropiate rest service.
    $scope.$on('deletePerson', function (event, id) {
        personService.delete({id: id}).$promise.then(
            function () {
                // Broadcast the event to refresh the grid.
                $rootScope.$broadcast('refreshGrid');
                // Broadcast the event to display a delete message.
                $rootScope.$broadcast('personDeleted');
                $scope.clearForm();
            },
            function () {
                // Broadcast the event for a server error.
                $rootScope.$broadcast('error');
            });
    });
});
app.controller('rolesController', function ($resource, $scope, $rootScope, rolesService, $window,saveService){

    $scope.roles = [];
    //$scope.arrayData = [];
    //$scope.arrayData.push({Name: $scope.ones});
    $scope.gridOptions2 = {
        data: 'roles',
        columnsDefs: [
            {field: 'name', displayName: 'name'},
            {field: 'description', displayName: 'description'}
        ],
        multiSelect:false,
        selectedItems: [],
        // Broadcasts an event when a row is selected, to signal the form that it needs to load the row data.
        afterSelectionChange: function (rowItem) {
            if (rowItem.selected) {
                $rootScope.$broadcast('roleSelected', $scope.gridOptions2.selectedItems[0].id);
            }
        }
    };

    $scope.loadData = function(id){
        $scope.personId = id;
        rolesService.query({id: id}).$promise.then(
            function(data) {
                $scope.roles = data;
                console.log($scope.roles)
            })
    };


    $scope.$on('personSelected', function (event, id){
        $scope.loadData(id);
    });

    $scope.deleteRow = function(id){
        if($scope.roles.length>1)$scope.roles.splice(id-1, 1);
        else $scope.roles.length = 0;
    };

    $scope.$on('roleFromListSelected',function(event,data){
       $scope.roles = data;
    });

    $scope.$on('roleSelected', function(event, id){
        $scope.deleteRow(id);
    });

    $scope.saveRoles = function(){
        saveService.save({id: $scope.personId}, $scope.roles).$promise.then(
            function () {
                $window.alert("Person's roles saved!");
            },
            function () {
                // Broadcast the event for a server error.
                $window.alert("ERROR!!!");
            });
    }
});

app.controller('roleListController', function(rolesService,$scope,rolesListService,$rootScope, $window){
    rolesListService.query().$promise.then(
        function(data) {
            $scope.roleList = data;
            console.log($scope.roleList)
        });

    $scope.roleList = [];
    //$scope.arrayData = [];
    //$scope.arrayData.push({Name: $scope.ones});
    $scope.gridOptions3 = {
        data: 'roleList',
        columnsDefs: [
            {field: 'name', displayName: 'name'
                //  , cellFilter: 'stringArrayFilter'
            },
            {field: 'description', displayName: 'description'
                //   , cellFilter: 'stringArrayFilter'
            }
        ],
        multiSelect: false,
        selectedItems: [],
        // Broadcasts an event when a row is selected, to signal the form that it needs to load the row data.
        afterSelectionChange: function (rowItem) {
            if (rowItem.selected) {
                $rootScope.$broadcast('roleFromAllSelected', $scope.gridOptions3.selectedItems[0].id);
            }
        }
    };



    $scope.addRole = function(id){
        var unique = false;
        var newItem = $scope.roleList[id-1];
        if($scope.roles.length>0) {
            angular.forEach($scope.roles, function (role, key) {
                unique = role.name != newItem.name;

            });
        }
        else{
            unique = true;
        }
        if(unique) {
            $scope.roles.push(newItem);
            $rootScope.$broadcast('roleFromListSelected', $scope.roles);
        }
    };

    $scope.$on('personSelected', function(event, id){

        rolesService.query({id: id}).$promise.then(
            function(data) {
                $scope.roles = data;
            });
    });
    $scope.$on('roleFromAllSelected',function(event, id){
        $scope.addRole(id);
    })


});

// Create a controller with name alertMessagesController to bind to the feedback messages section.
app.controller('alertMessagesController', function ($scope) {
    // Picks up the event to display a saved message.
    $scope.$on('personSaved', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Record saved successfully!' }
        ];
    });

    // Picks up the event to display a deleted message.
    $scope.$on('personDeleted', function () {
        $scope.alerts = [
            { type: 'success', msg: 'Record deleted successfully!' }
        ];
    });

    // Picks up the event to display a server error message.
    $scope.$on('error', function () {
        $scope.alerts = [
            { type: 'danger', msg: 'There was a problem in the server!' }
        ];
    });

    $scope.closeAlert = function (index) {
        $scope.alerts.splice(index, 1);
    };
});


app.controller('logedP', function ($scope,loginDude,logoutService,loginProc,isAdminService,$window) {
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
        $scope.logedPerson = loginDude.get().$promise.then(
            function(){
                $scope.isLoged = true;
                document.getElementById('loginForm').style.display = 'none';

            },
            function(){
                document.getElementById('logOutButton').style.display = 'none';
                document.getElementById('formsLi').style.display = 'none';
            }
        );
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

app.factory('loginProc', function($resource){
    return $resource('resources/article/login/:login')
});

app.factory('loginDude',function ($resource){
    return $resource('resources/user/logedIn');
});

app.factory('personService', function ($resource) {
    return $resource('resources/user/:id');
});

app.factory('rolesListService', function($resource){
   return $resource('resources/user/roles/list',{},
       {
           'query':{
               method:'GET',isArray: true
           }
       })
});
app.factory('logoutService',function($resource){
    return $resource('resources/user/logout/');
});

app.factory('saveService', function ($resource) {
    return $resource('resources/user/save/roles/:id/:pid');
});

app.factory('rolesService', function ($resource){
  return $resource('resources/user/roles/:id',{},
      {
          'query':{
              method:'GET', isArray: true
          }
      });
});