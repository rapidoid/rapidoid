Rapidoid.initializer(function($scope) {

    var reqId = 0;

    $scope._emit = function(event, eventId, eventArgs) {

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
            }, function(){
               doEmit(event, eventId, eventArgs);
            });

        } else {
            doEmit(event, eventId, eventArgs);
        }

    };

    function doEmit(event, eventId, eventArgs) {

        reqId++;

        var btn = $(event.currentTarget);
        var go = btn.data("go");

        eventId = eventId || 'bind';
        eventArgs = eventArgs || [];

        var x = document.querySelectorAll("input,textarea");

        var inputs = {};
        for (var i = 0; i < x.length; i++) {
            var t = $(x[i]);
            var _h = t.attr('_h') || t.attr('id') || t.attr('name');

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
            var _h = t.attr('_h') || t.attr('id') || t.attr('name');

            if (_h) {
                inputs[_h] = t.prop('selected');
            }
        }

        inputs._cmd = eventId;
        inputs._state = window.__state;

        for (var i = 0; i < eventArgs.length; i++) {
            inputs['_' + i] = eventArgs[i];
        }

        btn.append(' <i class="fa fa-refresh fa-spin"></i>');

        $.post(window.location.href, inputs).done(function(data) {

            if (typeof data === 'string' || data instanceof String) {

                if (go && data.indexOf('class="field-error"') < 0) {
                    Rapidoid.goAt(go);
                } else {
                    $scope.ajaxBodyContent = data + '<!--' + reqId + '-->';
                    $scope.$apply();
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
                if (data._sel_ === undefined) {
                    swal("Application error!", "The command couldn't be executed!", "error");
                    return;
                }

                for ( var sel in data._sel_) {
                    if (sel == 'body') {
                        $scope.ajaxBodyContent = data._sel_[sel];
                        $scope.$apply();
                    } else {
                        swal('Selector not supported: ' + sel);
                    }
                }
            }

        }).fail(function(data) {
            $('i:last-child', btn).remove();

            var title = "Server error: " + data.status + "!";
            var msg = data.statusText;

            swal(title, msg, "error");
            console.log(data);
        });
    }

});