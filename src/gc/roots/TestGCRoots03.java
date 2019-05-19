package gc.roots;

/**
 * @Date 2019/5/20 12:12 AM
 * <p>
 * 3、测试常量引用对象作为GCRoots
 * 注意：修饰符如果只是final会被回收，static final不会被回收，所以static final 才是常量的正确写法
 * <p>
 * -Xms1024m -Xmx1024m -Xmn512m -XX:+PrintGCDetails
 */
public class TestGCRoots03 {

    private static int _10MB = 10 * 1024 * 1024;
    private static final TestGCRoots03 t = new TestGCRoots03(8 * _10MB);

    private byte[] memory;

    public TestGCRoots03(int size) {
        memory = new byte[size];
    }

    public static void main(String[] args) {
        TestGCRoots03 testGCRoots03 = new TestGCRoots03(4 * _10MB);
        testGCRoots03 = null;
        System.gc();
    }
}
