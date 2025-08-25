package com.example;

public class SimpleTest {
    public static void main(String[] args) {
        try {
            System.out.println("=== 开始简单测试 ===");
            
            // 初始化
            System.out.println("初始化库...");
            Gifsicle.INSTANCE.gifsicle_init();
            System.out.println("初始化完成");
            
            // 测试压缩功能
            System.out.println("开始压缩测试...");
            String inputFile = "src/main/resources/logo.gif";
            String outputFile = "simple_test.gif";
            
            System.out.println("调用 gifsicle_optimize...");
            int result = Gifsicle.INSTANCE.gifsicle_optimize(inputFile, outputFile, 50);
            System.out.println("gifsicle_optimize 返回: " + result);
            
            // 清理
            System.out.println("清理资源...");
            Gifsicle.INSTANCE.gifsicle_cleanup();
            System.out.println("测试完成！");
            
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
