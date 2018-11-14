IntelligentArduinoCar
===========================

****
	
|姓名|邓旺|
|---|---|
|学号|15331061|

****

## 目录
* [开题构想](#开题构想)
  * [题目](#题目)
  * [阐述](#阐述)
  * [关键技术](#关键技术)
  * [相关链接](#相关链接)
  * [项目计划](#项目计划)
* [项目进展](#项目进展)
  * [第一阶段](#第一阶段)
  
## 开题构想
### 题目

Intelligent Arduino Car based on TensorFlow SSD-Mobilenet model for Android devices

### 阐述
在Android端运行[SSD-Mobilenet model](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android "TensorFlow example for Android")	物体定位追踪模型，使用Android手机提供的高清摄像头进行实时对象识别、定位与追踪，检测目标在当前画面中的位置，并将实时位置信息转化为控制信号传输到与手机连接的Arduino控制模块，该模块收到信号后可以控制小车的方向移动，最终的目的是使小车逐渐向目标物靠近，并且可以随着目标物的移动来修正方向，达到目标去哪儿，小车哪儿的效果。

### 关键技术
* TensorFlow 提供的物体识别模型—SSD-Mobilenet model
* Arduino控制模块的搭建与使用
* SSD-Mobilenet model模型到Android端的移植

### 相关链接
* [TensorFlow model for Android](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android)
* [给Android手机加双Arduino翅膀](https://www.arduino.cn/thread-7217-1-1.html)

### 项目计划
* 11.1~11.15： 购买组装Arduino，学习Arduino相关知识，做到使用Arduino控制小车

## 项目进展
### 第一阶段
```
时间：2018.11.1 ～ 2018.11.15
``` 
根据之前的项目计划，这个阶段的工作主要是Arduino环境的搭建，包括两个内容：
* 通过手机连接控制Arduino控制模块，实现将手机的信号转化为Arduino可是别的信号
* 使用Arduino模块控制小车的移动，包括前进、后退以及左右移动

对于第二个内容主要是便硬件层面的，在购买的小车的时候，卖家推荐了`Anduino UNO R3 开发板`和`2路电机&16路舵机驱动板`的组合方案，卖家提前把控制程序烧到了两个电路板中了。这样`2路电机&16路舵机驱动板`只需要接收到`Anduino UNO R3 开发板`传输过来的信号，然后通过连接就可以直接控制小车的移动，这样就不需要太关心硬件层面的东西。

对于第一个功能，之前看[给Android手机加双Arduino翅膀](https://www.arduino.cn/thread-7217-1-1.html)给出的方案的时候是使用USB连接手机和Arduino控制模块的，但是购买设备的时候只有Wifi和蓝牙两种连接方式，后来我买了使用wifi连接的设备，所以只有想其他办法。后来使用socket通信的方式进行连接，`Anduino UNO R3 开发板`加上一个wifi模块，这样就能扩散出一个特定Ip地址的wifi信号，然后手机连接上这个wifi信号就能够将信号传输到Arduino控制模块，实现与手机的互联。


