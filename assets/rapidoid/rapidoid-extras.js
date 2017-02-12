/*!
 * Rapidoid Extras
 * Author: Nikolche Mihajlovski
 * Licence: MIT
 */
window.Rapidoid = window.Rapidoid || {};

(function() {

    var Rapidoid = window.Rapidoid;

    // var theme;
    // try {
    // var m = /\bTHEME=(\w+?)\b/g.exec(document.cookie);
    // theme = m[1];
    // } catch (e) {}
    // if (theme && theme != 'none') {
    // document.write('<link href="/bootstrap/css/theme-' + theme + '.css"
    // rel="stylesheet">');
    // }

    var refresher;

    function _init() {
        if (Rapidoid.initialized) {
            return;
        }
        Rapidoid.initialized = true;

        $(function () {

        $('[data-toggle="tooltip"]').tooltip();

        $('.pretty').each(function() {
            $(this).prettyCheckable({ color: 'blue' });
        });

        $(document).ready(function() {
            $("select.select2").select2();
        });

          $('.copy-snippet').prepend('<button class="btn btn-default pull-right" data-copy-to-clipboard><i class="fa fa-copy"></i></button>');

          var clipboard = new Clipboard('[data-copy-to-clipboard]',{
            target:function(trigger){
              return trigger.nextElementSibling;
            }
          });

          clipboard.on('success', function(e){
            e.clearSelection();

            $(e.trigger).tooltip({'title': 'Copied!'});
            $(e.trigger).tooltip('show');

            setTimeout(function() {
                $(e.trigger).tooltip('destroy');
            }, 500);
          });

        });
    }

    function onServerError(data) {
        $('i:last-child', btn).remove();

        var title = data.status ? "Server error: " + data.status + "!" : "Cannot connect to the server!";
        var msg = data.statusText != "error" ? data.statusText : "";

        swal(title, msg, "error");
        console.log(data);
    }

    function _refresh() {
        var loc = Rapidoid.location || window.location.href;

        $.get(loc, {__event__: true}).done(function (data) {
            Rapidoid.setHtml(data);
        }).fail(onServerError);
    }

    function setAutoRefreshInterval(refreshInterval) {
        if (refresher !== undefined) {
            clearInterval(refresher);
        }
        refresher = window.setInterval(_refresh, refreshInterval);
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
        $.removeCookie('_token', '/', '');
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

    var scopeInitializers = [];

    function initializer(init) {
        scopeInitializers.push(init);
    }

    function _initScope($scope) {
        Rapidoid.scope = $scope;

        for (var i = 0; i < scopeInitializers.length; i++) {
            scopeInitializers[i]($scope);
        }
    }

    var plugins = [];

    function plugin(pl) {
        plugins.push(pl);
    }

    function runPlugins(app) {
        for (var i = 0; i < plugins.length; i++) {
            plugins[i](app);
        }
    }

    function initApp(app) {
        runPlugins(app);
    }

    function initMain($scope) {
        _initScope($scope);
    }

    function createApp(main, extraDependencies) {
        var dependencies = [ 'infinite-scroll', 'ngSanitize', 'ui.bootstrap' ];

        if (extraDependencies) {
            for (var i = 0; i < extraDependencies.length; i++) {
                dependencies.push(extraDependencies[i]);
            }
        }

        var app = angular.module('app', dependencies);

        initApp(app);

        app.controller('Main', [ '$scope', '$http', '$window', '$attrs', function($scope, $http, $window, $attrs) {
            initMain($scope);
            main($scope, $http, $window, $attrs);
        } ]);

        return app;
    }

    var idgen = 0;
    function setHtml(html) {
        Rapidoid.initialized = false;
        Rapidoid.scope[Rapidoid.holder] = html + '<!--' + (idgen++) + '-->';
        Rapidoid.scope.$apply();
    }

    Rapidoid.goAt = _goAt;
    Rapidoid.setTheme = _setTheme;
    Rapidoid.appendScript = _appendScript;
    Rapidoid.logout = _logout;
    Rapidoid.popup = _popup;
    Rapidoid.modal = _modal;
    Rapidoid.init = _init;
    Rapidoid.initialized = false;
    Rapidoid.setHtml = setHtml;
    Rapidoid.refresh = _refresh;
    Rapidoid.setAutoRefreshInterval = setAutoRefreshInterval;
    Rapidoid.onServerError = onServerError;

    Rapidoid.createApp = createApp;
    Rapidoid.initializer = initializer;
    Rapidoid.plugin = plugin;
    Rapidoid.initApp = initApp;
    Rapidoid.holder= 'ajaxBodyContent';

})();

window.$R = (function() {

    function get(url, onDone) {
        return $.get(url).done(onDone).fail(function(data) {
            swal("Communication error!", "Couldn't connect to the server!", "error");
            console.log(data);
        });
    }

    function post(url, data, onDone) {
        return $.post(url, data).done(onDone).fail(function(data) {
            swal("Communication error!", "Couldn't connect to the server!", "error");
            console.log(data);
        });
    }

    return {
            get : get,
            post : post,
            data : {}
    };

})();
