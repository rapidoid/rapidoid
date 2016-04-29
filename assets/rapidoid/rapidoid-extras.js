$(function () {
  $('[data-toggle="tooltip"]').tooltip();

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

})

/* Rapidoid Extras */
Rapidoid = (function() {

    // var theme;
    // try {
    // var m = /\bTHEME=(\w+?)\b/g.exec(document.cookie);
    // theme = m[1];
    // } catch (e) {}
    // if (theme && theme != 'none') {
    // document.write('<link href="/bootstrap/css/theme-' + theme + '.css"
    // rel="stylesheet">');
    // }

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

    var scopeInitializers = [];

    function initializer(init) {
        scopeInitializers.push(init);
    }

    function _initScope($scope) {
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

    return {
        goAt : _goAt,
        setTheme : _setTheme,
        appendScript : _appendScript,
        logout : _logout,
        popup : _popup,
        modal : _modal,

        createApp : createApp,
        initializer : initializer,
        plugin : plugin,
        initApp : initApp
    };

})();

$R = (function() {

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
            post : post
    };

})();