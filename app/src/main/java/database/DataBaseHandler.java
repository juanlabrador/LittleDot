package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import domains.Child;
import domains.DotCategory;
import domains.Image;

/**
 * Created by Hossam on 8/20/2015.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DataBaseHandler";

    // All Static variables
    // Database Version
    public static final int DATABASE_VERSION = 2;
    // Database Name
    public static final String DATABASE_NAME = "LittleDotDB";

    // Children table name
    private static final String TABLE_CHILDREN = "children";


    //  Children  Table Columns names

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String DOB = "dob";
    private static final String SEX = "sex";
    private static final String IMAGE = "image";


    // SQL create  Children table
    private static final String  CREATE_CHILDREN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_CHILDREN + "("
                    + KEY_ID + " TEXT PRIMARY KEY,"
                    + KEY_NAME + " TEXT,"
                    + DOB + " TEXT,"
                    + SEX + " INTEGER,"
                    + IMAGE + " BLOB)";



    //    DOTS table name
    private static final String TABLE_DOTS = "dots";


    //  DOTS  Table Columns names
    private static final String KEY_CATEGORY_ID = "categoryId";
    private static final String KEY_CHILD_ID = "childId";
    private static final String KEY_ENTRY_DATA = "entryData";
    private static final String KEY_DATE = "dotDate";
    private static final String KEY_NOTE = "dotNote";
    private static final String KEY_TAG = "dotTagImages";

    //  SQL  create  Dots table

    private static final String CREATE_DOTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_DOTS + "("
                    + KEY_ID + " TEXT PRIMARY KEY,"
                    + KEY_CHILD_ID + " TEXT,"
                    + KEY_CATEGORY_ID + " INTEGER,"
                    + KEY_ENTRY_DATA + " TEXT,"
                    + KEY_DATE + " TEXT, "
                    + KEY_NOTE + " TEXT, "
                    + KEY_TAG + " TEXT)";

    //    Images table name
    private static final String TABLE_IMAGES = "images";

    //    Images Table Columns names
    private static final String KEY_DOT_ID = "dotID";
    private static final String KEY_URL = "url";

    //  SQL  create  Dots table
    private static final String CREATE_IMAGES_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_IMAGES + "("
                    + KEY_ID + " TEXT PRIMARY KEY,"
                    + KEY_DOT_ID + " TEXT,"
                    + KEY_URL + " TEXT, "
                    + IMAGE + " BLOB)";

    // Pending children table name
    private static final String TABLE_PENDING_CHILDREN = "pending_child";

    //  SQL  create  Dots table
    private static final String CREATE_PENDING_CHILDREN_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PENDING_CHILDREN + "("
                    + KEY_ID + " TEXT PRIMARY KEY)";

    // Pending dots table name
    private static final String TABLE_PENDING_DOTS = "pending_dots";

    //  SQL  create  Dots table
    private static final String CREATE_PENDING_DOTS_TABLE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_PENDING_DOTS + "("
                    + KEY_ID + " TEXT PRIMARY KEY)";


    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    // Create DataBase Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_IMAGES_TABLE);
        db.execSQL(CREATE_CHILDREN_TABLE);
        db.execSQL(CREATE_DOTS_TABLE);
        db.execSQL(CREATE_PENDING_CHILDREN_TABLE);
        db.execSQL(CREATE_PENDING_DOTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHILDREN);

        // Create tables again
        onCreate(db);
    }

    /**
     * Adds new Child for delete when have internet connection
     * @param id children
     *
     */
    public void addPendingChild(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        db.insert(TABLE_PENDING_CHILDREN, null, values);
        db.close();
    }

    /**
     * Adds new dot for delete when have internet connection
     * @param id dot
     *
     */
    public void addPendingDot(String id){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        db.insert(TABLE_PENDING_DOTS, null, values);
        db.close();
    }

    /**
     * Return list of Children id in database for delete in server
     */
    public List<String> getAllPendingChildren(){
        List<String> ids = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_CHILDREN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return ids;
    }

    /**
     * Return list of dots id in database for delete in server
     */
    public List<String> getAllPendingDots(){
        List<String> ids = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_PENDING_DOTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ids.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return ids;
    }

    /**
     * Delete all pending children by delete once delete server success
     */
    public void deletePendingChildren(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PENDING_CHILDREN;
        db.execSQL(query);
        db.close();
    }

    /**
     * Delete all pending dots by delete once delete server success
     */
    public void deletePendingDots(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_PENDING_DOTS;
        db.execSQL(query);
        db.close();
    }

    /** ++++++++++++++++++++++++++++++++++++ CHILDREN ++++++++++++++++++++++++++++++++++++++*/
    /**
     * Adds new Child to table Children
     *
     * @param child Object
     *
     */
    public void addNewChild(Child child){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, child.getID());
        values.put(KEY_NAME, child.getName());
        values.put(DOB,child.getDob());
        values.put(SEX, child.getSex());
        values.put(IMAGE,child.getImage());
        db.insert(TABLE_CHILDREN, null, values);
        db.close();
    }

    /**
     * If child doesn't exist, add, if exist, don't add
     * @param child object
     */
    public void isChildExist(Child child) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_CHILDREN + " WHERE " + KEY_ID + " = \'" + child.getID() + "\'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            addNewChild(child);
        }
        cursor.close();
    }

    /**
     * Return list of Children in database
     */
    public List<Child> getAllChildChildren(){
        List<Child> childrenList=new ArrayList<Child>();
        String selectQuery = "SELECT  * FROM " + TABLE_CHILDREN;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Child child = new Child();
                child.setID(cursor.getString(0));
                child.setName(cursor.getString(1));
                child.setDob(cursor.getString(2));
                child.setSex(cursor.getInt(3));
                child.setImage(cursor.getBlob(4));
                // Adding contact to list
                childrenList.add(child);
            } while (cursor.moveToNext());
        }
        return childrenList;
    }

    /**
     * Update Child Data
     * @param child Object
     */
    public int updateChild(Child child){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, child.getName());
        values.put(DOB, child.getDob());
        values.put(SEX, child.getSex());
        values.put(IMAGE, child.getImage());
        int i = db.update(TABLE_CHILDREN, values, KEY_ID + " = ?",
                new String[]{String.valueOf(child.getID())});
        return i;
    }

    /**
     * Delete  Child data from table children , and delete all dots of that child
     * @param ID id of child
     */
    public int deleteChild(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        int i=  db.delete(TABLE_CHILDREN, KEY_ID + " = ?",
                new String[]{String.valueOf(ID)});
        db.delete(TABLE_DOTS, KEY_CHILD_ID + " = ?",
                new String[]{String.valueOf(ID)});
        db.close();
        return i;
    }


    /** ++++++++++++++++++++++++++++++++++++++++++ DOTS +++++++++++++++++++++++++++++++++++++*/
    /**
     * add new dot of a child
     * @param dotCategory Object
     * @param child Object
     */

    public void addNewDot(DotCategory dotCategory, Child child){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, dotCategory.getId());
        values.put(KEY_CHILD_ID, child.getID());
        values.put(KEY_CATEGORY_ID, dotCategory.getCategoryId());
        values.put(KEY_ENTRY_DATA, dotCategory.getEntryData().toString());
        values.put(KEY_DATE, dotCategory.getDate());
        values.put(KEY_NOTE, dotCategory.getNote());
        values.put(KEY_TAG, dotCategory.getTag());

        int dotID = (int) db.insert(TABLE_DOTS, null, values);
        if (dotCategory.getImages() != null) {
            addImage(dotCategory.getImages(), dotCategory.getId());
        }
        db.close();
    }

    /**
     * If dot doesn't exist, add, if exist, don't add
     * @param dot object
     */
    public void isDotExist(DotCategory dot, Child child) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_DOTS + " WHERE " + KEY_ID + " = \'" + dot.getId() + "\'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            addNewDot(dot, child);
        }
        cursor.close();
    }


    /**
     * Return list of Dots of a Child in database
     * Descending order newest dot will be in the first index
     * oldest will be in last index
     * @param child Object
     * @return childDots of Dot Objects

     */
    public List<DotCategory> getChildDots(Child child){
        List<DotCategory> childDotCategories =new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DOTS + " WHERE "
                + KEY_CHILD_ID + " = \'" + child.getID() + "\' ORDER BY " + KEY_DATE + " DESC ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DotCategory dotCategory = new DotCategory();

                dotCategory.setId(cursor.getString(0));
                dotCategory.setCategoryId(cursor.getInt(2));
                //Extract String
                String json = cursor.getString(3);
                JSONArray array = null;
                // String convert to JSONArray
                try {
                    array = new JSONArray(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dotCategory.setEntryData(array);
                dotCategory.setDate(cursor.getString(4));
                dotCategory.setNote(cursor.getString(5));
                dotCategory.setTag(cursor.getString(6));

                // For images
                ArrayList<Image> gallery = new ArrayList<>();
                gallery.addAll(getImages(dotCategory.getId()));
                if (!gallery.isEmpty())
                    dotCategory.setImages(gallery);
                childDotCategories.add(dotCategory);
            } while (cursor.moveToNext());
        }

        return childDotCategories;

    }

    /**
     * Update dot of Child
     * @param dotCategory Object
     * @return the number of rows affected
     */
    public int updateDot(DotCategory dotCategory){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CATEGORY_ID, dotCategory.getCategoryId());
        values.put(KEY_ENTRY_DATA, dotCategory.getEntryData().toString());
        values.put(KEY_DATE, dotCategory.getDate());
        values.put(KEY_NOTE, dotCategory.getNote());
        values.put(KEY_TAG, dotCategory.getTag());
        int dotID = db.update(TABLE_DOTS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(dotCategory.getId())});

        if (dotCategory.getImages() != null) {
            addImage(dotCategory.getImages(), dotCategory.getId());
        }
        return dotID;
    }

    /**
     * Return filter list of Dots of a Child in database based on selected category
     * Descending order newest dot will be in the first inex
     * oldest will be in last index
     * @param categoryID
     * @return childID
     */

    public List<DotCategory> getDotsOfCategoryForChild(int categoryID, String childID){
        List<DotCategory> childDotCategories =new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_DOTS +
                " WHERE " + KEY_CHILD_ID + " = \'" + childID  +"\' AND "
                + KEY_CATEGORY_ID + " = \'" + categoryID + "\' ORDER BY " + KEY_DATE + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DotCategory dotCategory = new DotCategory();

                dotCategory.setId(cursor.getString(0));
                dotCategory.setCategoryId(cursor.getInt(2));
                //Extract String
                String json = cursor.getString(3);
                JSONArray array = null;
                // String convert to JSONArray
                try {
                    array = new JSONArray(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dotCategory.setEntryData(array);
                dotCategory.setDate(cursor.getString(4));
                dotCategory.setNote(cursor.getString(5));
                dotCategory.setTag(cursor.getString(6));
                // For images
                ArrayList<Image> gallery = new ArrayList<>();
                gallery.addAll(getImages(dotCategory.getId()));
                if (!gallery.isEmpty())
                    dotCategory.setImages(gallery);
                // Adding contact to list
                childDotCategories.add(dotCategory);
            } while (cursor.moveToNext());
        }
        return childDotCategories;
    }

    /**
     * Delete Dot data from table Dot
     * @param dotCategory Object
     */
    public int deleteDot(DotCategory dotCategory){
        // Delete all images first
        if (dotCategory.getImages() != null)
            for (Image image : dotCategory.getImages()) {
                deleteImage(image);
            }
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_DOTS, KEY_ID + " = ?",
                new String[]{String.valueOf(dotCategory.getId())});
        db.close();
        return i;
    }

    /** ++++++++++++++++++++++++++++++++++++++++++ IMAGE +++++++++++++++++++++++++++++++++++++*/

    /**
     * Add image to db
     * @param images list
     * @param ID id dot
     */
    public void addImage(List<Image> images, String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues imagesValues = new ContentValues();
        for (Image img : images) {
            imagesValues.put(KEY_ID, img.getId());
            imagesValues.put(KEY_DOT_ID, ID);
            imagesValues.put(KEY_URL, img.getUrl());
            imagesValues.put(IMAGE, img.getBytes());
            db.insert(TABLE_IMAGES, null, imagesValues);
        }
        db.close();
    }

    /**
     * Add image to db
     * @param image object
     * @param dot id dot
     */
    public void addImage(Image image, String dot) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues imagesValues = new ContentValues();
        imagesValues.put(KEY_ID, image.getId());
        imagesValues.put(KEY_DOT_ID, dot);
        imagesValues.put(KEY_URL, image.getUrl());
        imagesValues.put(IMAGE, image.getBytes());
        db.insert(TABLE_IMAGES, null, imagesValues);
        db.close();
    }

    /**
     * If image doesn't exist, add, if exist, don't add
     * @param image object
     */
    public void isImageExist(Image image, String dot) {
        SQLiteDatabase db = getWritableDatabase();
        String Query = "SELECT * FROM " + TABLE_IMAGES + " WHERE " + KEY_ID + " = \'" + image.getId() + "\'";
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            addImage(image, dot);
        }
        cursor.close();
    }

    /**
     * Update image
     * @param image Object
     * @return the number of rows affected
     */
    public int updateImage(Image image){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_URL, image.getUrl());
        values.put(IMAGE, "");
        int dotID = db.update(TABLE_IMAGES, values, KEY_ID + " = ?",
                new String[]{String.valueOf(image.getId())});

        return dotID;
    }

    /**
     * Delete Image data from table images
     * @param image Object
     */
    public int deleteImage(Image image){
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_IMAGES, KEY_ID + " = ?",
                new String[]{String.valueOf(image.getId())});
        db.close();
        return i;
    }

    /**
     * get list image by dot
     * @param id
     * @return
     */
    public List<Image> getImages(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Image> gallery = new ArrayList<>();
        String selectImagesQuery = "SELECT * FROM " + TABLE_IMAGES + " WHERE "+ KEY_DOT_ID + " = \'" + id + "\'";
        Log.i(TAG, selectImagesQuery);
        Cursor imagesCursor = db.rawQuery(selectImagesQuery, null);
        if (imagesCursor.moveToFirst()) {
            do {
                Image image = new Image();
                image.setId(imagesCursor.getString(0));
                image.setUrl(imagesCursor.getString(2));
                image.setBytes(imagesCursor.getBlob(3));
                gallery.add(image);
            } while (imagesCursor.moveToNext());
        }
        db.close();
        return gallery;
    }

    /**
     * get list image by dot
     * @param id
     * @return
     */
    public Image getImage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectImagesQuery = "SELECT * FROM " + TABLE_IMAGES + " WHERE "+ KEY_ID + " = \'" + id + "\'";
        Cursor imagesCursor = db.rawQuery(selectImagesQuery, null);
        Image image = new Image();
        if(imagesCursor.getCount() > 0){
            image.setId(imagesCursor.getString(0));
            image.setUrl(imagesCursor.getString(2));
            image.setBytes(imagesCursor.getBlob(3));
        }
        imagesCursor.close();
        db.close();
        return image;
    }
}
