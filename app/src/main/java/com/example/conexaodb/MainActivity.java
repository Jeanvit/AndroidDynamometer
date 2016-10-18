package com.example.conexaodb;
//TO DO
/*String msg = "message";
Intent i = new Intent(Activity_A.this, Activity_B.class);
i.putExtra("keyMessage", msg);
startActivity(i);
In Activity_B

Bundle extras = getIntent().getExtras();
String msg = extras.getString("keyMessage");*/

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGesturePerformedListener;
import android.gesture.Prediction;
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

public class MainActivity extends Activity implements OnGesturePerformedListener {
	private Spinner spnSexo;
	boolean atualizar=false;
	String temp=null;
	EditText nome;
	ImageButton exame;
	EditText telefone;
	EditText email;
	EditText endereco;
	EditText cidade;
	EditText observacao,anotacao;
	EditText rg;
	GestureLibrary mLibrary;
	ImageButton editar;
	ImageButton salvar;
	ImageButton excluir;
	EditText nascimento;
	boolean retorno;
	DatabaseHandler banco = new DatabaseHandler(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sexos, android.R.layout.simple_spinner_item);
		super.onCreate(savedInstanceState);
		Cursor c;
		setContentView(R.layout.testes);
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		   if (!mLibrary.load()) {
		     finish();
		   }
		 
		   GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
		   gestures.addOnGesturePerformedListener(this);
		   gestures.setGestureVisible(false);
		
		salvar = (ImageButton) findViewById(R.id.btnSalva);
		exame = (ImageButton) findViewById(R.id.btnExames);
		observacao = (EditText) findViewById(R.id.edtObservacao);
		anotacao = (EditText) findViewById(R.id.edtAnotacao);
		nascimento = (EditText) findViewById(R.id.editText4);
		excluir = (ImageButton) findViewById(R.id.btnExcluir);
		ImageButton pesquisa = (ImageButton) findViewById(R.id.Button01);
		nome = (EditText) findViewById(R.id.editText6);
		editar =(ImageButton) findViewById(R.id.btnEditar);
		telefone = (EditText) findViewById(R.id.EditText01);
		email = (EditText) findViewById(R.id.editText2);
		endereco = (EditText) findViewById(R.id.editText3);
		cidade = (EditText) findViewById(R.id.editText5);
		rg = (EditText) findViewById(R.id.editText7);
		spnSexo = (Spinner) findViewById(R.id.spnSexo);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnSexo.setAdapter(adapter);
		editar.setEnabled(false);
		exame.setEnabled(false);
		excluir.setEnabled(false);
		
		salvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utilidades validar = new Utilidades();
				Pessoa p = new Pessoa();
				p.setNome(nome.getText().toString());
				p.setTelefone(telefone.getText().toString());
				p.setEmail(email.getText().toString());
				p.setEndereco(endereco.getText().toString());
				p.setCidade(cidade.getText().toString());
				p.setRg(rg.getText().toString());
				p.setNascimento(nascimento.getText().toString());
				p.setAnotacao(anotacao.getText().toString());
				p.setObservacao(observacao.getText().toString());
					if (atualizar==false){
						if((p.getNome().isEmpty())==false && p.getNascimento().isEmpty()==false){
							if(spnSexo.getSelectedItem().toString().equals("Masculino"))
								p.setSexo("Masculino");
							if(spnSexo.getSelectedItem().toString().equals("Feminino"))
								p.setSexo("Feminino");
							if(spnSexo.getSelectedItem().toString().equals("Outro"))
								p.setSexo("Outro");
							try{
								if (p.getNascimento().isEmpty()==true) {
										banco.adicionarPessoa(p);
										exibirAlerta("Banco", "Salvo com sucesso");
										limparControles();
								}else {
									boolean dataValida = validar.validarData(nascimento.getText().toString());
									if(dataValida==true){
										banco.adicionarPessoa(p);
										exibirAlerta("Banco", "Salvo com sucesso");
										limparControles();
									}
									else  Toast.makeText(MainActivity.this, "Por favor, preencha a data correntamente", Toast.LENGTH_LONG).show(); 
								}
							}catch (Exception e){
								exibirAlerta("ERRO","Erro ao salvar informações: " + e.getMessage().toString());
							}
							
						} else exibirAlerta("ERRO", "Por favor, preencha os campos Nome e Nascimento");
					}else{
						banco.alteraRegistro(p, temp);
						editar.setEnabled(true);
						excluir.setEnabled(true);
						salvar.setEnabled(false);
						exibirAlerta("Banco de dados", "Registro alterado com sucesso!");
						desabilitarControles();
						
					}
				
			}
		});
		
		exame.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putString("nome",nome.getText().toString());
				extras.putString("id", temp);
				extras.putString("idade", Utilidades.calculaIdade(nascimento.getText().toString(),"dd/MM/yyyy"));
				Intent i = new Intent(MainActivity.this, listaTestes.class);
				i.putExtras(extras);
				startActivity(i);
			}
		});
		
		nascimento.addTextChangedListener(new TextWatcher() {  
            boolean isUpdating;  
              
            @Override  
            public void onTextChanged(CharSequence s, int start, int before, int after) {  
                if (isUpdating) {  
                    isUpdating = false;  
                    return;  
                }  
                  
                boolean hasMask = s.toString().indexOf('/') > -1;  
                String str = s.toString().replaceAll("[/]", "");  
                if (after > before) {  
                    if (str.length() > 1) {  
                        nascimento.requestFocus();  
                        str = str.substring(0, 2) + '/' + str.substring(2);  
                    }  
                    if (str.length() > 5) {  
                        str = str.substring(0, 5) + '/' + str.substring(5);  
                    }  
                    isUpdating = true;  
                    nascimento.setText(str);  
                    nascimento.setSelection(nascimento.getText().length());  
                } else {  
                    isUpdating = true;  
                    nascimento.setText(str);  
                    nascimento.setSelection(Math .max(0, Math .min(hasMask ? start - before : start, str.length())));  
                }  
                  
            }  
              
            @Override  
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {  
                  
            }  
              
            @Override  
            public void afterTextChanged(Editable s) {  
                  
            }  
        }); 
		
		
		editar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				salvar.setEnabled(true);
				excluir.setEnabled(false);
				habilitarControles();
				editar.setEnabled(false);
				atualizar=true;
				
			}
		});
		
		excluir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

		        builder.setTitle("Confirmação");
		        builder.setMessage("Tem certeza que deseja excluir "+nome.getText().toString()+" e todas suas informações?");

		        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

		            public void onClick(DialogInterface dialog, int which) {
		            	try{
		            		banco.deletaRegistro(temp);
		            		exibirAlerta("Sucesso","Operação realizada com sucesso");
							editar.setEnabled(false);
							atualizar=false;
							excluir.setEnabled(false);
							exame.setEnabled(false);
							salvar.setEnabled(true);
							limparControles();
							habilitarControles();
			                dialog.dismiss();
		            	}catch (Exception e){
		            		exibirAlerta("Erro", "Ocorreu um erro ao excluir. Provavelmente ainda existem registros relacionados a esta pessoa");
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
			}
		});
		
		
		pesquisa.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				limparControles();
				atualizar=false;
				Bundle extra = new Bundle();
				extra.putString("tela", "cadastro");
				Intent i = new Intent(MainActivity.this, Pesquisa.class);
				i.putExtras(extra);
				startActivityForResult(i, 1);
			}
		});
	}
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		   ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		 
		   if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
		     String result = predictions.get(0).name;
		 
		     if ("slideEsquerda".equalsIgnoreCase(result)) {
		    	 limparControles();
		    	 salvar.setEnabled(true);
		    	 editar.setEnabled(false);
		    	 excluir.setEnabled(false);
		    	 exame.setEnabled(false);
		    	 atualizar=false;
		     }
		     if ("slideDireita".equalsIgnoreCase(result)) {
		    	 Bundle extra = new Bundle();			
					Intent intent = new Intent();
	                intent.setClass(MainActivity.this,
	                        Pesquisa.class);

					extra.putString("tela", "principal");
					intent.putExtras(extra);
	                startActivity(intent);
	                finish();
		     }
		     
		   }
		}
		
	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode!=0){
			  
			  atualizar=false;
			  String sexoD;
			  Cursor itemRecebido;
			  excluir.setEnabled(false);
			  temp=data.getExtras().getString("valor");
			 itemRecebido=banco.PesquisaSLike(temp, "_id","pessoa");
			 itemRecebido.moveToFirst();
			 nome.setText(itemRecebido.getString(itemRecebido.getColumnIndex("nome")));
			 cidade.setText(itemRecebido.getString(itemRecebido.getColumnIndex("cidade")));
			 rg.setText(itemRecebido.getString(itemRecebido.getColumnIndex("rg")));
			 endereco.setText(itemRecebido.getString(itemRecebido.getColumnIndex("endereco")));
			 email.setText(itemRecebido.getString(itemRecebido.getColumnIndex("email")));
			 telefone.setText(itemRecebido.getString(itemRecebido.getColumnIndex("telefone")));
			 nascimento.setText(itemRecebido.getString(itemRecebido.getColumnIndex("nascimento")));
			 observacao.setText(itemRecebido.getString(itemRecebido.getColumnIndex("observacoes")));
			 anotacao.setText(itemRecebido.getString(itemRecebido.getColumnIndex("anotacoes")));
			 
			 sexoD=itemRecebido.getString(itemRecebido.getColumnIndex("sexo"));
			 if(sexoD.equals("Masculino"))
				 spnSexo.setSelection(0);
			 if(sexoD.equals("Feminino"))
				 spnSexo.setSelection(1);
			 if(sexoD.equals("Outro"))
				 spnSexo.setSelection(2);
		 
			 editar.setEnabled(true);
			 exame.setEnabled(true);
			 salvar.setEnabled(false);
			 excluir.setEnabled(true);
			 exame.setEnabled(true);
			 desabilitarControles();
		}else {
			salvar.setEnabled(true);
			habilitarControles();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

    public void exibirAlerta (String titulo, String mensagem){
    	AlertDialog.Builder alerta = new AlertDialog.Builder(this);
    	alerta.setTitle(titulo);
    	alerta.setMessage(mensagem);
    	alerta.setNeutralButton("OK", null);
    	alerta.create().show();
    }
    
    public boolean alertaSimNao(){
    	retorno=false;
    	/*AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
    	builder.setTitle("Confirmação");
    	builder.setMessage("Deseja realmente executar essa operação?");
    	builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int id) {
    	        retorno = true;
    	    }
    	});
    	builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int id) {
    	        retorno = false;
    	    }
    	});
    	AlertDialog dialog = builder.create();
    	dialog.show();*/
    	AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                retorno=true;
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        return retorno;
    }
    public void limparControles(){
		telefone.setText("");
		email.setText("");
		nome.setText("");
		endereco.setText("");
		cidade.setText("");
		rg.setText("");	
		spnSexo.setSelection(0);
		nascimento.setText("");
		anotacao.setText("");
		observacao.setText("");
    }
    
    public void desabilitarControles(){
    	nome.setEnabled(false);
		telefone.setEnabled(false);
		email.setEnabled(false);
		endereco.setEnabled(false);
		cidade.setEnabled(false);
		rg.setEnabled(false);
		spnSexo.setEnabled(false);
		nascimento.setEnabled(false);
		anotacao.setEnabled(false);
		observacao.setEnabled(false);
    }
    
    public void habilitarControles(){
    	nome.setEnabled(true);
		telefone.setEnabled(true);
		email.setEnabled(true);
		endereco.setEnabled(true);
		cidade.setEnabled(true);
		rg.setEnabled(true);
		spnSexo.setEnabled(true);
		nascimento.setEnabled(true);
		anotacao.setEnabled(true);
		observacao.setEnabled(true);
    }
    
    @Override
    public void onBackPressed() {
		banco.close();
		finish();

	}
}
