import javassist.*;

public class Sample {

    public static void main(String... args) throws NotFoundException, CannotCompileException {


        final ClassPool classPool = ClassPool.getDefault();

        // Webアプリではクラスローダーが複数あるので対象のクラスが存在するクラスローダーを
        // ClassPoolに登録しないとうまく動作しない可能性があるとのこと。
        // WARのクラスローダーが欲しいので自分のクラスのローダーを取得して登録している
        // http://jboss-javassist.github.io/javassist/tutorial/tutorial.html
        classPool.insertClassPath(new ClassClassPath(Sample.class));

        CtClass simpleLoggerClass = classPool.get("SimpleLogger");
        // 変更済みなら終える(一度変更済みだと再度の変更はできない)
        if (simpleLoggerClass.isModified()) {
            return;
        }

        // 全コンストラクタを削除
        final CtConstructor[] constructors = simpleLoggerClass.getDeclaredConstructors();
        for (CtConstructor constructor : constructors) {
            simpleLoggerClass.removeConstructor(constructor);
        }

        // デフォルトコンストラクタを生成して追加
        // デフォルトコンストラクタ1つは作っておかないとnewできない
        simpleLoggerClass.addConstructor(CtNewConstructor.defaultConstructor(simpleLoggerClass));


        CtMethod methods = simpleLoggerClass.getDeclaredMethod("log");

        // ここらへん参考に置き換えるソースを記述
        // http://jboss-javassist.github.io/javassist/tutorial/tutorial2.html#alter
        String newSrc = "{"
                + "System.out.println(\"[modified]\" + $1);"
                + "}";

        methods.setBody(newSrc);

        // 読み込みの時と同様の理由で変更後クラスを登録する際にもクラスローダーを指定してあげる
        // http://jboss-javassist.github.io/javassist/tutorial/tutorial.html#load
        simpleLoggerClass.toClass(Sample.class.getClassLoader(), null);

        final SimpleLogger simpleLogger = new SimpleLogger();

        simpleLogger.log("test");
    }
}
