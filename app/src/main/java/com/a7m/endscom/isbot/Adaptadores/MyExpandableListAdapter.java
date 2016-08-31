package com.a7m.endscom.isbot.Adaptadores;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.a7m.endscom.isbot.Actividades.CatalogoArticuloActivity;
import com.a7m.endscom.isbot.Actividades.ClientesActivity;
import com.a7m.endscom.isbot.Actividades.CarritoPedidoActivity;
import com.a7m.endscom.isbot.Actividades.NuevoClienteActivity;
import com.a7m.endscom.isbot.Clases.ChildRow;
import com.a7m.endscom.isbot.Clases.ParentRow;
import com.a7m.endscom.isbot.R;

import java.util.ArrayList;

/**
 * Created by A7M on 20/08/2016.
 */
public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ParentRow> parentRowList;
    private ArrayList<ParentRow> originalList;

    public MyExpandableListAdapter(Context context
            , ArrayList<ParentRow> originalList) {
        this.context = context;
        this.parentRowList = new ArrayList<>();
        this.parentRowList.addAll(originalList);
        this.originalList = new ArrayList<>();
        this.originalList.addAll(originalList);
    }
    @Override
    public int getGroupCount() {
        return parentRowList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return parentRowList.get(groupPosition).getChildList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parentRowList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parentRowList.get(groupPosition).getChildList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        ParentRow parentRow = (ParentRow) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_row_plan_trabajo, null);
        }

        TextView heading = (TextView) convertView.findViewById(R.id.parent_text_PlantTrabajo);

        heading.setText(parentRow.getName().trim());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ChildRow childRow = (ChildRow) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater)
                    context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_row_plantrabajo, null);
        }


        ImageView childIcon = (ImageView) convertView.findViewById(R.id.child_icon);


        childIcon.setImageResource(childRow.getIcon());

        if (childRow.getIcon()!= 2130837581){
            Log.d("ICON",String.valueOf(childRow.getIcon()));
        }
        switch (childRow.getEstado()){
            case "0"://SIN DATOS
                childIcon.setColorFilter(ContextCompat.getColor(convertView.getContext(), R.color.ISB_red));
                break;
            case "1":
                childIcon.setColorFilter(ContextCompat.getColor(convertView.getContext(), R.color.colorAccent));
                break;
        }


        final TextView childText = (TextView) convertView.findViewById(R.id.child_text_planTrabajo  );
        childText.setText(childRow.getText().trim());

        final TextView childDir = (TextView) convertView.findViewById(R.id.child_cliente_direccion  );
        childDir.setText(childRow.getNombre().trim());



        final View finalConvertView = convertView;

        childText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent ints = new Intent(finalConvertView.getContext(), NuevoClienteActivity.class);
                ints.putExtra("IdCliente",childRow.getText());
                ints.putExtra("ClsNombre",childRow.getNombre());
                ints.putExtra("CLdir",childRow.getDirec());
                ints.putExtra("Estado",childRow.getEstado());
                finalConvertView.getContext().startActivity(ints);


                //finalConvertView.getContext().startActivity(new Intent(finalConvertView.getContext(), NuevoClienteActivity.class));

            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void filterData(String query) {
        query = query.toLowerCase();
        parentRowList.clear();

        if (query.isEmpty()) {
            parentRowList.addAll(originalList);
        }
        else {
            for (ParentRow parentRow : originalList) {
                ArrayList<ChildRow> childList = parentRow.getChildList();
                ArrayList<ChildRow> newList = new ArrayList<ChildRow>();

                for (ChildRow childRow: childList) {
                    if (childRow.getNombre().toLowerCase().contains(query)) {
                        newList.add(childRow);
                    }
                } // end for (com.example.user.searchviewexpandablelistview.ChildRow childRow: childList)
                if (newList.size() > 0) {
                    ParentRow nParentRow = new ParentRow(parentRow.getName(), newList);
                    parentRowList.add(nParentRow);
                }
            } // end or (com.example.user.searchviewexpandablelistview.ParentRow parentRow : originalList)
        } // end else

        notifyDataSetChanged();
    }
}

