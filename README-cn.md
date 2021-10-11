## helloworld-blockchain
helloworld-blockchain是一个helloworld级别的数字货币系统。  
helloworld-blockchain是一个helloworld级别的区块链公链系统。  
helloworld-blockchain设计目标是麻雀，秉承谚语麻雀虽小五脏俱全的理念，她小在代码精炼，小在不保留非必要区块链功能，小在零依赖、零配置的部署，她的全，在于区块链技术的宽度与深度的实践，在于丰富的文档。  
helloworld-blockchain架构清晰，文档丰富，精简易学，代码可读性高，代码注释清晰，为初学者学习研究实践区块链技术而生。
通过这个项目，学习者将深入理解区块链，从技术细节到系统架构都将带给学习者超乎想象的收益。
假设学习者将helloworld-blockchian不到万行的代码看完，学习者将会学会如下知识：哈希是什么?UTXO是什么?默克尔树是什么?挖矿是什么?钱包是什么?区块链是如何实现动态调整挖矿难度?区块链的不可篡改是如何实现的?数字货币的匿名性是如何实现的?如何自己发行一种数字货币?智能合约是怎么回事?等等所有区块链领域的根基知识在学习者的面前都将再无任何秘密而言。    
helloworld-blockchain开发调试简单，下载源码，导入idea，无需任何配置，找到启动类文件com.xingkaichun.helloworldblockchain.application.HelloWorldBlockchainApplication，右键运行，即可启动项目，然后打开浏览器，访问项目的前端地址 http://localhost/ ，快乐的调试玩耍吧。



## helloworld-blockchain区块链学习交流群
![helloworld-blockchain区块链交流群](https://user-images.githubusercontent.com/49269996/136790900-d91096ac-860b-4617-aef1-b82a0cb43c21.png)


## 项目意义
初学者实践与探索区块链技术。



## 演示
#### 演示地址
http://119.3.57.171/
#### 演示截图
![项目首页截图](https://z3.ax1x.com/2021/05/22/gLBlFA.png)



## 初识helloworld-blockchain
[部分学习者对区块链一窍不懂，拿到项目无从下手，怎么破局开始](https://github.com/helloworldcoin/helloworld-blockchain-java/issues/30)  
[项目部署与简单使用](https://github.com/helloworldcoin/helloworld-blockchain-java/issues/29)  
[IP拦截](https://github.com/helloworldcoin/helloworld-blockchain-java/issues/48)  
[如何维护自己的集群](https://github.com/helloworldcoin/helloworld-blockchain-java/issues/50)  



## 集成开发环境搭建
#### intellij idea
helloworld-blockchain项目没有任何复杂依赖，将项目导入idea，找到类文件com.xingkaichun.helloworldblockchain.application.HelloWorldBlockchainApplication，右键运行，正常情况下则会启动成功。接下来，打开浏览器，访问项目的前端地址 http://localhost/ ，快乐的调试玩耍吧。



## 打包与发布
#### 项目打包
```  
进入项目目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\helloworld-blockchain-java   
运行项目打包命令   
mvn -DskipTests=true clean package
```
#### 发布项目
```  
进入打包结果目录(请根据本地的实际项目目录运行命令)  
cd C:\Users\xingkaichun\IdeaProjects\helloworld-blockchain-java\helloworld-blockchain-application\target  
运行启动命令  
java -jar helloworld-blockchain-application-1.0-SNAPSHOT.jar  
```



## 架构图
![helloworld-blockchain-java架构图](https://user-images.githubusercontent.com/49269996/131206309-bf32c2de-efd3-4c0e-b075-e465fc53ffeb.png)




## 模块架构
#### helloworld-blockchain-application
启动后，在浏览器输入地址 http://localhost/ 进入区块链系统的前台。他对外提供钱包应用、区块链浏览器应用、节点控制台应用三种应用的功能。
* 区块链浏览器应用：对外提供了查询交易、查询区块、查询地址、查询区块链网络节点等功能。
* 钱包应用：对外提供查询资产、转账交易、新增账户、删除账户、查询账户、构建交易等功能。
* 节点控制台应用：为用户提供管理本地节点的功能，例如增/删/改/查网络节点、激活/停用矿工、激活/停用同步器。
#### helloworld-blockchain-core
该模块是整个区块链系统的核心，它代表着一个单机版区块链系统，它在底层维护着一条区块链的完整数据。设计之初，为了精简，它被设计为不含有网络模块。除了不含有网络模块外，它包含了一个区块链系统应有的所有功能，包含1.区块链账户生成 2.转账 3.提交交易至区块链 4.挖矿 5.新增区块到区块链 6.数据校验：区块验证、交易验证  7.链上区块回滚 8.链上区块查询、交易查询、账户资金查询...... 等等。
#### helloworld-blockchain-netcore
网络版区块链系统实现。底层依赖helloworld-blockchain-core模块。在单机版区块链系统的基础上，新增了网络功能，自动的在整个区块链网络中寻找/发布/同步：节点、区块、交易。
#### helloworld-blockchain-netcore-client
节点交互网络客户端，不同节点仅通过该客户端即可完成节点间的数据交互操作。设计上，两个节点间的数据交互的所有操作都定义在了该客户端之中。原则上，少定义两个节点间的数据交互操作行为，不定义非必要操作行为。
#### helloworld-blockchain-node-dto
该模块用于存放【节点之间数据传输使用的】dto类，不存在任何业务逻辑。该包中的dto类以字段精简【节约节点之间数据的传输流量】、类型简单【方便多种编程语言转换】为设计目标。 
#### helloworld-blockchain-setting
该模块存放全局配置
#### helloworld-blockchain-crypto
该模块封装了加密相关的工具。1.数字货币账户工具：①账户(私钥、公钥、公钥哈希、地址)生成工具。②私钥、公钥、公钥哈希、地址的相互转换工具。2.数字签名与签名验证工具。3.消息摘要工具：SHA-256、RipeMD160。4.字节编码工具：base58、hex。5.数据结构工具：默克尔树等。
#### helloworld-blockchain-util
该模块存放封装的开发工具类



## 技术文档
[必读--白皮书](https://www.zhihu.com/question/51047975/answer/1778438713)  
[钱包:公钥、私钥、地址](https://zhuanlan.zhihu.com/p/38196092)  
[UTXO未花费交易输出](https://www.zhihu.com/question/59913301/answer/1779203932)  
[交易模型](https://github.com/helloworldcoin/helloworld-blockchain-java/issues/41)  
[脚本](https://zhuanlan.zhihu.com/p/353582574)  
[双花攻击](https://zhuanlan.zhihu.com/p/258952892)  
[默克尔树](https://zhuanlan.zhihu.com/p/40142647)  
[数据结构](https://zhuanlan.zhihu.com/p/332265582)    
[哈希运算](https://zhuanlan.zhihu.com/p/354442546)  
[双哈希运算](https://zhuanlan.zhihu.com/p/353575311)  
[区块哈希](https://zhuanlan.zhihu.com/p/353570191)  
[交易哈希](https://zhuanlan.zhihu.com/p/353574892)  
[区块大小、交易大小](https://zhuanlan.zhihu.com/p/336827577)  