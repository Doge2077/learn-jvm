# 类文件结构

****

## Write Once，Run Anywhere
****

对于 `C` 语言从程序到运行需要经过编译的过程，只有经历了编译后，我们所编写的代码才能够翻译为机器可以直接运行的二进制代码，并且在不同的操作系统下，我们的代码都需要进行一次编译之后才能运行。

而 `Java` 不同于 `C`，由于 `JVM` 内置了解释器和即时编译器，这使得 `.java` 可以不经过编译就直接通过解释的方式直接运行。关于解释器和即时编译器的内容我们将在以后介绍。

我们来看一段简单的代码：

```java
public class Hello {
    public static void main(String[] args) {
        System.out.println("Hello world");
    }
}
```

我们将其保存为 `Hello.java`，然后运行：

```shell
java Hello.java
```

可以看到不出意外地输出了：

![image-20230905163020567](https://image.itbaima.net/images/40/image-20230905167713407.png)

这样我们就通过解释的方式运行了刚刚编写的 `.java` 文件，但为了更好的理解 `Java` 的类文件结构，我们接下来对 `.java` 文件进行编译：

```shell
javac Hello.java
```

可以看到在当前目录下生成了一个 `Hello.class` 文件：

![image-20230905163351158](https://image.itbaima.net/images/40/image-20230905164213676.png)

接下来我们运行：

```shell
java Hello
```

可以看到不出意外的输出了：

![image-20230905163722881](https://image.itbaima.net/images/40/image-20230905163889038.png)

这样我们就以运行编译后的文件方式运行了 `.class` 文件。

如果我们以十六进制打开该文件：

![image-20230905163517473](https://image.itbaima.net/images/40/image-20230905165686984.png)

你会看到上述文件充满了以两个字节为一组的字节码，前两组四个字节被成为“魔数”，说明该文件是否为一个能被虚拟机接受的 `Class` 文件，而 `Java` 的 `Class` 文件的魔数值为 `0xCAFEBABE` ☕👶。

“一次编写，到处运行”——这是 `Java` 在刚刚诞生之时提出的非常著名的宣传口号，而 `Java` 的解决方案便是将像上面这样的源代码编译为平台无关的中间格式——字节码文件（`.class`  文件），并通过对应的 `Java` 虚拟机读取和运行这些中间格式的编译文件，这就使得语言本身实现了跨平台。

****

## 结构概述

****

`Java` 源代码经过编译器编译后会生成字节码文件（`.class` 文件），其中字节码文件的结构如下：

1. 魔数（Magic Number）：字节码文件的前四个字节是一个固定的魔数（`0xCAFEBABE`），用于标识该文件为 `Java` 字节码文件。

2. 版本信息（Version）：紧随魔数之后的两个字节表示字节码文件的版本信息，分别是主版本号和次版本号。

3. 常量池（Constant Pool）：紧随版本信息之后是一个常量池表（Constant Pool Table），用于存储编译时生成的各种常量、符号引用和字面量。常量池中的每个项目都有一个索引，通过索引可以在运行时查找和使用。

4. 访问标志（Access Flags）：紧随常量池之后的两个字节表示类或接口的访问标志，用于指示该类或接口的访问级别和特性。

5. 类索引、父类索引和接口索引（Class Indexes）：紧随访问标志之后的两个字节表示类索引，用于指向常量池中该类的全限定名。接着是两个字节的父类索引，用于指向常量池中父类的全限定名。接口索引表紧随父类索引之后，用于指向常量池中实现的接口的全限定名。

6. 字段表（Field Table）：紧随接口索引之后是字段表，用于描述类或接口中定义的字段的访问标志、字段名、字段类型等信息。

7. 方法表（Method Table）：紧随字段表之后是方法表，用于描述类或接口中定义的方法的访问标志、方法名、方法参数、方法返回类型等信息。

8. 属性表（Attribute Table）：紧随方法表之后是属性表，用于存储与类、字段或方法相关的附加信息，如注解、代码行号表、异常表等。

****

# 字节码指令

****

## 生成反编译文件

****

由于字节码实在~~不是人读的~~难以理解，如果你感兴趣全部读懂可以自行深入学习（ ~~真的有人用纯字节码编程吗~~，我们还是康康能理解一点的东西罢）

我们利用 `javap` 命令对 `.class` 文件反编译，将其输出到文本：

```shell
javap -v Hello.class > Hello.txt
```

> `javap` 命令的一些常用选项包括：
>
> - `-c`：显示字节码指令。
> - `-l`：显示行号和本地变量表。
> - `-s`：显示内部类型签名。
> - `-verbose`：显示所有信息，可简化为 `-v`。

然后打开 `Hello.txt` 查看：

```asm
Classfile /L:/JAVA/BasicSyntax/Learn_JVM/code/Hello.class
  Last modified 2023年9月5日; size 415 bytes
  SHA-256 checksum 35b1e377d78c81fc0a324af427c3e67c3a468b293c544de8715343f4d97c0c52
  Compiled from "Hello.java"
public class Hello
  minor version: 0
  major version: 61
  flags: (0x0021) ACC_PUBLIC, ACC_SUPER
  this_class: #21                         // Hello
  super_class: #2                         // java/lang/Object
  interfaces: 0, fields: 0, methods: 2, attributes: 1
Constant pool:
   #1 = Methodref          #2.#3          // java/lang/Object."<init>":()V
   #2 = Class              #4             // java/lang/Object
   #3 = NameAndType        #5:#6          // "<init>":()V
   #4 = Utf8               java/lang/Object
   #5 = Utf8               <init>
   #6 = Utf8               ()V
   #7 = Fieldref           #8.#9          // java/lang/System.out:Ljava/io/PrintStream;
   #8 = Class              #10            // java/lang/System
   #9 = NameAndType        #11:#12        // out:Ljava/io/PrintStream;
  #10 = Utf8               java/lang/System
  #11 = Utf8               out
  #12 = Utf8               Ljava/io/PrintStream;
  #13 = String             #14            // Hello world
  #14 = Utf8               Hello world
  #15 = Methodref          #16.#17        // java/io/PrintStream.println:(Ljava/lang/String;)V
  #16 = Class              #18            // java/io/PrintStream
  #17 = NameAndType        #19:#20        // println:(Ljava/lang/String;)V
  #18 = Utf8               java/io/PrintStream
  #19 = Utf8               println
  #20 = Utf8               (Ljava/lang/String;)V
  #21 = Class              #22            // Hello
  #22 = Utf8               Hello
  #23 = Utf8               Code
  #24 = Utf8               LineNumberTable
  #25 = Utf8               main
  #26 = Utf8               ([Ljava/lang/String;)V
  #27 = Utf8               SourceFile
  #28 = Utf8               Hello.java
{
  public Hello();
    descriptor: ()V
    flags: (0x0001) ACC_PUBLIC
    Code:
      stack=1, locals=1, args_size=1
         0: aload_0
         1: invokespecial #1                  // Method java/lang/Object."<init>":()V
         4: return
      LineNumberTable:
        line 1: 0

  public static void main(java.lang.String[]);
    descriptor: ([Ljava/lang/String;)V
    flags: (0x0009) ACC_PUBLIC, ACC_STATIC
    Code:
      stack=2, locals=1, args_size=1
         0: getstatic     #7                  // Field java/lang/System.out:Ljava/io/PrintStream;
         3: ldc           #13                 // String Hello world
         5: invokevirtual #15                 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
         8: return
      LineNumberTable:
        line 3: 0
        line 4: 8
}
SourceFile: "Hello.java"
```

上述文件内容就是利用 `javap` 反汇编得到的全部信息，这实际上是因为 `Class` 文件采用了一种类似于 `C` 中结构体的伪结构来存储数据。

****

## 常见字节码指令

****

⚠大量定义警告⚠：以下将出现大量指令，用到查表即可，但需要重点学习方法调用和返回指令（重中之重）。

****

### 加载和存储指令

****

加载和存储指令用于将数据在栈帧中的局部变量表和操作数栈之间来回传输：

1. 局部变量加载指令（Load Instructions）：
   - `iload`: 从局部变量表加载一个int类型的变量到操作栈。
   - `lload`: 从局部变量表加载一个long类型的变量到操作栈。
   - `fload`: 从局部变量表加载一个float类型的变量到操作栈。
   - `dload`: 从局部变量表加载一个double类型的变量到操作栈。
   - `aload`: 从局部变量表加载一个引用类型的变量到操作栈。

2. 局部变量存储指令（Store Instructions）：
   - `istore`: 将一个int类型的数值从操作数栈存储到局部变量表。
   - `lstore`: 将一个long类型的数值从操作数栈存储到局部变量表。
   - `fstore`: 将一个float类型的数值从操作数栈存储到局部变量表。
   - `dstore`: 将一个double类型的数值从操作数栈存储到局部变量表。
   - `astore`: 将一个引用类型的数值从操作数栈存储到局部变量表。

3. 常量加载指令（Constant Instructions）：
   - `iconst`: 将int类型的常量加载到操作数栈。
   - `lconst`: 将long类型的常量加载到操作数栈。
   - `fconst`: 将float类型的常量加载到操作数栈。
   - `dconst`: 将double类型的常量加载到操作数栈。
   - `aconst_null`: 将null引用加载到操作数栈。

4. 操作数栈指令（Stack Instructions）：
   - `bipush`: 将一个字节大小的常量加载到操作数栈。
   - `sipush`: 将一个短整型大小的常量加载到操作数栈。

****

### 运算相关指令

****

1. 加法指令（Addition Instructions）：
   - `iadd`: 执行两个int类型操作数的相加操作。
   - `ladd`: 执行两个long类型操作数的相加操作。
   - `fadd`: 执行两个float类型操作数的相加操作。
   - `dadd`: 执行两个double类型操作数的相加操作。

2. 减法指令（Subtraction Instructions）：
   - `isub`: 执行两个int类型操作数的相减操作。
   - `lsub`: 执行两个long类型操作数的相减操作。
   - `fsub`: 执行两个float类型操作数的相减操作。
   - `dsub`: 执行两个double类型操作数的相减操作。

3. 乘法指令（Multiplication Instructions）：
   - `imul`: 执行两个int类型操作数的相乘操作。
   - `lmul`: 执行两个long类型操作数的相乘操作。
   - `fmul`: 执行两个float类型操作数的相乘操作。
   - `dmul`: 执行两个double类型操作数的相乘操作。

4. 除法指令（Division Instructions）：
   - `idiv`: 执行两个int类型操作数的相除操作。
   - `ldiv`: 执行两个long类型操作数的相除操作。
   - `fdiv`: 执行两个float类型操作数的相除操作。
   - `ddiv`: 执行两个double类型操作数的相除操作。

5. 求余指令（Remainder Instructions）：
   - `irem`: 执行两个int类型操作数的求余操作。
   - `lrem`: 执行两个long类型操作数的求余操作。
   - `frem`: 执行两个float类型操作数的求余操作。
   - `drem`: 执行两个double类型操作数的求余操作。

6. 取反指令（Negation Instructions）：
   - `ineg`: 对一个int类型操作数进行取反操作。
   - `lneg`: 对一个long类型操作数进行取反操作。
   - `fneg`: 对一个float类型操作数进行取反操作。
   - `dneg`: 对一个double类型操作数进行取反操作。

7. 位移指令（Shift Instructions）：
   - `ishl`: 对一个int类型操作数进行左移操作。
   - `ishr`: 对一个int类型操作数进行带符号右移操作。
   - `iushr`: 对一个int类型操作数进行无符号右移操作。
   - `lshl`: 对一个long类型操作数进行左移操作。
   - `lshr`: 对一个long类型操作数进行带符号右移操作。
   - `lushr`: 对一个long类型操作数进行无符号右移操作。

8. 按位或指令（Bitwise OR Instructions）：
   - `ior`: 对两个int类型操作数进行按位或操作。
   - `lor`: 对两个long类型操作数进行按位或操作。

9. 按位与指令（Bitwise AND Instructions）：
   - `iand`: 对两个int类型操作数进行按位与操作。
   - `land`: 对两个long类型操作数进行按位与操作。

10. 按位异或指令（Bitwise XOR Instructions）：
    - `ixor`: 对两个int类型操作数进行按位异或操作。
    - `lxor`: 对两个long类型操作数进行按位异或操作。

11. 局部变量自增指令（Increment Instructions）：
    - `iinc`: 对一个int类型的局部变量进行自增操作。

12. 比较指令（Comparison Instructions）：
    - `dcmpg`: 比较两个double类型操作数的大小（带NaN处理）。
    - `dcmpl`: 比较两个double类型操作数的大小（带NaN处理）。
    - `fcmpg`: 比较两个float类型操作数的大小（带NaN处理）。
    - `fcmpl`: 比较两个float类型操作数的大小（带NaN处理）。
    - `lcmp`: 比较两个long类型操作数的大小。

****

### 类型转换指令

****

1. 整数类型转换指令（Integer Conversion Instructions）：
   - `i2l`: 将int类型转换为long类型。
   - `i2f`: 将int类型转换为float类型。
   - `i2d`: 将int类型转换为double类型。
   - `l2i`: 将long类型转换为int类型。
   - `l2f`: 将long类型转换为float类型。
   - `l2d`: 将long类型转换为double类型。
   - `f2i`: 将float类型转换为int类型。
   - `f2l`: 将float类型转换为long类型。
   - `f2d`: 将float类型转换为double类型。
   - `d2i`: 将double类型转换为int类型。
   - `d2l`: 将double类型转换为long类型。
   - `d2f`: 将double类型转换为float类型。

2. 类型强制转换指令（Type Casting Instructions）：
   - `checkcast`: 检查对象是否可以强制转换为指定类型。
   - `instanceof`: 检查对象是否是指定类型的实例。

3. 数值拓宽和缩窄指令（Numeric Widening and Narrowing Instructions）：
   - `i2b`: 将int类型转换为byte类型。
   - `i2c`: 将int类型转换为char类型。
   - `i2s`: 将int类型转换为short类型。

****

### 对象创建与访问指令

****

1. 对象创建指令（Object Creation Instructions）：
   - `new`: 创建一个新的对象，并将其引用推送到操作数栈上。
   - `newarray`: 创建一个指定类型的新数组，并将其引用推送到操作数栈上。
   - `anewarray`: 创建一个引用类型的新数组，并将其引用推送到操作数栈上。
   - `multianewarray`: 创建一个多维数组，并将其引用推送到操作数栈上。

2. 对象访问指令（Object Access Instructions）：
   - `getfield`: 从对象中获取实例字段的值，并将其推送到操作数栈上。
   - `putfield`: 将一个值存储到对象的实例字段中。
   - `getstatic`: 获取静态字段的值，并将其推送到操作数栈上。
   - `putstatic`: 将一个值存储到静态字段中。
   - `arraylength`: 获取数组的长度，并将其推送到操作数栈上。
   - `aload`: 从局部变量表加载一个引用类型的变量到操作栈上。

****

### 操作数栈管理指令

****

1. 出栈指令（Pop Instructions）：
   - `pop`: 将操作数栈的栈顶元素出栈。
   - `pop2`: 将操作数栈的栈顶的一个或两个元素出栈。

2. 复制指令（Duplicate Instructions）：
   - `dup`: 复制操作数栈的栈顶元素，并将复制值重新压入栈顶。
   - `dup2`: 复制操作数栈的栈顶的一个或两个元素，并将复制值或双份的复制值重新压入栈顶。
   - `dup_x1`: 复制操作数栈的栈顶元素，并将复制值插入栈顶下面的一个元素之前。
   - `dup2_x1`: 复制操作数栈的栈顶的一个或两个元素，并将复制值或双份的复制值插入栈顶下面的一个元素之前。
   - `dup_x2`: 复制操作数栈的栈顶元素，并将复制值插入栈顶下面的两个元素之前。
   - `dup2_x2`: 复制操作数栈的栈顶的一个或两个元素，并将复制值或双份的复制值插入栈顶下面的两个元素之前。

3. 交换指令（Swap Instruction）：
   - `swap`: 将操作数栈最顶端的两个数值互换。

****

### 控制转移指令

****

1. 条件分支指令：
   - `ifeq`: 如果栈顶值等于0，则跳转到指定位置。
   - `iflt`: 如果栈顶值小于0，则跳转到指定位置。
   - `ifle`: 如果栈顶值小于等于0，则跳转到指定位置。
   - `ifne`: 如果栈顶值不等于0，则跳转到指定位置。
   - `ifgt`: 如果栈顶值大于0，则跳转到指定位置。
   - `ifge`: 如果栈顶值大于等于0，则跳转到指定位置。
   - `ifnull`: 如果栈顶值为null，则跳转到指定位置。
   - `ifnonnull`: 如果栈顶值不为null，则跳转到指定位置。
   - `if_icmpeq`: 如果栈顶两个int值相等，则跳转到指定位置。
   - `if_icmpne`: 如果栈顶两个int值不相等，则跳转到指定位置。
   - `if_icmplt`: 如果栈顶第二个int值小于栈顶第一个int值，则跳转到指定位置。
   - `if_icmpgt`: 如果栈顶第二个int值大于栈顶第一个int值，则跳转到指定位置。
   - `if_icmple`: 如果栈顶第二个int值小于等于栈顶第一个int值，则跳转到指定位置。
   - `if_icmpge`: 如果栈顶第二个int值大于等于栈顶第一个int值，则跳转到指定位置。
   - `if_acmpeq`: 如果栈顶两个引用值相等，则跳转到指定位置。
   - `if_acmpne`: 如果栈顶两个引用值不相等，则跳转到指定位置。

2. 复合条件分支指令：
   - `tableswitch`: 根据一个索引值进行跳转，通过索引值在一张表中查找跳转位置。
   - `lookupswitch`: 根据一个键值进行跳转，通过键值在一张表中查找跳转位置。

3. 无条件分支指令：
   - `goto`: 无条件跳转到指定位置。
   - `goto_w`: 无条件跳转到指定位置（扩展索引）。
   - `jsr`: 跳转到指定位置，并将返回地址压入栈顶。
   - `jsr_w`: 跳转到指定位置（扩展索引），并将返回地址压入栈顶。
   - `ret`: 返回到指定的局部变量索引位置。

****

## 方法调用和返回指令（重点）

****

### 指令概述

****

针对调用不同类型的方法，字节码指令集里设计了不同的指令：

1. `invokestatic`：用于调用静态方法。可以在类加载时将符号引用解析为直接引用。

2. `invokespecial`：用于调用实例构造器 `<init>()` 方法、私有方法和父类中的方法。也可以在类加载时将符号引用解析为直接引用。

3. `invokevirtual`：用于调用所有的虚方法。根据对象的实际类型进行分派（虚方法分派）。

4. `invokeinterface`：用于调用接口方法，会在运行时确定实现该接口的对象，并选择适合的方法进行调用。

5. `invokedynamic`：先在运行时动态解析出调用点限定符所引用的方法，然后再执行该方法。分派逻辑由用户设定的引导方法决定。

这些调用指令可以根据对象的类型和方法的特性进行不同的分派和调用。

其中，`invokestatic` 和 `invokespecial` 指令可以调用非虚方法，包括静态方法、私有方法、实例构造器和父类方法。而 `invokevirtual` 和 `invokeinterface` 指令用于调用虚方法，根据对象的实际类型进行分派。

此外，还有方法返回指令，根据返回值的类型进行区分，包括 `ireturn`、`lreturn`、`freturn`、`dreturn` 和 `areturn`，以及用于声明 `void` 方法和类初始化方法的 `return` 指令。

****







