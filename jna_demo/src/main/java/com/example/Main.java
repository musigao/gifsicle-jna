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
            File outputFile = new File("output.gif");
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

            String optimizedFile = "optimized.gif";
            System.out.println("Optimizing GIF to " + optimizedFile);
            int optimizeResult =
                    Gifsicle.INSTANCE.gifsicle_optimize(inputFile.getAbsolutePath(), optimizedFile, 80);
            if (optimizeResult == 0) {
                System.out.println("Optimization successful.");
            } else {
                System.err.println("Optimization failed.");
            }

            String resizedFile = "resized.gif";
            System.out.println("Resizing GIF to " + resizedFile);
            int resizeResult = Gifsicle.INSTANCE.gifsicle_resize(optimizedFile, resizedFile, 100, 100);
            if (resizeResult == 0) {
                System.out.println("Resize successful.");
            } else {
                System.err.println("Resize failed.");
            }

            String croppedFile = "cropped.gif";
            System.out.println("Cropping GIF to " + croppedFile);
            int cropResult = Gifsicle.INSTANCE.gifsicle_crop(resizedFile, croppedFile, "10,10+50x50");
            if (cropResult == 0) {
                System.out.println("Crop successful.");
            } else {
                System.err.println("Crop failed.");
            }

            System.out.println("Cleaning up Gifsicle...");
            Gifsicle.INSTANCE.gifsicle_cleanup();

            System.out.println("\nDemo finished. Check the generated files: " + optimizedFile + ", " + resizedFile + ", " + croppedFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadLibraryFromJar() throws Exception {
        String libName = System.mapLibraryName("gifsicle");
        File tempFile = File.createTempFile("lib", ".dylib");
        tempFile.deleteOnExit();

        try (InputStream in = Main.class.getResourceAsStream("/" + libName);
             OutputStream out = new FileOutputStream(tempFile)) {

            if (in == null) {
                File libFile = new File("src/.libs/" + libName);
                if (libFile.exists()) {
                    System.setProperty("jna.library.path", libFile.getParent());
                    return;
                }
                throw new IllegalStateException("Library " + libName + " not found in JAR or local build directory.");
            }

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        }
        System.setProperty("jna.library.path", tempFile.getParent());
    }
}