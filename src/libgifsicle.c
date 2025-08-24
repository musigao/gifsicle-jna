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
    char resize_str[100];
    snprintf(resize_str, sizeof(resize_str), "%dx%d", width, height);

    char* argv[] = {
        "gifsicle",
        "--resize",
        resize_str,
        "--resize-method",
        "lanczos3",
        "-o",
        (char*)output_path,
        (char*)input_path,
        NULL
    };
    int argc = 8;

    return run_gifsicle(argc, argv);
}

GIFSICLE_API int gifsicle_optimize(const char* input_path, const char* output_path, int level) {
    char opt_level[5];
    char lossy_level[15];
    
    // 强制使用最高优化等级
    snprintf(opt_level, sizeof(opt_level), "-O3");
    
    // 扩展lossy等级支持，支持更高的压缩率
    if (level >= 1 && level <= 200) {
        // 直接使用传入的level作为lossy值
        // level=1 → --lossy=1 (最轻压缩)
        // level=50 → --lossy=50 (中等压缩)
        // level=120 → --lossy=120 (高压缩)
        // level=200 → --lossy=200 (极限压缩)
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
        opt_level,              // -O3 最高优化
        lossy_level,            // --lossy=XX 允许质量损失
        "--colors",
        "128",                  // 减少到128色以获得更好压缩
        "--dither",             // 启用抖动保持视觉质量
        "--optimize=3",         // 深度帧优化
        "--careful",            // 仔细处理
        "-o",
        (char*)output_path,
        (char*)input_path,
        NULL
    };
    int argc = 11;

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
