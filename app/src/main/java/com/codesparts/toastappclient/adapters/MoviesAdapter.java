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
import com.codesparts.toastappclient.model.Movie;
import com.thecodesparts.toastappclient.R;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MyViewHolder> {

    public List<Movie> moviesList;
    public List<Movie> selectedMovieList;
    Context mContext;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public  TextView title, year, genre;
        ImageView letter, check;
        public LinearLayout movieListItem;
        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
            letter = (ImageView) view.findViewById(R.id.imageView);
            check = (ImageView) view.findViewById(R.id.checkIcon);
            movieListItem = (LinearLayout)view.findViewById(R.id.movieListItem);
        }
    }

    public MoviesAdapter(Context context, List<Movie> moviesList, List<Movie> selectedList) {
        this.mContext = context;
        this.moviesList = moviesList;
        this.selectedMovieList = selectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Movie movie = moviesList.get(position);
        String letter = String.valueOf(movie.getTitle().charAt(0));
        holder.title.setText(movie.getTitle());
        holder.genre.setText(movie.getGenre());
        holder.year.setText(movie.getYear());

        if(selectedMovieList.contains(moviesList.get(position))) {
            TextDrawable drawable = TextDrawable.builder().buildRound(" ", 0xff616161);
            holder.letter.setImageDrawable(drawable);
            holder.check.setVisibility(View.VISIBLE);
            holder.movieListItem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.selected_item));
        }
        else {
            TextDrawable drawable = TextDrawable.builder().buildRound(letter, generator.getColor(movie.getTitle()));
            holder.letter.setImageDrawable(drawable);
            holder.check.setVisibility(View.GONE);
            holder.movieListItem.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}
