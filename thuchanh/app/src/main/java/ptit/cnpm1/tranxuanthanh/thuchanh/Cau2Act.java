package ptit.cnpm1.tranxuanthanh.thuchanh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class Cau2Act extends AppCompatActivity implements View.OnClickListener {

    // Giá vé, đơn vị nghìn đồng (K)
    private static final int PRICE_FIRST = 1500;
    private static final int PRICE_BUSINESS = 1300;
    private static final int PRICE_ECONOMY = 1000;

    private EditText txtName;
    private EditText txtPhone;
    private RadioGroup rgType;
    private EditText txtQty;
    private EditText txtDiscount;
    private Button btnBook;

    private LinearLayout layoutResult;
    private TextView lblName;
    private TextView lblPhone;
    private TextView lblTotal;
    private RatingBar ratingBar;

    /** Tổng tiền sau giảm giá, chưa tính 5% thưởng khi chấm 5 sao. */
    private double baseTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.cau2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtName = findViewById(R.id.txtName);
        txtPhone = findViewById(R.id.txtPhone);
        rgType = findViewById(R.id.rgType);
        txtQty = findViewById(R.id.txtQty);
        txtDiscount = findViewById(R.id.txtDiscount);
        btnBook = findViewById(R.id.btnBook);

        layoutResult = findViewById(R.id.layoutResult);
        lblName = findViewById(R.id.lblName);
        lblPhone = findViewById(R.id.lblPhone);
        lblTotal = findViewById(R.id.lblTotal);
        ratingBar = findViewById(R.id.ratingBar);

        btnBook.setOnClickListener(this);

        // Mỗi lần đổi số sao thì tính lại tiền từ baseTotal
        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> showTotal(rating));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnBook) {
            book();
        }
    }

    private void book() {
        String name = txtName.getText().toString().trim();
        String phone = txtPhone.getText().toString().trim();
        String qtyText = txtQty.getText().toString().trim();
        String discountText = txtDiscount.getText().toString().trim();

        if (name.isEmpty() || phone.isEmpty() || qtyText.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        int checkedId = rgType.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn loại vé", Toast.LENGTH_SHORT).show();
            return;
        }

        int qty;
        double discount;
        try {
            qty = Integer.parseInt(qtyText);
            // Bỏ trống ô giảm giá thì coi như 0%
            discount = discountText.isEmpty() ? 0 : Double.parseDouble(discountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Số vé hoặc giảm giá không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (qty <= 0) {
            Toast.makeText(this, "Số vé phải lớn hơn 0", Toast.LENGTH_SHORT).show();
            return;
        }
        if (discount < 0 || discount > 100) {
            Toast.makeText(this, "Giảm giá phải từ 0 đến 100%", Toast.LENGTH_SHORT).show();
            return;
        }

        // R.id không còn là hằng số (AGP 8), nên dùng if/else chứ không dùng switch
        int price;
        if (checkedId == R.id.rbFirst) {
            price = PRICE_FIRST;
        } else if (checkedId == R.id.rbBusiness) {
            price = PRICE_BUSINESS;
        } else {
            price = PRICE_ECONOMY;
        }

        // Tổng tiền = giá vé x số vé - giảm giá, giảm giá tính theo % của tiền vé
        baseTotal = price * qty * (1 - discount / 100);

        lblName.setText("Họ tên: " + name);
        lblPhone.setText("Số điện thoại: " + phone);
        ratingBar.setRating(0);
        showTotal(0);
        layoutResult.setVisibility(View.VISIBLE);
    }

    private void showTotal(float rating) {
        double total = baseTotal;
        if (rating == 5) {
            total = total * 0.95; // 5 sao: giảm thêm 5%
        }
        lblTotal.setText("Tổng tiền: " + String.format(Locale.US, "%,.0fK", total));
    }
}
