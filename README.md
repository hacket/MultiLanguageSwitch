# MultiLanguageSwitch

Android多语言切换

## 特性
1. 支持多语言切换
2. Android7.0+WebView加载会重置语言设置
3. 屏幕旋转导致切换失败

## 使用
1. Application onCreate初始化
```kotlin
class BaseApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        MultiLangUtils.init(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        MultiLangUtils.applyLanguage(
            this,
            MultiLangUtils.getUserSettingLocale(this),
            saveConfig = true
        )
        super.onConfigurationChanged(newConfig)
    }

}
```
2. 切换语言
```kotlin
// MultiLangUtils
fun applyLanguage(context: Context, newUserLocale: Locale, activityClassName: String? = null, saveConfig: Boolean = false)
```
- context 上下文
- newUserLocale 要切换的语言的Locale
- activityClassName 切换语言后重启到目标Activity
- saveConfig 是否保存上一次切换语言

## 多语言切换注意

### ApplicationContext/Activity/Resources.getSystem()区别

1. Application/Activity的Locale是分开的要分别设置Locale；Resources.getSystem()是跟随系统语言的
2. 某些手机中，弹出Toast时如果使用的是getApplicationContext()，弹出的语言是系统默认的语言，所以最好都传 Activity 的 Context

### 全局Context/全局Resource的引用（单例/枚举）
```java
public class ResUtils {
    private static Resources res = GlobalContext.getAppContext().getResources();
    public static String getStr(@StringRes int resID) {
        return res.getString(resID);
    }
}
```
工具类中全局缓存了Resource了，那么在不杀进程的重启切换这部分语言资源是切换不过来的。

1. 单例
```kotlin
// 单例
object ObjTest {
    val name: String = ResUtils.getStr(R.string.title_tab_recommend)
    val name1: String =
        GlobalContext.getAppContext().resources.getString(R.string.title_tab_recommend)
}
```

2. 枚举
```kotlin
// 枚举
enum class EnumTest(val s: String) {
    ONE(ResUtils.getStr(R.string.title_tab_recommend));
}
```

3. 全局引用Resource
```
public class ResUtils {
    private static Resources res = GlobalContext.getApplication().getResources();
}
```

### 部分手机需要给Local设置语言还有国家才成效，所以最好都设置国家或地区。


### 失效-WebView加载会重置语言设置(Android7.0及+)

你的 app 加载了 WebView 你会发现语言又变回了系统默认的默认语言
> 在 Android 7 之前WebView的渲染是通过Android System webView来实现的。但是在Android7之后WebView会被作为一个应用程序的方式服务于各个三方APP。由于WebView这里是作为一个单独的应用程序，所以他不会被绑定到你自己APP设置的Local上。不仅如此，WebView还会把语言变成设备的Local设置。然后相应的资源文件也会被变成设备语言下的资源文件这样就导致了只要打开了含有WebView的页面，应用内语言设置就失效的问题。

* 解决1 切换语言前WebView.destroy()
```java
//处理Android7（N）WebView 导致应用内语言失效的问题
public static void destoryWebView(Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        new WebView(context).destroy();
    }
}
// 切换语言
Resources resources = context.getResources();
DisplayMetrics dm = resources.getDisplayMetrics();
Configuration config = resources.getConfiguration();
config.locale = getLocaleByType(type);
LogUtils.logd("setLocale: " + config.locale.toString());
resources.updateConfiguration(config, dm);
```

* 解决2：在 WebView 加载后还需要设置一遍语言。(App中WebView是单例的不能销毁的)
> 在 app 启动时就加载一次 WebView ，然后在设置语言，只要WebView第一次加载后修改了语言，后面再加载便不会重置为系统语言。
```java
public class AppApplication extends CommonApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Looper.myQueue().addIdleHandler(() -> {
            LogUtils.i("webView preload.");
            WebViewPreLoader.getInstance().preLoad(getApplicationContext());
            MultiLangUtils.applyLanguage(getApplicationContext(), MultiLangUtils.getUserSettingLocale(getApplicationContext()), null);
            return false;
        });
    }
}
```

* 注意：如果是调用的addIdleHandler，时机不明确，容易导致Locale被重置。需要把调用时机放到明确的时机或者在onActivityResume重新设置下用户保存的语言Locale
```
Looper.myQueue().addIdleHandler(() -> {
    WebViewPreLoader.getInstance().preLoad(getApplicationContext());
    return false;
});
```


### 失效-切换系统语言/横竖屏切换（屏幕旋转）
1. 系统切换语言，会把Activity/ApplicationContext的Locale更改
2. 系统切换语言会走Application的`onConfigurationChanged`，所以需要在这个方法中再设置一遍语言
```kotlin
class BaseApplication : Application() {
    @Override
    public void onConfigurationChanged(@NotNull Configuration newConfig) {
        MultiLangUtils.applyLanguage(this, MultiLangUtils.getUserSettingLocale(this), null);
        super.onConfigurationChanged(newConfig);
    }
}
```

### 失效-使用微信开源的热修复框架Tinker，打了包含资源的补丁之后会导致多语言失效
如果打了包含资源string文件的补丁之后，会导致多语言失效，本来选的繁体变成了简体语言，同时无论你怎么切换语言，都没有生效。这属于Tinker的bug，已经有人在Tinker的github主页上反馈了，但是这个issue 任然没有关闭：`https://github.com/Tencent/tinker/issues/302`


### Toolbar 或者 ActionBar 的title切换语言不起作用

默认 title 是从 AndroidManifest.xml 中 Activity 的 label 标签里读取的，我们在代码里手动设置一下 title即可

主题用的是：
```
Theme.AppCompat.Light.DarkActionBar
```

手动设置
```java
//Toolbar
toolbar.setTitle(R.string.app_name);
//ActionBar
actionBar.setTitle(R.string.title_activity_settings);
```

### Activity和V7#AppcompatActivity的返回Locale区别
只有`values-zh`和`values-en`资源，语言设置：`“中文简体 → 日语 → 英语”`

1. 用Activity
```
LocaleList.getDefault()        : zh_CN_#Hans,ja_JP,en_US,
Configuration.getLocales()     : en_US,zh_CN_#Hans,ja_JP,
LocaleList.getAdjustedDefault(): en_US,zh_CN_#Hans,ja_JP,
```
2. 当项目引用了AppCompat-v7包后（即便你的所有Activity继承的仍然只是原生的Activity，而非AppCompatActivity）
```
LocaleList.getDefault()        : zh_CN_#Hans,ja_JP,en_US,
Configuration.getLocales()     : zh_CN_#Hans,ja_JP,en_US,
LocaleList.getAdjustedDefault(): zh_CN_#Hans,ja_JP,en_US,
```
> 返回的都是系统实际的语言列表

大概google在这个包里做了处理，屏蔽了系统根据应用提供的资源调整语言列表的功能，相当于让一切回到7.0以前的版本。


## License

    Copyright 2020 hacket

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.