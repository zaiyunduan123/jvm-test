package gc.roots;

/**
 * @Date 2019/5/19 11:45 PM
 * <p>
 * 2、测试方法区中的静态变量引用的对象作为GCRoots
 * -Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails
 * <p>
 * 扩展：方法区存与堆一样，是各个线程共享的内存区域，用于存放已被虚拟机加载的类信息，常量，静态变量，即时编译器编译后的代码等数据
 */
public class TestGCRoots02 {
    private static int _10MB = 10 * 1024 * 1024;
    private byte[] memory;

    private static TestGCRoots02 testGCRoots02;

    public TestGCRoots02(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        System.out.println(String.format("开始：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        TestGCRoots02 t = new TestGCRoots02(4 * _10MB);
        t.testGCRoots02 = new TestGCRoots02(8 * _10MB);

        System.out.println(String.format("运行中：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        t = null;
        System.gc();

        System.out.println(String.format("结束：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));
    }
}
// 运行结果
/**
 * 开始：944 M
 * 运行中：816 M
 * [GC (System.gc()) [PSYoungGen: 146473K->839K(458752K)] 146473K->82767K(983040K), 0.0702949 secs] [Times: user=0.37 sys=0.03, real=0.07 secs]
 * [Full GC (System.gc()) [PSYoungGen: 839K->0K(458752K)] [ParOldGen: 81928K->82554K(524288K)] 82767K->82554K(983040K), [Metaspace: 3644K->3644K(1056768K)], 0.0250346 secs] [Times: user=0.14 sys=0.01, real=0.03 secs]
 * 结束：871 M
 * Heap
 * PSYoungGen      total 458752K, used 11796K [0x00000007a0000000, 0x00000007c0000000, 0x00000007c0000000)
 * eden space 393216K, 3% used [0x00000007a0000000,0x00000007a0b85370,0x00000007b8000000)
 * from space 65536K, 0% used [0x00000007b8000000,0x00000007b8000000,0x00000007bc000000)
 * to   space 65536K, 0% used [0x00000007bc000000,0x00000007bc000000,0x00000007c0000000)
 * ParOldGen       total 524288K, used 82554K [0x0000000780000000, 0x00000007a0000000, 0x00000007a0000000)
 * object space 524288K, 15% used [0x0000000780000000,0x000000078509eaa8,0x00000007a0000000)
 * Metaspace       used 3651K, capacity 4650K, committed 4864K, reserved 1056768K
 * class space    used 399K, capacity 402K, committed 512K, reserved 1048576K
 */


/**
 *  开始：944 M
 *  运行中：816 M
 *  结束：871 M
 *  可以看出最终只是回收t引用的对象（40M）
 *  t设置为null后，在minor GC后它引用的对象（40M）被完全回收，而testGCRoots02作为静态变量，存放在方法区，引用了对象（80M），在Minor GC后，被转移到老年代中，且在FullGC后，也不会被回收，继续保留在老年代中。
 */