Rapidoid.plugin(function(app) {

    app.factory('StreamData', [ '$http', function($http) {

        var StreamData = function() {
            this.items = [];
            this.busy = false;
            this.page = 1;
            this.after = '';
            this.url = '';
            this.cols = 1;
            this.reachedEnd = false;
        };

        StreamData.prototype.nextPage = function() {

            if (this.busy || this.reachedEnd) return;
            this.busy = true;

            var url = this.url;
            url = url.replace('{{page}}', '' + (this.page || 1));
            url = url.replace('{{after}}', '' + (this.after || ''));

            $http.get(url).success(function(data) {
                var items = data;
                for (var i = 0; i < items.length; i++) {
                    this.items.push(items[i]);
                    this.after = items[i].id;
                }
                this.page++;
                this.busy = false;
                this.reachedEnd = items.length == 0;
            }.bind(this));
        };

        StreamData.prototype.zoom = function(delta) {
            var cols = this.cols + delta;
            if (cols >= 1 && cols <= 4) {
                this.cols = cols;
            }
        }

        return StreamData;

    } ]);

    app.controller('StreamController', [ '$scope', '$http', '$window', '$attrs', 'StreamData',
            function($scope, $http, $window, $attrs, StreamData) {
                $scope.stream = new StreamData();

                $scope.stream.url = $attrs.url;
                $scope.stream.cols = $attrs.cols;
                $scope.stream.page = $attrs.page;
                $scope.stream.after = $attrs.after;

                $scope.items = $scope.stream.items;
            } ]);

    app.controller('StreamItemController', [ '$scope', '$http', '$window', '$attrs',
            function($scope, $http, $window, $attrs) {
                var index = $scope.rowN * $scope.cols + $scope.colN;
                $scope.it = function() {
                    return $scope.items[index];
                };
            } ]);

});
