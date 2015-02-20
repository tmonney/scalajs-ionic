import scala.language.postfixOps

import sbt._
import Keys._

import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.Import._
import WebKeys._

object IonicPlugin extends AutoPlugin {

  override def requires = SbtWeb

  object autoImport {
    val ionicJsFiles = taskKey[Seq[File]]("Gather JS files to process")
    val ionicStart = taskKey[Unit]("Start serving the Ionic application")
    val ionicStop = taskKey[Unit]("Stop the currently serving Ionic application")
  }

  import autoImport._
  import com.typesafe.sbt.web.pipeline.Pipeline

  val ionicPidFile = settingKey[File]("A file containing the PID of the last Ionic process")
  val copyJsFiles = taskKey[Pipeline.Stage]("Copy JS files")

  override lazy val projectSettings = Seq(
    copyJsFiles := { mappings =>
      val targetDir = webTarget.value / "ionic-js"
      val jsIn = ionicJsFiles.value
      val jsMapIn = jsIn map (js => file(js.absolutePath + ".map"))
      val out = copyFiles(jsIn ++ jsMapIn, targetDir)
      val targets = out map (f => s"js/${f.name}")
      mappings ++ (out zip targets)
    },

    pipelineStages in Assets := Seq(copyJsFiles),
    pipelineStages := Seq(copyJsFiles),

    ionicPidFile := target.value / "ionic.pid",

    ionicStart := {
      // TODO allow the user to configure flags
      Process("ionic" :: "serve" :: "--lab" :: "--serverlogs" :: "--consolelogs" :: Nil, baseDirectory.value).run()
      "pgrep node -n" #> ionicPidFile.value !
    },

    // make sure the assets are processed before starting the server
    ionicStart <<= ionicStart.dependsOn(copyJsFiles),

    ionicStop := {
      // TODO handle the case when the PID file does not exist
      val pid = IO.read(ionicPidFile.value)
      s"kill $pid".!
      IO.delete(ionicPidFile.value)
    }
  )

  // TODO there is probably something in SBT that does exactly that
  private def copyFiles(in: Seq[File], targetDir: File): Seq[File] = {
    val out = in map (targetDir / _.name)
    IO.copy(in zip out)
    out
  }
}
