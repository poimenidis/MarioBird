package android.example.com.mariobird;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Κώστας Ποιμενίδης on 13/01/2018.
 */

public class HighscoreAdapter extends BaseAdapter {

    private final Activity context;
    private List<String> id;
    private List<String> images;
    private List<String> rank;
    private List<String> playerName;
    private List<String> highscore;
    private LayoutInflater inflater;


    public HighscoreAdapter(Activity context) {


        this.context=context;
        inflater = (LayoutInflater)context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return id.size();
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

        holder.playerName.setText(playerName.get(position));
        holder.highscore.setText(highscore.get(position));
        holder.rank.setText(String.valueOf(position+1));

        return view;

    };

    public void setDatas(List<String> id, List<String> images, List<String> playerName, List<String> highscore) {
        this.id = id;
        this.playerName = playerName;
        this.highscore = highscore;
        this.images = images;
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView playerName;
        TextView rank;
        TextView highscore;


        public ViewHolder(View rowView) {


            rank = (TextView) rowView.findViewById(R.id.number);

            highscore = (TextView) rowView.findViewById(R.id.highscore);

            playerName = (TextView) rowView.findViewById(R.id.player_name);
        }
    }

    public void clear(){
        playerName.clear();
        highscore.clear();
        notifyDataSetChanged();
    }


    public List<String> getPlayerName() {
        return playerName;
    }

    public List<String> getRank() {
        return rank;
    }

    public List<String> getHighscore() {
        return highscore;
    }

    public List<String> getId() {
        return id;
    }
}