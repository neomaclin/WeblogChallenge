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

      def isSessionTmeOut(last: Int, entry: Int) = (entry - last) > 15

      sessionize[Int](original, isSessionTmeOut) mustEqual result
      sessionize[Int](Nil, isSessionTmeOut) mustEqual Nil
    }

  }

  "sessionDuration function " should {
    "be able determined the duration by first and last entry of a given session" in {

      val session = List(1, 2, 3, 4, 12, 20)

      def diff(first: Int, last: Int) = last - first

      sessionDuration[Int](session, diff) mustEqual 19
    }

  }
}