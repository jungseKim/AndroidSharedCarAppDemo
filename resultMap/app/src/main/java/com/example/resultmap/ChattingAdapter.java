package com.example.resultmap;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.CustomViewHolder> {

    private ArrayList<ChattingData> arrayList;
    private int inOut;
    private int imgCheck;

    public ChattingAdapter(ArrayList<ChattingData> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ChattingAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        inOut = viewType;
        if(viewType == 0)
        {
            if(imgCheck == 1)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_your_img,parent,false);
                CustomViewHolder holder = new CustomViewHolder(view);
                return holder;
            } else {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list,parent,false);
                CustomViewHolder holder = new CustomViewHolder(view);
                return holder;
            }


        } else if(viewType == 2)
        {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_in_out,parent,false);
            CustomViewHolder holder = new CustomViewHolder(view);
            return holder;
        } else {
            System.out.println("ChattingAdapter.CustonViewHolder 에서 imgCheck : " + imgCheck);
            if(imgCheck == 1)
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_img,parent,false);
                CustomViewHolder holder = new CustomViewHolder(view);
                return holder;
            }
            else
            {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my,parent,false);
                CustomViewHolder holder = new CustomViewHolder(view);
                return holder;
            }

//            CustomViewHolder holder = new CustomViewHolder(view);
//            return holder;
        }






    }

    @Override
    public void onBindViewHolder(@NonNull ChattingAdapter.CustomViewHolder holder, int position) {

        if(inOut == 2)
        {
            if(holder.nickName_first != null)
                holder.nickName_first.setText(arrayList.get(position).getNickName()+"님 이 들어오셨습니다.");
        } else {
            if(imgCheck == 1)
            {
                if(holder.textViewImage != null)
                    Glide.with(holder.textViewImage).load(arrayList.get(position).getChattingText()).into(holder.textViewImage);
            }else {
                if(holder.chattingText !=null)
                    holder.chattingText.setText(arrayList.get(position).getChattingText());
            }
//            holder.profile.setImage(arrayList.get(position).getProfile());
            Glide.with(holder.profile).load(arrayList.get(position).getProfile()).into(holder.profile);
            System.out.println("name : " + arrayList.get(position).getNickName() + ", profile : " + arrayList.get(position).getProfile());
            holder.nickName.setText(arrayList.get(position).getNickName());


//            holder.itemView.setTag(position);
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String curNickName= holder.nickName.getText().toString();
//                    Toast.makeText(v.getContext(),curNickName,Toast.LENGTH_SHORT).show();
//                }
//            });
        }




    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0);
    }

//    public void remove(int position)
//    {
//        try{
//            arrayList.remove(position);
//            notifyItemRemoved(position);
//        } catch(IndexOutOfBoundsException e) {
//            e.printStackTrace();
//        }
//    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView profile;
        protected TextView nickName;
        protected TextView chattingText;
        protected TextView nickName_first;
        protected ImageView textViewImage;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.profile=(ImageView) itemView.findViewById(R.id.profile);
            this.nickName=(TextView) itemView.findViewById(R.id.nickName);
            this.chattingText=(TextView) itemView.findViewById(R.id.chatting_text);
            this.nickName_first = (TextView) itemView.findViewById(R.id.nickName_first);
            this.textViewImage= (ImageView) itemView.findViewById(R.id.textViewImage);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChattingData chattingData=arrayList.get(position);
        imgCheck = chattingData.getImgCheck();
        System.out.println("getItemViewType에서 imgCheck : " + imgCheck);

        if (chattingData.getInOut() == 2)
            return 2;
        else if (chattingData.getNickName().equals(chattingData.getMyName()))
            return 1;
        else
            return 0;
    }
}
