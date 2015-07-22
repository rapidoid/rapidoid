$(function() {
    $.get('template.html', function(templ) {
        $.getJSON('demo.json', function(model) {
            try {
                var rendered = Mustache.render(templ, model);
            } catch (e) {
                alert('TEMPLATE ERROR:\n ' + e.message);
            }

            $('#placeholder').html(rendered);
        }).error(function(e, msg) {
            alert(msg);
        });
    });
});
