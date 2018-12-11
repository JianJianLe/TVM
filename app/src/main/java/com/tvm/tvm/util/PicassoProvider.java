package com.tvm.tvm.util;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

public final class PicassoProvider extends ContentProvider {

  @SuppressLint("StaticFieldLeak") static Context context;

  @Override public boolean onCreate() {
    context = getContext();
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@Nullable Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    return null;
  }

  @Nullable
  @Override
  public String getType(@Nullable Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@Nullable Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@Nullable Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@Nullable Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }


}