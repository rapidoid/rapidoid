/* ng-infinite-scroll - v1.2.0 - 2015-02-14 */
var mod;mod=angular.module("infinite-scroll",[]),mod.value("THROTTLE_MILLISECONDS",null),mod.directive("infiniteScroll",["$rootScope","$window","$interval","THROTTLE_MILLISECONDS",function(a,b,c,d){return{scope:{infiniteScroll:"&",infiniteScrollContainer:"=",infiniteScrollDistance:"=",infiniteScrollDisabled:"=",infiniteScrollUseDocumentBottom:"=",infiniteScrollListenForEvent:"@"},link:function(e,f,g){var h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y;return y=angular.element(b),t=null,u=null,i=null,j=null,q=!0,x=!1,w=null,p=function(a){return a=a[0]||a,isNaN(a.offsetHeight)?a.document.documentElement.clientHeight:a.offsetHeight},r=function(a){return a[0].getBoundingClientRect&&!a.css("none")?a[0].getBoundingClientRect().top+s(a):void 0},s=function(a){return a=a[0]||a,isNaN(window.pageYOffset)?a.document.documentElement.scrollTop:a.ownerDocument.defaultView.pageYOffset},o=function(){var b,c,d,g,h;return j===y?(b=p(j)+s(j[0].document.documentElement),d=r(f)+p(f)):(b=p(j),c=0,void 0!==r(j)&&(c=r(j)),d=r(f)-c+p(f)),x&&(d=p((f[0].ownerDocument||f[0].document).documentElement)),g=d-b,h=g<=p(j)*t+1,h?(i=!0,u?e.$$phase||a.$$phase?e.infiniteScroll():e.$apply(e.infiniteScroll):void 0):i=!1},v=function(a,b){var d,e,f;return f=null,e=0,d=function(){var b;return e=(new Date).getTime(),c.cancel(f),f=null,a.call(),b=null},function(){var g,h;return g=(new Date).getTime(),h=b-(g-e),0>=h?(clearTimeout(f),c.cancel(f),f=null,e=g,a.call()):f?void 0:f=c(d,h,1)}},null!=d&&(o=v(o,d)),e.$on("$destroy",function(){return j.unbind("scroll",o),null!=w?(w(),w=null):void 0}),m=function(a){return t=parseFloat(a)||0},e.$watch("infiniteScrollDistance",m),m(e.infiniteScrollDistance),l=function(a){return u=!a,u&&i?(i=!1,o()):void 0},e.$watch("infiniteScrollDisabled",l),l(e.infiniteScrollDisabled),n=function(a){return x=a},e.$watch("infiniteScrollUseDocumentBottom",n),n(e.infiniteScrollUseDocumentBottom),h=function(a){return null!=j&&j.unbind("scroll",o),j=a,null!=a?j.bind("scroll",o):void 0},h(y),e.infiniteScrollListenForEvent&&(w=a.$on(e.infiniteScrollListenForEvent,o)),k=function(a){if(null!=a&&0!==a.length){if(a instanceof HTMLElement?a=angular.element(a):"function"==typeof a.append?a=angular.element(a[a.length-1]):"string"==typeof a&&(a=angular.element(document.querySelector(a))),null!=a)return h(a);throw new Exception("invalid infinite-scroll-container attribute.")}},e.$watch("infiniteScrollContainer",k),k(e.infiniteScrollContainer||[]),null!=g.infiniteScrollParent&&h(angular.element(f.parent())),null!=g.infiniteScrollImmediateCheck&&(q=e.$eval(g.infiniteScrollImmediateCheck)),c(function(){return q?o():void 0},0,1)}}}]);

var theme;
try {
    var m = /\bTHEME=(\w+?)\b/g.exec(document.cookie);
    theme = m[1];
} catch (e) {}
if (theme && theme != 'none') {
    document.write('<link href="/bootstrap/css/theme-' + theme + '.css" rel="stylesheet">');
}

function goAt(url) {
    window.location.href = url;
}

function _appendScript(url) {
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;

    var head = document.getElementsByTagName('head')[0];
    head.appendChild(script);
}

function _stop(ev) {
    if (typeof ev.stopPropagation != "undefined") {
        ev.stopPropagation();
    } else if (typeof ev.preventDefault != "undefined") {
        ev.preventDefault();
    } else {
        ev.cancelBubble = true;
    }
}

/* http://stackoverflow.com/questions/2144386/javascript-delete-cookie */
function _delete_cookie(name, path, domain) {
    if (get_cookie(name)) {
        document.cookie = name + "=" + ((path) ? ";path=" + path : "") + ((domain) ? ";domain=" + domain : "")
                + ";expires=Thu, 01 Jan 1970 00:00:01 GMT";
    }
}

function _logout() {
    _delete_cookie('JSESSIONID', '/', '');
    _delete_cookie('COOKIEPACK', '/', '');
}

function _popup(popupUrl, onClosed) {
    var ww = 800;
    var hh = 600;

    var left = (screen.width / 2) - (ww / 2);
    var top = (screen.height / 2) - (hh / 2);

    var win = window
            .open(popupUrl, "windowname1", 'width=' + ww + ', height=' + hh + ', top=' + top + ', left=' + left);

    if (win.focus) {
        win.focus();
    }

    var winTimer = setInterval(function() {
        if (win.closed) {
            clearInterval(winTimer);
            if (onClosed) {
                onClosed(popupUrl);
            }
        }
    }, 100);
}

var rapidoidApp = angular.module('rapidoid-app', [ 'infinite-scroll', 'ngSanitize' ]);

function range(from, total) {
    var rangeArr = [];

    for (var i = from; i < from + total; i++) {
        rangeArr.push(i);
    }

    return rangeArr;
}

rapidoidApp.filter('rangex', function() {
    return function(input, from, total) {
        from = parseInt(from);
        total = parseInt(total);
        return range(from, total);
    }
});

rapidoidApp.filter('rowCount', function() {
    return function(input, cols) {
        return range(0, Math.ceil(input.length / cols));
    }
});

rapidoidApp.filter('modn', function() {
    return function(arr, n, remainder) {
        remainder = remainder || 0;
        return arr.filter(function(item, index) {
            return index % n == remainder;
        })
    };
});

// Based on:
// http://stackoverflow.com/questions/17417607/angular-ng-bind-html-unsafe-and-directive-within-it
rapidoidApp.directive('compile', [ '$compile', function($compile) {
    return function(scope, element, attrs) {
        scope.$watch(function(scope) {
            // watch the 'compile' expression for changes
            return scope.$eval(attrs.compile);
        }, function(value) {
            // when the 'compile' expression changes
            // assign it into the current DOM
            element.html(value);

            // compile the new DOM and link it to the current
            // scope.
            // NOTE: we only compile .childNodes so that
            // we don't get into infinite loop compiling ourselves
            $compile(element.contents())(scope);
        });
    };
} ]);

rapidoidApp.controller('Main', [ '$scope', '$http', '$window', function($scope, $http, $window) {

    $scope._emit = function(eventId, eventNav, eventArgs) {

        // _stop(ev);

        var x = document.querySelectorAll("input,textarea");
        var inputs = {};
        for (var i = 0; i < x.length; i++) {
            var t = $(x[i]);
            var _h = t.attr('_h');

            if (_h) {
                var val;

                if (t.prop('type') == 'checkbox' || t.prop('type') == 'radio') {
                    val = t.prop('checked');
                } else {
                    val = t.val();
                }

                inputs[_h] = val;
            }
        }

        x = document.querySelectorAll("option");

        for (var i = 0; i < x.length; i++) {
            var t = $(x[i]);
            var _h = t.attr('_h');

            if (_h) {
                inputs[_h] = t.prop('selected');
            }
        }

        $.post(window.location.href, {
            event : eventId,
            navigational : eventNav,
            args : eventArgs,
            inputs : JSON.stringify(inputs),
            __state : window.__state
        }).done(function(data) {
            if (data._redirect_) {
                goAt(data._redirect_);
                return;
            }

            if (data._state_) {
                window.__state = data._state_;
            }

            if (data["!errors"]) {
                $('.field-error').html('');
                errors = data["!errors"];
                for ( var h in errors) {
                    var err = errors[h];

                    var x = document.querySelectorAll("input,textarea,option");
                    for (var i = 0; i < x.length; i++) {
                        var t = $(x[i]);
                        var _h = t.attr('_h');
                        if (_h == h) {
                            $(t).next('.field-error').html(err);
                        }
                    }
                }
            } else {
                for ( var sel in data._sel_) {
                    if (sel == 'body') {
                        $scope.bodyContent = data._sel_[sel];
                        $scope.$apply();
                    } else {
                        alert('Selector not supported: ' + sel);
                    }
                }
            }
        }).fail(function(data) {
            alert("Error!");
            console.log(data);
        });
    }

    $scope.moreLess = function(item) {
        item.more = !item.more;
    }

    $scope.upvote = function(item) {
        item.vote = item.vote != 1 ? 1 : 0;
    }

    $scope.downvote = function(item) {
        item.vote = item.vote != -1 ? -1 : 0;
    }

    $scope.changeFavLocal = function(item) {
        var favs = JSON.parse(localStorage['favorites'] || '{}');
        item.fav = !item.fav;
        if (item.fav) {
            favs[item.id] = true;
        } else {
            delete favs[item.id];
        }
        localStorage['favorites'] = JSON.stringify(favs);
    }

} ]);

rapidoidApp.factory('StreamData', [ '$http', function($http) {

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

rapidoidApp.controller('StreamController', [ '$scope', '$http', '$window', '$attrs', 'StreamData',
        function($scope, $http, $window, $attrs, StreamData) {
            var dataUrl = $attrs.url;
            $scope.stream = new StreamData(dataUrl);
            $scope.items = $scope.stream.items;
            $scope.cols = 1;
        } ]);

rapidoidApp.controller('StreamItemController', [ '$scope', '$http', '$window', '$attrs',
        function($scope, $http, $window, $attrs) {
            var index = $scope.rowN * $scope.cols + $scope.colN;
            $scope.it = function() {
                return $scope.items[index];
            };
        } ]);
