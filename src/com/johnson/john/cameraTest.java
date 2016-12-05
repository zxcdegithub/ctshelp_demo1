package com.johnson.john;

import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class cameraTest extends UiAutomatorTestCase {
	public static void main(String[] args) throws Exception {
		String jarname="cameraTest";
		int android_id=1;
		String cts_root="/home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/";
		String packagenameString="com.johnson.john";
		new ctsHelper(jarname,android_id,cts_root,packagenameString);
	}
	
	public void testCamera1() throws UiObjectNotFoundException{
		UiDevice.getInstance().pressHome();
		UiObject browser=new UiObject(new UiSelector().text("Chrome"));
		browser.clickAndWaitForNewWindow();
		UiObject edit=new UiObject(new UiSelector().className("android.widget.EditText"));
		edit.click();
		UiDevice.getInstance().pressDelete();
		edit.setText("www.camera.com");
		UiDevice.getInstance().pressEnter();
	}

}
