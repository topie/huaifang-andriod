package com.topie.huaifang.http.bean.function

import com.google.gson.annotations.SerializedName

/**
 * Created by arman on 2017/10/2.
 * 调查问卷提交
 */
class HFFunQuestionRequestBody(val infoId: Int, val items: List<Item> = arrayListOf()) {
    /**
     * @param questionId 选项id
     * @param selectOptionId 选中的选项id
     */
    class Item(@SerializedName("itemId") val questionId: Int = 0, @SerializedName("optionId") var selectOptionId: Int = 0)
}