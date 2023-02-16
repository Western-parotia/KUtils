package com.foundation.widget.utils.diff

/**
 * 主要用于item数据变更时有更好的动画效果
 *
 * 举例：第4条的图片变化了，没有diff id时使用equals判断会产生删除第4条、新增第4条的动画效果，如果有个id就可以实现第4条自更新的动画效果。
 *      第5条刷新到了第1条并且里面的图片也变了，则会删除第5条、新增第1条，加上id可以实现第5条移动到第1条。
 */
interface IDiffId {
    val diffId: String
}