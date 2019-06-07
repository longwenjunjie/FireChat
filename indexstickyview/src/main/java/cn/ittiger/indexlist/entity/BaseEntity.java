package cn.ittiger.indexlist.entity;

/**
 * 你想要展示的数据实体基类
 *
 */
public interface BaseEntity {
    /**
     * 要索引的字段数据信息，例如联系人中对姓名进行索引，则此处返回姓名字段值
     * @return
     */
    String getIndexField();
}
