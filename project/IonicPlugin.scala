import scala.language.postfixOps

import scala.util.{Try, Success, Failure}

import sbt._
import Keys._

import com.typesafe.sbt.web.{ SbtWeb, PathMapping }
import com.typesafe.sbt.web.Import._
import WebKeys._

object IonicPlugin extends AutoPlugin {

  override def requires = SbtWeb

  object autoImport {
    val ionicJsFiles = TaskKey[Seq[File]]("ionic-js", "Entry point for the external Ionic JS files")
    val ionicServeArgs = SettingKey[Seq[String]]("ionic-serve-args", "Arguments passed to ionicServe")
    val ionicServe = TaskKey[Unit]("ionic-serve", "Serve the Ionic application")
    val ionicStop = TaskKey[Unit]("ionic-stop", "Stop the currently serving Ionic application")
  }

  import autoImport._
  import com.typesafe.sbt.web.pipeline.Pipeline

  val ionicPidFile = settingKey[File]("A file containing the PID of the last Ionic process")
  val jsFilesMapping = taskKey[Pipeline.Stage]("JS files mapping")

  override lazy val projectSettings = Seq(
    jsFilesMapping in Assets := jsMappings((ionicJsFiles in Assets).value),
    jsFilesMapping := jsMappings(ionicJsFiles.value, (ionicJsFiles in Assets).value),

    pipelineStages in Assets := Seq(jsFilesMapping in Assets),
    pipelineStages := Seq(jsFilesMapping),

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

  private def jsMappings(sources: Seq[File], exclusions: Seq[File] = Seq.empty) = { mappings: Seq[PathMapping] =>
    val included = sources diff exclusions
    val js = included map jsFileMapping
    val maps = included map jsSourceMapMapping
    (mappings ++ js ++ maps) filterNot { case (file, _) =>
      val sourceMapExclusions = exclusions.map(jsSourceMapMapping).map(_._1)
      (exclusions ++ sourceMapExclusions) contains file
    }
  }

  private def jsFileMapping(f: File) = {
    f -> s"js/${f.name}"
  }

  private def jsSourceMapMapping(f: File) = {
    file(f.absolutePath + ".map") -> s"js/${f.name}.map"
  }
}
