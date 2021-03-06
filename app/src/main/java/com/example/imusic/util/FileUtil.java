package com.example.imusic.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import com.example.imusic.MusicApplication;
import com.example.imusic.model.MusicBean;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


/**
 * 描述：${文件操作工具类}
 * 邮箱：strangermy@outlook.com
 *
 * @author Luoshipeng
 */

public class FileUtil {
    private static final String TAG = "====" + FileUtil.class.getSimpleName() + "    ";

    public static boolean getFavoriteFile() {
        return CheckBuildVersionUtil.checkAndroidVersionQ() ? FileUtil.isAndroidQFileExists(Constants.FAVORITE_FILE) : new File(Constants.FAVORITE_FILE).exists();

    }

    public static File getLyricsFile(String songName, String songArtisa) {

        File file = new File(Constants.MUSIC_LYRICS_ROOT);
        if (!file.exists()) {
            file.mkdirs();
        }
        File lyricFile = new File(file + "/", songName + "$$" + songArtisa + ".lrc");
        if (!lyricFile.exists()) {
            try {
                lyricFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtil.d(TAG, " ==========  下载歌词啦   ");
        return lyricFile;
    }


    public static long getId(String str) {
        //        String str="2017-06-12T10:22:59.890Z";
        return Long.parseLong(str.substring(11, 19)
                .replaceAll(":", ""));
    }

    /**
     * 歌曲是否从qq音乐下载过专辑图片
     *
     * @param imageType 1 歌曲图片 、2 歌手图片 、3 专辑图片 、4 通知栏图片
     * @param songName  s
     * @param artist    a
     * @return b
     */
    private static boolean albumFileExists(int imageType, String songName, String artist) {

        String albumPath = imageType == 1
                ? Constants.MUSIC_SONG_ALBUM_ROOT + songName + ".jpg" : imageType == 2
                ? Constants.MUSIC_ARITIST_IMG_ROOT + artist + ".jpg" : Constants.MUSIC_ALBUM_ROOT + artist + ".jpg";
        File file = new File(albumPath);
        return file.exists();
    }

    public static String getAlbumUrl(MusicBean bean, int imageType) {
        boolean b = FileUtil.albumFileExists(imageType, bean.getTitle(), bean.getArtist());
        return b ? StringUtil.getDownAlbum(bean.getTitle(), bean.getArtist()) : StringUtil.getAlbum(2, bean.getAlbumId(), bean.getArtist());
    }

    public static String getNotifyAlbumUrl(Context context, MusicBean bean) {
        boolean b = FileUtil.albumFileExists(1, bean.getTitle(), bean.getArtist());
        return b ? StringUtil.getDownAlbum(bean.getTitle(), bean.getArtist()) : StringUtil.getAlbumArtPath(context, String.valueOf(bean.getAlbumId()));
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID},
                MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public static boolean hasSdcard() {
        String state = Environment.getExternalStorageState();
        // 有存储的SDCard
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static File getHeaderFile() {
        File file = new File(Constants.HEADER_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        return new File(file, Constants.CROP_IMAGE_FILE_NAME);
    }

    public static Uri getPicUri(Context context, String savePath) {
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File pictureFile = new File(savePath, Constants.IMAGE_FILE_NAME);
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? FileProvider.getUriForFile(context, context.getPackageName(), pictureFile) : Uri.fromFile(pictureFile);
    }

    public static void deleteFileDirectory(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFile(file);
                } else {
                    file.delete();
                }
            }
            dir.delete();
        }
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

    public static File createFile(Context context, String fileName, String dirPath) {
        String apkFilePath = context.getExternalFilesDir(dirPath).getAbsolutePath();

        return new File(apkFilePath + File.separator + fileName);
    }

    public static boolean isAndroidQFileExists(String path) {
        AssetFileDescriptor afd = null;
        ContentResolver cr = MusicApplication.getIntstance().getContentResolver();
        try {
            Uri uri = Uri.parse(path);
            afd = cr.openAssetFileDescriptor(uri, "r");
            if (afd == null) {
                return false;
            } else {
                afd.close();
            }
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


}