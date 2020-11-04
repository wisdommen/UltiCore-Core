package com.ultikits.utils;

import com.ultikits.main.UltiCoreAPI;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

public class LanguageUtils {

    private String language;
    private String filePath;
    private Properties properties;

    public LanguageUtils(String languageFilePath, String language) {
        ResourceFile resourceFile = null;
        this.filePath = languageFilePath;
        this.language = language;
        try {
            resourceFile = new ResourceFile(languageFilePath, language);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.properties = resourceFile.getProperties();
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public String getWords(String key) {
        return properties.getProperty(key);
    }
}

class ResourceFile {

    String fileName;
    String filePath;
    Properties properties;

    public ResourceFile(String filePath, String fileName) throws FileNotFoundException {
        this.fileName = fileName;
        this.filePath = filePath + File.separator + "lang";
        try {
            copyAllLangFiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Properties p = new Properties();
        FileInputStream inputStream = new FileInputStream(new File(filePath + File.separator + fileName + ".properties"));
        BufferedReader inStream = new BufferedReader(new InputStreamReader(inputStream));
        try {
            p.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.properties = p;
    }

    public String getString(String key) {
        return properties.getProperty(key);
    }

    public Properties getProperties() {
        return properties;
    }

    private void copyAllLangFiles() throws IOException {
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        saveLangFiles();
    }

    public InputStream getResource(@NotNull String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }

    public void saveLangFiles() {
        saveResource(fileName + ".properties", true);
    }

    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + fileName);
        }

        File outFile = new File(filePath, resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(filePath, resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                System.out.println("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            System.out.println("Could not save " + outFile.getName() + " to " + outFile);
        }
    }
}
