package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import java.util.Locale;

// Một dòng của bảng nhansu
public class NhanSu {

    private final String ma;
    private final String hoTen;
    private final String ngaySinh; // dạng yyyy-MM-dd để so sánh / sắp xếp được
    private final String gioiTinh;
    private final String queQuan;
    private final String trinhDo;
    private final byte[] anh; // ảnh JPEG đã thu nhỏ, null nếu chưa có ảnh
    private final double luong;
    private final String phongBan;

    public NhanSu(String ma, String hoTen, String ngaySinh, String gioiTinh, String queQuan,
                  String trinhDo, byte[] anh, double luong, String phongBan) {
        this.ma = ma;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.queQuan = queQuan;
        this.trinhDo = trinhDo;
        this.anh = anh;
        this.luong = luong;
        this.phongBan = phongBan;
    }

    public String getMa() { return ma; }
    public String getHoTen() { return hoTen; }
    public String getNgaySinh() { return ngaySinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getQueQuan() { return queQuan; }
    public String getTrinhDo() { return trinhDo; }
    public byte[] getAnh() { return anh; }
    public double getLuong() { return luong; }
    public String getPhongBan() { return phongBan; }

    // "1990-05-12" -> "12/05/1990"
    public static String hienThiNgay(String ngay) {
        String[] p = ngay.split("-");
        return p[2] + "/" + p[1] + "/" + p[0];
    }

    // 15000000 -> "15.000.000 đ"
    public String getLuongText() {
        return String.format(Locale.US, "%,.0f", luong).replace(',', '.') + " đ";
    }
}
