package com.example.rocnikovaprace.Adaptery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.Adaptery.Adapter;
import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.SeznamKategorii;
import com.example.rocnikovaprace.ui.Kategorie;
import com.example.rocnikovaprace.ui.SlovickoSnake;

import java.util.List;

public class MalyAdapter extends RecyclerView.Adapter<Adapter.MyView> {

    private List<SlovickoSnake> list;

    public interface onNoteListener {
    }


    public class MyView
            extends RecyclerView.ViewHolder {


        TextView textView;
        ImageView obrazek;
        CardView cardView;

        // Konstruktor s parametrem View
        public MyView(View view) {
            super(view);

            // initialise TextView with id
            textView = (TextView) view
                    .findViewById(R.id.textview);

            obrazek = (ImageView) view
                    .findViewById(R.id.obrazek);

            cardView = (CardView) view
                    .findViewById(R.id.cardview);


        }
    }

    // Další konstruktor
    public MalyAdapter(List<SlovickoSnake> horizontalList) {
        this.list = horizontalList;
    }


    @Override
    public Adapter.MyView onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

        // Přiřazení rozložení a vzhledu položky v recyclerView
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.malyitem,
                        parent,
                        false);
        return new Adapter.MyView(itemView);
    }

    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.DEFAULT);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }
    @Override
    public void onBindViewHolder(final Adapter.MyView holder,
                                 final int position) {

        //Nastaví text a obrázek, každé položce v seznamu
        holder.textView.setText(list.get(position).nazev);
        holder.obrazek.setImageBitmap(convertStringToBitmap(list.get(position).obrazek));

        Kategorie kategorie = SeznamKategorii.podleNazvu(list.get(position).kategorie);
        if (kategorie != null) {
            holder.cardView.setCardBackgroundColor(Color.parseColor(kategorie.getBarva()));
            holder.cardView.setCardBackgroundColor(Color.parseColor(kategorie.getBarva()));
            Log.d("Barva", "Barva pro pozici " + position + ": " + kategorie.getBarva());
        }

    }



    // Vrátí délku recyclerView
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}