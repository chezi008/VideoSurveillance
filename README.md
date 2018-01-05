# VideoSurveillance
视频监控，四宫格视图。双击放大，视频无缝衔接
## Android 使用MediaCodec实现视频的无缝切换

### 一、功能说明：
#### 在**不同控件之间**实现视频的无缝切换。不会黑屏，也不需要重新创建解码器。

百度上面很多视频播放都是利用MediaPlayer+显示视图（SurfaceView、TextureView）进行本地或者网络视频的播放。那么利用MediaCodec对视频流进行硬解码的小伙伴该如何在不同的控件之间无缝切换呢？是不是TextureView的生命周期很难控制？

### 二、实现方案
![流程图.png](http://upload-images.jianshu.io/upload_images/419652-a35d4a7b26b24b55.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 三、实现效果
![效果图.gif](https://github.com/chezi008/VideoSurveillance/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE2.gif)

### 四、TextureView

#### 4.1 与SurfaceTexture的关系
TextureView与SurfaceTexture构成了组合关系，可见SurfaceTexture的确是由TextureView给『包办』了。在程序世界中，一个对象被『包办』无非是指：

（1）这个对象什么时候被创建？

（2）这个对象如何被创建？

（3）这个对象的产生带来了哪些变化，或者说程序自从有了它有哪些不同？​

（4）这个对象什么时候被销毁？​

之所以对​SurfaceTexture这个对象要大动笔墨，因为它是整个显示框架的『连接者』。

#### 4.2 生命周期控制
1. 切换至后台的时候会调用onSurfaceTextureDestroyed，从后台切换回来会调用onSurfaceTextureAvailable。
2. TextureView的ViewGroup remove TextureView的时候会调用onSurfaceTextureDestroyed方法。相同，TextureView的ViewGroup add TextureView的时候会调用onSurfaceTextureAvailable。**这些都是建立在视图可见的基础上**,如果视图不可见，add也不会调用onSurfaceTextureAvailable方法，remove也不会调用onSurfaceTextureDestroyed方法。
3. 当TextureView设置为Gone的时候，并不会调用onSurfaceTextureDestroyed方法法。

#### 4.3设置SurfaceView
是不是遇到过在播放视频返回后台再回来，发现TextureView显示视图是一片黑色？监听TextureView的生命周期你会发现，返回后台是调用了销毁方法的。那你就会问销毁之后岂不是有需要重新创建？重新创建会引来更多的问题，解码去也需要重新初始化。所以我们只能另寻他法，下面方法就是无缝切换的核心部分。

```
	@Override
	public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
	  Log.d(TAG, "onSurfaceTextureDestroyed: ");
	  mSurfaceTexture = surface;
	  return false;
	}
```
在销毁方法中我们注意，有一个返回参数。官方的解释如下
```
Invoked when the specified SurfaceTexture is about to be destroyed. If returns true, no rendering should happen inside the surface texture after this method is invoked. If returns false, the client needs to call release(). Most applications should return true.

大致的意思是如果返回ture，SurfaceTexture会自动销毁，如果返回false，SurfaceTexture不会销毁，需要用户手动调用release()进行销毁。
```
现在恍然大悟了吧，我们在销毁的时候返回false,并保存SurfaceTexture对象，然后从后台返回界面的时候在onSurfaceTextureAvailable()方法中，调用setSurfaceTexture(mSurfaceTexture)方法，这样就会恢复之前的画面了。
```
@Override
public void onSurfaceTextureAvailable(SurfaceTexture surface, 
int width, int height) {
    Log.d(TAG, "onSurfaceTextureAvailable: ");
    if(mSurfaceTexture!=null){
       mTextureView.setSurfaceTexture(mSurfaceTexture);
    }
}
```

### 五、可拖拽效果
使用ItemTouchHelper轻松实现RecyclerView拖拽排序和滑动删除
[传送门](http://chuansong.me/n/400690551872)
