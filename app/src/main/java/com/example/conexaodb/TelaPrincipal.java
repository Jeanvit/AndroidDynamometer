package com.example.conexaodb;
//TO DO
/*String msg = "message";
Intent i = new Intent(Activity_A.this, Activity_B.class);
i.putExtra("keyMessage", msg);
startActivity(i);
In Activity_B

Bundle extras = getIntent().getExtras();
String msg = extras.getString("keyMessage");*/

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

public class TelaPrincipal extends Activity {
	ImageButton iniciar,config,evolucao,avaliadores;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telaprincipal);
		iniciar = (ImageButton) findViewById(R.id.btnCadastro);
		config = (ImageButton) findViewById(R.id.btnConfig);
		evolucao = (ImageButton) findViewById(R.id.btnEvol);
		avaliadores = (ImageButton) findViewById(R.id.btnCadastroAvaliadores);
		iniciar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
                intent.setClass(TelaPrincipal.this,
                        MainActivity.class);
                startActivity(intent);
				
			}
		});
		
		config.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						Intent intent = new Intent();
		                intent.setClass(TelaPrincipal.this,
		                        Configuracoes.class);
		                startActivity(intent);
						
					}
		});
		
		evolucao.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle extra = new Bundle();			
				Intent intent = new Intent();
                intent.setClass(TelaPrincipal.this,
                        Pesquisa.class);

				extra.putString("tela", "principal");
				intent.putExtras(extra);
                startActivity(intent);
				

				
			}
		});
		
				
		avaliadores.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {		
				Intent intent = new Intent();
                intent.setClass(TelaPrincipal.this,
                        CadastroAvaliadores.class);
                startActivity(intent);
				
			}
		});
	}
	
}
