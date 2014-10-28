package com.empresa.oscar.exportando;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by UsuarioRasa on 25/10/2014.
 */
public class Purchase {
    private String proveedor;
    private String producto;
    private String fecha;
    private int cantidad;
    private int id_compra;
    Purchase(String proveedor,String producto,String fecha,int cantidad,int id_compra){
        this.producto=producto;
        this.proveedor=proveedor;
        this.cantidad=cantidad;
        this.id_compra=id_compra;
        this.fecha=fecha;

    }
    public String getProveedor(){
        return proveedor;
    }
    public String getProducto(){
        return producto;
    }public int getCantidad(){
        return cantidad;
    }
    public String getFecha(){
        return fecha;
    }
    public  int getId_compra(){
        return id_compra;
    }

}
class ViewHolderPurchase {
    TextView proveedor_texto;
    TextView producto_texto;
    TextView cantidad_texto;
    TextView fecha_texto;
    Button boton_compra;
}
class purchaseAdapter extends BaseAdapter
{
    protected Activity activity;
    protected ArrayList<Purchase> items;

    public purchaseAdapter(Activity activity, ArrayList<Purchase> items) {
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return  items.get(position).getId_compra();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi=convertView;
        ViewHolderPurchase viewhold;

        if(vi == null|| !(vi.getTag() instanceof ViewHolderLocacion)) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.purchase_list_item, null);

            viewhold=new ViewHolderPurchase();
            viewhold.proveedor_texto=(TextView) vi.findViewById(R.id.proveedor);
            viewhold.producto_texto=(TextView) vi.findViewById(R.id.producto);
            viewhold.cantidad_texto=(TextView) vi.findViewById(R.id.cantidad);
            viewhold.fecha_texto=(TextView) vi.findViewById(R.id.fecha);
            viewhold.boton_compra=(Button)vi.findViewById(R.id.recibido);
        }
        else{
            viewhold = (ViewHolderPurchase)vi.getTag();
        }

        Purchase pr = items.get(position);
        viewhold.proveedor_texto.setText(pr.getProveedor());
        viewhold.producto_texto.setText(pr.getProducto());
        viewhold.cantidad_texto.setText(Integer.toString(pr.getCantidad()));
        viewhold.fecha_texto.setText(pr.getFecha());
        viewhold.boton_compra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Purchase actualpurchase=items.get(position);

               new AlertDialog.Builder(activity)
                        .setTitle(R.string.RecepcionCompra)
                        .setMessage(R.string.ConfirmaRecepcionCompra)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String exito="";
                                // continue with delete
                                try {
                                   exito=new PostPurchaseReception(activity,actualpurchase.getId_compra()).execute().get();
                                    if(exito.equals("exito")){
                                        SharedPreferences prefs = activity.getSharedPreferences("Exporta",Activity.MODE_PRIVATE);
                                        String nick=prefs.getString("Empleado",null);
                                        String pass=prefs.getString("Password",null);
                                        String type=prefs.getString("Type",null);
                                        Intent compra_producto = new Intent(activity, PurchaseActivity.class);
                                        compra_producto.putExtra("nick",nick);
                                        compra_producto.putExtra("pass",pass);
                                        compra_producto.putExtra("type",type);
                                        compra_producto.putExtra("process","compra");
                                        compra_producto.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        compra_producto.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        activity.finish();
                                        activity.startActivity(compra_producto);

                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }

                                Log.e("Registrando","compra "+Integer.toString(actualpurchase.getId_compra()));

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Log.e("Cancela", "compra " + Integer.toString(actualpurchase.getId_compra()));
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });

        return(vi);
    }

}

