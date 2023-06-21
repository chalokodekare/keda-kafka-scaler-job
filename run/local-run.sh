source run/common.sh

docker run --env-file run/.envfile -d -p 1818:1818 $app:$buildTag

cmd /k
