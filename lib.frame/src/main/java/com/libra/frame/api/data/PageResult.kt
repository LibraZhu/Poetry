package com.libra.frame.api.data

import java.io.Serializable

/**
 * @author Libra
 * @date 2018/11/28
 * @description 封装分页结果集
 */
class PageResult<T> : Serializable {
    var page: Int? = 1// 要查找第几页
    var pageSize: Int? = 0// 每页显示多少条
    var totalPage: Int? = 0// 总页数
    var totalRows: Long? = 0L// 总记录数
    var rows: ArrayList<T>? = null// 结果集
}
