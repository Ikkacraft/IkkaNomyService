import com.github.play2war.plugin.{Play2WarKeys, Play2WarPlugin}

name := "IkkaNomy"

version := "1.0"

lazy val `ikkanomy` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(jdbc , cache , ws ,
  "mysql" % "mysql-connector-java" % "5.1.25",
  "com.typesafe.play" %% "anorm" % "2.4.0",
  "io.swagger" %% "swagger-play2" % "1.5.1")

Play2WarPlugin.play2WarSettings

Play2WarKeys.servletVersion := "3.1"

Play2WarKeys.targetName := Some("ikkanomy")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )