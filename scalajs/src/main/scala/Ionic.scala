package ionic

import scala.scalajs.js

import com.greencatsoft.angularjs.core.Promise
import com.greencatsoft.angularjs.injectable

@injectable("$ionicPlatform")
trait IonicPlatform extends js.Object {
  def ready(callback: js.Function0[_]): Promise = js.native
}