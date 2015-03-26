package ionic

import com.greencatsoft.angularjs.core.Promise
import com.greencatsoft.angularjs.injectable

import scala.scalajs.js

@injectable("$ionicPlatform")
trait IonicPlatform extends js.Object {
  def ready(callback: js.Function0[_]): Promise = js.native
}