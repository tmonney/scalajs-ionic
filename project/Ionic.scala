import scala.language.postfixOps

import scala.util.{Try, Success, Failure}

import sbt._
import Keys._

import com.typesafe.sbt.web.SbtWeb
import com.typesafe.sbt.web.Import._
import WebKeys._

object IonicPlugin extends AutoPlugin {

  override def requires = SbtWeb

  object autoImport {
    val ionicJsFiles = taskKey[Seq[File]]("Gather JS files to process")
    val ionicServeArgs = settingKey[Seq[String]]("Arguments passed to ionicServe")
    val ionicServe = taskKey[Unit]("Serve the Ionic application")
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

    ionicServeArgs := Seq("--serverlogs", "--consolelogs"),

    ionicServe := {
      Process("ionic" +: "serve" +: ionicServeArgs.value, baseDirectory.value).run()
      "pgrep node -n" #> ionicPidFile.value !
    },

    // make sure the assets are processed before starting the server
    ionicServe <<= ionicServe.dependsOn(assets),

    ionicStop := {
      val result = for {
        pid <- Try(IO.read(ionicPidFile.value))
        _ <- Try(s"kill $pid" !)
        _ <- Try(IO.delete(ionicPidFile.value))
      } yield pid.trim

      val log = streams.value.log

      result match {
        case Success(pid) => log.info(s"Stopped Ionic server (pid=$pid)")
        case Failure(e) => {
          log.error(s"Could not stop Ionic server") 
          throw e
        }
      }
    }
  )

  private def copyFiles(in: Seq[File], targetDir: File) = {
    IO.copy(in map (f => (f, targetDir / f.name)))
  }
}
