## Process this file with automake to produce Makefile.in
AUTOMAKE_OPTIONS = foreign check-news

# Define the shared library that will be built
lib_LTLIBRARIES = libgifsicle.la

# Define the programs to be built
bin_PROGRAMS = gifsicle @OTHERPROGRAMS@
EXTRA_PROGRAMS = gifview gifdiff

# Sources for the shared library - all the common code
libgifsicle_la_SOURCES = \
	libgifsicle.c \
	clp.c \
	fmalloc.c \
	giffunc.c \
	gifread.c \
	gifunopt.c \
	gifsicle.h \
	kcolor.h \
	kcolor.c \
	merge.c \
	optimize.c \
	quantize.c \
	support.c \
	xform.c \
	gifwrite.c \
	gifsicle.c

# Linker flags for the shared library
libgifsicle_la_LDFLAGS = -no-undefined -version-info 0:0:0

# Sources for the gifsicle executable (only its main file)
gifsicle_SOURCES = gifsicle-cli.c
# Link gifsicle against our shared library
gifsicle_LDADD = libgifsicle.la

# Sources for the gifview executable
gifview_SOURCES = gifview.c gifx.c
# Link gifview against our shared library and X11 libs
gifview_LDADD = libgifsicle.la @X_LIBS@ @X_PRE_LIBS@ -lX11 @X_EXTRA_LIBS@

# Sources for the gifdiff executable
gifdiff_SOURCES = gifdiff.c
# Link gifdiff against our shared library
gifdiff_LDADD = libgifsicle.la

# Common compiler flags
AM_CPPFLAGS = $(X_CFLAGS) $(WERROR) -I$(top_srcdir)/include

# Extra files for distribution
EXTRA_DIST = opttemplate.c Makefile.bcc Makefile.w32 win32cfg.h
