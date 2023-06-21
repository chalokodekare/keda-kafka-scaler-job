source run/common.sh

gradle bootjar

docker buildx build --build-arg app=$app --build-arg version=$version \
  --tag $app:$buildTag .

cmd /k
