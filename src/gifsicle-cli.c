/* gifsicle-cli.c - main() for gifsicle command line
   Copyright (C) 1997-2025 Eddie Kohler, ekohler@gmail.com
   This file is part of gifsicle.

   Gifsicle is free software. It is distributed under the GNU Public License,
   version 2; you can copy, distribute, or alter it at will, as long
   as this notice is kept intact and this source code is made available. There
   is no warranty, express or implied. */

#include <config.h>
#include "gifsicle.h"

int gifsicle_main(int argc, char *argv[]);

int main(int argc, char *argv[]) {
    return gifsicle_main(argc, argv);
}
