package com.johnson.john;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import javax.swing.AbstractAction;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ctsHelper {
	private String jarname="";
	private int android_id=1;
	private String cts_root="";
    private String packagenameString="";
	public ctsHelper(String jarname, int android_id, String cts_root,String packagenameString) throws Exception {
		// TODO Auto-generated constructor stub
		this.jarname=jarname;
		this.android_id=android_id;
		this.cts_root=cts_root;
		this.packagenameString=packagenameString;
	}
	
	public static void main(String[] args) throws Exception {
		String jarname="";
		int android_id=1;
		String cts_root="";
		String packagenameString="";
        for(int i=0;i<args.length;i++){
        	if(args[i].equals("--jarname")){
        		jarname=args[i+1];
        		System.out.println(args[i+1]);
        	}else 
        	if(args[i].equals("--android_id")){
        		android_id=Integer.valueOf(args[i+1]);
        		System.out.println(args[i+1]);
        	}else 
        	if(args[i].equals("--cts_root")){
        		cts_root=args[i+1];
        		System.out.println(args[i+1]);
        	}else 
        	if(args[i].equals("--packagenameString")){
        		packagenameString=args[i+1];
        		System.out.println(args[i+1]);
        		}
        }
        ctsHelper cts=new ctsHelper(jarname, android_id, cts_root, packagenameString);
        cts.runcts();
	}

	public void runcts() throws Exception {
		// TODO Auto-generated method stub
		createbuildfile();
		editbuildfile();
		buildwithant();
		copyjartotestcase();
		createtestcasexml();
		String testplanname=createtestplan();
		runctswithplan(testplanname);
		//runctswithplansh(testplanname);
	}

	private void createbuildfile() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("createbuildfile-----------------");
		String createbuildfilecmd="android create uitest-project -n "+jarname+" -t "+android_id+" -p "+getworkspace();
		execmd(createbuildfilecmd);
	}

	private void execmd(String createbuildfilecmd) throws IOException {
		// TODO Auto-generated method stub
		Process p = Runtime.getRuntime().exec(createbuildfilecmd);
		InputStream is=p.getInputStream();
		String lineString = "";
		BufferedReader bReader=new BufferedReader(new InputStreamReader(is));
		while((lineString=bReader.readLine())!=null){
			System.out.println(lineString);
		}
	}

	private String getworkspace() {
		// TODO Auto-generated method stub
		File emptyFile=new File("");
		return emptyFile.getAbsolutePath();
	}

	private void editbuildfile() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("editbuildfile-------------");
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

	private void buildwithant() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("buildwithant---------------------");
		String buildwithantcmdString = "ant -buildfile build.xml";
		execmd(buildwithantcmdString);
	}

	private void copyjartotestcase() throws IOException {
		// TODO Auto-generated method stub
		System.out.println("copyjartotestcase---------------------");
		String srcjar=getworkspace()+File.separator+"bin"+File.separator+jarname+".jar";
		String destjar=cts_root+"android-cts/repository/testcases/"+jarname+".jar";
		byte[] buffer=new byte[1024];
		FileInputStream inputStream=new FileInputStream(new File(srcjar));
		File destjarFile = new File(destjar);
		int len=0;
		FileOutputStream outputStream=new FileOutputStream(destjarFile);
		while((len = inputStream.read(buffer))!=-1){
			outputStream.write(buffer, 0, len);
			outputStream.flush();
		}
		
		inputStream.close();
		outputStream.close();
		System.out.println(destjarFile.getAbsolutePath());
	}

	private void createtestcasexml() throws ParserConfigurationException, IOException, Exception {
		// TODO Auto-generated method stub
		System.out.println("createtestcasexml------------------------");
		DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        
        Element root= document.createElement("TestPackage");
        String [] splitpackageStrings=packagenameString.split("\\.");
        System.out.println("splitpackageStrings="+splitpackageStrings[0]);
        Element parent=root;
       
        System.out.println("package = "+packagenameString);
        document.appendChild(root);
        root.setAttribute("appPackageName", packagenameString+"."+jarname);
        root.setAttribute("name", jarname);
        root.setAttribute("testType", "uiAutomator");
        root.setAttribute("jarPath", jarname+".jar");
        root.setAttribute("version", "1.0");
        
        String subpath=File.separator+"src"+File.separator;
        
        for(String splitString :splitpackageStrings){
        	System.out.println(splitString);
            Element testsuite= document.createElement("TestSuite");
            testsuite.setAttribute("name", splitString);
            subpath+=splitString+File.separator;
        	parent.appendChild(testsuite);
        	parent=testsuite;
        }
        
        Element testcase= document.createElement("TestCase");
        testcase.setAttribute("name", jarname);
        parent.appendChild(testcase);
        parent = testcase;
        
        
        String classnameString=packagenameString+"."+jarname;

//        URL url=new URL("file:"+getworkspace()+File.separator+"bin"+File.separator+jarname+".jar");
//        System.out.println("url--------------");
//        URLClassLoader loader=new URLClassLoader(new URL[]{url},Thread.currentThread().getContextClassLoader());
//        System.out.println("loader------------");
//        loader.loadClass(classnameString);
//        System.out.println("loader complete!-----------");
        
//        System.out.println(classnameString);
//        Class<?> clazz= Class.forName(classnameString);
//        Method method[]=clazz.getMethods();
//        for(Method method2:method){
//        	//System.out.println(method2.getName());
//        	if(method2.getName().matches("test.*?"))
//        	{
//        		System.out.println(method2.getName());
//        		Element test= document.createElement("Test");
//        		test.setAttribute("name", method2.getName());
//        		parent.appendChild(test);
//        	}
//        }
        
        File javaFile=new File(getworkspace()+subpath+jarname+".java");
        if(javaFile.exists()){
        	String lineString="";
        	BufferedReader bf=new BufferedReader(new FileReader(javaFile));
        	while((lineString=bf.readLine())!=null){
        		if(lineString.matches(".*public\\s+void\\s+test.*")){
        			int index_0=lineString.indexOf("test");
             	   	int index_1=lineString.indexOf("(");
        			System.out.println(lineString.substring(index_0, index_1));
        			Element test= document.createElement("Test");
        			test.setAttribute("name", lineString.substring(index_0, index_1));
        			parent.appendChild(test);
        		}
        	}
        }
        else{
        	System.out.println("javafile is not exists");
        }
        
        
        
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        String xmlfilepath=cts_root+"android-cts/repository/testcases/"+jarname+".xml";
        PrintWriter pw = new PrintWriter(new FileOutputStream(xmlfilepath));
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
        System.out.println("生成XML文件成功!");
        
	}

	private String createtestplan() throws Exception {
		// TODO Auto-generated method stub
		System.out.println("createtestcasexml------------------------");
		DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        
        Element root= document.createElement("TestPlan");
        root.setAttribute("version", "1.0");
        document.appendChild(root);
       
        String classnameString=packagenameString+"."+jarname;
//        System.out.println(classnameString);
//        Class<?> clazz= Class.forName(classnameString);
//        Method method[]=clazz.getMethods();
//        for(Method method2:method){
//        	//System.out.println(method2.getName());
//        	if(method2.getName().matches("test.*?"))
//        	{
//        		System.out.println(method2.getName());
//        		Element entry= document.createElement("Entry");
//        		test.setAttribute("include", classnameString+"#"+method2.getName());
//        		test.setAttribute("name", classnameString);
//        		root.appendChild(test);
//        	}
//        }
        Element entry= document.createElement("Entry");
        entry.setAttribute("name", classnameString);
        root.appendChild(entry);
        
        
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        DOMSource source = new DOMSource(document);
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
        String xmlfilepath=cts_root+"android-cts/repository/plans/"+"CTS-"+jarname+".xml";
        PrintWriter pw = new PrintWriter(new FileOutputStream(xmlfilepath));
        StreamResult result = new StreamResult(pw);
        transformer.transform(source, result);
        System.out.println("生成XML文件成功!");
        return("CTS-"+jarname);
        
		
	}

	private void runctswithplan(String testplanname) throws Exception {
		//java -cp /home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/android-cts/tools/tradefed-prebuilt.jar:/home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/android-cts/tools/hosttestlib.jar:/home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/android-cts/tools/cts-tradefed.jar -DCTS_ROOT="/home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/" com.android.cts.tradefed.command.CtsConsole run cts --plan CTS-TestDemo --skip-preconditions
		// TODO Auto-generated method stub
		System.out.println("Runctswithplan-----------------------");
		String jarfilepathString=cts_root+"android-cts/tools/tradefed-prebuilt.jar:"+cts_root+"android-cts/tools/hosttestlib.jar:"+cts_root+"android-cts/tools/cts-tradefed.jar";
		String runctswithplancmdString="java -cp "+jarfilepathString+" -DCTS_ROOT="+cts_root+" com.android.cts.tradefed.command.CtsConsole run cts --plan "+testplanname+" --skip-preconditions";
		System.out.println(runctswithplancmdString);
		execmd(runctswithplancmdString);
	}
	private void runctswithplansh(String testplanname) throws Exception {
		System.out.println("Runctswithplan-----------------------");
		execmd("chmod 777 "+getworkspace()+File.separator+"test.sh");
		String finalcmd=getworkspace()+File.separator+"test.sh"+" \""+cts_root+"\""+" \""+testplanname+"\"";
		System.out.println(finalcmd);
		execmd(finalcmd);
	}
}

