package ptit.cnpm1.tranxuanthanh.quanlynhansu;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.List;

// Đổ từng NhanSu vào item_nhansu.xml cho ListView ở Activity_main
public class NhanSuAdapter extends ArrayAdapter<NhanSu> {

    public NhanSuAdapter(Context context, List<NhanSu> list) {
        super(context, 0, list);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_nhansu, parent, false);
        }
        NhanSu ns = getItem(position);

        ImageView imgAnh = convertView.findViewById(R.id.imgAnh);
        if (ns.getAnh() != null) {
            imgAnh.setImageBitmap(BitmapFactory.decodeByteArray(ns.getAnh(), 0, ns.getAnh().length));
        } else {
            imgAnh.setImageResource(R.drawable.ic_person);
        }

        TextView tvTen = convertView.findViewById(R.id.tvTen);
        TextView tvPhongBan = convertView.findViewById(R.id.tvPhongBan);
        TextView tvLuong = convertView.findViewById(R.id.tvLuong);
        tvTen.setText(ns.getMa() + " - " + ns.getHoTen());
        tvPhongBan.setText(ns.getPhongBan() + " · " + ns.getTrinhDo());
        tvLuong.setText(getContext().getString(R.string.lbl_luong) + ": " + ns.getLuongText());
        return convertView;
    }
}
