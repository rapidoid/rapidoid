#!/usr/bin/env bash
set -euo pipefail

cat << EOF > /usr/local/bin/rpd
exec sudo docker run --rm -it --net=host -v \$(pwd):/app:ro rapidoid/rapidoid:snapshot "\$@"
EOF

chmod ugo+x /usr/local/bin/rpd
