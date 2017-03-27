#!/usr/bin/env bash
set -euo pipefail

cat << EOF > /usr/local/bin/rapidoid
exec sudo docker run --rm -it --net=host -v \$(pwd):/app:ro rapidoid "\$@"
EOF

cat << EOF > /usr/local/bin/rpd
exec sudo docker run --rm -it --net=host -v \$(pwd):/app:ro rapidoid/rapidoid:snapshot "\$@"
EOF

chmod ugo+x /usr/local/bin/rapidoid
chmod ugo+x /usr/local/bin/rpd
