lazy val root = project.in(file("."))
    .aggregate(scalajs, ionic)

lazy val scalajs = project.in(file("scalajs"))
    .enablePlugins(ScalaJSPlugin)
    .settings(
        commonSettings,
        libraryDependencies ++= Seq(
            "com.greencatsoft" %%% "scalajs-angular" % "0.5-SNAPSHOT",
            "com.lihaoyi" %%% "scalatags" % "0.4.6"
        ),
        jsDependencies ++= Seq(
            RuntimeDOM,
            "org.webjars" % "angularjs"             % "1.3.15"          / "angular.js",
            "org.webjars" % "angularjs"             % "1.3.15"          / "angular-sanitize.js"     dependsOn "angular.js",
            "org.webjars" % "angularjs"             % "1.3.15"          / "angular-animate.js"      dependsOn "angular.js",
            "org.webjars" % "angular-ui-router"     % "0.2.13"          / "angular-ui-router.js"    dependsOn "angular.js",
            "org.webjars" % "ionic"                 % "1.0.0-beta.14"   / "ionic.js"                dependsOn "angular-ui-router.js" dependsOn "angular-sanitize.js" dependsOn "angular-animate.js",
            "org.webjars" % "ionic"                 % "1.0.0-beta.14"   / "ionic-angular.js"        dependsOn "ionic.js"
        ),
        persistLauncher := true,
        skip in packageJSDependencies := false
    )

lazy val ionic = project.in(file("ionic"))
    .enablePlugins(IonicPlugin)
    .dependsOn(scalajs)
    .settings(
        commonSettings,
        ionicJsFiles in Assets := Seq(
            (fastOptJS in Compile in scalajs).value.data,
            file((scalaJSLauncher in Compile in scalajs).value.data.path),
            (packageJSDependencies in Compile in scalajs).value
        ),
        ionicJsFiles := Seq(
            (fullOptJS in Compile in scalajs).value.data,
            file((scalaJSLauncher in Compile in scalajs).value.data.path),
            (packageJSDependencies in Compile in scalajs).value
        )
    )

val commonSettings = Seq(
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.11.6",
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)