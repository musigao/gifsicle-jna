#ifndef LIBGIFSICLE_H
#define LIBGIFSICLE_H

#if defined(_WIN32) || defined(__CYGWIN__)
#  define GIFSICLE_API __declspec(dllexport)
#else
#  define GIFSICLE_API __attribute__((visibility("default")))
#endif

#ifdef __cplusplus
extern "C" {
#endif

/**
 * @brief Initializes the gifsicle library. Call this before any other functions.
 */
GIFSICLE_API void gifsicle_init(void);

/**
 * @brief Cleans up resources used by the gifsicle library. Call this when you are done.
 */
GIFSICLE_API void gifsicle_cleanup(void);

/**
 * @brief Crops a GIF image.
 * @param input_path Path to the input GIF file.
 * @param output_path Path to the output GIF file.
 * @param x The x-coordinate of the top-left corner of the crop rectangle.
 * @param y The y-coordinate of the top-left corner of the crop rectangle.
 * @param width The width of the crop rectangle.
 * @param height The height of the crop rectangle.
 * @return 0 on success, non-zero on failure.
 */
GIFSICLE_API int gifsicle_crop(const char* input_path, const char* output_path, const char* crop_spec);
GIFSICLE_API int gifsicle_get_dimensions(const char* input_path, int* width, int* height);

/**
 * @brief Resizes a GIF image.
 * @param input_path Path to the input GIF file.
 * @param output_path Path to the output GIF file.
 * @param width The new width.
 * @param height The new height.
 * @return 0 on success, non-zero on failure.
 */
GIFSICLE_API int gifsicle_resize(const char* input_path, const char* output_path, int width, int height);

/**
 * @brief Compresses/Optimizes a GIF image.
 * @param input_path Path to the input GIF file.
 * @param output_path Path to the output GIF file.
 * @param level Optimization level (e.g., 1, 2, or 3).
 * @return 0 on success, non-zero on failure.
 */
GIFSICLE_API int gifsicle_optimize(const char* input_path, const char* output_path, int level);

/**
 * @brief Gets the dimensions of a GIF image.
 * @param input_path Path to the input GIF file.
 * @param width Pointer to an integer to receive the width.
 * @param height Pointer to an integer to receive the height.
 * @return 0 on success, non-zero on failure.
 */
GIFSICLE_API int gifsicle_get_dimensions(const char* input_path, int* width, int* height);

#ifdef __cplusplus
}
#endif

#endif // LIBGIFSICLE_H
