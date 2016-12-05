cts_root=$1
testplan=$2
gnome-terminal -x bash -c "cd ${cts_root}/android-cts/tools;java -cp tradefed-prebuilt.jar:hosttestlib.jar:cts-tradefed.jar -DCTS_ROOT=${cts_root} com.android.cts.tradefed.command.CtsConsole run cts --plan ${testplan} --skip-preconditions;exec bash"
