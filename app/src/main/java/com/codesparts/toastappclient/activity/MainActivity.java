package com.codesparts.toastappclient.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.codesparts.toastappclient.model.Ingredient;
import com.codesparts.toastappclient.others.AlertDialogHelper;
import com.codesparts.toastappclient.activity.auth.LoginActivity;
import com.codesparts.toastappclient.adapters.IngredientsAdapter;
import com.codesparts.toastappclient.others.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thecodesparts.toastappclient.R;

import com.getbase.floatingactionbutton.FloatingActionsMenu;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, AlertDialogHelper.AlertDialogListener {

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    ActionMode mActionMode;
    Menu contextMenu;
    private List<Ingredient> ingredientList = new ArrayList<>();
    private List<Ingredient> ingredientSelectedList = new ArrayList<>();
    private IngredientsAdapter mAdapter;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    boolean isMultiSelect = false;
    private FloatingActionsMenu fabMenu;
    private Animation fab_open, fab_close;
    private Vibrator vibe;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    AlertDialogHelper alertDialogHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
        vibe = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE) ;
        initViews();

        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame_layout);
        frameLayout.getBackground().setAlpha(0);

        fabMenu = (FloatingActionsMenu) findViewById(R.id.fab_menu);
        fabMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {
            @Override
            public void onMenuExpanded() {
                frameLayout.getBackground().setAlpha(240);
                frameLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        fabMenu.collapse();
                        return true;
                    }
                });
            }

            @Override
            public void onMenuCollapsed() {
                frameLayout.getBackground().setAlpha(0);
                frameLayout.setOnTouchListener(null);
            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fabRecipe);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialogHelper.showAlertDialog("", "Do you want to add new contact", "LATER", "ADD", 2, false);

            }
        });

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initViews(){
        alertDialogHelper = new AlertDialogHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new IngredientsAdapter(this, ingredientList, ingredientSelectedList);
        recyclerView.setAdapter(mAdapter);

        recyclerView.setHasFixedSize(true);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isMultiSelect)
                    multiSelect(position);
                else
                    Toast.makeText(getApplicationContext(), ingredientSelectedList.get(position) + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (!isMultiSelect) {
                    ingredientSelectedList = new ArrayList<>();
                    isMultiSelect = true;
                    if (mActionMode == null) {
                        vibe.vibrate(50);
                        mActionMode = startSupportActionMode(mActionModeCallback);
                    }
                }
                multiSelect(position);
            }
        }));

        prepareMovieData();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_common_activity, menu);
        return true;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_multi_select, menu);
            contextMenu = menu;
            getWindow().setStatusBarColor(Color.parseColor("#ff434343"));
//            fabMenu.animate().translationY(fabMenu.getHeight() + 16).setInterpolator(new AccelerateInterpolator(2)).start();
            fabMenu.startAnimation(fab_close);
            fabMenu.setVisibility(View.INVISIBLE);
            getSupportActionBar().hide();
            fab.startAnimation(fab_open);
            fab.setVisibility(View.VISIBLE);
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            ingredientSelectedList = new ArrayList<>();
            fabMenu.startAnimation(fab_open);
            fabMenu.setVisibility(View.VISIBLE);
            getSupportActionBar().show();
            getWindow().setStatusBarColor(Color.parseColor("#ffcc4f3f"));
            fab.startAnimation(fab_close);
            fab.setVisibility(View.INVISIBLE);
            refreshAdapter();
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;

        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    return true;
                default:
                    return false;
            }
        }
    };

    public void refreshAdapter() {
        mAdapter.selectedIngredientList = ingredientSelectedList;
        mAdapter.moviesList = ingredientList;
        mAdapter.notifyDataSetChanged();
    }

    private void prepareMovieData() {
        Ingredient ingredient = new Ingredient("Bananas", "Fruits & Vegetables", "2.5 Kg.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Chicken Wings", "Meat Products", "3.25 Kg.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Apple Juice", "Fluids", "1.5 Ltr.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Onions", "Fruits & Vegetables", "3.25 Kgs.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Apple", "Fruits & Vegetables", "3.25 Kgs.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Milk", "Dairy", "1 Ltr.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Channa Dal", "Pulses", "0.5 Kgs.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Urad Dal", "Pulses", "0.5 Kgs.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Black Gram", "Pulses", "0.5 Kgs.");
        ingredientList.add(ingredient);

        ingredient = new Ingredient("Coriander", "Fruits & Vegetables", "0.5 Kgs.");
        ingredientList.add(ingredient);

        mAdapter.notifyDataSetChanged();
    }

    public void multiSelect(int position) {
        if (mActionMode != null) {
            if (ingredientSelectedList.contains(ingredientList.get(position)))
                ingredientSelectedList.remove(ingredientList.get(position));
            else
                ingredientSelectedList.add(ingredientList.get(position));

            if (ingredientSelectedList.size() > 0)
                mActionMode.setTitle( ingredientSelectedList.size() + " Selected ");
            else
                mActionMode.setTitle("");
            refreshAdapter();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Settings Click", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_filter:
                FirebaseAuth.getInstance().signOut(); //End user session
                startActivity(new Intent(MainActivity.this, LoginActivity.class)); //Go back to home page
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.sign_out) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authListener != null) {
            auth.removeAuthStateListener(authListener);
        }
    }

    //Dialog Callbacks Declaration
    @Override
    public void onPositiveClick(int from) {
        if(from == 1) {
            if(ingredientSelectedList.size() > 0) {
                for(Ingredient i : ingredientSelectedList)
                    ingredientList.remove(i);
                mAdapter.notifyDataSetChanged();
                if (mActionMode != null) {
                    mActionMode.finish();
                }
                Toast.makeText(getApplicationContext(), "Delete Click", Toast.LENGTH_SHORT).show();
            }
        }
        else if(from == 2) {
            if (mActionMode != null) {
                mActionMode.finish();
            }
            Ingredient mSample = new Ingredient("Ingredient" + ingredientList.size(), "Category" + ingredientList.size(), "" + ingredientList.size());
            ingredientList.add(mSample);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNegativeClick(int from) {}

    @Override
    public void onNeutralClick(int from) {}

}


