#include <stdio.h>
#include <stdlib.h>

int main() {
    printf("Testing simple gifsicle command...\n");
    
    char* argv[] = {
        "gifsicle",
        "-O2",
        "--lossy=50",
        "-o",
        "test_output.gif",
        "jna_demo/src/main/resources/logo.gif",
        NULL
    };
    int argc = 6;
    
    printf("About to call gifsicle with args:\n");
    for (int i = 0; i < argc; i++) {
        printf("  argv[%d] = %s\n", i, argv[i]);
    }
    
    // 直接调用系统命令来测试
    printf("Calling system command...\n");
    system("src/.libs/gifsicle -O2 --lossy=50 -o test_output.gif jna_demo/src/main/resources/logo.gif");
    printf("System command completed.\n");
    
    return 0;
}
