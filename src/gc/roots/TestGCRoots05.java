package gc.roots;

/**
 * @Date 2019/5/20 12:23 AM
 * <p>
 * 5、测试JVM是否使用引用计数器算法来判断对象是否存活
 * -Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails
 * <p>
 * 总结：JVM并没因为相互引用就不回收它们，所以JVM没有使用引用计数器算法来判断对象是否存活
 */
public class TestGCRoots05 {

    private static int _10MB = 10 * 1024 * 1024;
    private Object instance = null;

    private byte[] memory;

    public TestGCRoots05(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        System.out.println(String.format("开始：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        TestGCRoots05 t1 = new TestGCRoots05(4 * _10MB);
        TestGCRoots05 t2 = new TestGCRoots05(4 * _10MB);
        t1.instance = t2;
        t2.instance = t1;

        System.out.println(String.format("运行中：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        t1 = null;
        t2 = null;
        System.gc();

        System.out.println(String.format("结束：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

    }
}

// 运行结果
/**
 * 开始：944 M
 * 运行中：856 M
 * [GC (System.gc()) [PSYoungGen: 105513K->807K(458752K)] 105513K->815K(983040K), 0.0017649 secs] [Times: user=0.00 sys=0.00, real=0.00 secs]
 * [Full GC (System.gc()) [PSYoungGen: 807K->0K(458752K)] [ParOldGen: 8K->634K(524288K)] 815K->634K(983040K), [Metaspace: 3637K->3637K(1056768K)], 0.0067061 secs] [Times: user=0.02 sys=0.00, real=0.00 secs]
 * 结束：951 M
 * Heap
 * PSYoungGen      total 458752K, used 11796K [0x00000007a0000000, 0x00000007c0000000, 0x00000007c0000000)
 * eden space 393216K, 3% used [0x00000007a0000000,0x00000007a0b85370,0x00000007b8000000)
 * from space 65536K, 0% used [0x00000007b8000000,0x00000007b8000000,0x00000007bc000000)
 * to   space 65536K, 0% used [0x00000007bc000000,0x00000007bc000000,0x00000007c0000000)
 * ParOldGen       total 524288K, used 634K [0x0000000780000000, 0x00000007a0000000, 0x00000007a0000000)
 * object space 524288K, 0% used [0x0000000780000000,0x000000078009ea88,0x00000007a0000000)
 * Metaspace       used 3644K, capacity 4650K, committed 4864K, reserved 1056768K
 * class space    used 399K, capacity 402K, committed 512K, reserved 1048576K
 * Disconnected from the target VM, address: '127.0.0.1:62653', transport: 'socket'
 */

/**
 * 开始：944 M
 * 运行中：856 M
 * 结束：951 M
 * 可以看出最终会回收t1引用的对象（40M）和 t2引用的对象（40M）
 * JVM并没因为相互引用就不回收它们，所以JVM没有使用引用计数器算法来判断对象是否存活
 * */
