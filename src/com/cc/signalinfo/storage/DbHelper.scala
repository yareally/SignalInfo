package com.cc.signalinfo.storage

import android.content.Context
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}

/**
 * @author Wes Lanning
 * @version 2014-06-17
 */
object DbHelper {
  private var singleton: DbHelper = null

  def apply(context: Context, name: String, version: Int): DbHelper = {
    singleton = if (singleton == null) new DbHelper(context.getApplicationContext, name, version) else singleton
    singleton
  }
}

sealed class DbHelper private(context: Context, name: String, version: Int = 1) extends SQLiteOpenHelper(context, name, null, version) {
  override def onCreate(db: SQLiteDatabase) {

  }

  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
  }
}