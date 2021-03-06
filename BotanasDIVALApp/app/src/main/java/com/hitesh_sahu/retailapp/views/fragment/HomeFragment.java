

package com.hitesh_sahu.retailapp.views.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hitesh_sahu.retailapp.R;
import com.hitesh_sahu.retailapp.domain.ProductCategoryLoaderTask;
import com.hitesh_sahu.retailapp.models.Categories;
import com.hitesh_sahu.retailapp.models.DataCenter;
import com.hitesh_sahu.retailapp.utils.Utils;
import com.hitesh_sahu.retailapp.utils.Utils.AnimationType;
import com.hitesh_sahu.retailapp.views.activities.ECartHomeActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    int mutedColor = R.attr.colorPrimary;
    private CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView recyclerView;
    private DatabaseReference mDatabaseRef;
    private boolean doubleBackToExitPressedOnce;
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    private Handler mHandler = new Handler();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_product_category, container, false);

        view.findViewById(R.id.search_item).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                    Utils.switchFragmentWithAnimation(R.id.frag_container,
                            new SearchProductFragment(),
                            ((ECartHomeActivity) getActivity()), null,
                            AnimationType.SLIDE_UP);

                    }
                });

        final Toolbar toolbar = (Toolbar) view.findViewById(R.id.anim_toolbar);
        ((ECartHomeActivity) getActivity()).setSupportActionBar(toolbar);
        ((ECartHomeActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ECartHomeActivity) getActivity()).getmDrawerLayout()
                        .openDrawer(GravityCompat.START);
            }
        });

        collapsingToolbar = view.findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle("Categorias");

        ImageView header = view.findViewById(R.id.header);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {

                mutedColor = palette.getMutedColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        recyclerView = view.findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        mDatabaseRef=FirebaseDatabase.getInstance().getReference().child("Categories");

        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Categories> mListData=new ArrayList<>();
                for(DataSnapshot child: dataSnapshot.getChildren()){
                    Categories mData= child.getValue(Categories.class);
                    mData.setCategoryKey(child.getKey());
                    mListData.add(mData);
                }

                DataCenter.getCenterRepository().setListOfCategory(mListData);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });

        new ProductCategoryLoaderTask(recyclerView, getActivity(), (TextView) getActivity().findViewById(R.id.new_offers_banner)).execute();

//
//		if (simpleRecyclerAdapter == null) {
//			simpleRecyclerAdapter = new CategoryListAdapter(getActivity());
//			recyclerView.setAdapter(simpleRecyclerAdapter);
//
//			simpleRecyclerAdapter
//					.SetOnItemClickListener(new OnItemClickListener() {
//
//						@Override
//						public void onItemClick(View view, int position) {
//
//							if (position == 0) {
//								DataCenter.getCenterRepository()
//										.getAllElectronics();
//							} else if (position == 1) {
//								DataCenter.getCenterRepository()
//										.getAllFurnitures();
//							}
//							Utils.switchFragmentWithAnimation(
//									R.id.frag_container,
//									new ProductOverviewFragment(),
//									((ECartHomeActivity) getActivity()), null,
//									AnimationType.SLIDE_LEFT);
//
//						}
//					});
//		}

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (doubleBackToExitPressedOnce) {
                        // super.onBackPressed();

                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                        }

                        getActivity().finish();

                        return true;
                    }

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(),
                            "Presione otra vez para salir de la aplicación",
                            Toast.LENGTH_SHORT).show();

                    mHandler.postDelayed(mRunnable, 2000);

                }
                return true;
            }
        });

        return view;

    }

}
