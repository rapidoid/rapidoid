#!/usr/bin/env bash
set -euo pipefail
IFS=$'\n\t'

DISTRO=dist
DISTRO_JS=$DISTRO/rapidoid.min.js
DISTRO_CSS=$DISTRO/rapidoid.min.css

#curl http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css > rapidoid-html/src/main/resources/default/static/_rapidoid/bootstrap/css/bootstrap-3.3.6.min.css
#curl http://maxcdn.bootstrapcdn.com/font-awesome/4.5.0/css/font-awesome.min.css > rapidoid-html/src/main/resources/default/static/_rapidoid/font-awesome/css/font-awesome-4.5.0.min.css
#curl http://code.jquery.com/jquery-2.2.3.min.js > rapidoid-html/src/main/resources/default/static/_rapidoid/jquery-2.2.3.min.js
#curl http://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js > rapidoid-html/src/main/resources/default/static/_rapidoid/bootstrap/js/bootstrap-3.3.6.min.js
#curl http://ajax.googleapis.com/ajax/libs/angularjs/1.5.8/angular.min.js > rapidoid-html/src/main/resources/default/static/_rapidoid/angular-1.5.8.min.js

# cd assets
# rm ui-bootstrap-tpls.min.js && wget https://cdnjs.cloudflare.com/ajax/libs/angular-ui-bootstrap/0.12.1/ui-bootstrap-tpls.min.js
# cd ..

# JS
curl 'cdn.jsdelivr.net/g/underscorejs,jquery.cookie,jquery.easing,jquery.easy-pie-chart,jquery.validation(jquery.validate.min.js+additional-methods.min.js),jquery.parallax,jquery.prettycheckable(prettyCheckable.min.js),jquery.scrollto,jquery.timeago,angularjs(angular-sanitize.min.js+angular-resource.min.js+angular-animate.min.js+angular-cookies.min.js+angular-route.min.js+angular-loader.min.js+angular-touch.min.js),noty(packaged/jquery.noty.packaged.min.js),numeraljs,sortable,mustache.js,sweetalert,momentjs,select2,medium-editor,dropzone,typeahead.js,sortable,dygraphs,clipboard.js,highlight.js(highlight.min.js)' | sed 's/sourceMappingURL//g' > $DISTRO_JS

cat assets/*.js >> $DISTRO_JS
cat assets/rapidoid/rapidoid-extras.js >> $DISTRO_JS
cat assets/angular/*.js >> $DISTRO_JS

uglifyjs --comments '/^\s*\!|.*(license|MIT|Apache|BSD|http|\(c\)|\.js).*/' -o $DISTRO_JS $DISTRO_JS

# CSS
curl 'cdn.jsdelivr.net/g/sweetalert(sweetalert.css),select2(css/select2.min.css),medium-editor(css/medium-editor.min.css+css/themes/default.min.css),dropzone(dropzone.min.css),highlight.js(styles/default.min.css)' > $DISTRO_CSS

cat assets/*.css >> $DISTRO_CSS
cat assets/rapidoid/rapidoid-extras.css >> $DISTRO_CSS

cleancss -o $DISTRO_CSS $DISTRO_CSS

ls -l $DISTRO_JS
ls -l $DISTRO_CSS

# COPY TO DOCS
for DOCS in ../rapidoid.github.io/ ../java8org.github.io/; do
cp $DISTRO_JS $DOCS/rapidoid.min.js
cp $DISTRO_CSS $DOCS/rapidoid.min.css
cp rapidoid-html/src/main/resources/default/static/_rapidoid/bootstrap/css/theme-default.css $DOCS/theme-default.css
done

cp $DISTRO_JS rapidoid-html/src/main/resources/default/static/
cp $DISTRO_CSS rapidoid-html/src/main/resources/default/static/

# cp ../rapidoid.github.io/docs.css ../java8org.github.io/docs.css

echo
echo
