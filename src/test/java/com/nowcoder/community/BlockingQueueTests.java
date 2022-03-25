package com.nowcoder.community;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author 邓志斌
 * @version 1.0
 * @date 2022/3/25 9:58
 * 阻塞队列实例
 */
public class BlockingQueueTests {
    public static void main(String[] args) {
        BlockingQueue<Integer> blockingQueue=new ArrayBlockingQueue<>(10);
        new Thread(new Producer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
        new Thread(new Consumer(blockingQueue)).start();
    }
}

// 生产者
class Producer implements Runnable {
    private BlockingQueue<Integer> blockingQueue;
    public Producer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 100; i++) {
                Thread.sleep(20);
                blockingQueue.add(i);  // 生产数据，放入阻塞队列
                System.out.println(Thread.currentThread().getName() + "生产：" + blockingQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// 消费者
class Consumer implements Runnable {
    private BlockingQueue<Integer> blockingQueue;
    public Consumer(BlockingQueue<Integer> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(new Random().nextInt(1000));
                blockingQueue.poll();  // 消费数据
                System.out.println(Thread.currentThread().getName() + "消费：" + blockingQueue.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
