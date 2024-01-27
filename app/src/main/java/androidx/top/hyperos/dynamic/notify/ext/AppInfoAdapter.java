package androidx.top.hyperos.dynamic.notify.ext;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.top.hyperos.dynamic.notify.R;

import java.util.ArrayList;
import java.util.List;

public class AppInfoAdapter extends BaseAdapter {
    private Context mContext;
    private List<AppInfo> mInfo;
    public AppInfoAdapter(Context context, List<AppInfo> info) {
        this.mContext = context;
        this.mInfo = info;
    }

    private class ViewHolder {
        TextView appName;
        TextView appPackageName;
        TextView appState;
        ImageView appIcon;
    }

    @Override
    public int getCount() {
        return mInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return mInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_appinfo, null);
            LinearLayout layout = convertView.findViewById(R.id.app_info_layout);
            layout.setBackgroundDrawable(Tools.getRadiusBackground(Tools.getColor(R.color.pale), 25));
            viewHolder = new ViewHolder();
            viewHolder.appName = convertView.findViewById(R.id.app_name);
            viewHolder.appPackageName = convertView.findViewById(R.id.app_package);
            viewHolder.appState = convertView.findViewById(R.id.app_state);
            viewHolder.appIcon = convertView.findViewById(R.id.app_icon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.appName.setText(mInfo.get(position).getAppName());
        viewHolder.appPackageName.setText(mInfo.get(position).getPackageName());
        viewHolder.appState.setText(mInfo.get(position).getState());
        viewHolder.appIcon.setImageDrawable(mInfo.get(position).getIcon());
        return convertView;
    }

    public void rearrangeItems() {
        List<AppInfo> matchedItems = new ArrayList<>();
        List<AppInfo> unmatchedItems = new ArrayList<>();

        for (AppInfo item : mInfo) {
            if (matchesCondition(item)) {
                matchedItems.add(item);
            } else {
                unmatchedItems.add(item);
            }
        }
        mInfo.clear();
        mInfo.addAll(matchedItems);
        mInfo.addAll(unmatchedItems);
        notifyDataSetChanged();
    }

    private boolean matchesCondition(AppInfo item) {
        if (item.getStateBool()) {
            return true;
        }
        return false;
    }

}
