// Derived from: http://bootsnipp.com/snippets/featured/simple-vertical-tab (Copyright (c) 2013 Bootsnipp.com, MIT license, see http://bootsnipp.com/license)

$(document).ready(function() {
	$.get('https://code.jquery.com/jquery-2.1.1.min.js',function(){
		$("div.bhoechie-tab-menu>div.list-group>a").on("click",function(e) {
			e.preventDefault();
			$(this).siblings('a.active').removeClass("active");
			$(this).addClass("active");
			var index = $(this).index();
			$("div.bhoechie-tab>div.bhoechie-tab-content").removeClass("active");
			$("div.bhoechie-tab>div.bhoechie-tab-content").eq(index).addClass("active");
		});
	});
});