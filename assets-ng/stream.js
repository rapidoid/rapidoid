Rapidoid.plugin(function(app) {

    app.factory('StreamData', [ '$http', function($http) {

        var StreamData = function(dataUrl) {
            this.items = [];
            this.busy = false;
            this.page = 1;
            this.dataUrl = dataUrl;
            this.cols = 3;
        };

        StreamData.prototype.nextPage = function() {
            if (this.busy)
                return;
            this.busy = true;

            var url = this.dataUrl.replace('{{page}}', '' + this.page);

            $http.get(url).success(function(data) {
                var items = data;
                for (var i = 0; i < items.length; i++) {
                    this.items.push(items[i]);
                }
                this.page++;
                this.busy = false;
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
                var dataUrl = $attrs.url;
                $scope.stream = new StreamData(dataUrl);
                $scope.items = $scope.stream.items;
                $scope.cols = 1;
            } ]);

    app.controller('StreamItemController', [ '$scope', '$http', '$window', '$attrs',
            function($scope, $http, $window, $attrs) {
                var index = $scope.rowN * $scope.cols + $scope.colN;
                $scope.it = function() {
                    return $scope.items[index];
                };
            } ]);

});
