VERSION="1.4.4"
PLAY_LOC="/usr/local/share"
PLAY_PATH="${PLAY_LOC}/play-${VERSION}"
LOG_FILE="./system.log"
PLAY_COMMAND="${PLAY_PATH}/play"

if [ ! -f $PLAY_COMMAND ] ;
then
  PLAY_ARCH="${PLAY_LOC}/play-${VERSION}.zip";
  PLAY_URL="https://downloads.typesafe.com/play/${VERSION}/play-${VERSION}.zip";
  wget -O $PLAY_ARCH $PLAY_URL;
  unzip $PLAY_ARCH -d $PLAY_LOC ;
fi

$PLAY_COMMAND stop . --%prod

killall  java

git pull origin arash.play

$PLAY_COMMAND deps .
$PLAY_COMMAND clean .
$PLAY_COMMAND start . --%prod
