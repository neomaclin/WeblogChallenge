package models

import models.elb.ELBLogEntry

/**
  * Created by Neo on 2016/8/13.
  */
package object session {

  type Session[T] = List[T]

  val SessionWindow = 15 // as in Minutes

  //Generic function to aggregate list of entries to a list of sessions,
  //each session is just a list of original entries given that the difference between 2 nearby entries does
  //no excess the session window
  def sessionize[T](entries: List[T], excessSessionWindow: (T,T) => Boolean): List[List[T]] = {
    entries.foldLeft(List[List[T]](Nil)) {
      case (List(Nil), entry) => List(entry :: Nil)
      case (sessions@(initSessions :+ (init :+ lastEntry)), entry) =>
        if (excessSessionWindow(entry, lastEntry)) sessions :+ (entry :: Nil)
        else initSessions :+ (init :+ lastEntry :+ entry)
    }
  }

  //The duration of a session is determined by the difference of first and last entry of a given session
  def sessionDuration[T](session: List[T], diff: (T, T) => Long): Long = {
    session match {
      case Nil => 0
      case xs => diff(xs.head, xs.last)
    }
  }

  def sessionAvg[T](sessions: List[Session[T]], duration:(T, T) => Long ): Long = {
    val sessionDurations = sessions.map(session => sessionDuration(session, duration))
    sessionDurations.sum / sessionDurations.size
  }


}
