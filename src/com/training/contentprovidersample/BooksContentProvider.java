package com.training.contentprovidersample;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

public class BooksContentProvider extends ContentProvider {

	/** Reference to SQLiteOpenHelper. 
	* We will use our own private Database Helper class
	* to track the db access and updates (see this class definition below)
	*/
	private DatabaseHelper mDatabaseHelper;
	
	/** 
	 * this is the name of our db file that we created on previous step
	 */
	private static final String DATABASE_NAME = "books.db";

	/** Database version. */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Constant mapped to uri "com.training.contentprovidersample/books"} via mUriMatcher.
	 * We will use this constant in switch to decide what query we should perform depends on requested URI. You will see it later. 
	 */
	private static final int BOOKS = 1;
	/**
	 * Constant mapped to uri "com.training.contentprovidersample/books/#" via mUriMatcher.
	 * We will use this constant in switch to decide what query we should perform depends on requested URI. You will see it later. 
	 */
	private static final int BOOKS_ID = 2;
	
	/** Local UriMatcher. 
	* this class will help us parse the passed URI to content provider
	*/
	private static final UriMatcher mUriMatcher;
	
	/** Now we initialize UriMatcher and add two URI that we support in our content provider
	 * first parameter is authority, second parameter is path and the last parameter is code that will be assigned for the URI if it matches the path
	 * We support two URI: first one is to get all books, and the second one to get particular book by id 
	 */
	static {
		mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		mUriMatcher.addURI("com.training.contentprovidersample", "books", BOOKS);
		mUriMatcher.addURI("com.training.contentprovidersample", "books/#", BOOKS_ID);
	}

	
	/** Utility class for db creation and update. */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		/**
		 * Default constructor.
		 * @param context - parent context.
		 * We invoke super class constructor here and pass database name and version
		 * Third parameter is CursorFactory, we don't need to customize it, so it's null
		 */
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		/**
		 * Because we already created table manually in our db on previous steps, 
		 * we don't want to recreate it again. 
		 * But in real application you might want to create table in a code.
		 * Here is how you can do it   
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			
			db.execSQL("Create table books"
					+ "( _id INTEGER PRIMARY KEY AUTOINCREMENT, "					
					+ " title VARCHAR(100), "
					+ " author VARCHAR(100), "
					+ " genre VARCHAR(50));");
			
		}

		/**
		 * In case your db version is updated, you might want to make some update in db structure.
		 * In this simple app we don't want to do any changes.
		 * Still the commented code has some stupid implementation: drop all tables and recreate them.  
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// drop db
			/*
			db.execSQL("DROP TABLE IF EXISTS books");
			// create new db
			onCreate(db);
			*/
		}
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		
		return null;
	}

	/**
	 * In our sample application we will handle two types of URI requests:
	 * 1: "com.training.contentprovidersample/books" � to return whole list of books
	 * 2: "com.training.contentprovidersample/books/#� � to return specific book details that matches the book id 
	 * (for instance com.training.contentprovidersample/books/2 to get book with id equals 2)
	 * uri parameter - is URI itself that we will parse using our uri matcher
	 * projection - is the list of columns that expected to be in result cursor
	 * selection - is selection criteria for query
	 * selectionArgs - the list of values that used in selection criteria
	 * sortOrder - sorting order that client want to get for result data
	 */
	@Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        /*
         * Choose the table to query and a sort order based on the code returned for the incoming URI.
         * We use our uri matcher here
         */
        switch (mUriMatcher.match(uri)) {
            // If the incoming URI was for all of table3
            case BOOKS:
            	// let's sort books by title by default
                if (TextUtils.isEmpty(sortOrder)) sortOrder = "title ASC";
                break;

            // If the incoming URI was for a single row
            case BOOKS_ID:
                /*
                 * Because this URI was for a single row, the _ID value part is
                 * present. Get the last path segment from the URI; this is the _ID value.
                 * Then, append the value to the WHERE clause for the query
                 */
                selection = selection + "_ID = " + uri.getLastPathSegment();
                break;

            default:
                // If the URI is not recognized, you should do some error handling here.
        }
        // the code to actually do the query
        // initialize the query builder
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        // get access to database via our helper
		SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
		// set the appropriate table we want to select data from (we have only one table - books)
		qb.setTables("books");		
		// now execute query and get result cursor
		Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
		return c;
    }

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * This method is initial point for Content Provider.
	 * It's invoked even before any client tries get access to provider. 
	 * This is good place to do some initialization. 
	 */
	@Override
	public boolean onCreate() {
		/**
		 * All we want is create our internal Database Helper here
		 */
		mDatabaseHelper = new DatabaseHelper(getContext());
		boolean result = false;
		if (mDatabaseHelper != null) {
			// success so return true, we are ready to handle requests from clients
			result = true;
		}
		// something wrong, so return false
		return result;
	}

}
