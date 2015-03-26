package angular

import com.greencatsoft.angularjs._
import com.greencatsoft.angularjs.core.Scope
import com.greencatsoft.angularjs.internal.ServiceProxy

import scala.language.experimental.macros
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportDescendentClasses
import scalatags.Text

trait DirectiveTags extends Text.Cap with Text.Aggregate with Text.Attrs {
  val ngModel = "ng-model".attr
  val ngClick = "ng-click".attr
}

trait ScalaTagsDirective extends ElementDirective with TemplateSourceDirective with DirectiveTags {
  def tag: Tag

  override lazy val template = tag.toString()
}

trait ComponentDirective extends ScalaTagsDirective with IsolatedScope {
  protected def proxy[A <: Controller[_]]: js.Any = macro ServiceProxy.newClassWrapper[A]
}

@JSExportDescendentClasses
abstract class ComponentController[S <: Scope](scope: S) extends AbstractController(scope)