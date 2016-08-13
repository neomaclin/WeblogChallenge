import models.elb._
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

    val userSessions: RDD[(String, List[List[ELBLogEntry]])] =
      entries
        .groupBy(_.clientPort.ip)
        .mapValues(entries => sessionize[ELBLogEntry](entries.toList, entryTimeOut(_,_,SessionWindow)))

    userSessions.persist()

    val allSessions = userSessions.flatMap(_._2)
    val avgSessionTimeInMills = allSessions.map(session => sessionDuration(session, durationInMills)).reduce(_+_) / allSessions.count


    //val uniqueURLvisitsSession = allSessions.map(session => session.map(_.request.URL).distinct.length )
    val userEngagements = userSessions.mapValues(sessions => sessions.map(session => sessionDuration(session, durationInMills)).max)
    val mostEngagedUser = userEngagements.sortBy(_._2, ascending = false).first()._1

    println("Total number of entry is：" + entries.count())
    println("Total distinct IP count is :" + userSessions.count)
    println("Total number of sessions is :" + allSessions.count)
    println("Average time of sessions measured in milliseconds is :" + avgSessionTimeInMills )
    //println("unique URL visits per session is [" + uniqueURLvisitsSession.collect().mkString(", ")+"]")
    println("most engaged ip is ：" + mostEngagedUser)

  }

}
