package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 便于使用的 Gifsicle 工具类，提供了更简单的 API 和自动资源管理
 */
public class GifsicleUtil {
    
    private static boolean initialized = false;
    
    /**
     * 初始化 Gifsicle 库（自动加载动态库）
     */
    public static synchronized void init() throws Exception {
        if (!initialized) {
            loadNativeLibrary();
            Gifsicle.INSTANCE.gifsicle_init();
            initialized = true;
            
            // 添加 JVM 关闭钩子确保清理
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (initialized) {
                    Gifsicle.INSTANCE.gifsicle_cleanup();
                    initialized = false;
                }
            }));
        }
    }
    
    /**
     * 手动清理资源
     */
    public static synchronized void cleanup() {
        if (initialized) {
            Gifsicle.INSTANCE.gifsicle_cleanup();
            initialized = false;
        }
    }
    
    /**
     * 优化 GIF 文件
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param level 优化等级 (1-200)
     * @return 成功返回 true
     */
    public static boolean optimize(String inputPath, String outputPath, int level) {
        ensureInitialized();
        return Gifsicle.INSTANCE.gifsicle_optimize(inputPath, outputPath, level) == 0;
    }
    
    /**
     * 调整 GIF 尺寸
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param width 新宽度
     * @param height 新高度
     * @return 成功返回 true
     */
    public static boolean resize(String inputPath, String outputPath, int width, int height) {
        ensureInitialized();
        return Gifsicle.INSTANCE.gifsicle_resize(inputPath, outputPath, width, height) == 0;
    }
    
    /**
     * 裁剪 GIF
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     * @param cropSpec 裁剪规格，格式如 "x,y+widthxheight"
     * @return 成功返回 true
     */
    public static boolean crop(String inputPath, String outputPath, String cropSpec) {
        ensureInitialized();
        return Gifsicle.INSTANCE.gifsicle_crop(inputPath, outputPath, cropSpec) == 0;
    }
    
    /**
     * 连续处理：优化 + 调整尺寸
     */
    public static boolean optimizeAndResize(String inputPath, String outputPath, int level, int width, int height) {
        String tempPath = outputPath + ".temp";
        try {
            if (!optimize(inputPath, tempPath, level)) {
                return false;
            }
            if (!resize(tempPath, outputPath, width, height)) {
                return false;
            }
            new File(tempPath).delete(); // 清理临时文件
            return true;
        } catch (Exception e) {
            new File(tempPath).delete(); // 清理临时文件
            return false;
        }
    }
    
    private static void ensureInitialized() {
        if (!initialized) {
            throw new IllegalStateException("GifsicleUtil not initialized. Call init() first.");
        }
    }
    
    private static void loadNativeLibrary() throws Exception {
        String libName = System.mapLibraryName("gifsicle");
        String platform = System.getProperty("os.name").toLowerCase();
        String extension;
        
        if (platform.contains("win")) {
            extension = ".dll";
        } else if (platform.contains("mac")) {
            extension = ".dylib";
        } else {
            extension = ".so";
        }
        
        File tempFile = File.createTempFile("libgifsicle", extension);
        tempFile.deleteOnExit();

        try (InputStream in = GifsicleUtil.class.getResourceAsStream("/" + libName);
             OutputStream out = new FileOutputStream(tempFile)) {

            if (in == null) {
                // 开发环境回退
                File libFile = new File("src/.libs/" + libName);
                if (libFile.exists()) {
                    System.setProperty("jna.library.path", libFile.getParent());
                    System.out.println("Using development library: " + libFile.getAbsolutePath());
                    return;
                }
                throw new IllegalStateException("Native library " + libName + " not found. Expected extension: " + extension);
            }

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        
        System.setProperty("jna.library.path", tempFile.getParent());
        System.out.println("Loaded native library: " + tempFile.getAbsolutePath());
    }
}
