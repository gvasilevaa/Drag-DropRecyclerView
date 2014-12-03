package com.gergana.dragdroprecyclerview;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends Activity {

    private static String[] myDataset;
    private ReorderRecyclerView mRecyclerView;
    private ArrayList<SimpleItem> items;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (ReorderRecyclerView) findViewById(R.id.recycler_view);
        /* Get layout manager from the RecyclerView it self */
        mLayoutManager = mRecyclerView.getLayoutManager();

        mRecyclerView.setHasFixedSize(true);
        /* Populate dummy Items */
        items = populateData();

        /* Set Adapter */
        mAdapter = new RecyclerViewAdapter(items);
        mRecyclerView.setAdapter(mAdapter);
    }


    private ArrayList<SimpleItem> populateData() {
        myDataset = new String[200];
        for (int j = 0; j < 200; j++) {
            myDataset[j] = "Item " + j;
        }

        ArrayList<SimpleItem> items = new ArrayList<SimpleItem>();
        SimpleItem item = null;
        for (int i = 0; i < myDataset.length; i++) {
            item = new SimpleItem(i, myDataset[i], false);
            items.add(item);
        }


        return items;

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
