CWAC LoaderEx: Taking Loaders to the Next Level
===============================================

Android 3.0 introduced the `Loader` framework, and the
Android Compatibility Library allows you to use that
framework going back to Android 1.6. However, the only
supplied concrete implementation of a `Loader` is
`CursorLoader`, and that is only for use with a
`ContentProvider`. Moreover, while the `Loader` framework
handles database queries in the background, it does not
help with the rest of your CRUD operations.

This `LoaderEx` project is designed to help fill some
of those gaps. Presently, it provides a `SQLiteCursorLoader`,
offering the same basic concept as `CursorLoader`, but
for use with a `SQLiteDatabase` instead of a `ContentProvider`.
It also supplies some boilerplate `AsyncTasks` to handle
database inserts and deletes in the background. It also supplies:

- `SQLCipherCursorLoader`, for operations with
[SQLCipher for Android](http://sqlcipher.net/sqlcipher-for-android/)
- `SharedPreferencesLoader`, for retrieving the default
`SharedPreferences` object without tying up the main
application thread.

This is packaged as an Android library project, though a simple
JAR [is also available](https://github.com/commonsguy/downloads). If you are working on a native Honeycomb
application (i.e., not using the Android Compatibility
Library), please use the JAR &mdash; putting it in your project's
`libs/` directory should be sufficient.

Usage: SQLiteCursorLoader
-------------------------
Generally speaking, you use `SQLiteCursorLoader` in the same
fashion as you would use `CursorLoader` &mdash; by having your
activity implement `LoaderManager.LoaderCallbacks<Cursor>`
and calling `initLoader()` on the `LoaderManager`. Then, in
your `onCreateLoader()` callback method, you can return a
properly-constructed `SQLiteCursorLoader`. Everything else
behaves as `CursorLoader` does.

### Constructors

There is only one at this time, taking a `SQLiteDatabase`
object, plus the same parameters as is used by `rawQuery()`
on `SQLiteOpenHelper` &mdash; a `String` with your SQL query
and a `String[]` of positional parameter values (to replace
any `?` you have in your query).

**NOTE**: Version 0.4 and previous of this component took a
`SQLiteDatabase` as a parameter instead of a `SQLiteOpenHelper`.
The change was made so that database creation and upgrades can
occur on the background thread. Apologies for the API change.

### Packages

There are two implementations of `SQLiteCursorLoader`, in
two separate packages.

The one in `com.commonsware.cwac.loaderex` works using
the native API Level 11+ implementation of the `Loader`
framework.

The one in `com.commonsware.cwac.loaderex.acl` works
using the implementation of the `Loader` framework from
the Android Compatibility Library (ACL). You will need to
have the ACL as part of your build path in addition to having
the JAR or library project of `LoaderEx`.

In your code, you will choose the one you wish to use
based upon whether you are using the ACL or not.

### Database Modifications

If you use the `insert()`, `update()`, `delete()`, `replace()`, and
`execSQL()` methods on `SQLiteCursorLoader`, the loader
framework will automatically update you to reflect a new
`Cursor` with the changed data. These methods take
the same parameters as they do on `SQLiteDatabase`.

### AbstractCursorLoader

`SQLiteCursorLoader` itself extends an `AbstractCursorLoader`.
`AbstractCursorLoader` is much of the logic from the ACL's
`CursorLoader`, but with the actual query code abstracted
out. You are welcome to make your own subclasses of
`AbstractCursorLoader` if you are creating `Cursor`s from
other sources. Just override `buildCursor()` and have it
return the `Cursor` &mdash; this method is called on a
background thread and therefore is not time-limited.

Usage: SQLite*Task
------------------
**THESE CLASSES ARE DEPRECATED**

`SQLiteInsertTask` and `SQLiteDeleteTask` are also supplied
in this library. These simply perform `insert()` and `delete()`
calls on a `SQLiteDatabase` inside an `AsyncTask`, to get that
work off the main application thread. These classes are designed
to work on API Level 5 or higher and as such are not
`Loader`-aware.

However, you can arrange to do post-CRUD work by extending
these classes and overriding `onPostExecute(Exception)`:

    new SQLiteInsertTask(db.getWritableDatabase(),
                         "constants", DatabaseHelper.TITLE,
                         values) {
      @Override
      public void onPostExecute(Exception e) {
        getLoaderManager().restartLoader(0, null,
                                         ConstantsBrowser.this);
      }
    }.execute();

The `Exception` will be `null` if everything succeeded in
the background work; otherwise, it will be whatever `Exception`
was raised by the `insert()` call, etc.

Notes on Threading
------------------
`SQLiteDatabase` itself is thread-safe, in that it manages
a lock to ensure that two operations do not occur in
parallel. However, that assumes you are using a single
instance of `SQLiteDatabase`. Hence, if you are using
`SQLiteCursorLoader` and the other classes in this project
you will want to make sure that you are using a single
instance of your `SQLiteDatabase` object. If you have more than
one component using the database, that `SQLiteDatabase`
effectively will have to be global in scope, such as by
holding onto it (or its containing `SQLiteOpenHelper`)
in a static data member.

Usage: SQLCipherCursorLoader
----------------------------
This class works nearly identically to `SQLiteCursorLoader`.
The biggest difference is that it takes a SQLCipher for Android
version of `SQLiteDatabase` in its constructor, instead of
a `SQLiteOpenHelper`. The `SQLiteDatabase` will need to be readable or
writeable depending on what you are doing with it.

As with `SQLiteCursorLoader`, there are two editions of `SQLCipherCursorLoader`,
one in `com.commonsware.cwac.loaderex` and one in
`com.commonsware.cwac.loaderex.acl` &mdash; the latter is for use with the
Android Support package's version of the `Loader` framework.

Apps using `SQLCipherCursorLoader` will need a full copy
of SQLCipher for Android in their project for the project to 
run properly.

Usage: SQLCipherUtils
---------------------
There is a `SQLCipherUtils` class in `com.commonsware.cwac.loaderex` with
a couple of static methods that may be useful to those implementing SQLCipher
for Android in their projects.

`getDatabaseState()` will return a `SQLCipherUtils.State` enum indicating
what the state of the database is:

- `DOES_NOT_EXIST`, meaning that we cannot find a database file
- `UNENCRYPTED`, meaning that we have found a database file and believe
that it is unencrypted
- `ENCRYPTED`, meaning that we have found a database file and believe
that it is encrypted, and
- `UNKNOWN`, meaning that we do not know what is going on with the database

`getDatabaseState()` takes a `Context` and the name of the database as parameters.

`encrypt()` will replace an unencrypted database
with an encrypted version, given the supplied `Context`, the name of the database,
and the passphrase to use for encryption.

Usage: SharedPreferencesLoader
------------------------------
`SharedPreferencesLoader` largely mirrors `SQLiteCursorLoader`:

- There are two implementations, one for native API Level 11+
development (in the base `com.commonsware.cwac.loaderex` package)
and one for use with the Android Support package (in the
`com.commonsware.cwac.loaderex.acl` package).

- Your activity should implement the `LoaderManager.LoaderCallbacks<SharedPreferences>`
interface.

- In your `onCreateLoader()` method, return an instance of
`SharedPreferencesLoader`, which has a one-parameter constructor
taking your `Activity` (or other `Context`) as the parameter.

- In your `onLoadFinished()` method, make use of the
`SharedPreferences` object delivered unto you.

In addition, there is a static `persist()` method that takes
a `SharedPreferences.Editor` object and arranges to save those
edits on a background thread, regardless of Android API level.

Dependencies
------------
This project sometimes depends on the Android Support package
(formerly the Android Compatibility Library, or ACL). If you
are using it in source form as an Android
library project, you will need the Android Support package.
If you are using the JAR, you only need the Android Support
package if you are using the `.acl` editions of the classes. 

This project should work on API Level 7 and higher, except for any portions that
may be noted otherwise in this document. Please report bugs if you find features
that do not work on API Level 7 and are not noted as requiring a higher version.

Version
-------
This is version v0.7.1 of this module, meaning that its author
really should consider formalizing v1.0.0 before too long...

Demo
----
In the `demo/` sub-project you will find
a sample activity that demonstrates the use of `SQLiteCursorLoader`.
There are two implementations of this sample, one for the 
Android Support package and one for native API Level 11 work.
There are also sample activities demonstrating the use
of `SharedPreferencesLoader`.

Note that when you build the JAR via `ant jar`, the sample
activity is not included, nor any resources -- only the
compiled classes for the actual library are put into the JAR.

Future
------
Future editions of this project will add things like
support for `query()` in addition to `rawQuery()`-style queries

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please post a question
on [StackOverflow](http://stackoverflow.com/questions/ask) tagged with `commonsware` and `android`. Be sure to indicate
what CWAC module you are having issues with, and be sure to include source code 
and stack traces if you are encountering crashes.

If you have encountered what is clearly a bug, please post an [issue](https://github.com/commonsguy/cwac-loaderex/issues). Be certain to include complete steps
for reproducing the issue.

Do not ask for help via Twitter.

Release Notes
-------------
- v0.7.1: bug fix
- v0.7.0: added SQLCipher for Android support
- v0.6.0: added `replace()` (by request)
- v0.5.0: switched to taking a `SQLiteOpenHelper` instead of a `SQLiteDatabase`
- v0.4.0: added `insert()`, `update()`, `delete()`, and `execSQL()`; better on-change support
- v0.3.0: added `SharedPreferencesLoader`
- v0.2.0: added `SQLiteInsertTask` and `SQLiteDeleteTask`
- v0.1.0: initial release

Who Made This?
--------------
<a href="http://commonsware.com">![CommonsWare](http://commonsware.com/images/logo.png)</a>

