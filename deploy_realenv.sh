echo "###### remove plugins ######"
rm ~/.astah/sysml/plugins/model-validation-*.jar
rm ~/.astah/sysml/plugins/sysml4rtm-*.jar

echo "###### build model-validation ######"
cd ../model-validation
astah-build
astah-mvn install
cp target/model-validation-1.0-SNAPSHOT.jar ~/.astah/sysml/plugins

echo "###### build sysml4rtm ######"
cd ../sysml4rtm
astah-build
cp target/sysml4rtm-1.0-SNAPSHOT.jar ~/.astah/sysml/plugins

rm -r ~/.astah/sysml/cache
