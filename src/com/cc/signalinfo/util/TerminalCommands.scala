package com.cc.signalinfo.util

import scala.sys.process._
import scala.async.Async.async
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * @author Wes Lanning
 * @version 2014-04-23
 */
object TerminalCommands
{
    def launchActivity(pkgName: String, activityName: String): Future[Int] = async {
        val rootCmd = s"am start -a android.intent.action.MAIN -n $pkgName/$pkgName.$activityName"
        stringSeqToProcess(Seq("su", "-c", rootCmd)).!
    }
}
