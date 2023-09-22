package com.inprog.fragments.community.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.inprog.R;
import com.inprog.fragments.community.model.Postmodel;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder>{

    Context context;
    List<Postmodel> postList;

    public AdapterPost(Context context, List<Postmodel> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rowlayout, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        String posttitle = postList.get(position).getTitle();
        String postdesc = postList.get(position).getDesc();
        String postuname = postList.get(position).getName();
        String postimg = postList.get(position).getPostImage();
        String pfpimg = postList.get(position).getPfpimg();
        holder.posttit.setText(posttitle);
        holder.postdesc.setText(postdesc);
        holder.postuname.setText(postuname);
        Picasso.get().load(postimg).into(holder.postimg);
        Picasso.get().load(pfpimg).placeholder(R.drawable.defaultavatar).into(holder.pfpimag);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    class MyHolder extends RecyclerView.ViewHolder{

        private CircleImageView pfpimag;
        private ImageView postimg;
        private TextView postuname, posttit, postdesc;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            pfpimag = itemView.findViewById(R.id.postuserpfpimg);
            postuname = itemView.findViewById(R.id.postuseruname);
            posttit = itemView.findViewById(R.id.posttitle);
            postimg = itemView.findViewById(R.id.postimg);
            postdesc = itemView.findViewById(R.id.postdesc);
        }
    }
}
