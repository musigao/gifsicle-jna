package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 使用 GifsicleUtil 工具类的简化示例
 */
public class SimpleExample {

    public static void main(String[] args) {
        try {
            // 初始化库
            GifsicleUtil.init();
            
            // 准备测试文件
            File inputFile = prepareTestFile();
            
            // 使用简化的 API
            System.out.println("🚀 开始处理 GIF 文件...");
            
            // 方式1：单独操作
            boolean success1 = GifsicleUtil.optimize(inputFile.getAbsolutePath(), "optimized.gif", 60);
            System.out.println("优化: " + (success1 ? "✅ 成功" : "❌ 失败"));
            
            boolean success2 = GifsicleUtil.resize("optimized.gif", "resized.gif", 120, 120);
            System.out.println("调整尺寸: " + (success2 ? "✅ 成功" : "❌ 失败"));
            
            boolean success3 = GifsicleUtil.crop("resized.gif", "cropped.gif", "10,10+80x80");
            System.out.println("裁剪: " + (success3 ? "✅ 成功" : "❌ 失败"));
            
            // 方式2：组合操作
            boolean success4 = GifsicleUtil.optimizeAndResize(inputFile.getAbsolutePath(), "final.gif", 80, 100, 100);
            System.out.println("优化+调整尺寸: " + (success4 ? "✅ 成功" : "❌ 失败"));
            
            System.out.println("\n🎉 处理完成！生成的文件:");
            System.out.println("  - optimized.gif");
            System.out.println("  - resized.gif"); 
            System.out.println("  - cropped.gif");
            System.out.println("  - final.gif");
            
            // 显示文件大小
            printFileSizes(inputFile);
            
        } catch (Exception e) {
            System.err.println("❌ 错误: " + e.getMessage());
            e.printStackTrace();
        }
        // 注意：不需要手动调用 cleanup()，因为有 shutdown hook
    }
    
    private static File prepareTestFile() throws Exception {
        File inputFile = new File("input.gif");
        if (!inputFile.exists()) {
            try (InputStream in = SimpleExample.class.getResourceAsStream("/logo.gif");
                 OutputStream out = new FileOutputStream(inputFile)) {
                if (in == null) {
                    throw new IllegalStateException("找不到测试文件 logo.gif");
                }
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        }
        return inputFile;
    }
    
    private static void printFileSizes(File original) {
        System.out.println("\n📊 文件大小对比:");
        printFileInfo("原始文件", original);
        printFileInfo("优化后", new File("optimized.gif"));
        printFileInfo("调整尺寸", new File("resized.gif"));
        printFileInfo("裁剪后", new File("cropped.gif"));
        printFileInfo("最终文件", new File("final.gif"));
    }
    
    private static void printFileInfo(String name, File file) {
        if (file.exists()) {
            System.out.printf("  %s: %,d bytes%n", name, file.length());
        }
    }
}
