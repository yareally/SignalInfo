package com.cc.signalinfo.util

import android.app.{AlertDialog, Dialog, Activity}
import android.os.Bundle
import android.support.v4.app.LoaderManager.LoaderCallbacks
import android.support.v4.content.Loader
import android.support.v7.widget.Toolbar
import android.view.View
import android.view.View.OnClickListener
import android.widget.{TextView, Button}
import com.cc.signalinfo.storage.DbHelper

/**
 * Java Android is ugly. Let's pimp it out.
 *
 * @author Wes Lanning
 * @version 2014-11-11
 */
object PimpMyAndroid {

  implicit class PimpMyView(val view: View) extends AnyVal {

    def click(funct: View ⇒ Unit) = view.setOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = funct(v)
    })

    /**
     * Generic Type method to deal with annoying conversions for views
     *
     * @param id - the Resource ID for the view in question
     * @tparam T - the type associated with the widget (Button, TextView, etc)
     * @return the found view with the desired type
     */
    def find[T](id: Int): T = {
      view.findViewById(id).asInstanceOf[T]
    }
  }

  implicit class PimpMyTextView(val view: TextView) extends AnyVal {
    def text(txt: CharSequence): TextView = {
      view.setText(txt)
      view
    }

    def text(txtId: Int): TextView = {
      view.setText(txtId)
      view
    }
  }

  implicit class PimpMyToolbar(val a: Toolbar) extends AnyVal {
    def navClick(funct: View ⇒ Unit) = a.setNavigationOnClickListener(new OnClickListener {
      override def onClick(v: View): Unit = funct(v)
    })
  }

  implicit class PimpMyActivity(val a: Activity) extends AnyVal {
    /**
     * Generic Type method to deal with annoying conversions for views
     *
     * @param id - the Resource ID for the view in question
     * @tparam T - the type associated with the widget (Button, TextView, etc)
     * @return the found view with the desired type
     */
    def find[T](id: Int): T = {
      a.findViewById(id).asInstanceOf[T]
    }

    /**
     * Get a system service without manually casting it
     *
     * @param serviceName - e.g. Context.TELEPHONY_SERVICE, etc
     * @tparam T - the explicit type for the service
     * @return the system service
     */
    def getSysService[T](serviceName: String): T = {
      a.getSystemService(serviceName).asInstanceOf[T]
    }
  }

  /*
    implicit class PimpMyString(val str: StringContext) extends AnyVal {
      def sql[T](implicit conn: DbHelper, objType: Class) {}
    }

    implicit class PimpMyDb(val dbh: DbHelper) extends AnyVal {
      import scala.reflect.runtime.universe._

      def query[T: TypeTag](sql: String, param: Object = null): Traversable = {
        Set()
      }

      private def queryImpl(T: TypeTag) {
        T.tpe
      }
    }*/

  implicit class PimpMyActivityLoader(val a: Activity) extends AnyVal {
    def initLoader[T](funct: (Int, Bundle) ⇒ Loader[T], funct1: (Loader[T]) ⇒ Unit, funct2: (Loader[T], T) ⇒ Unit): LoaderCallbacks[T] = new LoaderCallbacks[T] {
      /**
       * Create a new shared preferences loader when there isn't one or
       * one is no longer instantiated
       *
       * @param i - the id for the loader we want (typically 0, but not always)
       * @param bundle - any extra stuff to fetch (probably not used)
       * @return the SharedPreferencesLoader
       */
      override def onCreateLoader(i: Int, bundle: Bundle): Loader[T] = funct(i, bundle)

      override def onLoaderReset(loader: Loader[T]): Unit = funct1(loader)

      /**
       * After the preferences have been loaded, do the stuff here
       *
       * @param loader - loader for the preferences
       * @param loaderData - all the data loaded from the loader
       */
      override def onLoadFinished(loader: Loader[T], loaderData: T): Unit = funct2(loader, loaderData)
    }

  }

}



