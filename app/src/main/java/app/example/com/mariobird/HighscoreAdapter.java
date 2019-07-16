package app.example.com.mariobird;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

/**
 * Created by Κώστας Ποιμενίδης on 13/01/2018.
 */

public class HighscoreAdapter extends BaseAdapter {

    private final Activity context;
    private List<UserClass> users;
    private LayoutInflater inflater;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();


    public HighscoreAdapter(Activity context) {


        this.context=context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder ;

        if (view == null) {
            view = inflater.inflate(R.layout.highscore_object, null,true);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }

        holder.playerName.setText(users.get(position).getName());
        holder.highscore.setText(users.get(position).getScore());
        holder.rank.setText(String.valueOf(position+1));
        Uri uri = Uri.parse(users.get(position).getImage());
        holder.profile.setImageURI(uri);

        if(users.get(position).getId().equals(mAuth.getUid())){
            holder.rank.setTextAppearance(context, R.style.boldText);
            holder.playerName.setTextAppearance(context, R.style.boldText);
            holder.highscore.setTextAppearance(context, R.style.boldText);
            holder.rank.setTextColor(ContextCompat.getColor(context, R.color.antique_white));
            holder.playerName.setTextColor(ContextCompat.getColor(context, R.color.antique_white));
            holder.highscore.setTextColor(ContextCompat.getColor(context, R.color.antique_white));
        }
        else{
            holder.rank.setTextAppearance(context, R.style.normalText);
            holder.playerName.setTextAppearance(context, R.style.normalText);
            holder.highscore.setTextAppearance(context, R.style.normalText);
            holder.rank.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.playerName.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.highscore.setTextColor(ContextCompat.getColor(context, R.color.white));
        }

        return view;

    };

    public void setDatas(List<UserClass> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView playerName;
        TextView rank;
        TextView highscore;
        SimpleDraweeView profile;


        public ViewHolder(View rowView) {

            profile = rowView.findViewById(R.id.profile);

            rank = (TextView) rowView.findViewById(R.id.number);

            highscore = (TextView) rowView.findViewById(R.id.highscore);

            playerName = (TextView) rowView.findViewById(R.id.player_name);

        }
    }

    public void clear(){
        users.clear();
        notifyDataSetChanged();
    }


    public List<UserClass> getUsers() {
        return users;
    }
}