/*
 * Derived from: https://github.com/imjustd/chrome-tabs (Copyright (c) Dimitar Stojanov, MIT license)
 */

Rapidoid.initializer(function($scope) {

    $scope.tabs = [];

    $scope.addTab = function(tab) {
        $scope.tabs.push(tab);
        $scope.tabs[$scope.tabs.length - 1].active = true;
    };

    $scope.removeTab = function(event, index) {
        event.preventDefault();
        event.stopPropagation();
        $scope.tabs.splice(index, 1);
    };

});