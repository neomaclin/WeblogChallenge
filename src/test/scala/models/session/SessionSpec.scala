package models.session

import models.elb.{Address, ELBLogEntry, Request, _}
import org.joda.time.DateTime
import org.specs2.mutable.Specification

/**
  * Created by Neo on 2016/8/13.
  */
class SessionSpec  extends Specification {

  "sessionize function " should {
    "be able to aggregate the list of entries into list of list entries" in {

      val original = List(1, 2, 3, 20, 21, 23)
      val result = List(List(1, 2, 3), List(20, 21, 23))

      def isSessionTmeOut(last: Int, entry: Int) = entry - last > 18

      sessionize[Int](original, isSessionTmeOut) mustEqual result
    }

  }
}