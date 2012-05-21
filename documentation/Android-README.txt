Android

==== Installing Android SDK and Emulator ====

1. Download the android SDK from android website (http://developer.android.com/sdk/index.html)

2. Unzip the file into your file system and navigate to it.

3. run the SDK Manager.exe ( This executable will install the android platforms and the emulator ).

4. Install the ADT plugin for Eclipse IDE, you can do so by setting up a remote update site(from Eclipse > Help > Update Software .. > add site)  at https://dl-ssl.google.com/android/eclipse/

5. You may need to add the Android SDK to your project by going to Properties->Android->Browse to your Android SDK folder

For more Help on this visit the android website (http://developer.android.com/sdk/index.html)




==== Creating an Android Virtual Device(AVD) ====


to start using the emulator we need to create an android virtual device.

To create an AVD:

1. In Eclipse, choose Window > Android SDK and AVD Manager.

2. Select Virtual Devices in the left panel.

3. Click New.
    The Create New AVD dialog appears.

4. Type the name of the AVD, such as "my_avd".

5. Choose a target. The target is the platform (that is, the version of the Android SDK, such as 2.1) you      want to run on the emulator.
    You can ignore the rest of the fields for now.

6. Click Create AVD.



For more help visit the android website (http://developer.android.com/resources/tutorials/hello-world.html)


==== Importing the required libraries ====

You need to import the following libraries which are found in the lib folder:
1. android-json-rpc-me.jar
2. cglib-2.2.jar
3. jsonrpc-mine.jar
4. servlet-2.3.jar



==== Creating a Run Configuration ====


The run configuration specifies the project to run, the Activity to start, the emulator or connected device to use, and so on.

1.Open the run configuration manager.
	*In Eclipse 3.3 (Europa), select Run > Open Run Dialog (or Open Debug Dialog)
	*In Eclipse 3.4 (Ganymede), select Run > Run Configurations (or Debug Configurations)

2.Expand the Android Application item and create a new configuration or open an existing one.
	*To create a new configuration:
		1. Select Android Application and click the New launch configuration icon above 				 the list (or, right-click Android Application and click New).
		2. Enter a Name for your configuration.
		3. In the Android tab, browse and select the project you'd like to run with the 				configuration.
	*To open an existing configuration, select the configuration name from the list nested below 	Android Application.

3.Adjust your desired launch configuration settings.



==== Running an Android application on an actual device ====

1. Declare the application as "debuggable" in the Android Manifest.
	- In Eclipse, open the AndroidManifest.xml file.
	- Click the "Application" tab.
	- Set "Debuggable" to "true".

2. Turn on "USB Debugging" on the Android device.
	- Go to Settings > Applications > Development.
	- Turn on USB debugging.
	
3. Install the USB drivers on Windows for the Android device.
	- Connect the device to the computer via the USB cable.
	- A list of drivers for various venders can be found at: 
		http://developer.android.com/sdk/oem-usb.html
	- NOTE: Install the Motorola driver to use the HaiPad 701-R.
	
4. Run the application on the device.
	- Now run your application in Eclipse.
	- At the "Run As" popup window select "Android Application".
	- If Eclipse asks you where you would like to launch the application 
		select "device" rather than the virtual device.
	
SOURCE: http://developer.android.com/guide/developing/device.html



==== Running the Test Cases from Android ====


1. Download the pdstore android project from the repository

2. Import the project into Eclipse

3. Go to Window -> Preferences -> Android -> SDK Location : Select your android sdk directory Then Click Apply and OK.

4. Go to AndroidManifest.xml and Uncomment the commented lines.

5. In your package explorer, go to src/nz.ac.auckland.pdstoreandroid/ and right click on android.java, then select Build-Path then click on Exclude

6. After that go to src/pdstore.changelog/ and open ChangeLogStore.java , make sure that the constant DEFAULT_PATH = "/data/data/nz.ac.auckland.pdstoreandroid/"

7. Finally, go to the Test source folder and select the class you want to test then right click > Run as > Android Junit Test


NOTE: If you want to run all the test classes in the test suite then you have to :
1. Go to Run from the top menu, then Run Configurations.
2. Select Android Junit Test
3. Click on new icon on the top left of the window
4. Select the radio button that says " Run all tests inside the project,package "
5. Select the instrumentation runner from Instrumentation Runner: drop down menu




==== Adding new Test classes ====



1. After you finish writing the test class with all the test cases, put the test class in the Test Source folder under the package "nz.ac.auckland.pdstoreandroid.tests"

2. Add your Test class to the test suite, to do so open the java file named MyInstrumentationTestRunner.java (which is located in the package "nz.ac.auckland.pdstoreandroid.tests")

3. In the method getAllTests() add the following command :
    suite.addTestSuite(yourTestClassName.class);

4. Make sure that your test cases are not preceded by @Test or @Before or @After because those markers are not supported by android platform. So remove them!

5. Make sure that all your test cases names start with the word test. for example :
    public void testSomething() {}
    public void testReverseFunction() {}

