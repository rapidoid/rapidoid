(function() {

    Rapidoid.initializer(function($scope) {

        $scope.toggleMore = function(item) {
            item.more = !item.more;
        }

        $scope.upvote = function(item) {
            item.vote = item.vote != 1 ? 1 : 0;
        }

        $scope.downvote = function(item) {
            item.vote = item.vote != -1 ? -1 : 0;
        }

        $scope.unvote = function(item) {
            item.vote = 0;
        }

        $scope.toggleFavorite = function(item) {
            var favs = JSON.parse(localStorage['favorites'] || '{}');
            item.fav = !item.fav;
            if (item.fav) {
                favs[item.id] = true;
            } else {
                delete favs[item.id];
            }
            localStorage['favorites'] = JSON.stringify(favs);
        }

    });

    function range(from, total) {
        var rangeArr = [];

        for (var i = from; i < from + total; i++) {
            rangeArr.push(i);
        }

        return rangeArr;
    }

    Rapidoid.plugin(function(app) {

        app.filter('rangex', function() {
            return function(input, from, total) {
                from = parseInt(from);
                total = parseInt(total);
                return range(from, total);
            }
        });

        app.filter('rowCount', function() {
            return function(input, cols) {
                return range(0, Math.ceil(input.length / cols));
            }
        });

        app.filter('modn', function() {
            return function(arr, n, remainder) {
                remainder = remainder || 0;
                return arr.filter(function(item, index) {
                    return index % n == remainder;
                })
            };
        });
    });

})();
