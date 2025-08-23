package com.example;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;

public interface Gifsicle extends Library {
    // Load the native library.
    // The library should be in a location where the system can find it.
    // For example, on macOS, you can put it in /usr/local/lib, or set
    // the jna.library.path system property.
    // For this demo, we'll assume it's in the resources folder.
    Gifsicle INSTANCE = Native.load("gifsicle", Gifsicle.class);

    // Declare the methods corresponding to the C functions
    void gifsicle_init();
    void gifsicle_cleanup();
    int gifsicle_crop(String input_path, String output_path, String crop_spec);
    int gifsicle_resize(String input_path, String output_path, int width, int height);
    int gifsicle_optimize(String input_path, String output_path, int level);
}
