package ptit.cnpm1.tranxuanthanh.vatpham;

import java.util.ArrayList;
import java.util.List;

/** Dữ liệu cố định của bài: hai danh mục, mỗi danh mục một danh sách vật phẩm. */
public class ItemData {

    public static final String[] CATEGORIES = {"Đồ dùng", "Linh kiện"};

    /** Trả về các vật phẩm thuộc danh mục ở vị trí position của Spinner. */
    public static List<Item> getItems(int position) {
        List<Item> items = new ArrayList<>();
        if (position == 0) {
            items.add(new Item("Sách", R.drawable.img_sach,
                    "Sách giáo khoa khổ A4, bìa cứng, dùng để học và tra cứu kiến thức."));
            items.add(new Item("Thước kẻ", R.drawable.img_thuocke,
                    "Thước nhựa trong dài 30 cm, chia vạch mm, dùng để kẻ và đo độ dài."));
            items.add(new Item("Bút chì", R.drawable.img_butchi,
                    "Bút chì gỗ ruột 2B, nét đậm, dễ tẩy, dùng để viết và vẽ phác thảo."));
            items.add(new Item("Tẩy", R.drawable.img_tay,
                    "Tẩy cao su mềm, tẩy sạch nét bút chì mà không làm rách giấy."));
        } else {
            items.add(new Item("Tai nghe", R.drawable.img_tainghe,
                    "Tai nghe chụp tai, jack 3.5 mm, có micro đàm thoại, đệm tai êm."));
            items.add(new Item("Ốp lưng", R.drawable.img_oplung,
                    "Ốp lưng silicon dẻo cho điện thoại, chống trầy xước và va đập nhẹ."));
            items.add(new Item("Chuột", R.drawable.img_chuot,
                    "Chuột quang có dây cổng USB, độ phân giải 1200 DPI, 3 nút bấm."));
            items.add(new Item("USB", R.drawable.img_usb,
                    "USB 3.0 dung lượng 32 GB, tốc độ đọc tới 100 MB/s, vỏ kim loại."));
            items.add(new Item("Bàn phím không dây", R.drawable.img_banphim,
                    "Bàn phím không dây kết nối 2.4 GHz qua đầu thu USB, dùng pin AA."));
        }
        return items;
    }
}
