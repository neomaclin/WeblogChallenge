package models

import org.joda.time.{DateTime, Duration, Minutes}
import org.joda.time.format.DateTimeFormat

package object elb {

  val EntryDateFormat = DateTimeFormat.forPattern("YYYY-MM-DD'T'HH:mm:ss.SSSSSS'Z'")
  val ELBEntryPattern = """(\S+) (\S+) (\S+) (\S+) (\S+) (\S+) (\S+) (\d+) (\d+) (\d+) (\d+) "(.*)" "(.*)" (\S+) (\S+)""".r
  val RequestPattern = """(\w+) (\S+) (\S+)""".r
  val AddressPattern = """(\S+):(\d+)""".r

  case class Address(ip: String, port: Int)
  case class Request(method: String, URL: String, httpProtocal: String)

  case class ELBLogEntry(timeStamp: DateTime, //2015-07-22T09:00:27.894580Z
                         elb: String, //marketpalce-shop
                         clientPort: Address, //203.91.211.44:51402
                         backendPort: Address, //10.0.4.150:80 or "-" for request that didn't reach backend
                         requestProcessTime: Double, // 0.000024
                         backendProcessTime: Double, // 0.15334
                         responseProcessTime: Double, // 0.000026
                         elbStatusCode: String, //  200
                         backendStatusCode: String, // 200
                         receivedByte: Long, // 0
                         sentByte: Long, // 1497
                         request: Request, // "GET https://paytm.com:443/shop/wallet/txnhistory?page_size=10&page_number=0&channel=web&version=2 HTTP/1.1"
                         userAgent: String, // "Mozilla/5.0 (Windows NT 6.1; rv:39.0) Gecko/20100101 Firefox/39.0"
                         sslCipher: String, // ECDHE-RSA-AES128-GCM-SHA256
                         sslProtocol: String // TLSv1.2
                        )

  def parseEntry(rawEntry: String): ELBLogEntry = {
    val ELBEntryPattern(timeStampRaw, elbRaw,
    clientPortRaw, backendPortRaw,
    requestProcessTimeRaw, backendProcessTimeRaw, responseProcessTimeRaw,
    elbStatusCodeRaw, backendStatusCodeRaw,
    receivedByteRaw, sentByteRaw,
    requestRaw,
    userAgentRaw,
    sslCiphersRaw, slProtocolRaw) = rawEntry

    val timeStamp: DateTime = parseLocalDateTime(timeStampRaw)
    val clientPort: Address = parseAddress(clientPortRaw)
    val backendPort: Address = parseAddress(backendPortRaw)
    val List(requestProcessTime, backendProcessTime, responseProcessTime) =
      List(requestProcessTimeRaw, backendProcessTimeRaw, responseProcessTimeRaw).map(_.toDouble)
    val List(receivedByte, sentByte) = List(receivedByteRaw, sentByteRaw).map(_.toLong)
    val userAgent = if (userAgentRaw.length > 8 * 1024 * 1024) userAgentRaw.substring(0, 8 * 1024 * 1024) else  userAgentRaw
    val RequestPattern(method, url, protocal) = requestRaw
    val request = Request(method, url, protocal)
    ELBLogEntry(timeStamp, elbRaw, clientPort, backendPort, requestProcessTime, backendProcessTime, responseProcessTime,
      elbStatusCodeRaw, backendStatusCodeRaw, receivedByte, sentByte, request, userAgent, sslCiphersRaw, slProtocolRaw)
  }

  private def parseAddress(s: String): Address = {
    s match {
      case "-" => Address("0.0.0.0", 0)
      case AddressPattern(ip, port) => Address(ip, port.toInt)
    }
  }

  private def parseLocalDateTime(s: String): DateTime = DateTime.parse(s, EntryDateFormat)

  def entryTimeOut(lastEntry: ELBLogEntry, entry: ELBLogEntry, windowSize: Int): Boolean = {
    Math.abs(Minutes.minutesBetween(lastEntry.timeStamp, entry.timeStamp).getMinutes) > windowSize
  }

  def durationInMills(first: ELBLogEntry, last: ELBLogEntry): Long = {
    new Duration(first.timeStamp, last.timeStamp).getMillis
  }

  def durationInSecs(first: ELBLogEntry, last: ELBLogEntry): Long = {
    new Duration(first.timeStamp, last.timeStamp).getStandardSeconds
  }

  def durationInMins(first: ELBLogEntry, last: ELBLogEntry): Long = {
    new Duration(first.timeStamp, last.timeStamp).getStandardMinutes
  }

}
