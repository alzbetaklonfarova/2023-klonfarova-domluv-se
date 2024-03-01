package com.example.rocnikovaprace.ui.PridejSlovicko;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.rocnikovaprace.MainActivity;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.SeznamKategorii;
import com.example.rocnikovaprace.databinding.FragmentSlideshowBinding;
import com.example.rocnikovaprace.ui.Kategorie;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class PridejSlovicko extends Fragment {

    private static final int CAMERA_REQUEST = 100;
    private static final int STORAGE_REQUEST = 200;
    String cameraPermission[];
    String storagePermission[];
    ImageButton imageButton;

    private PridejSlovickoViewModel pridejSlovickoViewModel;
    private FragmentSlideshowBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        pridejSlovickoViewModel =
                new ViewModelProvider(this).get(PridejSlovickoViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Vytvoří dialog pro zadání hesla
        AlertDialog.Builder builder
                = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.zadejte_heslo));

        // Nastaví vzhled dialogu
        final View customLayout
                = getLayoutInflater()
                .inflate(
                        R.layout.heslodialog,
                        null);
        builder.setView(customLayout);
        builder.setCancelable(false);
        builder.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {

                    //Ověří správnost zadaného hesla, pokud je špatné vrátí uživatele na domovskou obrazovku
                    @Override
                    public void onClick(
                            DialogInterface dialog,
                            int which) {
                        EditText editText
                                = customLayout
                                .findViewById(
                                        R.id.dialogoveheslo);
                        String zeSouboru = "";
                        File heslosoubor = new File(getContext().getFilesDir(), "heslo.txt");
                        try (BufferedReader br = new BufferedReader(new FileReader(heslosoubor))) {
                            zeSouboru = br.readLine();
                        } catch (Exception e) {
                            System.out.println("Chyba při čtení ze souboru.");
                        }

                        if (zeSouboru.equals(editText.getText().toString())) {
                        } else {
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }


                    }
                });
        AlertDialog dialog
                = builder.create();
        dialog.show();


        // Inicializuji imageView, ale protože je to v onCreateView musí ho najít view
        imageButton = root.findViewById(R.id.imageButton);

        Spinner spinner = root.findViewById(R.id.spinner);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, SeznamKategorii.nazvy());
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        //Nastavím pozadí image View
        imageButton.setBackgroundResource(R.drawable.kliknutimvloziteobrazek);
        imageButton.setImageResource(R.drawable.kliknutimvloziteobrazek);

        // povolení galerie a fotoaparátu
        cameraPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // Po kliknutí se začíná vybírat obrázek
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePicDialog();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Vytvoří dialog na výběr obrázku, má dvě možnosti, buď z galerie nebo z fotoaparátu. Poté dialog zobrazí.
    private void showImagePicDialog() {
        String options[] = {getString(R.string.fotoaparat), getString(R.string.galerie)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getString(R.string.vyberte_obrazek_z));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    if (!checkCameraPermission()) {
                        requestCameraPermission();
                    } else {
                        pickFromGallery();
                    }
                } else if (which == 1) {
                    if (!checkStoragePermission()) {
                        requestStoragePermission();
                    } else {
                        pickFromGallery();
                    }
                }
            }
        });
        builder.create().show();
    }

    // kontrola oprávnění úložiště
    private Boolean checkStoragePermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    // Žádost o povolení galerie
    private void requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST);
    }

    // kontrola oprávnění fotoaparátu
    private Boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }

    // Žádost o povolení fotoaparátu
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    // Žádost o povolení fotoaparátu a galerie, pokud ještě není dáno
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_REQUEST: {
                if (grantResults.length > 0) {
                    boolean camera_accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (camera_accepted && writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.zadost1), Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST: {
                if (grantResults.length > 0) {
                    boolean writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (writeStorageaccepted) {
                        pickFromGallery();
                    } else {
                        Toast.makeText(getContext(), getString(R.string.zadost2), Toast.LENGTH_LONG).show();
                    }
                }
            }
            break;
        }
    }

    // Tady se vybere obrázek z galerie a nastaví se oříznutí na čtverec
    private void pickFromGallery() {

        CropImage.activity().setAspectRatio(1, 1).start(getActivity());

    }


}