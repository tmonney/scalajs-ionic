import Ionic._

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

lazy val root = project.in(file("."))
	.aggregate(scalajs, ionic)

lazy val scalajs = project.in(file("scalajs"))
	.enablePlugins(ScalaJSPlugin)

lazy val ionic = project.in(file("ionic"))
	.enablePlugins(SbtWeb)
	.settings(
		libraryDependencies ++= Seq(
			"org.webjars" % "ionic" % "1.0.0-beta.14"
		),
		scalaJsFiles := Seq((fastOptJS in Compile in scalajs).value.data),
		pipelineStages in Assets := Seq(copyScalaJs),
		pipelineStages := Seq(copyScalaJs)
	)
