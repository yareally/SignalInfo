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
    /**
     * Launches an activity via the underlying system console in android.
     *
     * @param pkgName - package the activity to launch is located in
     * @param activityName - name of the activity to launch
     * @return the exit code returned after the command completes
     */
    def launchActivity(pkgName: String, activityName: String): Future[Int] = async {
        val rootCmd = s"am start -a android.intent.action.MAIN -n $pkgName/$pkgName.$activityName"
        stringSeqToProcess(Seq("su", "-c", rootCmd)).!
    }
}
