#ifndef LCDF_INTTYPES_H
#define LCDF_INTTYPES_H
/* Define known-width integer types. */

/* If we have stdint.h, use it and skip our custom definitions */
#if defined(__STDC_VERSION__) && __STDC_VERSION__ >= 199901L
# include <stdint.h>
#elif defined(HAVE_INTTYPES_H)
# include <inttypes.h>
#elif defined(HAVE_SYS_TYPES_H)
# include <sys/types.h>
# ifdef HAVE_U_INT_TYPES
typedef u_int8_t uint8_t;
typedef u_int16_t uint16_t;
typedef u_int32_t uint32_t;
# endif
#elif defined(_WIN32)
# include <stdint.h>
/* For Windows, just use the standard stdint.h types */
#else
/* Fallback definitions for very old systems */
typedef signed char int8_t;
typedef unsigned char uint8_t;
typedef short int16_t;
typedef unsigned short uint16_t;
typedef int int32_t;
typedef unsigned int uint32_t;
#endif

#ifndef HAVE_UINTPTR_T
# if !defined(_WIN32) && !defined(__STDC_VERSION__)
#  if SIZEOF_VOID_P == SIZEOF_UNSIGNED_LONG
typedef unsigned long uintptr_t;
#  elif SIZEOF_VOID_P == SIZEOF_UNSIGNED_INT
typedef unsigned int uintptr_t;
#  endif
# endif
#endif

#endif
