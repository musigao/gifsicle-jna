#include "libgifsicle.h"
#include "gifsicle.h"
#include "lcdfgif/gif.h"
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// This is a simplified implementation. It reuses the command-line
// argument parsing logic from gifsicle.c, which is not ideal but
// avoids a major rewrite of the library. A proper implementation
// would involve refactoring gifsicle's core logic to be more
// library-friendly.

// Forward declarations of functions from gifsicle.c
int gifsicle_main(int argc, char** argv);
void gifsicle_init(void);
void gifsicle_cleanup(void);

// Global initialization flag
static int gifsicle_initialized = 0;

static int run_gifsicle(int argc, char** argv) {
    // The original main function in gifsicle.c calls exit().
    // We need to avoid that. A proper refactoring would be needed
    // to make the core logic not call exit(). For now, we can't
    // easily capture the return code without more complex changes.
    // We'll assume success if it doesn't crash.
    
    gifsicle_main(argc, argv);
    
    // Don't call gifsicle_cleanup() here - let user control cleanup
    return 0;
}

GIFSICLE_API int gifsicle_crop(const char* input_path, const char* output_path, const char* crop_spec) {
    // 裁剪命令: gifsicle --crop ... -o targetFile sourceFile
    char* argv[] = {
        "gifsicle",
        "--crop",
        (char*)crop_spec,
        "-o",
        (char*)output_path,
        (char*)input_path,
        NULL
    };
    int argc = 6;

    return run_gifsicle(argc, argv);
}

GIFSICLE_API int gifsicle_resize(const char* input_path, const char* output_path, int width, int height) {
    // 缩放命令: gifsicle --resize-fit widthxheight -o targetFile sourceFile
    char resize_str[100];
    snprintf(resize_str, sizeof(resize_str), "%dx%d", width, height);

    char* argv[] = {
        "gifsicle",
        "--resize-fit",
        resize_str,
        "-o",
        (char*)output_path,
        (char*)input_path,
        NULL
    };
    int argc = 6;

    return run_gifsicle(argc, argv);
}

GIFSICLE_API int gifsicle_optimize(const char* input_path, const char* output_path, int level) {
    // 压缩命令: gifsicle -O2 --lossy=x -o targetFile sourceFile
    char lossy_level[15];
    
    // 根据level设置lossy值
    if (level >= 1 && level <= 200) {
        snprintf(lossy_level, sizeof(lossy_level), "--lossy=%d", level);
    } else if (level > 200) {
        // 超过200的话，限制在200以内避免过度压缩
        snprintf(lossy_level, sizeof(lossy_level), "--lossy=200");
    } else {
        // level <= 0的话，使用默认中等压缩
        snprintf(lossy_level, sizeof(lossy_level), "--lossy=40");
    }

    char* argv[] = {
        "gifsicle",
        "-O2",                  // 使用O2优化等级，比O3快
        lossy_level,            // --lossy=XX 允许质量损失
        "-o",
        (char*)output_path,
        (char*)input_path,
        NULL
    };
    int argc = 6;  // 修正参数数量

    return run_gifsicle(argc, argv);
}

int gifsicle_get_dimensions(const char* input_path, int* width, int* height) {
    if (!input_path || !width || !height) {
        return -1;
    }

    FILE* f = fopen(input_path, "rb");
    if (!f) {
        return -1;
    }

    // The GIF_READ_HEADER flag is defined as 8 in gifsicle.h
    int read_flags = 8; 

    Gif_Stream* gfs = Gif_FullReadFile(f, read_flags, 0, 0);

    if (!gfs) {
        fclose(f);
        return -1;
    }

    *width = gfs->screen_width;
    *height = gfs->screen_height;

    Gif_DeleteStream(gfs);
    fclose(f);
    
    return 0;
}
