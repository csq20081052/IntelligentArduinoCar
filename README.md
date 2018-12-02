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
    * [功能实现](#功能实现)
    * [相关代码](#相关代码)
  * [第二阶段](#第二阶段)
  	* [第一部分](#第一部分)（当前）
  
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
整个项目主要分成三个阶段：

* 第一阶段（11.1~11.15）： 购买组装Arduino，学习Arduino相关知识，做到使用Arduino控制小车；
这一阶段主要是Arduino的学习，从购买组装Arduino小车到实现用android程序控制小车进行基本的前后左右移动，实现遥控的功能。

* 第二阶段（11.16~12.15）: tensorflow对象识别模型学习，根据项目需要进行个人定制；
这一阶段集中于tensorflow的学习，首先是tensorflow编译环境的搭建，主然后是对象识别模型的学习，根据给出的android demo,在理解其原理的基础上进行适当的改造，以达到项目的需要，预想的效果就是能够识别特定的物体，能够检测物体在屏幕中的相对位置

* 第三阶段（12.15~1.15）: 整合优化
这一阶段需要对前面两个阶段的产物进行一个整合，以达到最终的目的。另外，就是项目优化，如Arduino怎么更灵活地控制小车，tensorflow对象识别模型怎么更精确地识别物体等等，以达到更好的效果

## 项目进展
### 第一阶段
```
时间：2018.11.1 ～ 2018.11.15
``` 
根据之前的项目计划，这个阶段的工作主要是Arduino环境的搭建，包括两个内容：
* 通过手机连接控制Arduino控制模块，实现将手机的信号转化为Arduino可是别的信号
* 使用Arduino模块控制小车的移动，包括前进、后退以及左右移动

#### 功能实现
对于第二个内容主要是便硬件层面的，在购买的小车的时候，卖家推荐了`Anduino UNO R3 开发板`和`2路电机&16路舵机驱动板`的组合方案，卖家提前把控制程序烧到了两个电路板中了。这样`2路电机&16路舵机驱动板`只需要接收到`Anduino UNO R3 开发板`传输过来的信号，然后通过连接就可以直接控制小车的移动，这样就不需要太关心硬件层面的东西。

对于第一个功能，之前看[给Android手机加双Arduino翅膀](https://www.arduino.cn/thread-7217-1-1.html)给出的方案的时候是使用USB连接手机和Arduino控制模块的，但是购买设备的时候只有Wifi和蓝牙两种连接方式，后来我买了使用wifi连接的设备，所以只有想其他办法。后来使用socket通信的方式进行连接，`Anduino UNO R3 开发板`加上一个wifi模块，这样就能扩散出一个特定Ip地址的wifi信号，然后手机连接上这个wifi信号就能够将信号传输到Arduino控制模块，实现与手机的互联。

#### 相关代码
* [arduino嵌入程序](./anduino)
* [android代码部分](./AndroidSrc)

### 第二阶段
第二阶段计划分成两个部分完成，第一部分主要是tensorflow环境的搭建与demo的编译
#### 第一部分
```
时间：2018.11.16 ～ 2018.12.1
``` 
这一部分的主要是熟悉使用tensorflow进行android开发的流程；

第一步就是尝试编译tensorflow给出的demo中的源码，这里官网提供了两种编译方式，两种方式的目的都是一样的，都是获取对象识别相关模块的依赖。
两种方式我都进行了尝试了，第一种使用android studio编译的方式非常简单，通过`JCenter`的方式添加依赖，需要在Android Studio中安装好`ndk`、`CMake`、`LLDB`这几个东西，然后修改`nativeBuildSystem`的值为'none'就可以直接编译了。
另外一种方式是使用`bazel`编译tensorflow的源码生成apk，整个过程下来，感觉这一种方式相比第一种方法更复杂，耗时特更久，对于本项目的应用场景，第一种方式也更适用。

在这个过程中有一个很收益的收获，那就是学习新的东西、新的技术最好的途径是直接看官方文档，而不是照的网上各种各样的技术博客来学习，一来博客的质量参差不齐，二是随着技术的更新有些博客已经过时了，这样照的博客来就会遇到很多奇怪的问题。最开始，我是照着博客使用bazel编译tensorflow的方式来添加依赖的，中间就遇到很到很多问题，比如：bazel版本的问题、ndk版本的问题，中间花了很多时间，现在看来很不必要，所以以后学习一个新的东西还是直接参考官方文档，避免走冤枉路。



