package com.csc.capturetool.myapplication.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by SirdarYangK on 2018/11/2
 * des:
 */
public class FileUtil {
    private static final String TAG = "FileUtil";

    public static final int BUFFER_SIZE = 4 * 1024;

    public static boolean existSDCard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static String getCacheDirPath(Context context) {
        return context.getCacheDir().getAbsolutePath() + "/";
    }

    public static String parseFilename(String url) {
        try {
            String[] tmp = url.split("/");
            String filename = tmp[tmp.length - 1];
            if (isLocalUri(url))
                return filename;
            else
                return URLDecoder.decode(filename, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String replaceExtension(String filename, String extension) {
        StringBuffer sb = new StringBuffer(filename.substring(0, filename.lastIndexOf(".")));
        sb.append(".");
        sb.append(extension);
        return sb.toString();
    }

    public static String parseFileDir(String filepath) {
        int index = filepath.lastIndexOf('/');
        return filepath.substring(0, index + 1);
    }

    public static String getFrontName(String filename) {
        return filename.split("\\.")[0];
    }

    public static String getExtension(String filename) {
        String[] tmp = filename.split("\\.");
        return "." + tmp[tmp.length - 1];
    }

    public static File copyFile(String oldPath, String newPath) throws IOException {
        InputStream inStream = new FileInputStream(oldPath);
        return copyFile(inStream, newPath);
    }

    public static File copyFile(InputStream inStream, String target) throws IOException {
        FileOutputStream fs = new FileOutputStream(target);
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        while ((len = inStream.read(buffer)) != -1) {
            fs.write(buffer, 0, len);
        }
        inStream.close();
        fs.close();
        return new File(target);
    }

    public static void deleteAllFiles(File file) {
        if (file != null && file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (File subfile : files)
                    deleteAllFiles(subfile);
            }
        }
    }

    public static boolean checkFolder(String folder) {
        File file = new File(folder);
        if (!file.exists()) {
            return file.mkdirs();
        } else if (!file.isDirectory()) {
            return false;
        } else {
            return true;
        }
    }

    public static final String LOCAL_URI_PREFIX = "file://";

    public static boolean isLocalUri(String uri) {
        return uri.startsWith(LOCAL_URI_PREFIX) || uri.startsWith("/");
    }

    public static File getFileFromUrl(String url, String target) throws IOException {
        InputStream inputStream = (InputStream) new URL(url).getContent();
        return FileUtil.copyFile(inputStream, target);
    }

    public static String removeLocalPrefix(String uri) {
        return uri.replace(LOCAL_URI_PREFIX, "");
    }

    /**
     * 获取目录或文件大小 <功能详细描述>
     *
     * @param file
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static double getDirSize(File file) {
        // 判断文件是否存在
        if (file.exists()) {
            // 如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                double size = 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {// 如果是文件则直接返回其大小,以“兆”为单位
                double size = (double) file.length() / 1024 / 1024;
                return size;
            }
        } else {
            return 0.0;
        }
    }

    public static void saveFile(InputStream in, File file) {
        try {
            FileOutputStream out = new FileOutputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean saveFile(String path, String data) {
        if (TextUtils.isEmpty(path) || data == null) {
            return false;
        }
        File f = new File(path);
        if (f.exists() && !f.delete()) {
            return false;
        }

        try {
            if (!f.createNewFile()) {
                return false;
            }
        } catch (Exception e) {
        }

        boolean ok = false;
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(f);
            fos.write(data.getBytes("UTF-8"));
            fos.flush();
            ok = true;
        } catch (Exception e) {

        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (Exception e) {
                // ignore;
            }
        }
        return ok;
    }

    /**
     * file->String
     *
     * @param file
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static String getStringFromFile(File file) {
        if (file == null || !file.exists() || !file.canRead()) {
            return null;
        }

        FileInputStream fis = null;
        BufferedInputStream bufferedInput = null;
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        try {
            fis = new FileInputStream(file);

            byte[] buffer = new byte[512];

            bufferedInput = new BufferedInputStream(fis);

            int bytesRead = 0;

            while ((bytesRead = bufferedInput.read(buffer)) != -1) {

                byteArrayInputStream.write(buffer, 0, bytesRead);
            }
            String content = byteArrayInputStream.toString();
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            byteArrayInputStream = null;
            if (fis != null) {
                try {
                    fis.close();
                    if (bufferedInput != null) {
                        bufferedInput.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 图片保存目录
     *
     * @param context
     * @return
     */
    public static String getImageDir(Context context) {
        String imageDir = "";
        if (existSDCard()) {
            imageDir = Environment.getExternalStorageDirectory() + "/hachi/image";
        } else {
            imageDir = context.getCacheDir() + "/hachi/image";
        }
        File file = new File(imageDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return imageDir;
    }

    /**
     * 文件下载目录
     *
     * @param context
     * @return
     */
    public static String getDownloadDir(Context context) {
        String dir = "";
        if (existSDCard()) {
            dir = Environment.getExternalStorageDirectory() + "/hachi/download";
        } else {
            dir = context.getCacheDir() + "/hachi/download";
        }
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public static File getCacheDirectory(Context context) {
        File appCacheDir = null;
        if (Environment.getExternalStorageState().equals("mounted")) {
            appCacheDir = getExternalCacheDir(context);
        }

        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }

        return appCacheDir;
    }

    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir = new File(new File(dataDir, context.getPackageName()), "cache");
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }

            try {
                (new File(appCacheDir, ".nomedia")).createNewFile();
            } catch (IOException var4) {
            }
        }

        return appCacheDir;
    }

    public static String getPicPathByUri(Context context, Uri uri) {
        int sdkVersion = Build.VERSION.SDK_INT;
        if (sdkVersion >= 19) { // api >= 19
            return getRealPathFromUriAboveApi19(context, uri);
        } else { // api < 19
            return getRealPathFromUriBelowAPI19(context, uri);
        }
    }

    private static String getRealPathFromUriBelowAPI19(Context context, Uri uri) {
        return getDataColumn(context, uri, null, null);
    }

    @SuppressLint("NewApi")
    private static String getRealPathFromUriAboveApi19(Context context, Uri uri) {
        String filePath = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // 如果是document类型的 uri, 则通过document id来进行处理
            String documentId = DocumentsContract.getDocumentId(uri);
            if (isMediaDocument(uri)) { // MediaProvider
                // 使用':'分割
                String id = documentId.split(":")[1];

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = {id};
                filePath = getDataColumn(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, selectionArgs);
            } else if (isDownloadsDocument(uri)) { // DownloadsProvider
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(documentId));
                filePath = getDataColumn(context, contentUri, null, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是 content 类型的 Uri
            filePath = getDataColumn(context, uri, null, null);
        } else if ("file".equals(uri.getScheme())) {
            // 如果是 file 类型的 Uri,直接获取图片对应的路径
            filePath = uri.getPath();
        }
        return filePath;
    }

    private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        String path = null;

        String[] projection = new String[]{MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(projection[0]);
                path = cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            if (cursor != null) {
                cursor.close();
            }
        }
        return path;
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

}
