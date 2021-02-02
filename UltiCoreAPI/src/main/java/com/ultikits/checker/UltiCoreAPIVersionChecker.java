package com.ultikits.checker;

import com.ultikits.main.UltiCore;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class UltiCoreAPIVersionChecker {

    public static boolean isOutDate = false;
    public static String version;
    public static String current_version;


    public static void runTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    String url = "https://wisdommen.github.io";
                    if (UltiCore.language.equals("zh")) {
                        url = "https://download.ultikits.com/index.markdown";
                    }
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setDoInput(true);
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                    //获取输入流
                    InputStream input = connection.getInputStream();
                    //将字节输入流转换为字符输入流
                    InputStreamReader streamReader = new InputStreamReader(input, StandardCharsets.UTF_8);
                    //为字符输入流添加缓冲
                    BufferedReader br = new BufferedReader(streamReader);
                    // 读取返回结果
                    String data = br.readLine();
                    while (data != null) {
                        //获取带有附件id的文本
                        if (data.contains("UltiCore")) {
                            String target = br.readLine();
                            //获取版本
                            version = target.split("version: ")[1].split("<")[0];
                            current_version = UltiCore.getInstance().getDescription().getVersion();
                            int currentVersion = getVersion(current_version);
                            int onlineVersion = getVersion(version);
                            if (currentVersion < onlineVersion) {
                                isOutDate = true;
                                UltiCore.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED +"[UltiCore]"+ String.format(UltiCore.languageUtils.getString("join_send_update_reminding"), version, current_version));
                            }
                            break;
                        }
                        data = br.readLine();
                    }
                    // 释放资源
                    br.close();
                    streamReader.close();
                    input.close();
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskAsynchronously(UltiCore.getInstance());
    }

    private static int getVersion(String version) {
        while (version.contains(".")) {
            version = version.replace(".", "");
        }
        return Integer.parseInt(version);
    }

    public static void downloadNewVersion() {
        new BukkitRunnable() {

            @Override
            public void run() {
                UltiCore.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[UltiCore] " + UltiCore.languageUtils.getString("downloading"));
                if (startDownload()) {
                    UltiCore.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[UltiCore] " + UltiCore.languageUtils.getString("download_successfully"));
                    this.cancel();
                    return;
                }
                UltiCore.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.GREEN + String.format("[UltiCore] " + UltiCore.languageUtils.getString("download_failed"), version));
            }

        }.runTaskAsynchronously(UltiCore.getInstance());
    }

    private static boolean startDownload() {
        if (!downloadFromGitHub()) {
            return downloadFromOwnServer();
        }
        return true;
    }

    private static boolean downloadFromOwnServer() {
        try {
            String urlString = "https://download.ultikits.com/collections/UltiCore/UltiCoreAPI-" + version + ".jar";
            return download(urlString, "UltiCoreAPI-" + version + ".jar");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean downloadFromGitHub() {
        try {
            String urlString = "https://raw.githubusercontent.com/wisdommen/wisdommen.github.io/master/collections/UltiCore/UltiCoreAPI-" + version + ".jar";
            return download(urlString, "UltiCoreAPI-" + version + ".jar");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean download(String urlString, String outputName) throws IOException {
        URL url = new URL(urlString);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(UltiCore.getInstance().getDataFolder().getPath().replace(File.separator + "UltiCore", "") + File.separator + outputName);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        return true;
    }

    public static void deleteOldVersion() {
        List<File> files = getFiles(UltiCore.getInstance().getDataFolder().getPath().replace(File.separator + "UltiCore", ""));
        for (File file : files) {
            if (file.getName().contains("UltiCoreAPI-") && !file.getName().equals("UltiCoreAPI-" + version + ".jar")) {
                file.delete();
            }
        }
    }

    private static @Nullable List<File> getFiles(String path) {
        File folder = new File(path);
        if (folder.listFiles() != null) {
            return Arrays.asList(Objects.requireNonNull(folder.listFiles()));
        }
        return null;
    }
}
