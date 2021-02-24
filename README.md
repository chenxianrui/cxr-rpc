## cxr-rpc
[cxr-rpc](https://github.com/chenxianrui/cxr-rpc) 是一款基于 Netty+Kyro+Zookeeper 实现的简易版 RPC 框架。


### RPC是什么
RPC（Remote Procedure Call） 是一种远程过程调用的协议，其让进程调用远程的服务就像是本地的服务一样，广泛应用在大规模分布式应用中，作用是有助于系统的垂直拆分，使系统更易拓展。
举个例子：在一场考试中，A不会的题，但是B会，因此，A需要让B传小抄。你没有的东西别人有，你需要向别人要，这就是 RPC。
现在常见的 RPC 框架有：RMI，Hession，Dubbo，Grpc。
### 项目原理
网络通信使用的是 Netty，生产者端向注册中心 Zookeeper 注册 IP端口，消费者端通过注
册中心获取服务提供端的 IP与端口号，通过 Netty 与消费者端建立连接。消费者将请求的接口名，
方法，数据等封装进 RpcRequeset 对象中，经过 Kyro 的序列化成二进制内容发送到生产者端，
生产者再将接收到的数据经过反序列化成对象后寻找相应的接口与方法进行调用，最后再发送响应成功消息
给消费端，告诉他调用方法与接口成功。监控模块不断监听 zookeeper，利用 websocket 在前台实时的展示数据。
### 项目模块结构
└─cxr-rcp<br/>
&nbsp;&nbsp;&nbsp;├─crpc-admin（监控前台vue页面模块）<br/>
&nbsp;&nbsp;&nbsp;├─crpc-common（公共类模块）<br/>
&nbsp;&nbsp;&nbsp;├─crpc-example-client（测试客户端模块）<br/>
&nbsp;&nbsp;&nbsp;├─crpc-example-server（测试服务端模块）<br/>
&nbsp;&nbsp;&nbsp;├─crpc-monitor（监控模块）<br/>
&nbsp;&nbsp;&nbsp;├─crpc-registry（注册中心模块）<br/>
&nbsp;&nbsp;&nbsp;└─crpc-remoting（核心模块）<br/>

### 项目运行截图
服务监控：<br/>
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021022416123761.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMzMTE3Ng==,size_16,color_FFFFFF,t_70#pic_center)
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210224161207791.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMzMTE3Ng==,size_16,color_FFFFFF,t_70#pic_center)
提供者：<br/>
![在这里插入图片描述](https://img-blog.csdnimg.cn/2021022416114326.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMzMTE3Ng==,size_16,color_FFFFFF,t_70#pic_center)
消费者：<br/>
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210224161152898.jpg?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3dlaXhpbl80NDMzMTE3Ng==,size_16,color_FFFFFF,t_70#pic_center)
    注：图片水印来自我的csdn博客
