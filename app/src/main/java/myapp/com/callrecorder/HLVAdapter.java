package myapp.com.callrecorder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by SSPL on 12/18/17.
 */
class HLVAdapter extends RecyclerView.Adapter<HLVAdapter.ViewHolder> {
    ArrayList<String> Name;
    ArrayList<String> misscallNumber;
    ArrayList<String> misscallDateTime;
    Context context;

    public HLVAdapter(Context context, ArrayList<String> misscallNumber, ArrayList<String> misscallDateTime, ArrayList<String> Name) {
        super();
        this.context = context;
        this.misscallNumber = misscallNumber;
        this.misscallDateTime = misscallDateTime;
        this.Name = Name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.grid_item, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        if (Name.get(i).equals("")) {
            viewHolder.tvName.setVisibility(View.GONE);
        }
        viewHolder.tvName.setText(Name.get(i));
        viewHolder.tvSpecies.setText(misscallNumber.get(i));
        viewHolder.tvdateTime.setText(misscallDateTime.get(i));

        viewHolder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (isLongClick) {
                    Toast.makeText(context, "#" + position + " - " + misscallNumber.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                    context.startActivity(new Intent(context, MainActivity.class));
                } else {
                    Toast.makeText(context, "#" + position + " - " + misscallNumber.get(position), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return misscallNumber.size();
        //return misscallDateTime.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvName, tvSpecies, tvdateTime;
        private ItemClickListener clickListener;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvSpecies = (TextView) itemView.findViewById(R.id.tv_species);
            tvdateTime = (TextView) itemView.findViewById(R.id.tv_dateTime);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }
        @Override
        public void onClick(View view) {
            clickListener.onClick(view, getPosition(), false);
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            return true;
        }
    }
}