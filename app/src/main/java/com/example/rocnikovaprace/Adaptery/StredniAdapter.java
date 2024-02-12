package com.example.rocnikovaprace.Adaptery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rocnikovaprace.R;
import com.example.rocnikovaprace.Slovicka;
import com.example.rocnikovaprace.ui.SlovickoSnake;

import java.util.List;


public class StredniAdapter extends RecyclerView.Adapter<Adapter.MyView> {


    private List<SlovickoSnake> list;


    public static class MyView
            extends RecyclerView.ViewHolder {


        TextView textView;
        ImageView obrazek;
        CardView cardView;

        // Konstruktor s paramentrem View
        public MyView(View view) {
            super(view);


            textView = (TextView) view
                    .findViewById(R.id.textview);

            obrazek = (ImageView) view
                    .findViewById(R.id.obrazek);

            cardView = (CardView) view
                    .findViewById(R.id.cardview);


        }
    }

    //Další konstruktor
    public StredniAdapter(List<SlovickoSnake> horizontalList) {
        this.list = horizontalList;
    }

    // Metoda, která se stará o rozložení a vzhled jednotlivých položek v seznamu
    @Override
    public Adapter.MyView onCreateViewHolder(ViewGroup parent,
                                             int viewType) {

        // Inflate item.xml using LayoutInflator
        View itemView
                = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.stredniitem,
                        parent,
                        false);

        // return itemView
        return new Adapter.MyView(itemView);
    }

    //Metoda, která převádí string na bitmapu zdroj:http://www.java2s.com/example/android/graphics/convert-bitmap-to-string.html
    public static Bitmap convertStringToBitmap(String string) {
        byte[] byteArray1;
        byteArray1 = Base64.decode(string, Base64.URL_SAFE);
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray1, 0,
                byteArray1.length);
        return bmp;
    }
    @Override
    public void onBindViewHolder(final Adapter.MyView holder,
                                 final int position) {

        //Nastaví text a obrázek, každé položce v seznamu
        holder.textView.setText(list.get(position).nazev);
        holder.obrazek.setImageBitmap(convertStringToBitmap(list.get(position).nazev));


    }

    // Vrátí délku seznamu
    @Override
    public int getItemCount() {
        return list.size();
    }


}









