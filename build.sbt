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
		copyScalaJS := { mappings =>
			val targetDir = (target in Assets).value / "scalajs"
			val jsIn = (fastOptJS in Compile in scalajs).value.data
			val jsMapIn = file(jsIn.absolutePath + ".map")
			val jsOut = targetDir / jsIn.name
			val jsMapOut = targetDir / jsMapIn.name
			IO.copyFile(jsIn, jsOut)
			IO.copyFile(jsMapIn, jsMapOut)
			mappings ++ Seq(jsIn -> s"js/${jsOut.name}", jsOut -> s"js/${jsMapOut.name}")
		},
		pipelineStages in Assets := Seq(copyScalaJS),
		pipelineStages := Seq(copyScalaJS)
	)

val copyScalaJS = taskKey[Pipeline.Stage]("Copy ScalaJS output")

val start = taskKey[Unit]("Start serving the Ionic application")
val stop = taskKey[Unit]("Stop the served Ionic application")
val pidFile = settingKey[File]("A file containing the PID of the last Ionic process")
pidFile := target.value / "ionic.pid"
start := {
	Process("ionic" :: "serve" :: "--serverlogs" :: "--consolelogs" :: Nil, ionic.base).run()
	"pgrep node -n" #> pidFile.value !
}
stop := {
	val pid = IO.read(pidFile.value)
	s"kill $pid" !;
	IO.delete(pidFile.value)
}

