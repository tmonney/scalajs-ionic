import com.typesafe.sbt.web.pipeline.Pipeline

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
		copyScalaJS := { mappings: Seq[(File, String)] =>
			val targetDir = (target in Assets).value / "scalajs"
			val in: File = (fastOptJS in Compile in scalajs).value.data
			val out: File = targetDir / in.name
			IO.copyFile(in, out)
			mappings :+ ((in, s"js/${in.name}"))
		},
		pipelineStages in Assets := Seq(copyScalaJS),
		pipelineStages := Seq(copyScalaJS)
	)

val copyScalaJS = taskKey[Pipeline.Stage]("Copy ScalaJS output")
