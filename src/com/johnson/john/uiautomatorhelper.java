package com.johnson.john;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class uiautomatorhelper {
	private static int android_id = 1;
	private static String jar_name = "";
	private static String test_class = "";
	private static String test_name = "";
	private static String jarfileabs="";
	public uiautomatorhelper(String jarName, String testClass, String testName,int androidId) throws IOException{
		jar_name=jarName;
		test_class=testClass;
		test_name=testName;
		android_id=androidId;
		jarfileabs=getworkspace()+File.separator+"bin"+File.separator+jar_name+".jar";
		runtest();
	}
	public uiautomatorhelper(String jarName, String testClass, int androidId) throws IOException{
		jar_name=jarName;
		test_class=testClass;
		android_id=androidId;
		jarfileabs=getworkspace()+File.separator+"bin"+File.separator+jar_name+".jar";
		runtest();
	}
	public uiautomatorhelper(String jarName, String testClass, String testName) throws IOException{
		jar_name=jarName;
		test_class=testClass;
		test_name=testName;
		jarfileabs=getworkspace()+File.separator+"bin"+File.separator+jar_name+".jar";
		runtest();
	}
	
	private void runtest() throws IOException {
		// TODO Auto-generated method stub
		createxml();
		modifyxml();
		buildwithant();
		pushjarfile();
		exetest();
		
	}
	private void createxml() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Create build.xml -------------------");
		if(!(xmlisexist())){
			String createxmlcmd = "android create uitest-project -n "+jar_name+" -t "+android_id+" -p "+getworkspace()+"";
			execcmd(createxmlcmd);
		}
	}
	
	private void modifyxml() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Modify build.xml ----------------------");
		if(xmlisexist()){
			File buildxmlFile=new File("build.xml");
			BufferedReader bReader=new BufferedReader(new InputStreamReader(new FileInputStream(buildxmlFile)));
			String lineString = "";
			StringBuffer sBuffer=new StringBuffer();
			while((lineString=bReader.readLine())!=null){
				if (lineString.matches(".*help.*")){
					lineString=lineString.replaceAll("help", "build");
				}
				sBuffer.append(lineString+System.getProperty("line.separator"));
			}
			bReader.close();
			BufferedWriter bWriter=new BufferedWriter(new FileWriter(new File("build.xml")));
			bWriter.write(sBuffer.toString());
			bWriter.close();
		}	
	}
	private void buildwithant() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Ant build jar -------------");
		if(xmlisexist()){
			String antcmd = "ant -buildfile build.xml";
			execcmd(antcmd);
		}
		
	}
	private void pushjarfile() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Push jar to device---------------");
		if(jarexist()){
			String pushcmd="adb push "+jarfileabs+" /data/local/tmp/";
			execcmd(pushcmd);
		}
		
	}

	private void exetest() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("Execute uiautomator test-------------");
		String execmdString="";
		if(test_name!=""){
			execmdString="adb shell uiautomator runtest "+jar_name+".jar -c "+test_class+"#"+test_name;
		}
		else{
			execmdString="adb shell uiautomator runtest "+jar_name+".jar -c "+test_class;
		}
		execcmd(execmdString);
		
	}
	
	private void execcmd(String createxmlcmd) throws IOException {
		// TODO Auto-generated method stub
		InputStream inputStream =  Runtime.getRuntime().exec(createxmlcmd).getInputStream();
		String lineString="";
		BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream));
		while((lineString=bf.readLine())!=null){
			System.out.println(lineString);
		}
	}
	private String getworkspace() {
		File emptyFile = new File("");
		return emptyFile.getAbsolutePath();
	}
	private boolean xmlisexist() {
		File buildfile = new File("build.xml");
		if(buildfile.exists())
		{
			return true;
		}
		return false;
	}
	private boolean jarexist() {
		// TODO Auto-generated method stub
		File jarfileFile=new File(jarfileabs);
		if(jarfileFile.exists()){
			return true;
		}
		return false;
	}
	
}
