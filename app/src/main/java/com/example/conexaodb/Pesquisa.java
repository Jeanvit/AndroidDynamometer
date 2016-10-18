package com.example.conexaodb;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.InputType;
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


public class Pesquisa extends Activity implements OnGesturePerformedListener{
	private ImageButton pesquisar;
	GestureLibrary mLibrary;
	private EditText campo;
	private Spinner opcoes;
	Intent data = new Intent();
	Cursor temporario;
	String itemSelecionado;
	DatabaseHandler banco;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_pesquisa);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.itensPesquisa, android.R.layout.simple_spinner_item);
		final String campos[] = {"nome", "_id", "telefone", "email"};
		final ListView listView = (ListView) findViewById(R.id.listView1);
		campo = (EditText) findViewById(R.id.edtPessoa);
		pesquisar = (ImageButton) findViewById(R.id.btnNova);
		opcoes = (Spinner) findViewById(R.id.spnOpcoes);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		opcoes.setAdapter(adapter);
		
		super.onCreate(savedInstanceState);
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		   if (!mLibrary.load()) {
		     finish();
		   }
		 
		   GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures1);
		   gestures.addOnGesturePerformedListener(this);
		   gestures.setGestureVisible(false);
		pesquisar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CursorAdapter dataSource;
				Cursor c = null;
				banco = new DatabaseHandler(Pesquisa.this);
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
			            dataSource = new SimpleCursorAdapter(Pesquisa.this, R.layout.lvpesquisa1, c, campos, new int[] { R.id.tvData, R.id.tvId, R.id.tvTelefone , R.id.tvMail },0);
			             
			            //relaciona o dataSource ao próprio listview
			            listView.setAdapter(dataSource);
			        }else{
			            Toast.makeText(Pesquisa.this, "Nenhum registro encontrado", Toast.LENGTH_LONG).show(); 
			        }
			}
		});
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			 
			 @Override
			 public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				 Intent intent = getIntent();
				 Bundle extras = intent.getExtras();
				 TextView nome = (TextView) view.findViewById(R.id.tvData);
				 TextView temp = (TextView) view.findViewById(R.id.tvId);
				 if (extras.getString("tela").equals("cadastro")==true){
					 if (banco.verificaSeEAvaliador(temp.getText().toString())==false){
						 String playerChanged = nome.getText().toString();
						 Toast.makeText(Pesquisa.this,"Você escolheu : " + playerChanged, Toast.LENGTH_SHORT).show();
						 data.putExtra("valor", temp.getText().toString());
						 setResult(2, data);
						 banco.close();
						 finish();
					 } else {
						 String playerChanged = nome.getText().toString();
						 Toast.makeText(Pesquisa.this,"Para editar " + playerChanged +" vá ao cadastro de avaliadores", Toast.LENGTH_SHORT).show();
					 }
				 }else{
					 temporario = banco.PesquisaSLike(temp.getText().toString(), "idPessoa","exame");
						 if (temporario.getCount() >0 && temporario != null){
							 	Bundle pessoa = new Bundle();			
								Intent proximaTela = new Intent();
				                proximaTela.setClass(Pesquisa.this,
				                        Evolucao.class);
				                pessoa.putString("id", temp.getText().toString());
				                pessoa.putString("nome", nome.getText().toString());
				                proximaTela.putExtras(pessoa);
				                startActivity(proximaTela);
						 }
						 else {
							exibirAlerta("Informação","Não existem exames para essa Pessoa"); 
						 }
				 }
				 
			 }
			 });
				
				
	}
	
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		   ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		 
		   if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
		     String result = predictions.get(0).name;
		     Intent intent = getIntent();
			 Bundle extras = intent.getExtras();
		 
		     if ("slideEsquerda".equalsIgnoreCase(result) && extras.getString("tela").equals("cadastro")==false) {
		    	 Intent intente = new Intent();
	                intente.setClass(Pesquisa.this,
	                         MainActivity.class);
	                startActivity(intente);
	                finish();
		     }
		     if ("slideDireita".equalsIgnoreCase(result) && extras.getString("tela").equals("cadastro")==false) {
					Intent intente = new Intent();
	                intente.setClass(Pesquisa.this,
	                         CadastroAvaliadores.class);
	                startActivity(intente);
	                finish();
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
	 
	 
	/*@Override
	public void onBackPressed() {
		setResult(-1);
		banco.close();
		finish();
    }*/
}
