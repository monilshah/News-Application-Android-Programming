package com.example.newsapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.Map;


public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.HomeViewHolder> implements View.OnLongClickListener{

    private Context mContext;
    private  BookmarkAdapter bookmarkAdapter;
    private ArrayList<HomeItem> mHomeList;
    private OnItemClickListener mListener;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String TEXT = "text";
    public int x=10;
    public int y = 5;


    // private View.OnLongClickListener onLongClickListener;


    // left to implement onCLick long press.
    @Override
    public boolean onLongClick(View v) {

        return false;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void removeItem(HomeItem hm, String key) {
       mHomeList.remove(hm);
       checkSize();
        notifyDataSetChanged();


        SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();

    }

    public BookmarkAdapter(Context context, ArrayList<HomeItem> homeList) {
        mContext = context;
        bookmarkAdapter = this;
        mHomeList = homeList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.bookmark_items, parent, false);

        return new HomeViewHolder(v);
    }

    public void swap(ArrayList<HomeItem> items){
        mHomeList = items;
        notifyDataSetChanged();
    }

    public void refresh(){
        ((BookmarkFragment) mListener).loadData();
        swap(((BookmarkFragment) mListener).mHomeList);
       checkSize();
    }

    public void checkSize(){
        if(mHomeList.isEmpty()) {
            TextView textView = ((BookmarkFragment) mListener).view.findViewById(R.id.textBookmark);
            textView.setText("No Bookmarked Articles");
        }
    }


    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        View v = holder.itemView;
        HomeItem currentItem = mHomeList.get(position);
        ImageView bookmarkView = (ImageView) v.findViewById(R.id.bookmark);
        holder.item = currentItem;

        String imageUrl = currentItem.getImageUrl();
        String title = currentItem.getmTitle();
        String section = currentItem.getmSection();
        String time = currentItem.getmTime();
        String articleID = currentItem.getmArticleId();

        SharedPreferences sharedPreferences = mContext.getSharedPreferences("",Context.MODE_PRIVATE);

        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            //Log.d("map values", entry.getKey() + ": ");
            if(articleID.compareToIgnoreCase(entry.getKey())==0){
                //Log.i("Found", articleID);
                bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_fill));
                break;
            }
            else{
                bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
            }
        }

        holder.mTextViewTitle.setText(title);


        String newDate = time.substring(0,10);
        String[] dateContents = newDate.split("-");
        StringBuffer sb = new StringBuffer();
        sb.append(dateContents[2]);
        switch (Integer.parseInt(dateContents[1])){
            case 1: sb.append(" Jan");break;
            case 2: sb.append(" Feb");break;
            case 3: sb.append(" Mar");break;
            case 4: sb.append(" Apr");break;
            case 6: sb.append(" Jun");break;
            case 7: sb.append(" Jul");break;
            case 8: sb.append(" Aug");break;
            case 9: sb.append(" Sept");break;
            case 10: sb.append(" Oct");break;
            case 11: sb.append(" Nov");break;
            case 12: sb.append(" Dec");break;
            default:sb.append(" May");
        }

        holder.mTextViewTime.setText(sb.toString());
        holder.mTextViewSection.setText(section);
        Picasso.with(mContext).load(imageUrl).fit().centerInside().into(holder.mImageView);
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

        public HomeViewHolder(View itemView) {
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
                    bookmarkAdapter.notifyDataSetChanged();
                }
            });



            final ImageView bookmarkView = (ImageView) itemView.findViewById(R.id.bookmark);

            bookmarkView.setTag(R.drawable.ic_bookmark_border);    //When you change the drawable
            final int drawableId = (Integer)bookmarkView.getTag();

            bookmarkView.setOnClickListener(new View.OnClickListener() {
                int positionForBookmark;
                @Override
                public void onClick(View v) {

                    positionForBookmark = getAdapterPosition();

                    HomeItem currentItemForLongBK = mHomeList.get(positionForBookmark);
                    String title = currentItemForLongBK.getmTitle();

                    String articleID = currentItemForLongBK.getmArticleId();

                        bookmarkView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
                    Toast.makeText(mContext,'"'+ title+'"'+" was removed from bookmarks", Toast.LENGTH_SHORT).show();
                        removeItem(currentItemForLongBK, articleID);
                }
            });

        }

        public void initializeLongClick() {


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
//                    dialog.setTitle("DialogTitle");


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

                            break;
                        }
                        else{
                            bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));
                        }
                    }

                    bookmarkDia.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                bookmarkDia.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bookmark_border));


                                removeDataDialog(currentItemForLongCLick);
                                dialog.dismiss();
                                refresh();
//                                System.out.println("NOTIFIED");
                                //System.out.println(sharedPreferences.contains(articleIDDiag));
//                                notifyDataSetChanged();
                                Toast.makeText(mContext,'"'+title +'"'+" was removed from bookmarks", Toast.LENGTH_SHORT).show();



                        }
                    });

                    ImageView twitterImageView = (ImageView) dialog.findViewById(R.id.twitterLogo);

                    twitterImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(x>y){
                                String tweetUrl = String.format("https://twitter.com/intent/tweet?text=%s&url=%s&hashtags=CSCI571NewsSearch",
                                        urlEncode("Check out this Link:\n"),
                                        urlEncode(webURL));
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


        public String urlEncode(String s) {
            try {
                return URLEncoder.encode(s, "UTF-8");
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException("URLEncoder.encode() failed for " + s);
            }
        }

        public void removeDataDialog(HomeItem currentItemForLongBK){

            mHomeList.remove(currentItemForLongBK);
            notifyDataSetChanged();

            String articleID = currentItemForLongBK.getmArticleId();

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(articleID);
            currentItemForLongBK.setBookmarkFlag(false);
            editor.commit();
        }

    }
}
