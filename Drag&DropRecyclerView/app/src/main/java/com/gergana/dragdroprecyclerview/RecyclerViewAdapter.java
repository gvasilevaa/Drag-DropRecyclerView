package com.gergana.dragdroprecyclerview;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.util.ArrayList;

/**
 * Created by gergana on 12/2/14.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    public ArrayList<SimpleItem> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerViewAdapter(ArrayList<SimpleItem> myDataset) {

        this.mDataset = myDataset;

    }

    public RecyclerViewAdapter() {
        super();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {

        RecyclerView.ViewHolder vh = null;
        try {
            View v = null;
            if (viewType == TYPE_ITEM) {
                // create a new view
                v = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_item, parent, false);
                vh = new ViewItemHolder(v);


            } else if (viewType == TYPE_HEADER) {
                // create header view
                v = (View) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.recyclerview_header, parent, false);
                vh = new ViewHeaderHolder(v);
            }

        } catch (RuntimeException e) {
            Log.e("RuntimeException", "There is no type that matches the type " + viewType + "  make sure your using types correctly");
            e.printStackTrace();
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (position == 0 && holder instanceof ViewHeaderHolder) {
            //cast holder to ViewHeaderHolder and set data
        } else {
            if (holder instanceof ViewItemHolder) {
                //cast holder to ViewItemHolder and set data
                //mDataset items begin from index 0 and so we decrement position because we have header on position 0
                final SimpleItem item = mDataset.get(position - 1);
                if (item != null) {

                    /* Populate item information */
                    ((ViewItemHolder) holder).folderName.setText(item.getName());

                    ((ViewItemHolder) holder).itemView.setTag(holder);


                }
            }
        }
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        long h = (mDataset.get(position-1)).getName().hashCode();
        return h;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;

        return TYPE_ITEM;
    }


    //------------------------------- Item Holder---------------------------------------//
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewItemHolder extends RecyclerView.ViewHolder {

        public View recycleViewItem;
        public TextView folderName;
        private boolean checked = false;
        private ImageView icon;
        private ImageView checkIcon;
        private TextDrawable.IBuilder mDrawableBuilder;


        public ViewItemHolder(View v) {
            super(v);
            recycleViewItem = v;
            icon = (ImageView) v.findViewById(R.id.folder_image);
            checkIcon = (ImageView) v.findViewById(R.id.check_icon);
            folderName = (TextView) v.findViewById(R.id.folder_name);

            mDrawableBuilder = TextDrawable.builder()
                    .rect();
            //Set drawable
            TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .width(60)  // width in px
                    .height(60) // height in px
                    .endConfig()
                    .buildRect("A", Color.RED);


            icon.setImageDrawable(drawable);

        }


    }

    public static class ViewHeaderHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public View recycleViewHeader;

        public ViewHeaderHolder(View v) {
            super(v);
            recycleViewHeader = v;

        }
    }


}