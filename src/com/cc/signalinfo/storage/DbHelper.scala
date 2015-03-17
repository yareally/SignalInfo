package com.cc.signalinfo.storage.java

import android.annotation.TargetApi
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase

import android.database.sqlite.SQLiteOpenHelper
import android.os.Build

/**
 * @author Wes Lanning
 * @version 2014-06-17
 */
object DbHelper
{
    private var singleton: DbHelper = null

    def apply(context: Context, name: String, version: Int): DbHelper = {
         classOf[SQLiteOpenHelper] synchronized {
             if (singleton == null) {
                 singleton = new DbHelper(context.getApplicationContext, name, version)
             }
             return singleton
         }
    }
}


sealed class DbHelper private(context: Context, name: String, version: Int = 1) extends SQLiteOpenHelper(context, name, null, version)
{

    override def onCreate(db: SQLiteDatabase) {

    }

    override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
}