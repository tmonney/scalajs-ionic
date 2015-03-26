package app

import com.greencatsoft.angularjs._
import ionic._

import scala.scalajs.js
import scala.scalajs.js.JSApp

object IonicApp extends JSApp {
  override def main() {
    val module = Angular.module("ionic-app", Seq("ionic"))
    module.run[CordovaConfig]
    module.directive(NameComponent)
  }
}

class CordovaConfig(ionicPlatform: IonicPlatform) extends Runnable {
  override def initialize() {
    ionicPlatform.ready(() => {
      val window = js.Dynamic.global.window
      val cordova = window.cordova
      if (!js.isUndefined(cordova) && !js.isUndefined(cordova.plugins.Keyboard)) {
        cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
      }
      if (!js.isUndefined(window.StatusBar)) {
        window.StatusBar.styleDefault();
      }
    })
  }
}