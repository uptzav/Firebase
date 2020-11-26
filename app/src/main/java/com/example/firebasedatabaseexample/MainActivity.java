package com.example.firebasedatabaseexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.media.tv.TvView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FloatingActionButton fab;
    ScaleAnimation shrinkAnim;
    private RecyclerView recyclerView;
    private StaggeredGridLayoutManager layoutManager;
    private FirebaseRecyclerAdapter<Movie,MovieViewHolder> adapter;
    private TextView tvNoMovies;

    private DatabaseReference movieReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    private static final String userId = "53";
    public static String idMovies = "idMovies";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView)findViewById(R.id.my_recycler_view);
        tvNoMovies = (TextView)findViewById(R.id.tv_no_movies);

        shrinkAnim = new ScaleAnimation(1.15f,0f,1.15f,0f
                , Animation.RELATIVE_TO_SELF,
                0.5f,Animation.RELATIVE_TO_SELF,0.5f);

        if(recyclerView != null){
            recyclerView.setHasFixedSize(true);
        }

        layoutManager = new StaggeredGridLayoutManager(2
                , StaggeredGridLayoutManager.HORIZONTAL);

        recyclerView.setLayoutManager(layoutManager);

        movieReference = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("movies");
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        FirebaseRecyclerOptions<Movie> options = new FirebaseRecyclerOptions.Builder<Movie>()
                .setQuery(movieReference,Movie.class)
                .build();

        FirebaseRecyclerAdapter<Movie,MovieViewHolder> adapter =
                new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull MovieViewHolder holder
                            , int position, @NonNull Movie model) {
                        if(tvNoMovies.getVisibility() == View.VISIBLE){
                            tvNoMovies.setVisibility(View.GONE);
                        }
                        holder.tvMovieName.setText(model.getMovieName());
                        holder.ratingBar.setRating(model.getMovieRating());

                        Picasso.get()
                                .load(model.getMoviePoster())
                                .fit().centerCrop()
                                .into(holder.ivMoviePoster);

                        //TODO: código para los botones de mantenimiento
                    }

                    @NonNull
                    @Override
                    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent
                            , int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.movie_board_item,parent,false);
                        MovieViewHolder movieViewHolder = new MovieViewHolder(view);
                        return movieViewHolder;
                    }
                };
        //_ establecer el adaptador
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_container, new AddMovieFragment())
                        .addToBackStack(null)
                        .commit();
                //_animación
                shrinkAnim.setDuration(400);
                fab.setAnimation(shrinkAnim);
                shrinkAnim.start();
                shrinkAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        fab.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        if(fab.getVisibility() == View.GONE){
            fab.setVisibility(View.VISIBLE);
        }
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder{
        TextView tvMovieName;
        RatingBar ratingBar;
        ImageView ivMoviePoster;
        ImageButton eliminar;
        ImageButton modificar;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMovieName = (TextView)itemView.findViewById(R.id.tv_name);
            ratingBar = (RatingBar)itemView.findViewById(R.id.rating_bar);
            ivMoviePoster = (ImageView)itemView.findViewById(R.id.movie_poster);
        }
    }
}