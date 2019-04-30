package com.libra.frame.api.data;

import java.io.Serializable;

/**
 * @author libra
 * @since 2018-12-27
 */
public class Favorite implements Serializable {

    public static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    public long id;
    /**
     * 诗文id
     */
    public int poetryId;
    public String poetryTitle;
    public String poetryAuthor;
    public String poetryContent;
    /**
     * 用户id
     */
    public int uid;
    /**
     * 收藏时间
     */
    public String createTime;
    /**
     * 状态 0:取消收藏; 1:收藏
     */
    public int status;
}
