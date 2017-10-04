package me.liuqingwen.android.projectfilereader

import android.view.View

/**
 * Created by Qingwen on 2017-2017-10-3, project: ProjectFileReader.
 *
 * @Author: Qingwen
 * @DateTime: 2017-10-3
 * @Package: me.liuqingwen.android.projectfilereader in project: ProjectFileReader
 *
 * Notice: If you are using this class or file, check it and do some modification.
 */

inline val View.isVisible
    get() = this.visibility == View.VISIBLE
