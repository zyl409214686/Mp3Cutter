# Mp3Cutter，个人原创的mp3剪切小工具。

> 适用于剪切制作手机铃声以及闹铃等用途，功能简单易用、界面风格简洁，采用MaterialDesign设计规范。



- 首页使用了CoordinatorLayout+AppBarLayout+DrawerLayout+NavigationView的经典MD设计风格。
- 项目整体采用了MVP+databinding+rxjava+dagger框架，数据缓存使用了greendao。
- 音频频谱的绘制主要是通过Visualizer中获取到的波形数据来进行绘制。
- mp3剪切核心功能使用了jaudiotagger jar包根据时间获取到文件中的数据标记位置。




## 使用说明+gif
### Step1. 选择mp3文件
<a href="gif/step1_select_mp3.gif"><img src="gif/step1_select_mp3.gif" width="33%"/></a>
### Step2. 通过滑块选择剪切范围然后点击剪切按钮
<a href="gif/step2_mp3cutter.gif"><img src="gif/step2_mp3cutter.gif" width="33%"/></a>
### 操作技巧：使用**切换**按钮切换当前播放的滑块
![](screenshot/screenshot_doswitch.png)

## mp3剪切实现思路：
1. 首先通过自定义范围seekbar获取到两个时间点**startTime**, **endTime**
2. 通过**jaudiotagger**库获取mp3音乐部分**首帧字节位置(firstFramePosition)**，并获取到mp3文件的**比特率**（kbps）也就是每秒千字节
3. 根据**startTime**和步骤2中获取到的mp3**比特率**转换为**每毫秒的字节数据（startBpm）**，
  ** startBpm = kbps * 1024L / 8L / 1000L**
4. 根据首帧字节位置和步骤3中算出来的startTime 的startBpm算出需要截取的mp3首位置对应的字节位置（startTimeIndex）：
  **startTimePostion = firstFramePosition +startBpm**
  同理算出第二个字节位置**endTimePosition**
5. 有了首个字节位置**startTimePostion**和第二个字节位置**endTimePosition**然后就是文件操作啦~~~
  此部分逻辑可以看`com.zyl.mp3cutter.mp3cut.logic.Mp3CutLogic类`



## Blog
https://juejin.im/post/5a324f3f5188253da72e7956

## 感谢
* [jaudiotagger](http://www.jthink.net/jaudiotagger/)
* [RXJava](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [greendao](https://github.com/greenrobot/greenDAO)
* [StatusBarUtil](https://github.com/laobie/StatusBarUtil)
* [Dagger2](https://github.com/google/dagger)
* [PermissionsDispatcher](https://github.com/permissions-dispatcher/PermissionsDispatcher)
* [logger](https://github.com/orhanobut/logger)
* [AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView)
* [baseAdapter](https://github.com/hongyangAndroid/baseAdapter)
* [CustomRangeSeekBar](https://github.com/zyl409214686/CustomRangeSeekBar)

## TODO
- 增加主题颜色设置
- 增加频谱的样式设置
- 增加闹铃的设置
- 增加对音乐文件的处理如分享等
- 国际化支持
- 增加滑块当前状态绘制

## 关于我

一个来自于北京的android开发者

## 联系我
 - Email: zyl409214686@163.com
 - Blog : [blog](http://www.zouyulong.com)
 - GitHub: [zyl409214686](https://github.com/zyl409214686)

##  License

Mp3Cutter is under CC BY-NC-SA license.