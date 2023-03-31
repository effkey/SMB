package com.example.here;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UserSettingsActivity extends AppCompatActivity {
    private Button usernameButton;
    private Button passwordButton;
    private Button emailButton;
    private Button dataButton;
    private Button avatarButton;
    private Button logOutButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_view);

        usernameButton = (Button) findViewById(R.id.change_username_button);
        passwordButton = (Button) findViewById(R.id.change_password_button);
        emailButton = (Button) findViewById(R.id.change_email_button);
        dataButton = (Button) findViewById(R.id.change_data_button);
        avatarButton = (Button) findViewById(R.id.change_avatar_button);
        logOutButton = (Button) findViewById(R.id.log_out_button);

        this.usernameButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserSettingsActivity.this);
                LayoutInflater inflater = UserSettingsActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_view, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Zmiana nazwy użytkownika");
                dialogBuilder.setMessage("Wpisz nową nazwę:");
                final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
                dialogBuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String enteredText = editText.getText().toString();
                        // zrób coś z wpisanym tekstem, dodać później funkcjonalność
                    }
                });
                dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // nic nie rób
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });

        this.passwordButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserSettingsActivity.this);
                LayoutInflater inflater = UserSettingsActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_view, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Zmiana hasła użytkownika");
                dialogBuilder.setMessage("Wpisz nowe hasło:");
                final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
                dialogBuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String enteredText = editText.getText().toString();
                        // zrób coś z wpisanym tekstem, dodać później funkcjonalność
                    }
                });
                dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // nic nie rób
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });

        this.emailButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserSettingsActivity.this);
                LayoutInflater inflater = UserSettingsActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_view, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Zmiana emaila użytkownika");
                dialogBuilder.setMessage("Wpisz nowy email:");
                final EditText editText = (EditText) dialogView.findViewById(R.id.edit_text);
                dialogBuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String enteredText = editText.getText().toString();
                        // zrób coś z wpisanym tekstem, dodać później funkcjonalność
                    }
                });
                dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // nic nie rób
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });

        this.dataButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserSettingsActivity.this);
                // tu jeszcze dodać funkcjonalność na podstawie tego co w innych przyciskach
            }
        });

        this.avatarButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                // tu jeszcze dodać funkcjonalność zmiany awatara
            }
        });

        this.logOutButton.setOnClickListener(new View.OnClickListener() {     // utwórz okno dialogowe po naciśnięciu
            @Override
            public void onClick(View view) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(UserSettingsActivity.this);
                LayoutInflater inflater = UserSettingsActivity.this.getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.dialog_view, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setTitle("Potwierdź wylogowanie");
                dialogBuilder.setMessage("Czy na pewno chcesz się wylogować?");
                dialogBuilder.setPositiveButton("Potwierdź", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // wyloguj, dodać później funkcjonalność
                    }
                });
                dialogBuilder.setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // nic nie rób
                    }
                });
                AlertDialog b = dialogBuilder.create();
                b.show();
            }
        });



    }


}
