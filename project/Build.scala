import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "android-scala-navigation-drawer",
    version := "0.1",
    versionCode := 0,
    scalaVersion := "2.10.0",
    platformName in Android := "android-17",
    javacOptions ++= Seq("-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")
  )

  val proguardSettings = Seq (
    useProguard in Android := true
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "change-me",
      unmanagedBase <<= baseDirectory { base => base / "libs" }
    )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "android-scala-navigation-drawer",
    file("."),
    settings = General.fullAndroidSettings
  )
}
