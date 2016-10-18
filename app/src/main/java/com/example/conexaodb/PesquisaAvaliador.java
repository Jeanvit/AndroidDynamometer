package com.example.conexaodb;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.widget.AdapterView.OnItemClickListener;


public class PesquisaAvaliador extends Activity{
	private ImageButton pesquisar;
	private EditText campo;
	private Spinner opcoes;
	Intent data = new Intent();
	Cursor temporario;
	String itemSelecionado,Senha;
	DatabaseHandler banco;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pesquisaavaliador);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.itensPesquisa, android.R.layout.simple_spinner_item);
		final String campos[] = {"nome", "_id", "telefone", "email"};
		final ListView listView = (ListView) findViewById(R.id.listView1Ava);
		campo = (EditText) findViewById(R.id.edtAvaPessoa);
		pesquisar = (ImageButton) findViewById(R.id.btnAvaNova);
		opcoes = (Spinner) findViewById(R.id.spnAvaOpcoes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		opcoes.setAdapter(adapter);
		
		super.onCreate(savedInstanceState);
		   
		pesquisar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CursorAdapter dataSource;
				Cursor c = null;
				banco = new DatabaseHandler(PesquisaAvaliador.this);
				itemSelecionado= opcoes.getSelectedItem().toString();
				if (itemSelecionado.equals("Nome"))
					c = banco.PesquisaLike( campo.getText().toString(), "nome","pessoa");
				if (itemSelecionado.equals("ID"))
					c = banco.PesquisaLike( campo.getText().toString(), "_id","pessoa");
				if (itemSelecionado.equals("Email"))
					c = banco.PesquisaLike( campo.getText().toString(), "email","pessoa");
				if (itemSelecionado.equals("Cidade"))
					c = banco.PesquisaLike( campo.getText().toString(), "cidade","pessoa");
				
				 if (c.getCount() >0 && c != null){
			            dataSource = new SimpleCursorAdapter(PesquisaAvaliador.this, R.layout.lvpesquisa1, c, campos, new int[] { R.id.tvData, R.id.tvId, R.id.tvTelefone , R.id.tvMail },0);
			            listView.setAdapter(dataSource);
			        }else{
			            Toast.makeText(PesquisaAvaliador.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
			        }
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			 
			 @Override
			 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				 Cursor pesquisa;
				 TextView nome = (TextView) view.findViewById(R.id.tvData);
				 TextView temp = (TextView) view.findViewById(R.id.tvId);
				 pesquisa = banco.PesquisaSLike(temp.getText().toString(), "idPessoa", "avaliador");
				 pesquisa.moveToFirst();
					 String playerChanged = nome.getText().toString();
					 if (banco.verificaSeEAvaliador(temp.getText().toString())==true){
						 Toast.makeText(PesquisaAvaliador.this,"Você escolheu : " + playerChanged, Toast.LENGTH_SHORT).show();
						 data.putExtra("valor", temp.getText().toString());
						 data.putExtra("nome", nome.getText().toString());
						 data.putExtra("senha", pesquisa.getString(pesquisa.getColumnIndex("senha")));
						 setResult(2, data);
						 banco.close();
						 finish();
					 } else {
						 Toast.makeText(PesquisaAvaliador.this,"A pessoa : " + playerChanged+ " não é um avaliador", Toast.LENGTH_SHORT).show(); 
					}
			 }
			 
		});
				
				
	}
	
	
	 public void exibirAlerta (String titulo, String mensagem){
	    	AlertDialog.Builder alerta = new AlertDialog.Builder(this);
	    	alerta.setTitle(titulo);
	    	alerta.setMessage(mensagem);
	    	alerta.setNeutralButton("OK", null);
	    	alerta.create().show();
	    }
	/*@Override
	public void onBackPressed() {
		setResult(-1);
		banco.close();
		finish();
    }*/
}
