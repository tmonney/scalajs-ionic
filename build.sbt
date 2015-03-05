
version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.5"

lazy val root = project.in(file("."))
	.aggregate(scalajs, ionic)

lazy val scalajs = project.in(file("scalajs"))
	.enablePlugins(ScalaJSPlugin)

lazy val ionic = project.in(file("ionic"))
	.enablePlugins(IonicPlugin)
	.dependsOn(scalajs)
	.settings(
		libraryDependencies ++= Seq(
			"org.webjars" % "ionic" % "1.0.0-beta.14"
		),
		ionicJsFiles in Assets := Seq((fastOptJS in Compile in scalajs).value.data),
		ionicJsFiles := Seq((fullOptJS in Compile in scalajs).value.data)
	)
