package ptit.cnpm1.tranxuanthanh.vatpham;

import java.io.Serializable;

/** Một vật phẩm: tên hiện trên ListView, ảnh và mô tả hiện ở màn hình chi tiết. */
public class Item implements Serializable {

    private final String name;
    private final int imageRes;
    private final String description;

    public Item(String name, int imageRes, String description) {
        this.name = name;
        this.imageRes = imageRes;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getImageRes() {
        return imageRes;
    }

    public String getDescription() {
        return description;
    }
}
