libgdx-quadtree
===============

A very simple implementation of a Quadtree in LibGDX.

###How To Get Started

Clone the repo to your desktop or download and extract the master [ZIP](https://github.com/innerlogic/libgdx-quadtree/archive/master.zip) file.

#### Setup Environment Variables
Certain environment variables need to be set. Refer to the [LibGDX Prerequisites](https://github.com/libgdx/libgdx/wiki/Prerequisites) wiki entry for instructions on how to install all the necessary prerequisites.

  * `JAVA_HOME` (JDK installation directory)
  * `ANDROID_HOME` (Android SDK installation directory) 

#### Grab LibGDX binaries and other dependencies

In order to manage dependencies and allow for other interesting build/development related tasks, [LibGDX has leveraged Gradle](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle) as a potential tool. Using [Gradle](http://www.gradle.org/) helps ensure that all dependencies are kept up-to-date and prevents the need for JARs and IDE-related files to be in source control.  This also allows developers to work with the source in whatever environment they feel most comfortable with. For more information on Gradle and how LibGDX leverages it, visit the  [LibGDX wiki](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle).

For purposes of this project, it is rather simple to get up and running. From the project directory in the command line / terminal, run the following command:

    gradlew clean
    
This will pull in the needed dependencies so you (the developer) don't have to worry about them later :-)

#### Generate IDE-related Files

LibGDX and Gradle make it very easy to generate / leverage files for your IDE of choice. Visit the [LibGDX wiki](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle) for instructions for [Eclipse](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle#wiki-as-a-file-system) and [IntelliJ](https://github.com/libgdx/libgdx/wiki/Project-Setup-Gradle#wiki-importing-to-intellij-idea). 

