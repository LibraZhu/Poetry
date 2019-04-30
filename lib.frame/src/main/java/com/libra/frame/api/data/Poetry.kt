package com.libra.frame.api.data

import java.io.Serializable

class Poetry : Serializable {

    /**
     * 诗文id
     */
    var id: Int = 0
    /**
     * 诗文标题
     */
    var title: String? = null
    /**
     * 诗文内容
     */
    var content: String? = null
    /**
     * 作者
     */
    var author: String? = null
    /**
     * 朝代
     */
    var dynasty: String? = null
    /**
     * 标签类型
     */
    var tag: String? = null
    /**
     * 点赞数
     */
    var likeNum: Int? = null

    override fun toString(): String {
        return "Poetry{" +
                ", id=" + id +
                ", title=" + title +
                ", content=" + content +
                ", author=" + author +
                ", dynasty=" + dynasty +
                ", tag=" + tag +
                ", likeNum=" + likeNum +
                "}"
    }
}
