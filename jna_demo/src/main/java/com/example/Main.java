package com.example;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class Main {

    public static void main(String[] args) {
        try {
            loadLibraryFromJar();

            File inputFile = new File("input.gif");
            if (!inputFile.exists()) {
                try (InputStream in = Main.class.getResourceAsStream("/logo.gif");
                     OutputStream out = new FileOutputStream(inputFile)) {
                    if (in == null) {
                        System.err.println("Could not find logo.gif in resources.");
                        return;
                    }
                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                }
            }

            System.out.println("Initializing Gifsicle...");
            Gifsicle.INSTANCE.gifsicle_init();

            try {
                String optimizedFile = "optimized.gif";
                System.out.println("Optimizing GIF to " + optimizedFile + " with level 80...");
                int optimizeResult = Gifsicle.INSTANCE.gifsicle_optimize(inputFile.getAbsolutePath(), optimizedFile, 80);
                if (optimizeResult == 0) {
                    System.out.println("✅ Optimization successful.");
                    printFileSize(inputFile, new File(optimizedFile));
                } else {
                    System.err.println("❌ Optimization failed with code: " + optimizeResult);
                }

                String resizedFile = "resized.gif";
                System.out.println("\nResizing GIF to " + resizedFile + " (100x100)...");
                int resizeResult = Gifsicle.INSTANCE.gifsicle_resize(optimizedFile, resizedFile, 100, 100);
                if (resizeResult == 0) {
                    System.out.println("✅ Resize successful.");
                } else {
                    System.err.println("❌ Resize failed with code: " + resizeResult);
                }

                String croppedFile = "cropped.gif";
                System.out.println("\nCropping GIF to " + croppedFile + " (10,10+50x50)...");
                int cropResult = Gifsicle.INSTANCE.gifsicle_crop(resizedFile, croppedFile, "10,10+50x50");
                if (cropResult == 0) {
                    System.out.println("✅ Crop successful.");
                } else {
                    System.err.println("❌ Crop failed with code: " + cropResult);
                }

                System.out.println("\n🎉 Demo finished successfully!");
                System.out.println("Generated files:");
                System.out.println("  - " + optimizedFile);
                System.out.println("  - " + resizedFile);
                System.out.println("  - " + croppedFile);
                
            } finally {
                // Ensure cleanup is always called
                System.out.println("\n🧹 Cleaning up Gifsicle...");
                Gifsicle.INSTANCE.gifsicle_cleanup();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadLibraryFromJar() throws Exception {
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

        try (InputStream in = Main.class.getResourceAsStream("/" + libName);
             OutputStream out = new FileOutputStream(tempFile)) {

            if (in == null) {
                // Fallback to local build directory for development
                File libFile = new File("src/.libs/" + libName);
                if (libFile.exists()) {
                    System.setProperty("jna.library.path", libFile.getParent());
                    return;
                }
                throw new IllegalStateException("Library " + libName + " not found in JAR or local build directory. Expected extension: " + extension);
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
    
    private static void printFileSize(File originalFile, File optimizedFile) {
        if (originalFile.exists() && optimizedFile.exists()) {
            long originalSize = originalFile.length();
            long optimizedSize = optimizedFile.length();
            double reduction = ((double)(originalSize - optimizedSize) / originalSize) * 100;
            System.out.printf("   Original: %,d bytes -> Optimized: %,d bytes (%.1f%% reduction)%n", 
                             originalSize, optimizedSize, reduction);
        }
    }
}