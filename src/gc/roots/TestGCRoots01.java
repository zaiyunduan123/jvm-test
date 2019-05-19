package gc.roots;

/**
 * @Date 2019/5/19 11:13 PM
 * <p>
 * 1、 CG Roots 测试：虚拟机栈（栈帧中的局部变量）中引用的对象作为GCRoots
 * -Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails
 * <p>
 * 扩展：虚拟机栈中存放了编译器可知的八种基本数据类型,对象引用,returnAddress类型（指向了一条字节码指令的地址）
 */
public class TestGCRoots01 {
    private int _10MB = 10 * 1024 * 1024;
    private byte[] memory = new byte[8 * _10MB];

    public static void main(String[] args) {
        System.out.println(String.format("第一次GC开始前：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        method01();
        System.out.println(String.format("第一次GC结束后：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        System.gc();
        System.out.println(String.format("第二次GC结束后：%d M", Runtime.getRuntime().freeMemory() / (1024 * 1024)));
    }

    public static void method01() {
        // 虚拟机栈（栈帧中的局部变量）中引用的对象
        TestGCRoots01 testGCRoots01 = new TestGCRoots01();
        System.gc();
    }
}

//  运行结果
/**
 * 第一次GC开始前：944 M
 * [GC (System.gc()) [PSYoungGen: 105513K->855K(458752K)] 105513K->82783K(983040K), 0.0740550 secs] [Times: user=0.32 sys=0.03, real=0.08 secs]
 * [Full GC (System.gc()) [PSYoungGen: 855K->0K(458752K)] [ParOldGen: 81928K->82554K(524288K)] 82783K->82554K(983040K), [Metaspace: 3646K->3646K(1056768K)], 0.0307943 secs] [Times: user=0.14 sys=0.01, real=0.03 secs]
 * 第一次GC结束后：871 M
 * [GC (System.gc()) [PSYoungGen: 7864K->32K(458752K)] 90419K->82586K(983040K), 0.0011369 secs] [Times: user=0.01 sys=0.00, real=0.00 secs]
 * [Full GC (System.gc()) Disconnected from the target VM, address: '127.0.0.1:61425', transport: 'socket'
 * [PSYoungGen: 32K->0K(458752K)] [ParOldGen: 82554K->538K(524288K)] 82586K->538K(983040K), [Metaspace: 3646K->3646K(1056768K)], 0.0065145 secs] [Times: user=0.02 sys=0.00, real=0.01 secs]
 * 第二次GC结束后：951 M
 * Heap
 * PSYoungGen      total 458752K, used 11796K [0x00000007a0000000, 0x00000007c0000000, 0x00000007c0000000)
 * eden space 393216K, 3% used [0x00000007a0000000,0x00000007a0b85370,0x00000007b8000000)
 * from space 65536K, 0% used [0x00000007bc000000,0x00000007bc000000,0x00000007c0000000)
 * to   space 65536K, 0% used [0x00000007b8000000,0x00000007b8000000,0x00000007bc000000)
 * ParOldGen       total 524288K, used 538K [0x0000000780000000, 0x00000007a0000000, 0x00000007a0000000)
 * object space 524288K, 0% used [0x0000000780000000,0x00000007800869d8,0x00000007a0000000)
 * Metaspace       used 3652K, capacity 4650K, committed 4864K, reserved 1056768K
 * class space    used 399K, capacity 402K, committed 512K, reserved 1048576K
 */


/**
 * 第一次GC开始前：944 M
 * 第一次GC结束后：871 M
 * 第二次GC结束后：951 M
 * 可以看出第一次GC没有回收new出的对象（80M），第二次才回收
 * 1、method01方法内的testGCRoots01为局部变量，引用了new出的对象（80M），它是虚拟机栈（栈帧中的局部变量）中引用的对象，所以作为GCRoots的对象，在Minor GC后被转移到老年代中，且FullGC也不会回收该对象，仍保留在老年代中。
 * 2、执行method01方法结束后，局部变量testGCRoots01随方法消失，不再有引用类型指向该对象，该在Full GC后，被完全回收，老年代腾出该对象之前所占的空间。
 */
