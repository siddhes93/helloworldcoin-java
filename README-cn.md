[English](https://github.com/helloworldcoin/helloworldcoin-java/blob/master/README-cn.md) / 中文

<h1><p align="center">helloworldcoin</p></h1>  

helloworldcoin是一个helloworld级别的数字货币系统。   
   
如果你懂java , [helloworldcoin-java](https://github.com/helloworldcoin/helloworldcoin-java) ；
如果你懂cpp , [helloworldcoin-cpp](https://github.com/helloworldcoin/helloworldcoin-cpp) ；
如果你懂go , [helloworldcoin-go](https://github.com/helloworldcoin/helloworldcoin-go) 。
<br/><br/><br/>



## 演示
#### 演示地址
[english](http://119.3.57.171/en/index.html) or [中文](http://119.3.57.171/cn/index.html) or [language list](http://119.3.57.171)。  
#### 演示截图
![项目首页截图](https://z3.ax1x.com/2021/05/22/gLBlFA.png)



## 集成开发环境搭建
#### intellij idea
``` 
1. 将项目导入 idea
2. 找到 '主类' com.helloworldcoin.application.HelloworldcoinApplication
3. 运行 '主类'
```
#### eclipse
``` 
1. 将项目导入 eclipse
2. 找到 '主类' com.helloworldcoin.application.HelloworldcoinApplication
3. 运行 '主类'
```



## 打包与发布
#### 项目打包
```
mvn -DskipTests=true clean package
```
#### 发布项目
```
java -jar helloworldcoin-application-1.0.0.jar
```



## 架构图
![helloworldcoin-java架构图](https://user-images.githubusercontent.com/49269996/131206309-bf32c2de-efd3-4c0e-b075-e465fc53ffeb.png)



## 模块架构
#### helloworldcoin-application
启动后，在浏览器输入地址 http://localhost/ 进入区块链系统的前台。他对外提供钱包应用、区块链浏览器应用、节点控制台应用三种应用的功能。
* 区块链浏览器应用：对外提供了查询交易、查询区块、查询地址、查询区块链网络节点等功能。
* 钱包应用：对外提供查询资产、转账交易、新增账户、删除账户、查询账户、构建交易等功能。
* 节点控制台应用：为用户提供管理本地节点的功能，例如增/删/改/查网络节点、激活/停用矿工、激活/停用同步器。
#### helloworldcoin-core
该模块是整个区块链系统的核心，它代表着一个单机版区块链系统，它在底层维护着一条区块链的完整数据。设计之初，为了精简，它被设计为不含有网络模块。除了不含有网络模块外，它包含了一个区块链系统应有的所有功能，包含1.区块链账户生成 2.转账 3.提交交易至区块链 4.挖矿 5.新增区块到区块链 6.数据校验：区块验证、交易验证  7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
#### helloworldcoin-netcore
网络版区块链系统实现。底层依赖helloworldcoin-core模块。在单机版区块链系统的基础上，新增了网络功能，自动的在整个区块链网络中寻找/发布/同步：节点、区块、交易。
#### helloworldcoin-netcore-client
节点交互网络客户端，不同节点仅通过该客户端即可完成节点间的数据交互操作。设计上，两个节点间的数据交互的所有操作都定义在了该客户端之中。原则上，少定义两个节点间的数据交互操作行为，不定义非必要操作行为。
#### helloworldcoin-node-dto
该模块用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 
#### helloworldcoin-setting
该模块存放全局配置。
#### helloworldcoin-crypto
该模块封装了加密相关的工具。1.数字货币账户工具：①账户(私钥、公钥、公钥哈希、地址)生成工具。②私钥、公钥、公钥哈希、地址的相互转换工具。2.数字签名与签名验证工具。3.消息摘要工具：SHA-256、RipeMD160。4.字节编码工具：base58、hex。5.数据结构工具：默克尔树等。
#### helloworldcoin-util
该模块存放工具类。