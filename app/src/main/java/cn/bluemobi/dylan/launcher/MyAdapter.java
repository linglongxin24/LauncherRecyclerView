package cn.bluemobi.dylan.launcher;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.bluemobi.dylan.simple.SimpleAdapter;

/**
 * <p/>
 * Date: 16/6/7 16:40
 * Author: linglongxin24@163.com
 * <p/>
 */
public class MyAdapter extends SimpleAdapter<Bean, MyAdapter.ViewHolder> {
    private String Tag = "LauncherView";
    private boolean isLongPress = false;

    public MyAdapter setLongPress(boolean longPress) {
        isLongPress = longPress;
        return this;
    }

    public boolean isLongPress() {
        return isLongPress;
    }

    public MyAdapter(List<List<Bean>> mData) {
        super(mData);
    }


    @Override
    protected ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sample_vertical, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindSubViewHolder(final ViewHolder holder, int mainPosition, int subPosition) {
        bindData(holder, mainPosition, subPosition);
    }

    /**
     * 绑定数据
     *
     * @param holder       ViewHolder
     * @param mainPosition 父级位置
     * @param subPosition  子级位置
     */
    private void bindData(final ViewHolder holder, final int mainPosition, final int subPosition) {
        final TextView tv_name = (TextView) holder.itemView.findViewById(R.id.tv_name);
        if (getSubItemCount(mainPosition) == 1) {
            tv_name.setText(getSubSource(mainPosition).get(0).getName());
        } else {
            if (subPosition == -1) {
                tv_name.setText(getSubSource(mainPosition).get(0).getFolderName());
            } else {
                tv_name.setText(getSubSource(mainPosition).get(subPosition).getName());
            }
        }

        final TextView tv_del = (TextView) holder.itemView.findViewById(R.id.tv_del);
        tv_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(tv_del.getContext(), "删除", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView tv_2 = (TextView) holder.itemView.findViewById(R.id.tv_2);
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(tv_del.getContext(), "编辑", Toast.LENGTH_SHORT).show();
            }
        });
        final TextView tv3 = (TextView) holder.itemView.findViewById(R.id.tv3);
        tv3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(tv_del.getContext(), "信息", Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getSubItemCount(mainPosition) == 1) {
                    setLongPress(false);
                    notifyDataSetChanged();
                }
            }
        });
        final View view = holder.itemView.findViewById(R.id.view);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(view.getContext(), "parentIndex: " + mainPosition + "\nindex: " + subPosition, Toast.LENGTH_SHORT).show();
            }
        });

        if (isLongPress) {
            tv_del.setVisibility(View.VISIBLE);
            tv_2.setVisibility(View.VISIBLE);
            tv3.setVisibility(View.VISIBLE);
        } else {
            tv_del.setVisibility(View.GONE);
            tv_2.setVisibility(View.GONE);
            tv3.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDragStart(ViewHolder viewHolder, int parentIndex, int index) {
        super.onDragStart(viewHolder, parentIndex, index);
        setLongPress(true);
        notifyDataSetChanged();
    }

    @Override
    protected void onDragAnimationEnd(ViewHolder viewHolder, int parentIndex, int index) {
        super.onDragAnimationEnd(viewHolder, parentIndex, index);
    }

    /**
     * 交换位置
     *
     * @param selectedPosition 当前选择的item位置
     * @param targetPosition   要移动到的位置
     */
    @Override
    protected void onMove(int selectedPosition, int targetPosition) {
        super.onMove(selectedPosition, targetPosition);
        Log.d(Tag, "--onMove--selectedPosition=" + selectedPosition + "targetPosition=" + targetPosition);
    }

    /**
     * 拖动合并
     *
     * @param selectedPosition 选中的位置
     * @param targetPosition   合并的位置
     */
    @Override
    protected void onMerged(int selectedPosition, int targetPosition) {
        super.onMerged(selectedPosition, targetPosition);
        List<Bean> beanList = mData.get(targetPosition);
        for (int i = 0; i < beanList.size(); i++) {
            Bean bean = beanList.get(i);
            if (bean.isFolder()) {
                break;
            }
            bean.setFolder(true);
            bean.setFolderName("文件夹" + (getItemCount() + 1));
            beanList.set(i, bean);
        }
        mData.set(targetPosition, beanList);
        if (selectedPosition < targetPosition) {
            notifyItemChanged(targetPosition - 1);
        } else {
            notifyItemChanged(targetPosition);
        }

        Log.d(Tag, "--onMerged--selectedPosition=" + selectedPosition + "targetPosition="
                + targetPosition + "getSubSource(mainPosition).get(0).getFolderName()="
                + getSubSource(targetPosition).get(0).getFolderName());
    }

    @Override
    protected void onSubMove(List<Bean> beans, int selectedPosition, int targetPosition) {
        super.onSubMove(beans, selectedPosition, targetPosition);
        Log.d(Tag, "--onSubMove--selectedPosition=" + selectedPosition + "targetPosition=" + targetPosition);
    }

    /**
     * 显示子集对话框回调
     *
     * @param dialog         对话框
     * @param parentPosition 父级位置
     */
    @Override
    protected void onSubDialogShow(Dialog dialog, final int parentPosition) {
        super.onSubDialogShow(dialog, parentPosition);
        final EditText et_folder = (EditText) dialog.findViewById(com.bluemobi.dylan.R.id.et_folder);
        et_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_folder.setFocusable(true);
                et_folder.setFocusableInTouchMode(true);
                et_folder.requestFocus();
            }
        });
        et_folder.setText(getSubSource(parentPosition).get(0).getFolderName());
        et_folder.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                getSubSource(parentPosition).get(0).setFolderName(et_folder.getText().toString().trim());
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                et_folder.setFocusable(false);
                et_folder.setFocusableInTouchMode(false);
            }
        });
    }

    @Override
    protected void onBindMainViewHolder(ViewHolder holder, int position) {
        bindData(holder, position, -1);
//        super.onBindMainViewHolder(holder, position);
    }

    @Override
    public View getView(ViewGroup parent, View convertView, int mainPosition, int subPosition) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_inner, parent, false);
        }
        return convertView;
    }

    @Override
    protected void onItemClick(View view, int parentIndex, int index) {

//        Toast.makeText(view.getContext(), "parentIndex: " + parentIndex + "\nindex: " + index, Toast.LENGTH_SHORT).show();
    }

    static class ViewHolder extends SimpleAdapter.ViewHolder {
        TextView tv_del;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_del = (TextView) itemView.findViewById(R.id.tv_del);
        }
    }

}
