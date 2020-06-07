package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.ParseException;
import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> implements View.OnLongClickListener{

    private Context mContext;
    public int rand1 = 45;
    private  HomeAdapter homeAdapter;
    private ArrayList<HomeItem> mHomeList;
    public int abs2 = 27;
    private OnItemClickListener mListener;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public int idx1 = 9;
    public int idx2 = 2;
    @Override
    public boolean onLongClick(View v) {

        return false;
    }
    public void removeItem(HomeItem hm, String key) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();

    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public HomeAdapter(Context context, ArrayList<HomeItem> homeList) {
        mContext = context;
        homeAdapter = this;
        mHomeList = homeList;
    }


    public void refreshList() {
        ArrayList<HomeItem> items = new ArrayList<>();
        items.addAll(mHomeList);
        mHomeList.clear();
        mHomeList.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.home_item, parent, false);
        return new HomeViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        View v = holder.itemView;
        HomeItem currentItem = mHomeList.get(position);

        ImageView bookmarkView = (ImageView) v.findViewById(R.id.bookmark);

        String imageUrl = currentItem.getImageUrl();
        String title = currentItem.getmTitle();
        String section = currentItem.getmSection();
        String time = currentItem.getmTime();

        holder.item = currentItem;
        String articleID = currentItem.getmArticleId();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("",Context.MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        if (allEntries.isEmpty()){
            bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
//            System.out.println("NOT FOUND");
            bookmarkView.setTag(R.drawable.ic_bookmark_border);
        }
        else {
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                //Log.d("map values", entry.getKey() + ": ");
//                System.out.println(articleID);
                if (articleID.compareToIgnoreCase(entry.getKey()) == 0) {
//                    System.out.println("FOUND1");
                    //Log.i("Found", articleID);
                    bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_fill));
                    bookmarkView.setTag(R.drawable.ic_bookmark_fill);
                    currentItem.setBookmarkFlag(true);
                    break;
                } else {
                    bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
//                    System.out.println("NOT FOUND");
                    bookmarkView.setTag(R.drawable.ic_bookmark_border);
                }
            }
        }
        holder.mTextViewTitle.setText(title);


            String timeToDisplay = null;
            idx1 = 5;

            Date pasTime = null;
            Date nowTime = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            TimeZone gmtTime = TimeZone.getTimeZone("PST");
            dateFormat.setTimeZone(gmtTime);
            int sel = 6;
            int deSel = 1;
        long modSec = 0;
        long modMin=0;
        long modHr = 0;
        long modDay = 0;

            try { pasTime = dateFormat.parse(time); }
            catch (ParseException | java.text.ParseException e) { e.printStackTrace(); }

            if(idx1>idx2){
                sel=9;
                long modDate = nowTime.getTime() - pasTime.getTime();
                if(idx1>idx2){
                    modSec = TimeUnit.MILLISECONDS.toSeconds(modDate);
                    modMin = TimeUnit.MILLISECONDS.toMinutes(modDate);
                }

                deSel=2;
                if(idx1>idx2){
                    modHr   = TimeUnit.MILLISECONDS.toHours(modDate);
                    modDay  = TimeUnit.MILLISECONDS.toDays(modDate);
                }


                if (modSec < 60 && sel>deSel) {
                    timeToDisplay = modSec+"s "+"ago";
                }
                else if (modMin < 60 && sel>deSel) {
                    timeToDisplay = modMin+"m "+"ago";
                }
                else if (modHr < 24 && deSel<sel) {
                    timeToDisplay = modHr+"h "+"ago";
                }
                else {
                    timeToDisplay = modDay+"d "+"ago";
                }
            }else {
                idx1 = idx1+idx2;
            }

        holder.mTextViewTime.setText(timeToDisplay);
        holder.mTextViewSection.setText(section);
        Picasso.with(mContext).load(imageUrl).into(holder.mImageView);
        holder.initializeLongClick();
    }

    @Override
    public int getItemCount() {
        return mHomeList.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextViewTitle;
        public TextView mTextViewSection;
        public TextView mTextViewTime;
        public HomeItem item;

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        public HomeViewHolder(final View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
            mTextViewTitle = itemView.findViewById(R.id.text_view_title);
            mTextViewSection = itemView.findViewById(R.id.text_view_section);
            mTextViewTime = itemView.findViewById(R.id.text_view_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                    homeAdapter.notifyDataSetChanged();
                }
            });

            final ImageView bookmarkView = (ImageView) itemView.findViewById(R.id.bookmark);

            bookmarkView.setTag(R.drawable.ic_bookmark_border);    //When you change the drawable


            bookmarkView.setOnClickListener(new View.OnClickListener() {
                int positionForBookmark;

                @Override
                public void onClick(View v) {

                    HomeItem currentItemForLongBK = item;

                    String imageUrl = currentItemForLongBK.getImageUrl();
                    String title = currentItemForLongBK.getmTitle();
                    String section = currentItemForLongBK.getmSection();
                    String time = currentItemForLongBK.getmTime();
                    String articleID = currentItemForLongBK.getmArticleId();

                    String data = imageUrl + "@split@" + title + "@split@" + section + "@split@" + time + "@split@" + articleID + "@split@"+currentItemForLongBK.getmwebURL();

                    int idToCheck = (Integer) bookmarkView.getTag();
                    if (idToCheck == R.drawable.ic_bookmark_border) {
                        bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_fill));
                        bookmarkView.setTag(R.drawable.ic_bookmark_fill);

                        editor.putString(articleID, data);
                        editor.commit();
                        Toast.makeText(mContext, '"'+title +'"'+" was added to bookmarks", Toast.LENGTH_SHORT).show();

                    } else {
                        bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
                        bookmarkView.setTag(R.drawable.ic_bookmark_border);
                        Toast.makeText(mContext, '"'+title +'"'+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
                        removeItem(currentItemForLongBK, articleID);
                    }
                }
            });
        }

        public void initializeLongClick() {
            // Long click item view
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final HomeItem currentItemForLongCLick = item;

                    String imageUrl = currentItemForLongCLick.getImageUrl();
                    final String title = currentItemForLongCLick.getmTitle();
                    final String articleIDDiag = currentItemForLongCLick.getmArticleId();
                    final String webURL = currentItemForLongCLick.getmwebURL();


                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setTitle("DialogTitle");

                    TextView text = (TextView) dialog.findViewById(R.id.text);
                    text.setText(title);
                    ImageView imageDialog = (ImageView) dialog.findViewById(R.id.image_dialog);

                    Picasso.with(mContext).load(imageUrl).fit().centerInside().into(imageDialog);

                    final ImageView bookmarkDia = (ImageView) dialog.findViewById(R.id.bookmarkDialog);



                    Map<String, ?> allEntries = sharedPreferences.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                        if(articleIDDiag.compareToIgnoreCase(entry.getKey())==0){

                            bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_fill));
                            currentItemForLongCLick.setBookmarkFlag(true);
                            //bookmarkDia.setTag(R.drawable.ic_bookmark_fill);
                            break;
                        }
                        else{
                            bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
//                            System.out.println("not found");
                            //bookmarkDia.setTag(R.drawable.ic_bookmark_border);
                        }
                    }

                    bookmarkDia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                if(!currentItemForLongCLick.getBookmarkFlag()){
                                    bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_fill));

                                  saveData(currentItemForLongCLick);
                                    notifyDataSetChanged();
                              Toast.makeText(mContext,'"'+title +'"'+" was added to bookmarks", Toast.LENGTH_SHORT).show();

                            } else {
                                bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));


                                removeDataDialog(currentItemForLongCLick);
//                                System.out.println("NOTIFIED");
//                                System.out.println(sharedPreferences.contains(articleIDDiag));
                                    notifyDataSetChanged();
                                    Toast.makeText(mContext,'"'+title +'"'+" was removed from bookmarks", Toast.LENGTH_SHORT).show();

                            }

                    }
                    });

                    ImageView twitterImageView = (ImageView) dialog.findViewById(R.id.twitterLogo);

                    twitterImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(rand1>abs2){
                                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                                        urlEncode("Check out this Link:\n"),
                                        urlEncode(webURL));
                                if(idx1>idx2){
                                    int res = idx1*idx2;
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetUrl));
                                mContext.startActivity(intent);

                            }

                        }
                    });
                    dialog.show();

                    return false;
                }
            });
        }

        public void saveData( HomeItem currentItemForLongBK){
            String imageUrl = currentItemForLongBK.getImageUrl();
            String title = currentItemForLongBK.getmTitle();
            String section = currentItemForLongBK.getmSection();
            String time = currentItemForLongBK.getmTime();
            String articleID = currentItemForLongBK.getmArticleId();

            String data = imageUrl+"@split@"+title+"@split@"+section+"@split@"+time+"@split@"+articleID+"@split@"+currentItemForLongBK.getmwebURL();

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(articleID, data);
            editor.commit();
            currentItemForLongBK.setBookmarkFlag(true);
        }


        public void removeDataDialog(HomeItem currentItemForLongBK){
            String articleID = currentItemForLongBK.getmArticleId();

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(articleID);
            currentItemForLongBK.setBookmarkFlag(false);
            editor.commit();
        }

        public String urlEncode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException("URLEncoder.encode() failed for " + s);
            }
        }


}
}
