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
  * [11.1至11.15工作总结](#11.1至11.15工作总结)
  
### 开题构想
#### 题目

Intelligent Arduino Car based on TensorFlow SSD-Mobilenet model for Android devices

#### 阐述
在Android端运行[SSD-Mobilenet model](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android "TensorFlow example for Android")	物体定位追踪模型，使用Android手机提供的高清摄像头进行实时对象识别、定位与追踪，检测目标在当前画面中的位置，并将实时位置信息转化为控制信号传输到与手机连接的Arduino控制模块，该模块收到信号后可以控制小车的方向移动，最终的目的是使小车逐渐向目标物靠近，并且可以随着目标物的移动来修正方向，达到目标去哪儿，小车哪儿的效果。

#### 关键技术
* TensorFlow 提供的物体识别模型—SSD-Mobilenet model
* Arduino控制模块的搭建与使用
* SSD-Mobilenet model模型到Android端的移植

#### 相关链接
* [TensorFlow model for Android](https://github.com/tensorflow/tensorflow/tree/master/tensorflow/examples/android)
* [给Android手机加双Arduino翅膀](https://www.arduino.cn/thread-7217-1-1.html)

#### 项目计划
* 11.1~11.15： 购买组装Arduino，学习Arduino相关知识，做到使用Arduino控制小车

### 项目进展
#### 11.1至11.15工作总结
总结中包括，这两周的工作进展，（如果有的话）阅读资料和项目设计的更改和代码的增加。期待你的进展
