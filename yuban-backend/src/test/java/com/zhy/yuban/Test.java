//package com.zhy.yuban;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ThreadPoolExecutor;
//import java.util.concurrent.TimeUnit;
//
//class ExecutorServiceExample {
//
//    public static void main(String[] args) {
//        // 创建一个固定大小的线程池
//        ExecutorService executor = Executors.newFixedThreadPool(20);
//
//        // 创建自定义大小的线程池
//        new ThreadPoolExecutor();
//
//        // 提交10个任务到线程池
//        for (int i = 0; i < 40; i++) {
//            Runnable worker = new WorkerThread("" + i);
//            executor.execute(worker); // 调用execute方法启动线程
//        }
//
//        // 关闭线程池（这将会导致之前提交的任务被终止，所以通常会在所有任务都提交之后再关闭）
//        // 在这个例子中，并不立即关闭它，而是在所有任务完成后才关闭
//        // executor.shutdown();
//
//        // 为了展示，让主线程睡眠一段时间，以确保所有任务都有时间完成
//        try {
//            TimeUnit.SECONDS.sleep(3);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // 关闭线程池（确保所有任务都已经完成）
//        executor.shutdown();
//
//        // 等待所有任务完成
//        try {
//            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
//                // 如果等待时间到了，但线程池还没有关闭，那么可以选择强制关闭
//                executor.shutdownNow();
//            }
//        } catch (InterruptedException e) {
//            executor.shutdownNow();
//        }
//
//        System.out.println("所有线程运行完毕");
//    }
//
//    /**
//     * 内部类，实现Runnable接口，作为线程的任务
//     */
//    public static class WorkerThread implements Runnable {
//
//        private String command;
//
//        public WorkerThread(String command) {
//            this.command = command;
//        }
//
//        @Override
//        public void run() {
//            System.out.println(Thread.currentThread().getName() + " 开始处理命令: " + command);
//            processCommand();
//            System.out.println(Thread.currentThread().getName() + " 结束处理命令");
//        }
//
//        private void processCommand() {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        @Override
//        public String toString() {
//            return this.command;
//        }
//    }
//}