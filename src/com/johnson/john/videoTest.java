package com.johnson.john;


import com.android.uiautomator.core.UiDevice;
import com.android.uiautomator.core.UiObject;
import com.android.uiautomator.core.UiObjectNotFoundException;
import com.android.uiautomator.core.UiSelector;
import com.android.uiautomator.testrunner.UiAutomatorTestCase;

public class videoTest extends UiAutomatorTestCase {
	UiDevice device = UiDevice.getInstance();
	public static void main(String[] args) throws Exception {
		String jarname="videoTest";
		int android_id=1;
		String cts_root="/home/jian/Downloads/adt-bundle-linux-x86_64-20140702/sdk/";
		String packagenameString="com.johnson.john";
		ctsHelper cts= new ctsHelper(jarname,android_id,cts_root,packagenameString);
		cts.runcts();
		
	}
	public void testVideo() throws UiObjectNotFoundException{
		device.pressHome();
		UiObject browser = new UiObject(new UiSelector().text("Chrome"));
		browser.clickAndWaitForNewWindow();
		UiObject editObject = new UiObject(new UiSelector().className("android.widget.EditText"));
		editObject.click();
		device.pressDelete();
		editObject.setText("www.video.com");
		device.pressEnter();
		device.pressHome();
	}
	public void testxml1() throws Exception {
		device.pressRecentApps();
	}
	public void testxml2() throws Exception {
		device.pressRecentApps();
	}
	public void testxml3() throws Exception {
		device.pressRecentApps();
	}
}



