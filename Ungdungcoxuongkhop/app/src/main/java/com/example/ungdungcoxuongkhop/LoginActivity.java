package com.example.ungdungcoxuongkhop;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;  // Import View
import android.widget.ImageButton;
import android.widget.ProgressBar;  // Import ProgressBar
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth auth;
    private ImageButton btnGoogleSignIn;
    private ProgressBar progressBar; // Sử dụng ProgressBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
        auth = FirebaseAuth.getInstance();
        googleSignInClient = setupGoogleSignIn();

        // Lấy ProgressBar từ layout (giả sử bạn đã thêm nó trong XML)
        progressBar = findViewById(R.id.progressBar);  // Đảm bảo ID trong XML là đúng

        progressBar.setIndeterminate(true);  // Chạy không biết được thời gian
        progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar khi chưa sử dụng

        btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
    }

    private GoogleSignInClient setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(this, gso);
    }

    private void signInWithGoogle() {
        progressBar.setVisibility(View.VISIBLE);  // Hiển thị ProgressBar khi đăng nhập
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 123);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(this, "Lỗi đăng nhập", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(account.getIdToken(), null))
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE);  // Ẩn ProgressBar sau khi xử lý
                    if (task.isSuccessful() && auth.getCurrentUser() != null) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            fetchUserID(user.getUid(), user.getEmail(), user.getDisplayName());
                        }
                    } else {
                        Toast.makeText(this, "Xác thực Firebase thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchUserID(String uid, String email, String fullName) {
        String url = "http://172.22.144.1/ungdung_api/add_user.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Log.d("fetchUserID", "Response: " + response);
                    if (response.equalsIgnoreCase("Thành công")) {
                        SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                        sharedPreferences.edit().putString("id_nguoi_dung", uid).apply();

                        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("email", email);
                params.put("full_name", fullName);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);
    }
}
