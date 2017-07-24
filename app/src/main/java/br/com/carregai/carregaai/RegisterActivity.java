package br.com.carregai.carregaai;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout mEmail, mPassword;
    private FirebaseAuth mAuth;

    private ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        mDialog = new ProgressDialog(this);

        mEmail = (TextInputLayout)findViewById(R.id.input_login_register);
        mPassword = (TextInputLayout)findViewById(R.id.input_password_register);
    }

    public void login(View v){
        finish();
    }

    public void register(View v){
        String email = mEmail.getEditText().getText().toString();
        String password = mPassword.getEditText().getText().toString();

        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){

            mDialog.setTitle("Registrando");
            mDialog.setMessage("Aguarde enquanto fazemos seu registro,");
            mDialog.setCanceledOnTouchOutside(false);
            mDialog.show();

            createUser(email, password);
        }else{
            Toast.makeText(RegisterActivity.this,  "Algum erro ocorreu. Confira os campos e tente novamente.", Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(String email, String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mDialog.dismiss();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        }else{
                            mDialog.dismiss();
                            Toast.makeText(RegisterActivity.this,  "Algum erro ocorreu. Tente novamente.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
