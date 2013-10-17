libgdx-quadtree
===============

A very simple implementation of a Quadtree in LibGDX.

###How To Get Started

First, clone the repo to your desktop or download and extract the master [ZIP](https://github.com/innerlogic/libgdx-quadtree/archive/master.zip) file.

#### Setup Environment Variables
Certain environment variables need to be set. Refer to the [LibGDX Prerequisites](https://github.com/libgdx/libgdx/wiki/Prerequisites) wiki entry for instructions on how to install all the necessary prerequisites.

  * `JAVA_HOME` (JDK installation directory)
  * `ANDROID_HOME` (Android SDK installation directory) 

#### Grab the proper LibGDX and other dependencies

In order to manage dependencies and allow for other interesting build/development related tasks, parts of the [LibGDX Gradle Template](https://github.com/libgdx/libgdx-gradle-template) have been leveraged. Based on [Gradle](http://www.gradle.org/), this ensures that all 3rd party dependencies are kept up-to-date and prevents the need for JARs and IDE-related files to be in source control.  This also allows developers to work with the source in whatever environment they feel most comfortable with.

The [README](https://github.com/innerlogic/libgdx-quadtree) of the template has detailed instruction on the many tasks available, but it is rather simple to get up and running. From the project directory in the command line / terminal, run the following command:

    gradlew clean
    
This will pull in the needed dependencies so you (the developer) don't have to worry about them later :-)

#### Generate IDE-related Files

The LibGDX Gradle Template makes it very easy to generate files for your IDE of choice.

##### Eclipse

    gradlew eclipse

Refer to the [Eclipse Usage](https://github.com/libgdx/libgdx-gradle-template#eclipse-usage) portion of the LibGDX Gradle Template wiki for instructions on how to setup for running/debugging.

##### IntelliJ IDEA

    gradlew idea
    
Refer to the [IntelliJ IDEA Usage](https://github.com/libgdx/libgdx-gradle-template#intellij-idea-usage) portion of the LibGDX Gradle Template wiki for instructions on how to setup for running/debugging. 
Also, be sure to ignore any prompt about adding an Android facet.
