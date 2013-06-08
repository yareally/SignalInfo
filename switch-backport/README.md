Android Switch Widget Backport
==============================

A backport of the Switch widget (http://developer.android.com/reference/android/widget/Switch.html)
that was introduced on Android 4.

This port works on Android 2.1+.

How to use
----------

This is an Android library project, you have to add it as a dependency to your project (please
see http://developer.android.com/guide/developing/projects/projects-eclipse.html#ReferencingLibraryProject to
know how to do that.)

Alternatively, you can use it as a Maven dependency (the artefact is pushed to Maven Central) by putting this 
in your pom.xml:
```xml
<dependency>
    <groupId>org.jraf</groupId>
    <artifactId>android-switch-backport</artifactId>
    <version>1.0</version>
    <type>apklib</type>
</dependency>
```

### Switch

Once you have done that, have a theme for your application (or Activity), that declares the `switchStyle` item
to be one of the two possible themes: either `Widget.Holo.CompoundButton.Switch` (dark) or `Widget.Holo.Light.CompoundButton.Switch`
(light).

The simplest way to do that is to create a `themes.xml` file in your project's `res/values` folder with this contents:
```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="Theme" parent="@android:style/Theme">
        <item name="switchStyle">@style/Widget.Holo.CompoundButton.Switch</item>
    </style>

</resources>
```
And use it in your Application or Activity by updating your `AndroidManifest.xml` file:
```xml
(...)
<application
    android:theme="@style/Theme" 
(...)
```

or
```xml
(...)
<activity
    android:theme="@style/Theme" 
(...)
```

Then in your layout xml files you use the widget like this:
```xml
<org.jraf.android.backport.switchwidget.Switch
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

### SwitchPreference

Add switchPreferenceStyle to your 'themes.xml'

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>

    <style name="Theme" parent="@android:Theme">
        <item name="switchStyle">@style/Widget.Holo.CompoundButton.Switch</item>
        <item name="switchPreferenceStyle">@style/Preference.SwitchPreference</item>
    </style>

</resources>
```

Then in your preference xml file:

```xml
<PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:switchpref="http://schemas.android.com/apk/res-auto" >

    <org.jraf.android.backport.switchwidget.SwitchPreference
        android:key="testKey"
        android:title="SwitchPreference Test"
        switchpref:switchTextOff="@string/off"
        switchpref:switchTextOn="@string/on"
        switchpref:summaryOff="@string/summary_off"
        switchpref:summaryOn="@string/summary_on" />

</PreferenceScreen>
```

### Sample app

A sample app is available in the [sample](sample) folder, and also on the Play Store:
[![Get it on Google Play](http://www.android.com/images/brand/get_it_on_play_logo_small.png)](https://play.google.com/store/apps/details?id=org.jraf.android.backport.switchwidget.sample)

Credits
-------

The code was copied directly from the Android 4.0.3 (API 15) source code, then slightly tweaked by myself (BoD@JRAF.org) to make
it run on 2.1+.  The few modifications I made are documented in the code (look for 'XXX' comments).
The SwitchPreference part was added later by Intrications (intrications.com / github.com/intrications), also by taking code
from Android and tweaking it a bit.

Licence
-------

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
