/*
 * Derived from: https://github.com/imjustd/chrome-tabs (Copyright (c) Dimitar Stojanov, MIT license)
 */

Rapidoid.initializer(function($scope, $http, $window) {

    var counter = 1;
    $scope.tabs = [];

    $scope.addTab = function(title, content) {
        title = title || 'Untitled ' + (counter++);
        content = content || '';

        $scope.tabs.push({
            title : title,
            content : content
        });

        $scope.tabs[$scope.tabs.length - 1].active = true;
    };

    $scope.removeTab = function(event, index) {
        event.preventDefault();
        event.stopPropagation();
        $scope.tabs.splice(index, 1);
    };

});