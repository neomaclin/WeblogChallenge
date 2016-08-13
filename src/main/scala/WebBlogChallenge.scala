import models.elb.{ELBLogEntry, _}
import models.session._
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by Neo on 2016/8/11.
  */
object WebBlogChallenge {

  def main(args: Array[String]) {

    val logFile = "2015_07_22_mktplace_shop_web_log_sample.log"

    val conf = new SparkConf().setAppName("WebBlogChallenge")

    val sc = new SparkContext(conf)

    val logData = sc.textFile(logFile)

    val entries: RDD[ELBLogEntry] = logData.map(parseEntry)

    println(entries.collect())

//    val userSessions =
//      entries
//        .groupBy(_.clientPort.ip)
//        .mapValues(entries => sessionize[ELBLogEntry](entries.toList, entryTimeOut(_,_,SessionWindow)))
//
//    userSessions.persist()
//
//    println(userSessions)

   // sc.stop()
    //
//
//    val userAvgSessionTimeinMills = userSessions.mapValues(sessions => sessionAvg(sessions, durationInMills))
//    val userAvgSessionTimeinSecs = userSessions.mapValues(sessions => sessionAvg(sessions, durationInSecs))
//    val userAvgSessionTimeinMins = userSessions.mapValues(sessions => sessionAvg(sessions, durationInMins))
//
//    val userSessionHits = userSessions.mapValues(sessions => sessions.map(hitsPerSession))
//
//
//    val userEngagements = userSessions.mapValues(sessions => sessions.map(session => sessionDuration(session, durationInMins)).max)
//    val mostEngagedUser = userEngagements.sortBy(_._2, ascending = false).first()._1
//
//
//    println(userSessions)
  }

//  def hitsPerSession(session: Session[ELBLogEntry]): Map[String, Int] = {
//    session.map(entry => entry.request.URL).groupBy(Predef.identity).mapValues(_.length)
//  }


}
