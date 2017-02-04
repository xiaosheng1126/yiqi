package com.hong.bo.shi.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipFileUtils
{
    public static boolean unzip(File srcFile, File dstDir)
    {
        try {
            ZipFile zipFile = new ZipFile(srcFile, ZipFile.OPEN_READ);
            Enumeration<ZipEntry> entries = (Enumeration<ZipEntry> )zipFile.entries();
            while (entries.hasMoreElements())
            {
                ZipEntry entry = entries.nextElement();
                extractEntity(entry, zipFile, dstDir);
            }
        }
        catch (IOException e)
        {
            return false;
        }
        return true;
    }

    private static void extractEntity(ZipEntry entry, ZipFile zipFile, File dstDir) throws IOException
    {
        File file = new File(dstDir, entry.getName());
        if(entry.isDirectory()) {
            file.mkdirs();
        }
        else {
            InputStream inputStream=zipFile.getInputStream(entry);
            FileUtils.copyFile(inputStream, file);
        }
    }
}
