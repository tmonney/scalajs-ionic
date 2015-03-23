import scala.scalajs.js
import scala.scalajs.js.JSApp
import scala.scalajs.js.annotation.JSExport

import com.greencatsoft.angularjs._

import scalatags.Text.all._

import ionic._

@JSExport
object IonicApp extends JSApp {
  override def main() {
  	val module = Angular.module("ionic-app", Seq("ionic"))
	module.run[CordovaConfig]
	module.directive[HeyDirective]
  }
}

@JSExport
@injectable("hey")
class HeyDirective extends ElementDirective with TemplateSourceDirective {
  override val template =
  	div (
	  	p("First paragraph"),
  		p(fontWeight.bold, "Second paragraph")
  	).toString
}

class CordovaConfig(ionicPlatform: IonicPlatform) extends Runnable {
	override def initialize() {
    	ionicPlatform.ready(() => {
    		val window = js.Dynamic.global.window
    		val cordova = window.cordova
    		if(!js.isUndefined(cordova) && !js.isUndefined(cordova.plugins.Keyboard)) {
      			cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    		}
    		if(!js.isUndefined(window.StatusBar)) {
      			window.StatusBar.styleDefault();
    		}
		})
  	}
}