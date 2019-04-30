package com.libra.frame.api.data

import java.io.Serializable

/**
 * @author Libra
 * @since 2019/4/17
 */
class Comment:Serializable {
    /**
     * 编号
     */
    var id: Long? = null
    /**
     * 诗文id
     */
    var poetryId: Int? = null
    /**
     * 诗文标题
     */
    var poetryTitle: String? = null
    /**
     * 内容
     */
    var content: String? = null
    /**
     * 用户id,名字,头像
     */
    var fromUid: Int? = null
    var fromUserName: String? = null
    var fromUserAvatar: String? = null
    /**
     * 点赞数
     */
    var likeNum: Int? = null
    /**
     * 回复数
     */
    var replyNum: Int? = null
    /**
     * 状态 0:审核; 1:通过; -1:删除
     */
    var status: Int? = null
    /**
     * 评论时间
     */
    var createTime: String? = null
}
