package com.ganargatul.moviecatalogue.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.ganargatul.moviecatalogue.database.MovieHelper;
import com.ganargatul.moviecatalogue.database.TvHelper;

import java.util.Objects;

import static com.ganargatul.moviecatalogue.database.DatabaseContract.AUTHORITY;
import static com.ganargatul.moviecatalogue.database.DatabaseContract.MovieColoumn.CONTENT_URI;
import static com.ganargatul.moviecatalogue.database.DatabaseContract.TABLE_MOVIE;
import static com.ganargatul.moviecatalogue.database.DatabaseContract.TABLE_TV;
import static com.ganargatul.moviecatalogue.database.DatabaseContract.TvColoumn.CONTENT_URI_TV;

public class MovieTvProvider extends ContentProvider {
    private static final  int MOVIE = 1;
    private static final  int MOVIE_ID = 2;

    static final int TV = 10;
    static final int TV_ID=11;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private MovieHelper movieHelper;
    TvHelper tvHelper;
    static {
        sUriMatcher.addURI(AUTHORITY,TABLE_MOVIE,MOVIE);
        sUriMatcher.addURI(AUTHORITY,TABLE_MOVIE + "/#",MOVIE_ID);
    }

    static {
        sUriMatcher.addURI(AUTHORITY,TABLE_TV,TV);
        sUriMatcher.addURI(AUTHORITY,TABLE_TV + "/#",TV_ID);
    }
    @Override
    public boolean onCreate() {
        movieHelper = MovieHelper.getINSTANCE(getContext());
        movieHelper.open();
        tvHelper = TvHelper.getINSTANCE(getContext());
        tvHelper.open();
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Cursor query(Uri uri, String[] strings, String s, String[] strings1, String s1) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                cursor = movieHelper.queryProvider();
                break;
            case MOVIE_ID:
                cursor = movieHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
            case TV:
                cursor = tvHelper.queryProvider();
                break;
            case TV_ID:
                cursor=tvHelper.queryByIdProvider(uri.getLastPathSegment());
                break;
                default:
                    cursor = null;
                    break;
        }
        if (cursor!=null){
            cursor.setNotificationUri(Objects.requireNonNull(getContext()).getContentResolver(),uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        long added;
        Uri mUri = null;
        switch (sUriMatcher.match(uri)){
            case MOVIE:
                added = movieHelper.insertProvider(contentValues);
                if (added>0){
                   mUri = ContentUris.withAppendedId(CONTENT_URI,added);
                }
                break;
            case TV:

                added = tvHelper.insertProvider(contentValues);
                if (added>0){
                    mUri = ContentUris.withAppendedId(CONTENT_URI_TV,added);
                }
                default:
                    added=0;
                    break;
        }
        if (added > 0){
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri,null);
        }
        return mUri;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int delete(Uri uri, String s, String[] strings) {

        int deleted ;
        switch (sUriMatcher.match(uri)){
            case MOVIE_ID:

                deleted = movieHelper.deleteProvider(uri.getLastPathSegment());
                break;
            case TV_ID:

                deleted=tvHelper.deleteProvider(uri.getLastPathSegment());
                default:
                    deleted = 0;
                    break;
        }
      if (deleted > 0 ){
          Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri,null);

      }
        return deleted;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        int updated;
        switch (sUriMatcher.match(uri)){
            case MOVIE_ID:
                updated = movieHelper.updateProvider(uri.getLastPathSegment(),contentValues);
                break;
            case TV_ID:
                updated = tvHelper.updateProvider(uri.getLastPathSegment(),contentValues);
            default:
                updated = 0;
                break;
        }
        if (updated>0){
            Objects.requireNonNull(getContext()).getContentResolver().notifyChange(uri,null);

        }

        return updated;
    }
}
