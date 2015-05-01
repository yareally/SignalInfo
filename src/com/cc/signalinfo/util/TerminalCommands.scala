
package com.cc.signalinfo.util

import java.io.{FileNotFoundException, IOException}

import android.os.Build
import android.os.Build.VERSION
import com.cc.signalinfo.BuildConfig

import scala.async.Async.{async, await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source
import scala.sys.process._

/**
 * @author Wes Lanning
 * @version 2014-04-23
 */
object TerminalCommands {
  private val SEL_ENFORCED         = '1'
  private val SEL_ENABLED_LOCATION = "/sys/fs/selinux/enforce"

  /**
   * Launches an activity via the underlying system console in android.
   *
   * @param pkgName - package the activity to launch is located in
   * @param activityName - name of the activity to launch
   * @return the exit code returned after the command completes
   */
  def launchActivity(pkgName: String, activityName: String, enableDebug: Boolean = false): Future[Int] = async {
    val rootCmd = s"am start --user 0 -a android.intent.action.MAIN -n $pkgName/$pkgName.$activityName"
    //val rootCmd = s"am start -a //android.intent.action.MAIN -n $pkgName/$pkgName.$activityName"


    //val cmdSeq = if (await(isSELinuxEnforcing)) {
      //Seq("su", "--context u:r:system_app:s0", "-c", rootCmd)
   // }
   // else {
    val cmdSeq = Seq("su", "-c", rootCmd)
    //}
    stringSeqToProcess(cmdSeq).!
  }

  /**
   * Detect if SELinux is set to enforcing, caches result
   *
   * @return true if SELinux set to enforcing, or false in the case of
   *         permissive or not present
   */
  def isSELinuxEnforcing: Future[Boolean] = async {
    var enforcing = false

    if (Build.VERSION.SDK_INT < 17) {
      enforcing = false
    }
    else if (VERSION.SDK_INT >= 19) {
      enforcing = true // 4.4+ builds are enforcing by default
    }
    else {
      // api 17, 18...the redheaded stepchildren of the post honeycomb era...sigh
      try {
        enforcing = Source.fromFile(SEL_ENABLED_LOCATION).next() == SEL_ENFORCED
      } catch {
        case ex: FileNotFoundException ⇒ println(s"$SEL_ENABLED_LOCATION not found: ${ex.printStackTrace() }")
        case ex: IOException ⇒ println(s"Error trying to read $SEL_ENABLED_LOCATION: ${ex.printStackTrace() }")
      }
    }
    enforcing
}

}
