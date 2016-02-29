package com.plo.ploworks;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.plo.ploworks.network.Constants;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;
    private CharSequence mTitle, mDrawerTitle;
    private String[] mMenuTitle;
    private int positionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTitle = mDrawerTitle = getTitle();
        mMenuTitle = getResources().getStringArray(R.array.nav_drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.layout_drawer);
        mDrawerList = (ListView) findViewById(R.id.list_drawer);

        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mMenuTitle));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (positionFragment != position) {
                    selectItem(position);
                } else {
                    mDrawerList.setItemChecked(position, true);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
            }
        });

        if (savedInstanceState == null) {
            selectItem(0);
        }
    }

    //function to selecting item
    private void selectItem(int position){
        // update the main content by replacing fragments
        Bundle args = new Bundle();
        FragmentManager fragmentManager = getSupportFragmentManager();

        //drawer item position
        positionFragment = position;
        switch (position){
            case 0:
                Fragment homeFragment = new HomeFragmentActivity();
                homeFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.frame_container, homeFragment).commit();
                break;
            case 1:
                Fragment userFragment =  new UserFragmentActivity();
                userFragment.setArguments(args);
                fragmentManager.beginTransaction().replace(R.id.frame_container, userFragment).commit();
                break;
            case 2:
                logout();
        }

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    private void logout(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //read from sharedPreferences
                        SharedPreferences prefs = getApplicationContext().getSharedPreferences(Constants.USER_PREFERENCES_NAME, Context.MODE_PRIVATE);
                        prefs.edit().clear().commit();

                        Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }

}
