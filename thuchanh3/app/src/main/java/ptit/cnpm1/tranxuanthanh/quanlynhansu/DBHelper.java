package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

// Tạo CSDL quanlynhansu.db, tạo bảng nhansu và các hàm thêm / sửa / xoá / tìm kiếm / sắp xếp
public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "quanlynhansu.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE = "nhansu";
    private static final String COL_MA = "ma";
    private static final String COL_HO_TEN = "hoten";
    private static final String COL_NAM_SINH = "namsinh";
    private static final String COL_GIOI_TINH = "gioitinh";
    private static final String COL_QUE_QUAN = "quequan";
    private static final String COL_TRINH_DO = "trinhdo";
    private static final String COL_ANH = "anh";
    private static final String COL_LUONG = "luong";
    private static final String COL_PHONG_BAN = "phongban";

    // Thứ tự sắp xếp danh sách
    public static final int SORT_NONE = 0;
    public static final int SORT_LUONG_TANG = 1;
    public static final int SORT_LUONG_GIAM = 2;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COL_MA + " TEXT PRIMARY KEY, "
                + COL_HO_TEN + " TEXT NOT NULL, "
                + COL_NAM_SINH + " INTEGER, "
                + COL_GIOI_TINH + " TEXT, "
                + COL_QUE_QUAN + " TEXT, "
                + COL_TRINH_DO + " TEXT, "
                + COL_ANH + " BLOB, "
                + COL_LUONG + " REAL, "
                + COL_PHONG_BAN + " TEXT)");

        // Vài nhân sự mẫu để danh sách có dữ liệu ngay lần chạy đầu
        db.insert(TABLE, null, toValues(new NhanSu("NS01", "Nguyễn Văn An", 1990, "Nam", "Hà Nội", "Đại học", null, 15000000, "Phần mềm")));
        db.insert(TABLE, null, toValues(new NhanSu("NS02", "Trần Thị Bình", 1995, "Nữ", "Nam Định", "Cao đẳng", null, 9000000, "Hành chính")));
        db.insert(TABLE, null, toValues(new NhanSu("NS03", "Lê Văn Cường", 1988, "Nam", "Hải Phòng", "Thạc sĩ", null, 22000000, "Dự án")));
        db.insert(TABLE, null, toValues(new NhanSu("NS04", "Phạm Thị Dung", 1998, "Nữ", "Thanh Hóa", "Đại học", null, 12000000, "Kỹ thuật")));
        db.insert(TABLE, null, toValues(new NhanSu("NS05", "Hoàng Minh Đức", 1992, "Nam", "Nghệ An", "Tiến sĩ", null, 30000000, "Phần mềm")));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // Danh sách nhân sự có mã hoặc họ tên chứa từ khoá (rỗng = tất cả), sắp xếp theo sort
    public List<NhanSu> getAll(String keyword, int sort) {
        String selection = null;
        String[] args = null;
        if (!keyword.isEmpty()) {
            selection = COL_MA + " LIKE ? OR " + COL_HO_TEN + " LIKE ?";
            args = new String[]{"%" + keyword + "%", "%" + keyword + "%"};
        }

        String orderBy;
        if (sort == SORT_LUONG_TANG) {
            orderBy = COL_LUONG + " ASC, " + COL_MA + " ASC";
        } else if (sort == SORT_LUONG_GIAM) {
            orderBy = COL_LUONG + " DESC, " + COL_MA + " ASC";
        } else {
            orderBy = COL_MA + " ASC";
        }

        List<NhanSu> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.query(TABLE, null, selection, args, null, null, orderBy)) {
            while (c.moveToNext()) {
                list.add(fromCursor(c));
            }
        }
        return list;
    }

    // Một nhân sự theo mã, null nếu không có
    public NhanSu getByMa(String ma) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor c = db.rawQuery("SELECT * FROM " + TABLE + " WHERE " + COL_MA + " = ?", new String[]{ma})) {
            return c.moveToFirst() ? fromCursor(c) : null;
        }
    }

    // false nếu thêm không được (vd trùng mã)
    public boolean insert(NhanSu ns) {
        return getWritableDatabase().insert(TABLE, null, toValues(ns)) != -1;
    }

    public boolean update(NhanSu ns) {
        return getWritableDatabase().update(TABLE, toValues(ns), COL_MA + " = ?", new String[]{ns.getMa()}) > 0;
    }

    public boolean delete(String ma) {
        return getWritableDatabase().delete(TABLE, COL_MA + " = ?", new String[]{ma}) > 0;
    }

    private static ContentValues toValues(NhanSu ns) {
        ContentValues values = new ContentValues();
        values.put(COL_MA, ns.getMa());
        values.put(COL_HO_TEN, ns.getHoTen());
        values.put(COL_NAM_SINH, ns.getNamSinh());
        values.put(COL_GIOI_TINH, ns.getGioiTinh());
        values.put(COL_QUE_QUAN, ns.getQueQuan());
        values.put(COL_TRINH_DO, ns.getTrinhDo());
        values.put(COL_ANH, ns.getAnh());
        values.put(COL_LUONG, ns.getLuong());
        values.put(COL_PHONG_BAN, ns.getPhongBan());
        return values;
    }

    private static NhanSu fromCursor(Cursor c) {
        return new NhanSu(
                c.getString(c.getColumnIndexOrThrow(COL_MA)),
                c.getString(c.getColumnIndexOrThrow(COL_HO_TEN)),
                c.getInt(c.getColumnIndexOrThrow(COL_NAM_SINH)),
                c.getString(c.getColumnIndexOrThrow(COL_GIOI_TINH)),
                c.getString(c.getColumnIndexOrThrow(COL_QUE_QUAN)),
                c.getString(c.getColumnIndexOrThrow(COL_TRINH_DO)),
                c.getBlob(c.getColumnIndexOrThrow(COL_ANH)),
                c.getDouble(c.getColumnIndexOrThrow(COL_LUONG)),
                c.getString(c.getColumnIndexOrThrow(COL_PHONG_BAN)));
    }
}
