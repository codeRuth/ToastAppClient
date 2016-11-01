package com.codesparts.toastappclient.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.codesparts.toastappclient.model.Ingredient;
import com.thecodesparts.toastappclient.R;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.MyViewHolder> {

    public List<Ingredient> moviesList;
    public List<Ingredient> selectedIngredientList;
    private Context mContext;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public  TextView title, quantity, category;
        ImageView letter, check;
        public LinearLayout ingredientListItem;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            category = (TextView) view.findViewById(R.id.category);
            quantity = (TextView) view.findViewById(R.id.quantityItem);
            letter = (ImageView) view.findViewById(R.id.imageView);
            check = (ImageView) view.findViewById(R.id.checkIcon);
            ingredientListItem = (LinearLayout)view.findViewById(R.id.movieListItem);
        }
    }

    public IngredientsAdapter(Context context, List<Ingredient> moviesList, List<Ingredient> selectedList) {
        this.mContext = context;
        this.moviesList = moviesList;
        this.selectedIngredientList = selectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ingredient_list_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ingredient ingredient = moviesList.get(position);
        String letter = String.valueOf(ingredient.getTitle().charAt(0));
        holder.title.setText(ingredient.getTitle());
        holder.category.setText(ingredient.getCategory());
        holder.quantity.setText(ingredient.getQuantity());

        if(selectedIngredientList.contains(moviesList.get(position))) {
            TextDrawable drawable = TextDrawable.builder().buildRound(" ", 0xff616161);
            holder.letter.setImageDrawable(drawable);
            holder.check.setVisibility(View.VISIBLE);
            holder.ingredientListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected_item));
        }
        else {
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getColor(ingredient.getTitle()));
            holder.letter.setImageDrawable(drawable);
            holder.check.setVisibility(View.GONE);
            holder.ingredientListItem.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
