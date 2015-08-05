Rapidoid.initializer(function($scope) {

    $scope._emit = function(eventId, eventArgs) {

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
    };

});