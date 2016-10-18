package com.example.conexaodb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.widget.AdapterView.OnItemClickListener;


public class listaTestesAvaliador extends Activity {
	private EditText campo;
	private TextView idExame;
	DatabaseHandler banco;
	private EditText idade;
	ListView listView ;
	Cursor c = null;
	CursorAdapter dataSource;
	private String id,textoNome,textoIdade;
	final String campos[] = {"data", "_id", "duracao","tipo","idAvaliador","valMaximo","mao"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_listatestesavaliador);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		listView = (ListView) findViewById(R.id.listView1ExameAvaliador);
		campo = (EditText) findViewById(R.id.edtExameAvaliador);
		idade = (EditText) findViewById(R.id.edtIdadeExame);
		super.onCreate(savedInstanceState);
		banco = new DatabaseHandler(this);
		id = extras.getString("id");
		textoNome = extras.getString("nome").toString();
		textoIdade = extras.getString("idade").toString();
		campo.setText(textoNome);
		idade.setText(textoIdade);
		try {
			c = banco.PesquisaSLike(id, "idAvaliador","exame");
			Toast.makeText(listaTestesAvaliador.this, id+"", Toast.LENGTH_LONG).show(); 
			dataSource = new SimpleCursorAdapter(listaTestesAvaliador.this, R.layout.lvtestes, c, campos, new int[] { R.id.tvData, R.id.tvId, R.id.tvTempo , R.id.tvtipo, R.id.tvResp, R.id.tvvalMax, R.id.tvMao},0);
			 if (c.getCount() >0 && c != null){
		                
		            //relaciona o dataSource ao próprio listview
		            listView.setAdapter(dataSource);
		        }else{
		            Toast.makeText(listaTestesAvaliador.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
		        }
			} catch (Exception e){
				 Toast.makeText(listaTestesAvaliador.this, "ERRO: "+e.getMessage().toString(), Toast.LENGTH_LONG).show();
			}
		
			
		listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                    int index, long arg3) {
            	idExame = (TextView) v.findViewById(R.id.tvId);
            	AlertDialog.Builder builder = new AlertDialog.Builder(listaTestesAvaliador.this);

		        builder.setTitle("Confirmação");
		        builder.setMessage("Tem certeza que deseja excluir o exame com ID: "+idExame.getText().toString()+" e todas suas informações?");
		        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

		            public void onClick(DialogInterface dialog, int which) {

						try{
							banco.deletaRegistroExame(idExame.getText().toString());
							Toast.makeText(listaTestesAvaliador.this,"Registro excluído com sucesso!", Toast.LENGTH_SHORT).show();
							c = banco.PesquisaSLike(id, "idAvaliador","exame");
							dataSource.changeCursor(c);
							if (c.getCount() >0 && c != null){
					            //relaciona o dataSource ao próprio listview
					            listView.setAdapter(dataSource);
					        }else{
					        	listView.setAdapter(dataSource);
					            Toast.makeText(listaTestesAvaliador.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
					        
					        }
							
						}catch (Exception e){
							Toast.makeText(listaTestesAvaliador.this,"Erro ao excluir registro. Erro = "+ e.getMessage().toString(),Toast.LENGTH_SHORT).show();
						}
		            }

		        });

		        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();	
                return false;
            }
		}); 
		
		listView.setOnItemClickListener(new OnItemClickListener() {
		    @Override
		    public void onItemClick(AdapterView<?> list, View view, int position, long id) {
		    	Cursor cursor;
		    	TextView valores = (TextView) view.findViewById(R.id.tvId);
		    	cursor=banco.pesquisaDireta("_id="+valores.getText().toString(), "exame");
		    	cursor.moveToFirst();
		    	exibirAlerta("Valores Brutos do exame", cursor.getString(cursor.getColumnIndex("valores")));
		    	

		    	}

		    }
		);
	
	
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (data==null) {
			if (resultCode==Activity.RESULT_CANCELED){
				try{
					c = banco.PesquisaSLike(id, "idAvaliador","exame");
					dataSource.changeCursor(c);
					if (c.getCount() >0 && c != null){
			            listView.setAdapter(dataSource);
			        }else{
			        	listView.setAdapter(dataSource);
			            Toast.makeText(listaTestesAvaliador.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
			        }
					
				}catch (Exception e){
					Toast.makeText(listaTestesAvaliador.this,"Erro ao acessar banco. Erro = "+ e.getMessage().toString(),Toast.LENGTH_SHORT).show();
				}
			}
		}
		else {
			try{
				c = banco.PesquisaSLike(id, "idPessoa","exame");
				dataSource.changeCursor(c);
				if (c.getCount() >0 && c != null){
		            listView.setAdapter(dataSource);
		        }else{
		        	listView.setAdapter(dataSource);
		            Toast.makeText(listaTestesAvaliador.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
		        }
				
			}catch (Exception e){
				Toast.makeText(listaTestesAvaliador.this,"Erro ao acessar banco. Erro = "+ e.getMessage().toString(),Toast.LENGTH_SHORT).show();
			}
			
		}
	}
	
	public void exibirAlerta (String titulo, String mensagem){
    	AlertDialog.Builder alerta = new AlertDialog.Builder(this);
    	alerta.setTitle(titulo);
    	alerta.setMessage(mensagem);
    	alerta.setNeutralButton("OK", null);
    	alerta.create().show();
 }

	@Override
	public void onBackPressed() {
		Intent IntentParent = getIntent();
		setResult(Activity.RESULT_CANCELED, IntentParent);
		banco.close();
		finish();

	}
}