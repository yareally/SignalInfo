# A little help on building the app

Although this is an opensource project, it's not a project I have gone out of my way to make easy to build for anyone and everyone (no maven, etc). I don't like much and don't use it unless other people working with me are using it. Eventually I will probably use SBT.

### However, for anyone that does wish to build it, here are some tips:

1. Scala 2.11 is required. Yes, there is a Java branch if you want to build that instead. However, I don't support it anymore and you're on your own for building it. If you have questions about building the Scala version and your first question isn't "How do I build this for Eclipse", then feel free to ask :)
2. If you are having troubles with your IDE of choice, use Intellij IDEA (the stable version). That is what I use and I cannot offer IDE specific help otherwise. Will it build in Android Studio? Maybe, I don't know and don't care to find out really.
4. The following dependencies are needed (all jars are under library directory):
  * scala-async.jar - For async stuff
  * android-support-v4.jar
  * android-support-v7-appcompat.jar
  * android-support-v7-gridlayout.jar
5. Ensure the following Intellij modules in this repo are linked as dependencies to the main project:
  * appcompat module
  * Google Play Services - (unless you remove the xml bits for this in the MainActivities' XML)
  * cwac-loaderex - Currently for SharedPreferencesLoader until I rewrite with Scala Futures


If you included everything above, it should build. If not, you missed something from the above or decided to make it harder on yourself by not using Intellij when it's what the project is set up to use already.