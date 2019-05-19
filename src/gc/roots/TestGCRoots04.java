package gc.roots;

/**
 * @Date 2019/5/20 12:23 AM
 * <p>
 * 4、测试成员变量引用对象是否可作为GCRoots
 * -Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails
 *
 * 总结：成员变量是存储在堆内存的对象中的，和对象共存亡，所以是不能作为GC Roots
 */
public class TestGCRoots04 {

    private static int _10MB = 10 * 1024 * 1024;
    private TestGCRoots04 t;

    private byte[] memory;

    public TestGCRoots04(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        System.out.println(String.format("开始：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        TestGCRoots04 testGCRoots04 = new TestGCRoots04(4 * _10MB);
        testGCRoots04.t = new TestGCRoots04(8 * _10MB);

        System.out.println(String.format("运行中：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

        testGCRoots04 = null;
        System.gc();

        System.out.println(String.format("结束：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));

    }
}

// 运行结果
/**
 * 开始：944 M
 * 运行中：816 M
 * [GC (System.gc()) [PSYoungGen: 146473K->791K(458752K)] 146473K->799K(983040K), 0.0016330 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 * [Full GC (System.gc()) [PSYoungGen: 791K->0K(458752K)] [ParOldGen: 8K->634K(524288K)] 799K->634K(983040K), [Metaspace: 3638K->3638K(1056768K)], 0.0065346 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
 * 结束：951 M
 * Heap
 * PSYoungGen      total 458752K, used 11796K [0x00000007a0000000, 0x00000007c0000000, 0x00000007c0000000)
 * eden space 393216K, 3% used [0x00000007a0000000,0x00000007a0b85370,0x00000007b8000000)
 * from space 65536K, 0% used [0x00000007b8000000,0x00000007b8000000,0x00000007bc000000)
 * to   space 65536K, 0% used [0x00000007bc000000,0x00000007bc000000,0x00000007c0000000)
 * ParOldGen       total 524288K, used 634K [0x0000000780000000, 0x00000007a0000000, 0x00000007a0000000)
 * object space 524288K, 0% used [0x0000000780000000,0x000000078009ea88,0x00000007a0000000)
 * Metaspace       used 3645K, capacity 4650K, committed 4864K, reserved 1056768K
 * class space    used 399K, capacity 402K, committed 512K, reserved 1048576K
 */

/**
 *  开始：944 M
 *  运行中：816 M
 *  结束：951 M
 *  可以看出最终会回收testGCRoots04引用的对象（40M）和 t引用的对象（80M）
 *  testGCRoots04被置为null，MinorGC后testGCRoots04之前引用的对象（40M）被完全回收；t为成员变量，也叫实例变量，不同于类变量（静态变量）或者常量，前面讲到类变量是存储在方法区中，而成员变量是存储在堆内存的对象中的，和对象共存亡，所以是不能作为GC Roots的，从日志中也可看出t在MinorGC后，跟随testGCRoots04一起被完全回收。不再占用任何空间。
 */
