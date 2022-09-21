package com.foundation.widget.utils.ext

import android.content.Intent
import android.net.Uri

/**
 * @author LJF
 * @Email lijiefeng@51xpx.com
 * @Time  2021/10/29 0029
 * @Description Intent扩展
 */

/**
 * 打电话
 */
fun Intent.toDial(phone: String): Intent {
    action = Intent.ACTION_DIAL
    data = Uri.parse("tel:${phone}")
    return this
}