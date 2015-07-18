/*
 * Copyright 2015 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.ehviewer.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.hippo.ehviewer.client.data.GalleryBase;
import com.hippo.ehviewer.dao.DaoMaster;
import com.hippo.ehviewer.dao.DaoSession;
import com.hippo.ehviewer.dao.GalleryBaseObj;
import com.hippo.ehviewer.dao.GalleryBaseObjDao;
import com.hippo.ehviewer.dao.SpiderObj;

public class DBUtils {

    private static DaoSession sDaoSession;

    public static void initialize(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(
                context.getApplicationContext(), "ehviewer", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        sDaoSession = daoMaster.newSession();
    }

    public static SpiderObj getSpiderObj(int gid) {
        return sDaoSession.getSpiderObjDao().load((long) gid);
    }

    public static void insertSpiderObj(SpiderObj spiderObj) {
        sDaoSession.getSpiderObjDao().insert(spiderObj);
    }

    public static void addGalleryBase(GalleryBase galleryBase) {
        GalleryBaseObjDao galleryBaseObjDao = sDaoSession.getGalleryBaseObjDao();
        GalleryBaseObj galleryBaseObj = galleryBaseObjDao.load((long) galleryBase.gid);
        if (galleryBaseObj == null) {
            // add new item, set refernce 1
            galleryBaseObj = new GalleryBaseObj();
            galleryBaseObj.setGid((long) galleryBase.gid);
            galleryBaseObj.setToken(galleryBase.token);
            galleryBaseObj.setTitle(galleryBase.title);
            galleryBaseObj.setTitleJpn(galleryBase.titleJpn);
            galleryBaseObj.setThumb(galleryBase.thumb);
            galleryBaseObj.setCategory(galleryBase.category);
            galleryBaseObj.setPosted(galleryBase.posted);
            galleryBaseObj.setUploader(galleryBase.uploader);
            galleryBaseObj.setRating(galleryBase.rating);
            galleryBaseObj.setReference(1);
            galleryBaseObjDao.insert(galleryBaseObj);
        } else {
            // already in db, add refernce
            galleryBaseObj.setReference(galleryBaseObj.getReference() + 1);
            galleryBaseObjDao.update(galleryBaseObj);
        }
    }

    public static void removeGalleryBase(GalleryBase galleryBase) {
        GalleryBaseObjDao galleryBaseObjDao = sDaoSession.getGalleryBaseObjDao();
        GalleryBaseObj galleryBaseObj = galleryBaseObjDao.load((long) galleryBase.gid);
        if (galleryBaseObj.getReference() <= 1) {
            // No reference, delete it
            galleryBaseObjDao.deleteByKey((long) galleryBase.gid);
        } else {
            // Still has reference, sub refernce
            galleryBaseObj.setReference(galleryBaseObj.getReference() - 1);
            galleryBaseObjDao.update(galleryBaseObj);
        }
    }
}
