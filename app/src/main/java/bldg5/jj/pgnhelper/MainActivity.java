package bldg5.jj.pgnhelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import bldg5.jj.pgnhelper.adapters.OnSwipeTouchListener;
import bldg5.jj.pgnhelper.common.Game;

public class MainActivity extends AppCompatActivity {
    private int nMoveNumber;
    private CB boardShowing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        setContentView(R.layout.content_main);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);

        Button btnFirst = (Button) findViewById(R.id.btnFirst);
        Button btnPrev = (Button) findViewById(R.id.btnPrev);
        Button btnNext = (Button) findViewById(R.id.btnNext);
        Button btnLast = (Button) findViewById(R.id.btnLast);
        Button btnSwitch = (Button) findViewById(R.id.btnSwitch);
        boardShowing = (CB) findViewById(R.id.boardShowing);


        Intent intent = getIntent();
        // final Games allGames = (Games) intent.getSerializableExtra("DisplayedGames");
        final Game gameSelected = (Game) intent.getSerializableExtra("SelectedGame");
        String game_info = (String) intent.getStringExtra("GameInfo");
        boardShowing.setGame(gameSelected);

        final TextView txtMove = (TextView) findViewById(R.id.txtCurrentMove);

        // if it's long, make the font smaller
        // if it's really long trim it.
        // under 170, 20 size
        // between 170 and 300, 15 size
        // higher than 300, 12 size
        if (game_info.length() > 170) {
            if (game_info.length() > 300) {
                game_info = game_info.substring(0, 500);
                txtMove.setTextSize(12.0f);
            } else {
                // strGameInfo = strGameInfo.substring(0, 170);
                txtMove.setTextSize(15.0f);
            }
        } else {
            txtMove.setTextSize(20.0f);
        }

        // set the text
        txtMove.setText(game_info);
        // set up the navigation drawer.
        setupDrawer();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next(txtMove);
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prev(txtMove);
            }
        });

        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                first(txtMove);
            }
        });

        btnLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                last(txtMove);
            }
        });

        btnSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardShowing.switchSides();
            }
        });

        final Context context = this.getApplicationContext();

        // set the swipe listener
        boardShowing.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() { last(txtMove); }

            public void onSwipeRight() { prev(txtMove); }

            public void onSwipeLeft() { next(txtMove); }

            public void onSwipeBottom() { first(txtMove); }
        });

        // what is my screen density?
        /* http://stackoverflow.com/questions/3166501/getting-the-screen-density-programmatically-in-android
        0.75 - ldpi
        1.0 - mdpi
        1.5 - hdpi
        2.0 - xhdpi
        3.0 - xxhdpi
        4.0 - xxxhdpi
        */
        float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        Log.i("PGNHelper", String.valueOf(scale));
    }

    private void next(TextView txtMove) {
        nMoveNumber = boardShowing.getMoveNumber() + 1;
        boardShowing.setMoveNumber(nMoveNumber);

        // the pgn is set on instancing of CB so set the text view
        // but it has to be after the move number is set.
        txtMove.setText(boardShowing.getMove());
        boardShowing.halfMove();
    }

    private void prev(TextView txtMove) {
        nMoveNumber = boardShowing.getMoveNumber() - 1;
        nMoveNumber = (nMoveNumber <= 0) ? 0 : nMoveNumber;

        boardShowing.setMoveNumber(nMoveNumber);

        // the pgn is set on instancing of CB so set the text view
        // but it has to be after the move number is set.
        txtMove.setText(boardShowing.getMove());
        boardShowing.halfMoveBackwards();
    }

    private void first(TextView txtMove) {
        nMoveNumber = 0;
        boardShowing.setMoveNumber(nMoveNumber);
        txtMove.setText(boardShowing.getMove());
        boardShowing.initBoard();
    }

    private void last(TextView txtMove) {
        // if the game ends on a white move, this will be higher than
        // the max # of UI moves by one.
        nMoveNumber = 2 * boardShowing.getNumMoves();
        boardShowing.setMoveNumber(nMoveNumber);
        txtMove.setText(boardShowing.getMove());
        boardShowing.toTheEnd();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        final TextView txtMove = (TextView) findViewById(R.id.txtCurrentMove);

        outState.putInt("nMoveNumber", nMoveNumber);
        outState.putString("txtMove", txtMove.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
        final TextView txtMove = (TextView) findViewById(R.id.txtCurrentMove);
        nMoveNumber = savedState.getInt("nMoveNumber");

        String move = savedState.getString("txtMove");
        txtMove.setText(move);

        if (nMoveNumber > 0) {
            boardShowing.setMoveNumber(nMoveNumber);
            boardShowing.toMoveNumber(nMoveNumber);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawer() {
        // https://github.com/mikepenz/MaterialDrawer
        // navigation drawer
        new DrawerBuilder().withActivity(this).build();
        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem homeItem = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.drawer_item_home);
        SecondaryDrawerItem searchItem = new SecondaryDrawerItem().withIdentifier(2).withName(R.string.drawer_item_search);

        //create the drawer and remember the `Drawer` result object
        Drawer result = new DrawerBuilder()
            .withActivity(this)
            // .withToolbar(toolbar)
            .addDrawerItems(
                    homeItem,
                    searchItem,
                    new DividerDrawerItem(),
                    new SecondaryDrawerItem().withName(R.string.drawer_item_settings)
            )
            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                @Override
                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                    if (position == 0) {
                        // home
                    }

                    if (position == 1) {
                        // search
                    }
                    return true;
                }
            }).build();

    }
}
