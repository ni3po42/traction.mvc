/* Copyright 2013 Tim Stratton

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package ni3po42.android.amvvmdemo.providers;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class DemoProvider
    extends ContentProvider
{
    public static final class Columns
    {
        public static final String ID = "_id";
        public static final String Name = "name";
        public static final String Quest = "quest";
        public static final String FavoriteColor = "favoritecolor";
        public static final String OtherAnswer = "otheranswer";
    }

    public static final String URI_AUTHORITY = "ni3po42.android.amvvmdemo";

    public static Uri CONTENT_URI = Uri.parse("content://ni3po42.android.amvvmdemo");

    helper db;

    @Override
    public boolean onCreate()
    {
        db = new helper(getContext(), 1);

        return true;
    }

    private final static String[] projection = new String[]{
            Columns.ID, Columns.Name, Columns.Quest, Columns.FavoriteColor, Columns.OtherAnswer
    };

    @Override
    public Cursor query(Uri uri, String[] notUsed, String alsoNotUsed, String[] selArgs, String nope)
    {
        if (db == null)
            return null;

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("answers");

        SQLiteDatabase rdb = db.getReadableDatabase();

        Cursor c = qb.query(rdb, projection, null, selArgs, null, null, "_id");
        return c;
    }

    @Override
    public String getType(Uri uri)
    {
        return "vnd.android.cursor.dir/"+URI_AUTHORITY+".answers";
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues)
    {
        //not supported
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings)
    {
        //not supported
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings)
    {
        //not supported
        return 0;
    }


    private static String DATABASE_NAME = "amvvmdemo.db";

    class helper extends SQLiteOpenHelper
    {
        public helper(Context context, int version)
        {
            super(context, DATABASE_NAME, null, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            //just use some static data...
            StringBuilder sb = new StringBuilder();
            sb.append("CREATE TABLE answers( _id integer primary key autoincrement, name TEXT NOT NULL, quest TEXT NOT NULL, favoritecolor INTEGER DEFAULT -1, otheranswer TEXT DEFAULT NULL);");

            sqLiteDatabase.execSQL(sb.toString());

            ContentValues cv = new ContentValues();
            cv.put(Columns.Name, "Sir Lancelot of Camelot");
            cv.put(Columns.Quest, "To seek the Holy Grail");
            cv.put(Columns.FavoriteColor, 0xFF0000FF);
            cv.putNull(Columns.OtherAnswer);
            sqLiteDatabase.insert("answers", null, cv);

            cv = new ContentValues();
            cv.put(Columns.Name, "Sir Robin of Camelot");
            cv.put(Columns.Quest, "To seek the Holy Grail");
            cv.put(Columns.OtherAnswer, "I don't know that!! AAAAAAHHH!!!!");
            sqLiteDatabase.insert("answers", null, cv);

            cv = new ContentValues();
            cv.put(Columns.Name, "Sir Galahad of Camelot");
            cv.put(Columns.Quest, "I seek the Grail");
            cv.put(Columns.FavoriteColor, 0xFFFFFF00);
            cv.put(Columns.OtherAnswer, "Blue! No! Yellow!!!!");
            sqLiteDatabase.insert("answers", null, cv);

            cv = new ContentValues();
            cv.put(Columns.Name, "Arthur, King of the Britons!");
            cv.put(Columns.Quest, "To seek the Holy Grail!");
            cv.put(Columns.OtherAnswer, "What do you mean, an African or European swallow?");
            sqLiteDatabase.insert("answers", null, cv);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion)
        {
            //eh...
        }
    }

}
