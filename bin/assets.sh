DISTRO=rapidoid-html/src/main/resources/public
DISTRO_JS=$DISTRO/rapidoid.min.js
DISTRO_CSS=$DISTRO/rapidoid.min.css

# JS
curl 'cdn.jsdelivr.net/g/underscorejs,jquery.cookie,jquery.easing,jquery.easy-pie-chart,jquery.parallax,jquery.prettycheckable,jquery.scrollto,jquery.timeago,angularjs(angular-sanitize.min.js+angular-resource.min.js+angular-animate.min.js+angular-cookies.min.js+angular-route.min.js+angular-loader.min.js+angular-touch.min.js),noty(packaged/jquery.noty.packaged.min.js),numeraljs,sortable,chartist.js,jquery.flot(jquery.flot.min.js+jquery.colorhelpers.min.js+jquery.flot.canvas.min.js+jquery.flot.categories.min.js+jquery.flot.crosshair.min.js+jquery.flot.errorbars.min.js+jquery.flot.fillbetween.min.js+jquery.flot.image.min.js+jquery.flot.navigate.min.js+jquery.flot.pie.min.js+jquery.flot.resize.min.js+jquery.flot.selection.min.js+jquery.flot.stack.min.js+jquery.flot.symbol.min.js+jquery.flot.threshold.min.js+jquery.flot.time.min.js),mustache.js' | sed 's/sourceMappingURL//g' > $DISTRO_JS
cat assets/*.js >> $DISTRO_JS
cat assets-rapidoid/rapidoid-extras.js | uglifyjs >> $DISTRO_JS
ls -l $DISTRO_JS

# CSS
curl 'cdn.jsdelivr.net/g/chartist.js(chartist.min.css)' > $DISTRO_CSS
cat assets/*.css >> $DISTRO_CSS
cat assets-rapidoid/rapidoid-extras.css >> $DISTRO_CSS
ls -l $DISTRO_CSS

# TODO: angular.textangular(textAngular-sanitize.min.js+textAngular.min.js),angular.angucomplete-alt,angular.file-upload,bootstrap.lightbox
