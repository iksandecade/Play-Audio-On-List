package project.iksandecade.playaudioonlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import project.iksandecade.playaudioonlist.dao.DaoMaster;
import project.iksandecade.playaudioonlist.dao.DaoSession;

/**
 * Created by
 * Name         : Ihksan Sukmawan
 * Email        : iksandecade@gmail.com
 * Company      : Meridian.Id
 * Date         : 18/11/16
 * Project      : PlayAudioOnList
 */

public class DaoHandler {

    public static DaoSession getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "list-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();

        DaoMaster daoMaster = new DaoMaster(db);
        DaoSession mDaoSession = daoMaster.newSession();
        return mDaoSession;
    }
}
