package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import java.util.Locale;

// Một dòng của bảng nhansu
public class NhanSu {

    private final String ma;
    private final String hoTen;
    private final int namSinh;
    private final String gioiTinh;
    private final String queQuan;
    private final String trinhDo;
    private final byte[] anh; // ảnh JPEG đã thu nhỏ, null nếu chưa có ảnh
    private final double luong;
    private final String phongBan;

    public NhanSu(String ma, String hoTen, int namSinh, String gioiTinh, String queQuan,
                  String trinhDo, byte[] anh, double luong, String phongBan) {
        this.ma = ma;
        this.hoTen = hoTen;
        this.namSinh = namSinh;
        this.gioiTinh = gioiTinh;
        this.queQuan = queQuan;
        this.trinhDo = trinhDo;
        this.anh = anh;
        this.luong = luong;
        this.phongBan = phongBan;
    }

    public String getMa() { return ma; }
    public String getHoTen() { return hoTen; }
    public int getNamSinh() { return namSinh; }
    public String getGioiTinh() { return gioiTinh; }
    public String getQueQuan() { return queQuan; }
    public String getTrinhDo() { return trinhDo; }
    public byte[] getAnh() { return anh; }
    public double getLuong() { return luong; }
    public String getPhongBan() { return phongBan; }

    // 15000000 -> "15.000.000 đ"
    public String getLuongText() {
        return String.format(Locale.US, "%,.0f", luong).replace(',', '.') + " đ";
    }
}
