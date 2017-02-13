Rapidoid.initializer(function ($scope) {

    $scope._emit = function (event, eventId, eventArgs) {

        event.preventDefault();
        event.stopPropagation();

        var btn = $(event.currentTarget);
        var confirm = btn.data("confirm");

        if (confirm) {
            swal({
                title: "Are you sure?",
                text: confirm,
                type: "warning",
                showCancelButton: true,
                confirmButtonColor: "#DD6B55",
                confirmButtonText: "Yes!",
                closeOnConfirm: true
            }, function () {
                doEmit(event, eventId, eventArgs);
            });

        } else {
            doEmit(event, eventId, eventArgs);
        }

    };

    function doEmit(event, eventId, eventArgs) {

        var btn = $(event.currentTarget);
        var go = btn.data("go");

        eventId = eventId || 'bind';
        eventArgs = eventArgs || [];

        var x = document.querySelectorAll("input,textarea");

        var inputs = {};

        for (var p in $R.data) {
            var param = $R.data[p];
            inputs[p] = (typeof (param) == 'function') ? param() : param;
        }

        for (var i = 0; i < x.length; i++) {
            var t = $(x[i]);
            var _h = t.attr('_h') || t.attr('name') || t.attr('id');

            if (_h) {
                var val;
                var type = t.prop('type');

                if (type == 'radio') {
                    if (t.prop('checked')) {
                        inputs[_h] = t.val();
                    }

                } else if (type == 'checkbox') {
                    if (t.prop('checked')) {
                        if (inputs[_h] === undefined) {
                            inputs[_h] = [];
                        }
                        inputs[_h].push(t.val());
                    }

                } else {
                    inputs[_h] = t.val();
                }
            }
        }

        x = document.querySelectorAll("select");

        for (var i = 0; i < x.length; i++) {
            var t = $(x[i]);
            var _h = t.attr('_h') || t.attr('id') || t.attr('name');

            if (_h) {
                inputs[_h] = t.val();
            }
        }

        inputs._cmd = eventId;
        inputs._state = window.__state;

        for (var i = 0; i < eventArgs.length; i++) {
            inputs['_' + i] = eventArgs[i];
        }

        btn.append(' <i class="fa fa-refresh fa-spin"></i>');

        var loc = Rapidoid.location || window.location.href;
        $.post(loc, inputs).done(function (data, textStatus, request) {

            if (typeof data === 'string' || data instanceof String) {

                if (data.indexOf('class="field-error"') < 0) {

                    var redir = request.getResponseHeader('X-Rapidoid-Redirect');

                    if (redir) {
                        Rapidoid.goAt(redir);
                        return;

                    } else {
                        if (go) {
                            Rapidoid.goAt(go);
                            return;

                        } else {
                            inputs.__event__ = true;

                            $.get(loc, inputs).done(function (data) {
                                Rapidoid.setHtml(data);
                            }).fail(Rapidoid.onServerError);

                        }
                    }

                } else {
                    Rapidoid.setHtml(data);
                }

                return;
            }

            if (data._redirect_) {
                Rapidoid.goAt(data._redirect_);
                return;
            }

            if (data._state_) {
                window.__state = data._state_;
            }

            if (data["!errors"]) {
                $('.field-error').html('');

                errors = data["!errors"];

                for (var h in errors) {
                    var err = errors[h];
                    var x = document.querySelectorAll("input,textarea,option");

                    for (var i = 0; i < x.length; i++) {
                        var t = $(x[i]);
                        var _h = t.attr('_h');

                        if (_h == h) {
                            $(t).next('.field-error').html(err);
                        }
                    }

                    $("body").addClass("with-validation-errors");
                }

            } else {
                if (data._sel_ === undefined) {
                    swal("Application error!", "The command couldn't be executed!", "error");
                    return;
                }

                for (var sel in data._sel_) {
//                    if (sel == 'body') {
//
//                        $scope.ajaxBodyContent = data._sel_[sel];
//                        $scope.$apply();
//
//                    } else {
                    swal('Selector not supported: ' + sel);
//                    }
                }
            }

        }).fail(Rapidoid.onServerError);
    }

});
