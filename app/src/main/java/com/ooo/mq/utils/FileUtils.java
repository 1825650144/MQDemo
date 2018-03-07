package com.ooo.mq.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.ooo.mq.base.BaseApplication;
import com.ooo.mq.base.BaseConfig;
import com.socks.library.KLog;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;



/**
 * 文件操作工具类
 * dongdd on 2017/8/27 21:57
 */

public class FileUtils {

    public static final String ICON_DIR = "icon";
    public static final String APP_STORAGE_ROOT = "MsgDemo";

    /**
     * 获取默认的文件路径 SD卡优先
     *
     * @return
     */
    public static String getFileDefaultPath() {
        String filePath;
        // 判断是否存在SD卡
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // SD卡根目录的
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {// 系统下载缓存根目录的
            filePath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        }
        KLog.d(BaseConfig.LOG, "文件默认路径：" + filePath);
        return filePath;
    }

    /**
     * 获取默认的文件路径 SD卡优先
     *
     * @return
     */
    public static String getFileDefaultPath(String fileName) {
        String filePath;
        // 判断是否存在SD卡
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // SD卡根目录的
        if (hasSDCard) {
            filePath = Environment.getExternalStorageDirectory().toString() + File.separator + fileName;
        } else {// 系统下载缓存根目录的
            filePath = Environment.getDownloadCacheDirectory().toString() + File.separator + fileName;
        }
        KLog.d(BaseConfig.LOG, "文件默认路径：" + filePath);
        return filePath;
    }


    /**
     * 将字符数据存储到手机中
     *
     * @param dataStr
     * @param fileName
     */
    public static String saveString(String dataStr, String fileName) {
        // 获取默认的文件路径
        String filePath = getFileDefaultPath(fileName);

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream outStream = new FileOutputStream(file);
            outStream.write(dataStr.getBytes());
            outStream.close();
            return filePath;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 将指定的文件流写入指定的文件中
     *
     * @param in
     * @param file
     */
    public static void writeFile(InputStream in, File file) throws IOException {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (file != null && file.exists()) {
            file.delete();
        }

        FileOutputStream out = new FileOutputStream(file);
        byte[] buffer = new byte[1024 * 128];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }
        out.flush();
        out.close();
        in.close();
    }

    /**
     * 将图片存储到本地并在相冊中显示
     *
     * @param context
     * @param bmp     imageView转bitmap : ((BitmapDrawable) ivCode.getDrawable()).getBitmap()
     */
    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
//        File appDir = new File(Environment.getExternalStorageDirectory(), "si");
        File appDir = new File(getAppExternalStoragePath());

        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".png";
        File file = new File(appDir, fileName);
        KLog.d(BaseConfig.LOG, "path:" + file.getPath());
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
        Toast.makeText(context, "图片保存在：" + file.getPath(), Toast.LENGTH_SHORT).show();
    }

    /**
     * 打开指定路径的文件
     *
     * @param activity
     * @param fileName
     * @return
     */
    public static int openFile(Activity activity, String fileName) {
        return openFile(activity, fileName, 0);
    }

    /**
     * @param activity
     * @param fileName 文件名称/文件路径
     * @param type     0：文件名称；1：文件路径
     * @return
     */
    public static int openFile(Activity activity, String fileName, int type) {
        try {
            String filePath = fileName;
            if (type == 0) {
                filePath = getFileDefaultPath(fileName);
            }
            File file = new File(filePath);
            // 判断文件是否存在
//        if (!file.exists()) {
//            return 0;
//        }
            // 在MIME和文件类型的匹配表中找到对应的MIME类型
            String fileType = filePath.substring(filePath.lastIndexOf("."), filePath.length()).toLowerCase();
            String fileMIME = "*/*";
            boolean isValidFileType = false;
            for (int i = 0; i < FileUtils.MIME_MapTable.length; i++) {
                if (fileType.equals(FileUtils.MIME_MapTable[i][0])) {
                    fileMIME = FileUtils.MIME_MapTable[i][1];
                    isValidFileType = true;
                    break;
                }
            }
            if (!isValidFileType) {
                return 1;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Uri uri;
            if (Build.VERSION.SDK_INT >= 24) {
                uri = FileProvider.getUriForFile(activity, BaseConfig.AUTHORITIES, file);
            } else {
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, fileMIME);
            activity.startActivity(Intent.createChooser(intent, ""));
            return 2;
        } catch (Exception e) {
            KLog.d(BaseConfig.LOG, "打开文件error：" + e.getMessage());
            return -1;
        }
    }


    /**
     * 将file转换为String
     *
     * @param filePath
     * @return
     */
    public static String fileToString(String filePath) {
        String result = "";
        try {
            File file = new File(filePath);
            // 判断文件是否存在
            if (!file.exists()) {
                return result;
            }
            InputStream is = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuffer buffer = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
            result = buffer.toString();
            is.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            KLog.d(BaseConfig.LOG, "file转换String异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 将file转换为String
     *
     * @param fileString
     * @return
     */
    public static boolean stringToFile(String filePath, String fileString) {
        boolean result = true;
        try {
            // 判断文件字符串是否为空
            if (fileString.length() == 0) {
                return result;
            }
            ByteArrayInputStream ins = new ByteArrayInputStream(fileString.getBytes());
            File file = new File(filePath);
            OutputStream os = new FileOutputStream(file);
            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
            KLog.d(BaseConfig.LOG, "String转换File异常：" + e.getMessage());
        }
        return result;
    }

    /**
     * 通过文件uri获取到url文件路径。4.4之前
     * 打开文件选择器后，通过onActivityForResult返回的uri获取选中文件地址
     *
     * @return 返回的文件路径。
     */
    public static String getFilePath(Context context, Uri uri) {
        String filename = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            filename = getPathByUri4kitkat(context, uri);
        } else {
            if ("content".equalsIgnoreCase(uri.getScheme())) {
                Cursor cursor = null;
                try {
                    //处理有些手机从文件管理器中获取文件失败问题。
                    cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        filename = cursor.getString(column_index);
                    }
                } catch (Exception e) {

                }
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                filename = uri.toString().replace("file://", "");// 替换file://
                if (!filename.startsWith("/mnt")) {// 加上"/mnt"头
                    filename += "/mnt";
                }
            }
        }
        return filename;
    }

    /**
     * Android4.4以上从uri获取到文件路径
     *
     * @param context
     * @param uri     文件URI
     */
    @SuppressLint("NewApi")
    public static String getPathByUri4kitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File
            return uri.getPath();
        }
        return null;
    }

    /**
     * 根据uri获取到数据列的值
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * 判断是否为外部存储文件
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * 判断是否为正在下载的文件
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * 判断是否是媒体文件
     *
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * 产生图片的路径，这里是在缓存目录下
     */
    public static String generateImgePathInStoragePath() {
        return getDir(ICON_DIR) + String.valueOf(System.currentTimeMillis()) + ".jpg";
    }

    /**
     * 已登陆账号为标识存储图像
     *
     * @param phoneNo
     * @return
     */
    public static String getImgPathInStoragePath(String phoneNo) {
        return getDir(ICON_DIR) + phoneNo + ".jpg";
    }


    /**
     * 获取应用目录，当SD卡存在时，获取SD卡上的目录，当SD卡不存在时，获取应用的cache目录
     */
    public static String getDir(String name) {
        StringBuilder sb = new StringBuilder();
        if (isSDCardAvailable()) {
            sb.append(getAppExternalStoragePath());
        } else {
            sb.append(getFilesPath());
        }
        sb.append(name);
        sb.append(File.separator);
        String path = sb.toString();
        if (createDirs(path)) {
            return path;
        } else {
            return null;
        }
    }

    /**
     * 判断SD卡是否挂载
     */
    public static boolean isSDCardAvailable() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 获取SD下当前APP的目录
     */
    public static String getAppExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        sb.append(APP_STORAGE_ROOT);
        sb.append(File.separator);
        return sb.toString();
    }

    /**
     * 获取应用的cache目录
     */
    public static String getCachePath() {
        File f = BaseApplication.context().getCacheDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 获取应用的files目录
     */
    public static String getFilesPath() {
        File f = BaseApplication.context().getFilesDir();
        if (null == f) {
            return null;
        } else {
            return f.getAbsolutePath() + "/";
        }
    }

    /**
     * 创建文件夹
     */
    public static boolean createDirs(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists() || !file.isDirectory()) {
            return file.mkdirs();
        }
        return true;
    }


    /**
     * 按质量压缩bm
     *
     * @param bm
     * @param quality 压缩率
     * @return
     */
    public static String saveBitmapByQuality(Bitmap bm, int quality) {
        String croppath = "";
        try {
            File f = new File(FileUtils.generateImgePathInStoragePath());
            //得到相机图片存到本地的图片
            croppath = f.getPath();
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.JPEG, quality, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return croppath;
    }

    public static Bitmap decodeUriAsBitmap(Uri uri) {
        Bitmap bitmap = null;
        try {
            // 先通过getContentResolver方法获得一个ContentResolver实例，
            // 调用openInputStream(Uri)方法获得uri关联的数据流stream
            // 把上一步获得的数据流解析成为bitmap
            bitmap = BitmapFactory.decodeStream(BaseApplication.context().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }


    /**
     * 常见文件类型对应MIME类型
     */
    public final static String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };


}
