/*
MIT license (see http://bootsnipp.com/license)

Derived from: http://bootsnipp.com/snippets/featured/custom-login-registration-amp-forgot-password

Copyright (c) 2013 Bootsnipp.com
Copyright (c) 2016 Nikolche Mihajlovski (MODIFIED the original version)

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

// Options for Message
// ----------------------------------------------
var options = {
	'btn-loading' : '<i class="fa fa-spinner fa-spin"></i>',
	'btn-success' : '<i class="fa fa-check"></i>',
	'btn-error' : '<i class="fa fa-remove"></i>',
	'msg-success' : 'All Good! Redirecting...',
	'msg-error' : 'Wrong login credentials!',
	'useAJAX' : true,
};

$(function (){
// ----------------------------------------------
// Validation

  $("#login-form").validate({
  	rules: {
      lg_username: "required",
  	  lg_password: "required",
    },
  	errorClass: "form-invalid"
  });
  
	// Form Submission
  $("#login-form").submit(function() {
      try {
            remove_loading($(this));
      } catch(e) {
            console.log(e);
      }

      try {
            login($(this));
      } catch(e) {
            console.log(e);
      }

      return false;
  });
	
});

// Loading
// ----------------------------------------------
function remove_loading($form) {
	$form.find('[type=submit]').removeClass('error success');
	$form.find('.login-form-main-message').removeClass('show error success')
			.html('');
}

function form_loading($form) {
	$form.find('[type=submit]').addClass('clicked')
			.html(options['btn-loading']);
}

function form_success($form) {
	$form.find('[type=submit]').addClass('success')
			.html(options['btn-success']);
	$form.find('.login-form-main-message').addClass('show success').html(
			options['msg-success']);
}

function form_failed($form, msg) {
	$form.find('[type=submit]').addClass('error').html(options['btn-error']);
	$form.find('.login-form-main-message').addClass('show error').html(msg);
}

function login($form) {
	if ($form.valid()) {
		form_loading($form);

        var user = $form.find('[name=lg_username]').val();
        var pass = $form.find('[name=lg_password]').val();

        $.post(window.Rapidoid.contextPath + window.Rapidoid.loginUri, {username: user, password: pass}).done(function(data) {

        if (data.success) {
            form_success($form);

            setTimeout(function() {
                location.reload();
            }, 300);
        } else {
            form_failed($form, options['msg-error']);
        }

        }).fail(function(data) {
            swal("Communication error!", "Couldn't connect to the server!", "error");
            console.log(data);
            form_failed($form, "Couldn't connect to the server!");
        });
	}
}
