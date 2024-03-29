package com.ethan.FamiCare;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.ethan.FamiCare.sport.Sport1;
import com.ethan.FamiCare.sport.Sport2;
import com.ethan.FamiCare.sport.Sport3;
import com.ethan.FamiCare.sport.Sport4;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link sportFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class sportFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public sportFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment sportFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static sportFragment newInstance(String param1, String param2) {
        sportFragment fragment = new sportFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private View mainview;
    private GridView gridView;
    private CardView card1,card2,card3,card4;
    String[] sportname={"1","2","3","4"};
    int[] images={R.drawable.sport1_1,R.drawable.sport2_1,R.drawable.sport3,R.drawable.sport4_3};
    //private Intent[] sportactivity={new Intent(getActivity(), Sport1.class),new Intent(getActivity(), Sport1.class),new Intent(getActivity(), Sport1.class),new Intent(getActivity(), Sport1.class),new Intent(getActivity(), Sport1.class)};



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainview=inflater.inflate(R.layout.fragment_sport, container, false);
        //gridView=mainview.findViewById(R.id.gridview)  ;
        card1=mainview.findViewById(R.id.card1);
        card2=mainview.findViewById(R.id.card2);
        card3=mainview.findViewById(R.id.card3);
        card4=mainview.findViewById(R.id.card4);
       // SportAdapter sportAdapter=new SportAdapter(sportname,images,getContext());
      //  gridView.setAdapter(sportAdapter);


      card1.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent = new Intent(getActivity(), Sport1.class);
              startActivity(intent);
          }
      });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Sport2.class);
                startActivity(intent);
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Sport3.class);
                startActivity(intent);
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Sport4.class);
                startActivity(intent);
            }
        });

        /*
       gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
           @Override
           public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               if(position==0) {
                   Intent intent = new Intent(getActivity(), Sport1.class);
                   startActivity(intent);
               }
                if (position==1) {
                   Intent intent=new Intent(getActivity(), Sport2.class) ;
                   startActivity(intent);
               }
                if (position==2) {
                   Intent intent=new Intent(getActivity(), Sport3.class) ;
                   startActivity(intent);
               }
               if(position==3) {
                   Intent intent = new Intent(getActivity(), Sport4.class);
                   startActivity(intent);
               }

           }
       });

         */




        return mainview;

    }

    class SportAdapter extends BaseAdapter{
        private String[] sportname;
        private int[] images;
        private android.content.Context context;

        private LayoutInflater layoutInflater;

        public SportAdapter(String[] sportname, int[] images, Context context){
            this.sportname=sportname;
            this.images=images;
            this.context=context;
            layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sportname.length;
        }

        @Override
        public Object getItem(int position) {
            return sportname[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView==null) {
                convertView = layoutInflater.inflate(R.layout.sport_gridview_item, parent, false);
            }
            ImageView imageView=convertView.findViewById(R.id.grid_image);
            TextView textView=convertView.findViewById(R.id.grid_name);

            imageView.setImageResource(images[position]);
            textView.setText(sportname[position]);

            return convertView;
        }

    }
}