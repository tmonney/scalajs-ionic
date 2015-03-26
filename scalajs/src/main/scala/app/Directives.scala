package app

import angular.{ComponentController, ComponentDirective}
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.injectable

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.Text.all._

@JSExport
@injectable("sjsName")
object NameComponent extends ComponentDirective {
  val firstName = "firstName"
  val lastName = "lastName"

  bindings ++= Seq(
    firstName := "first",
    lastName := "last"
  )

  override def tag = div(border := "1px solid blue", margin := "5px", padding := 5,
    label(`for` := firstName, "First name:"),
    input(id := firstName, name := firstName, `type` := "text", ngModel := firstName),
    br,
    label(`for` := lastName, "Last name:"),
    input(id := lastName, name := lastName, `type` := "text", ngModel := lastName),
    br,
    span("Your name: {{firstName}} {{lastName}}"),
    br,
    button(ngClick := "controller.reset()", "Reset")
  )

  override def controller = Some(proxy[NameController])
}

class NameScope extends Scope {
  var firstName: String = js.native
  var lastName: String = js.native
}

class NameController(scope: NameScope) extends ComponentController(scope) {

  @JSExport
  def reset(): Unit = {
    scope.firstName = "Thierry"
    scope.lastName = "Monney"
  }
}
