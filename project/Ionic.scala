import sbt._
import Keys._

import com.typesafe.sbt.web.pipeline.Pipeline
import com.typesafe.sbt.web.Import._
import WebKeys._

object Ionic {

  val scalaJsFiles = taskKey[Seq[File]]("Gather Scala.js files to process")

  val copyScalaJs = taskKey[Pipeline.Stage]("Copy Scala.js output")

  val ionicStart = taskKey[Unit]("Start serving the Ionic application")

  val ionicStop = taskKey[Unit]("Stop the currently serving Ionic application")

  val ionicPidFile = settingKey[File]("A file containing the PID of the last Ionic process")

  ionicPidFile := target.value / "ionic.pid"

  copyScalaJs := { mappings =>
    val targetDir = webTarget.value / "scalajs"
    val jsIn = scalaJsFiles.value
    val jsOut = jsIn map (targetDir / _.name)
    val jsTargets = jsOut map (f => s"js/${f.name}")
    IO.copy(jsIn zip jsOut)


    val jsMapIn = jsIn map (js => file(js.absolutePath + ".map"))
    val jsMapOut = jsMapIn map (targetDir / _.name)
    val jsMapTargets = jsMapOut map (f => s"js/${f.name}")
    IO.copy(jsMapIn zip jsMapOut)

    mappings ++ (jsOut zip jsTargets) ++ (jsMapOut zip jsMapTargets)
  }

  ionicStart := {
  	Process("ionic" :: "serve" :: "--serverlogs" :: "--consolelogs" :: Nil, baseDirectory.value).run()
    "pgrep node -n" #> ionicPidFile.value !
  }

  ionicStop := {
    val pid = IO.read(ionicPidFile.value)
    s"kill $pid".!
    IO.delete(ionicPidFile.value)
  }
}
