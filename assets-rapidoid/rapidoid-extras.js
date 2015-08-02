/* Rapidoid Extras */
Rapidoid = (function() {

    var theme;
    try {
        var m = /\bTHEME=(\w+?)\b/g.exec(document.cookie);
        theme = m[1];
    } catch (e) {}
    if (theme && theme != 'none') {
        document.write('<link href="/bootstrap/css/theme-' + theme + '.css" rel="stylesheet">');
    }

    function _goAt(url) {
        window.location.href = url;
    }

    function _setTheme(theme) {
        document.cookie = 'THEME=' + theme + '; path=/';
        location.reload();
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

    function _logout() {
        $.removeCookie('JSESSIONID', '/', '');
        $.removeCookie('COOKIEPACK', '/', '');
        location.reload();
    }

    function _popup(popupUrl, onClosed) {
        var ww = 800;
        var hh = 600;

        var left = (screen.width / 2) - (ww / 2);
        var top = (screen.height / 2) - (hh / 2);

        var win = window.open(popupUrl, "windowname1", 'width=' + ww + ', height=' + hh + ', top=' + top + ', left='
                + left);

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

    function _modal(title, content, footer, options) {
        $('#_modal_title').html(title || '');
        $('#_modal_body').html(content || '');
        if (footer) {
            $('#_modal_footer').html(footer || '');
        }
        $('#_modal_box').modal(options || {});
    }

    var app = angular.module('app', [ 'infinite-scroll', 'ngSanitize', 'ui.bootstrap' ]);

    // Based on:
    // http://stackoverflow.com/questions/17417607/angular-ng-bind-html-unsafe-and-directive-within-it
    app.directive('compile', [ '$compile', function($compile) {
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

    var _scopeInitializers = [];

    function _initializer(initializer) {
        _scopeInitializers.push(initializer);
    }

    function _initScope($scope, $http, $window) {
        for (var i = 0; i < _scopeInitializers.length; i++) {
            _scopeInitializers[i]($scope, $http, $window);
        }
    }

    app.controller('Main', [ '$scope', '$http', '$window', function($scope, $http, $window) {

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
                    _goAt(data._redirect_);
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
                            $scope.ajaxBodyContent = data._sel_[sel];
                            $scope.$apply();
                        } else {
                            alert('Selector not supported: ' + sel);
                        }
                    }
                }
            }).fail(function(data) {
                swal("Communication error", "Couldn't connect to the server!", "error");
                console.log(data);
            });
        }

        _initScope($scope, $http, $window);

    } ]);

    return {
        goAt : _goAt,
        setTheme : _setTheme,
        appendScript : _appendScript,
        logout : _logout,
        popup : _popup,
        modal : _modal,
        app : app,
        initializer : _initializer
    };

})();
