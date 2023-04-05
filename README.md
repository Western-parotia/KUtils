# 简介
这是一个常用kotlin工具类库，涵盖了app常用开发的kotlin拓展和一些其他工具
# 初始化
```
//初始化传入application
MjUtils.init(application)
//初始化debug状态
MjUtils.setDebug(BuildConfig.DEBUG)
//如果使用了“.dp”拓展（百分比功能），请设置
//如：30.dp就是30在该值的比例
//如是按照375的设计图为基准，则传入375后即可和设计图无缝使用
//如是按照百分比布局为准，则传入100后即可相当于百分之xx
MjUtils.setUiScreenWidth(100)
```
# 功能说明
* GlobalExt：dp相关拓展，如：
```
val d = 10.dp//按照MjUtils.setUiScreenWidth的比例的值
```
* ColorExt：color的int拓展，如：
```
R.color.aaa.toColorInt
```
* HandlerExt：全局handler使用，如：
```
postMain{代码}
postDelayedLifecycle（lifecycle）{代码}
```
* StringExt：常用字符串操作，如：
```
val i = "aaa"toSafeInt(-1)//返回-1
val s = "" emptyThen "空"//返回“空”
"打印".log（"测试标题"）
```
* TextViewExt：TextView常用拓展，如：
```
tv.isTextBold = false
tv.maxLength = 9
//RecyclerView的adapter中
tv.addTextChangedSingleListener{
    //此处不会引起复用问题
}
```
* IUIContext：简化需要Fragment或Activity时的各种判断，如：
```
//原来
class Test {
    private var f: Fragment? = null
    private var a: AppCompatActivity? = null

    constructor(fragment: Fragment) {
        f = fragment
    }

    constructor(activity: AppCompatActivity) {
        a = activity
    }

    fun test() {
        var fm: FragmentManager? = null
        f?.let {
            fm = it.childFragmentManager
        }
        a?.let {
            fm = it.supportFragmentManager
        }
        if (fm == null) {
            return
        }
        //这里才是其他代码
    }
}
//现在
class Test2(private val ui: IUIContext) {
    fun test() {
        val fm = ui.currentFragmentManager
        //其他代码
    }
}
//调用者只需要
Test2(this（Fragment或Activity）.toUIContext()).test()
//如果认为toUIContext浪费了一个对象，可以将自己的BaseActivity/Fragment实现IUIContext（参考Activity/FragmentUIContextWrapper的代码）
```
* UIContextExt：对IUIContext的拓展，如：
```
ui.doOnCreated{
    //created后才执行的代码
}
```
* ViewExt：view常用拓展，如：
```
view.postLifecycle{
    //不会因detach而导致崩溃问题
}
vew.expandTouchArea/expandTouchAreaForScrollingView()//小图标点击区域拓展，小按钮不需要再写比较奇怪的padding逻辑了
```
* LifecycleExt：常用obs功能，如：
```
fragment.lifecycle.addObserver { thisObs, owner, event ->
    if (event == Lifecycle.Event.ON_RESUME) {
        fragment.lifecycle.removeObserver(thisObs)
        println("测试")
    }
}
fragment.lifecycle.doOnNextResumed{
    //下一次resume才会回调
}
fragment.lifecycle.doOnDestroyed{代码}
```
* ISelectedListBean：无需增加字段即可对bean实现单选、多选、反选等效果，示例：
```
class Ttt : ISelectedListBean {
    override var isSelected = false
}
fun main() {
    val list = listOf<Ttt>(Ttt(),Ttt())
    list.selectedPosition = 1//选中1
    val hasSelected = list.hasSelected()//有没有选中的
    val selectedData = list.getSelectedData()//第一个选中的数据
    val allSelectData = list.getAllSelectData()//全部选择的数据
}
```
* 其他拓展：ArrayExt、MapExt、NumberExt、LiveDataExt、AdapterExt、EditTextExt、ViewPagerExt、WebViewExt等
* 其他工具：MjValueAnimator、Base64Utils、MjKeyboardUtils、MjPage、UriSplice、ViewImageSpanUtils等