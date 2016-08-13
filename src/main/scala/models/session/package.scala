package models

/**
  * Created by Neo on 2016/8/13.
  */
package object session {

  val SessionWindow = 15 // as in Minutes

  //Generic function to aggregate list of entries to a list of sessions,
  //each session is just a list of original entries given that the difference between 2 nearby entries does
  //no excess the session window
  def sessionize[T](entries: List[T], excessSessionWindow: (T, T) => Boolean): List[List[T]] = {
    entries.foldLeft(List[List[T]](Nil)) {
      case (List(Nil), entry) => List(entry :: Nil)
      case (sessions@(initSessions :+ (init :+ lastEntry)), entry) =>
        if (excessSessionWindow(lastEntry, entry)) sessions :+ (entry :: Nil)
        else initSessions :+ (init :+ lastEntry :+ entry)
    }
  }

  //The duration of a session is determined by the difference of first and last entry of such
  def sessionDuration[T](session: List[T], diff: (T, T) => Long): Long = {
    session match {
      case Nil => 0
      case xs => Math.abs(diff(xs.head, xs.last))
    }
  }

}
