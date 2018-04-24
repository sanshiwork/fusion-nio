# fusion-nio

> JDK网络编程示例

1. BIO:定义1个Boss线程处理连接，N个Worker线程处理IO

2. NIO:定义1个Boss线程处理连接，1个Worker线程处理IO

3. AIO:不需要自定义线程，回调函数所在线程由底层提供  

``` bash
可以使用telent进行测试，如
telnet localhost 8000 -> 输入内容并观察控制台输出