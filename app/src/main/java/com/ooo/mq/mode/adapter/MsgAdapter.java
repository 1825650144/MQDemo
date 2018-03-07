package com.ooo.mq.mode.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ooo.mq.R;
import com.ooo.mq.mode.entity.ItemMsg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 消息适配器
 * Created by dongtengfei on 2017/9/8.
 */

public class MsgAdapter extends RecyclerView.Adapter<MsgAdapter.ViewHolder> {

    private List<ItemMsg> msgList;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    /**
     * 构造方法
     *
     * @param msgList  数据集
     * @param mContext
     */
    public MsgAdapter(List<ItemMsg> msgList, Context mContext) {
        this.msgList = msgList;
        this.mContext = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder holder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_msg, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if (1 == msgList.get(position).getType()) {
            //左侧接收
            holder.llLeft.setVisibility(View.VISIBLE);
            holder.llRight.setVisibility(View.GONE);
            holder.tvDteTimeLeft.setText(formatDate(msgList.get(position).getDateStamp()));
            if (msgList.get(position).getTypeBody() == 2) {
                holder.tvContentLeft.setVisibility(View.GONE);
                holder.llFileLeft.setVisibility(View.VISIBLE);
                String fileName = msgList.get(position).getBody().substring(msgList.get(position).getBody().lastIndexOf("/") + 1, msgList.get(position).getBody().length()).toLowerCase();
                holder.tvFileLeft.setText(fileName);
            } else {
                holder.tvContentLeft.setVisibility(View.VISIBLE);
                holder.llFileLeft.setVisibility(View.GONE);
                holder.tvContentLeft.setText(msgList.get(position).getBody());
            }
        } else if (2 == msgList.get(position).getType()) {
            //右侧接收
            holder.llLeft.setVisibility(View.GONE);
            holder.llRight.setVisibility(View.VISIBLE);
            holder.tvContentRight.setText(msgList.get(position).getBody());
            if (msgList.get(position).getTypeBody() == 2) {
                holder.tvContentRight.setVisibility(View.GONE);
                holder.llFileRight.setVisibility(View.VISIBLE);
                String fileName = msgList.get(position).getBody().substring(msgList.get(position).getBody().lastIndexOf("/") + 1, msgList.get(position).getBody().length()).toLowerCase();
                holder.tvFileRight.setText(fileName);
            } else {
                holder.tvContentRight.setVisibility(View.VISIBLE);
                holder.llFileRight.setVisibility(View.GONE);
                holder.tvContentRight.setText(msgList.get(position).getBody());
            }
            holder.tvDateTimeRight.setText(formatDate(msgList.get(position).getDateStamp()));
        }
        // 文件类型
        if (msgList.get(position).getTypeBody() == 2) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(msgList.get(position).getBody());
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return msgList.get(position).getType();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * 左侧内容，右侧内容，左侧时间，右侧时间
         */
        private TextView tvContentLeft, tvContentRight, tvDteTimeLeft, tvDateTimeRight;
        /**
         * 左侧头像，右侧头像
         */
        private ImageView ivIconLeft, ivIconRight;
        /**
         * 左侧布局，右侧布局
         */
        private LinearLayout llLeft, llRight;
        /**
         * 附件显示
         */
        private LinearLayout llFileRight, llFileLeft;
        private TextView tvFileRight, tvFileLeft;

        public ViewHolder(View itemView) {
            super(itemView);
            tvContentLeft = itemView.findViewById(R.id.tv_content_left);
            tvContentRight = itemView.findViewById(R.id.tv_content_right);
            tvDteTimeLeft = itemView.findViewById(R.id.tv_dateTime_left);
            tvDateTimeRight = itemView.findViewById(R.id.tv_dateTime_right);
            ivIconLeft = itemView.findViewById(R.id.iv_icon_left);
            ivIconRight = itemView.findViewById(R.id.iv_icon_right);

            llLeft = itemView.findViewById(R.id.ll_left);
            llRight = itemView.findViewById(R.id.ll_right);

            llFileRight = itemView.findViewById(R.id.ll_file_right);
            llFileLeft = itemView.findViewById(R.id.ll_file_left);
            tvFileRight = itemView.findViewById(R.id.tv_file_right);
            tvFileLeft = itemView.findViewById(R.id.tv_file_left);
        }
    }

    /**
     * 格式化为 **月**日 **:**:**
     *
     * @param date 时间戳
     * @return 格式后的时间
     */
    public static String formatDate(long date) {
        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        Date d1 = new Date(date);
        return format.format(d1);
    }

    /**
     * item点击监听事件
     */
    public interface OnItemClickListener {
        void onItemClick(String filePath);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
