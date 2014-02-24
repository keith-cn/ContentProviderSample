package com.training.contentprovidersample;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// projection specifies the columns we want to get in result data.
		String[] projection = { "_id", "title", "author", "genre" };
		/**
		 * The most important part of Content Provider invocation is URI shema =
		 * content:// authority = com.training.contentprovidersample (remember
		 * we registered it in Manifest file?) path = books (that our content
		 * provider can parse. Remember we use UriMatcher to parse it?)
		 */
		Uri mBooks = Uri
				.parse("content://com.training.contentprovidersample/books");

		/**
		 * Now invoke our content provider and get Cursor as result of operation
		 * getContentResolver used to get instance of ContentResolved that is
		 * responsible for finding appropriate ContentProvider basing on
		 * authority First parameter - URI Second parameter - projection Thirst
		 * parameter - selection if we want specify some selection (we don't
		 * now). It might be "title = ?" for example Fourth parameter -
		 * selection parameters (all '?' characters from selection criteria will
		 * be replaced by element of array from this parameter). For example
		 * {"Android Book Title"} And last parameter is sorting order. For
		 * instance "title ASC" to sort by title
		 */
		Log.e("Keith", "before get cursor");
		Cursor queryCursor = getContentResolver().query(mBooks, projection, "",
				null, null);
		Log.e("Keith", "after get cursor");

		// check the cursor on validity
		if (queryCursor == null) {
			Log.d("SampleContentProvider", "can't create cursor");
		} else {
			// Iterate through all cursor elements to get all data from our
			// query
			while (queryCursor.moveToNext()) {
				// each cursor element has data according to projection
				// Use type specific method to get appropriate column data
				int id = queryCursor.getInt(0);
				String title = queryCursor.getString(1);
				String author = queryCursor.getString(2);
				String genre = queryCursor.getString(3);
				// Log it to LogCat console
				Log.d("SampleContentProvider", "Book[" + id + "] = title: "
						+ title + " author: " + author + " genre: " + genre);
			}
			// When we are done, close cursor
			//queryCursor.close();
		}

		/**
		 * This string array specifies all columns that will be used from
		 * Cursor. All values of these columns will be added to appropriate
		 * TextView
		 */
		String[] fromColumns = { "title", "author", "genre" };
		/**
		 * Here we specify the IDs of TextView widgets that will be used to
		 * display each specific column. They should correspond to previous
		 * array with column name
		 */
		int[] toViews = { R.id.title, R.id.author, R.id.genge };
		/**
		 * Now we are ready to create SimpleCursorAdapter. first parameter is
		 * context second parameter is layout that will be used for row
		 * representation in ListView third parameter is cursor with data fourth
		 * parameter is array of columns from cursor fifth parameter is array of
		 * IDs for UI widgets the last one is flags parameter that tells adapter
		 * how to load data (see documentation for details).
		 */
		Log.e("Keith", "before get adapter");
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.row_layout, queryCursor, fromColumns, toViews, 0);
		Log.e("Keith", "after get adapter");
		/**
		 * We need get the link to ListView class from our UI. To do so we use
		 * findViewById method with UI widget id as parameter
		 */
		ListView listView = (ListView) findViewById(R.id.listView1);
		/**
		 * Now join the ListView and adapter for it
		 */
		listView.setAdapter(adapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
