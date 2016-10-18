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
import android.content.Context;
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
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ImageButton;

public class CadastroAvaliadores extends Activity implements OnGesturePerformedListener {
	private Spinner spnSexo;
	boolean atualizar=false;
	String temp=null;
	EditText nome;
	ImageButton exame;
	EditText telefone;
	EditText email;
	EditText endereco;
	private Context ctx;
	String senhaString;
	EditText cidade;
	EditText senha;
	EditText confSenha;
	EditText observacao,anotacao;
	EditText rg; 
	GestureLibrary mLibrary;
	ImageButton editar;
	ImageButton salvar;
	ImageButton excluir;
	EditText nascimento;
	EditText instituicao;
	boolean senhaConfirmada=false;
	boolean retorno;
	DatabaseHandler banco = new DatabaseHandler(this);
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.sexos, android.R.layout.simple_spinner_item);
		super.onCreate(savedInstanceState);
		Cursor c;
		setContentView(R.layout.activity_cadastroava);
		
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		   if (!mLibrary.load()) {
		     finish();
		   }
		 
		   GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gesturesAva);
		   gestures.addOnGesturePerformedListener(this);
		   gestures.setGestureVisible(false);
		  
		instituicao = (EditText) findViewById(R.id.edtInstituicao);
		salvar = (ImageButton) findViewById(R.id.btnAvaSalva);
		exame = (ImageButton) findViewById(R.id.btnAvaExames);
		observacao = (EditText) findViewById(R.id.edtAvaObservacao);
		anotacao = (EditText) findViewById(R.id.edtAvaAnotacao);
		nascimento = (EditText) findViewById(R.id.edtAvaNasc);
		excluir = (ImageButton) findViewById(R.id.btnAvaExcluir);
		ImageButton pesquisa = (ImageButton) findViewById(R.id.btnAvaPesquisa);
		nome = (EditText) findViewById(R.id.edtAvaNome);
		editar =(ImageButton) findViewById(R.id.btnAvaEditar);
		telefone = (EditText) findViewById(R.id.edtAvatel);
		email = (EditText) findViewById(R.id.edtAvaEmail);
		endereco = (EditText) findViewById(R.id.edtAvaEnd);
		cidade = (EditText) findViewById(R.id.edtAvaCidade);
		rg = (EditText) findViewById(R.id.edtAvaRG);
		spnSexo = (Spinner) findViewById(R.id.spnAvaSexo);
		senha = (EditText) findViewById(R.id.edtAvaSenha);
		confSenha = (EditText) findViewById(R.id.edtAvaConfSenha);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnSexo.setAdapter(adapter);
		editar.setEnabled(false);
		exame.setEnabled(false);
		excluir.setEnabled(false);
		
		salvar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Utilidades validar = new Utilidades();
				Avaliador p = new Avaliador();
				p.setNome(nome.getText().toString());
				p.setTelefone(telefone.getText().toString());
				p.setEmail(email.getText().toString());
				p.setEndereco(endereco.getText().toString());
				p.setCidade(cidade.getText().toString());
				p.setRg(rg.getText().toString());
				p.setNascimento(nascimento.getText().toString());
				p.setAnotacao(anotacao.getText().toString());
				p.setObservacao(observacao.getText().toString());
				p.setSenha(senha.getText().toString());
				p.setInstituicao(instituicao.getText().toString());
				
				if ((p.getSenha().equals(confSenha.getText().toString()) && p.getSenha().isEmpty()==false)){	
					p.setSenha(Utilidades.md5(p.getSenha()));
					if (atualizar==false){
						if((p.getNome().isEmpty())==false && p.getNascimento().isEmpty()==false && p.getSenha().isEmpty()==false){
							if(spnSexo.getSelectedItem().toString().equals("Masculino"))
								p.setSexo("Masculino");
							if(spnSexo.getSelectedItem().toString().equals("Feminino"))
								p.setSexo("Feminino");
							if(spnSexo.getSelectedItem().toString().equals("Outro"))
								p.setSexo("Outro");
							try{
								if (p.getNascimento().isEmpty()==true) {
										banco.adicionarAvaliador(p);
										exibirAlerta("Informação", "Salvo com sucesso");
										limparControles();
								}else {
									boolean dataValida = validar.validarData(nascimento.getText().toString());
									if(dataValida==true){
										banco.adicionarAvaliador(p);
										exibirAlerta("Banco", "Salvo com sucesso");
										limparControles();
									}
									else  Toast.makeText(CadastroAvaliadores.this, "Por favor, preencha a data correntamente", Toast.LENGTH_LONG).show(); 
								}
							}catch (Exception e){
								exibirAlerta("ERRO","Erro ao salvar informações: " + e.getMessage().toString());
							}
							
						} else exibirAlerta("ERRO", "Por favor, preencha os campos Nome, Nascimento e Senha corretamente");
					}else{
						banco.alteraRegistroAvaliador(p, temp);
						editar.setEnabled(true);
						excluir.setEnabled(true);
						salvar.setEnabled(false);
						exibirAlerta("Banco de dados", "Registro alterado com sucesso!");
						desabilitarControles();
						
					}
				}
				else {
					Toast.makeText(CadastroAvaliadores.this, "A senha e a confirmação não conferem", Toast.LENGTH_LONG).show(); 
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
				Intent i = new Intent(CadastroAvaliadores.this, listaTestesAvaliador.class);
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

				AlertDialog.Builder builder2;
		        final EditText input = new EditText(CadastroAvaliadores.this);
		 		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		 		builder2 = new AlertDialog.Builder(CadastroAvaliadores.this);
		        builder2.setTitle("Confirmação");
		        builder2.setView(input);
		        builder2.setMessage("Insira a senha de "+nome.getText().toString());

		        builder2.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
		        
		            public void onClick(DialogInterface dialog, int which) {
		            	String md5 = Utilidades.md5(input.getText().toString());
		            	if (md5.equals(senhaString)) {
		            		salvar.setEnabled(true);
		            		excluir.setEnabled(false);
		            		habilitarControles();
		            		editar.setEnabled(false);
		            		atualizar=true;
		            		dialog.dismiss();
		            	}else {
		            		Toast.makeText(CadastroAvaliadores.this, "Senha incorreta", Toast.LENGTH_LONG).show(); 
		            		dialog.dismiss();
		            	}
						
		            }

		        });

		        builder2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		            }
		        });

		        AlertDialog alert2 = builder2.create();
		        alert2.show();				
			
				
				
			}
		});
		
		excluir.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				AlertDialog.Builder builder3;
		        final EditText input = new EditText(CadastroAvaliadores.this);
		 		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		 		builder3 = new AlertDialog.Builder(CadastroAvaliadores.this);
		        builder3.setTitle("Confirmação");
		        builder3.setView(input);
		        builder3.setMessage("Insira a senha de "+nome.getText().toString());

		        builder3.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
		        
		            public void onClick(DialogInterface dialog, int which) {
		            	String md5 = Utilidades.md5(input.getText().toString());
		            	if (md5.equals(senhaString)) {
		            		AlertDialog.Builder builder = new AlertDialog.Builder(CadastroAvaliadores.this);

		    		        builder.setTitle("Confirmação");
		    		        builder.setMessage("Tem certeza que deseja excluir "+nome.getText().toString()+" e todas suas informações?");

		    		        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {

		    		            public void onClick(DialogInterface dialog, int which) {
		    		            	try{
		    		            		banco.deletaRegistroAvaliador(temp);
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
		            	}else {
		            		Toast.makeText(CadastroAvaliadores.this, "Senha incorreta", Toast.LENGTH_LONG).show(); 
		            		dialog.dismiss();
		            	}
						
		            }

		        });

		        builder3.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

		            @Override
		            public void onClick(DialogInterface dialog, int which) {
		                dialog.dismiss();
		            }
		        });

		        AlertDialog alert3 = builder3.create();
		        alert3.show();
				
				
				//----
								
			}
		});
		
		
		pesquisa.setOnClickListener(new View.OnClickListener() {	
			@Override
			public void onClick(View v) {
				limparControles();
				atualizar=false;
				Intent i = new Intent(CadastroAvaliadores.this, PesquisaAvaliador.class);
				startActivityForResult(i, 1);
			}
		});
	}
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		   ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		 
		   if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
		     String result = predictions.get(0).name;
		 
		     if ("slideEsquerda".equalsIgnoreCase(result)) {
		    	 Bundle extra = new Bundle();			
					Intent intent = new Intent();
	                intent.setClass(CadastroAvaliadores.this,
	                        Pesquisa.class);

					extra.putString("tela", "principal");
					intent.putExtras(extra);
	                startActivity(intent);
	                finish();
		     }
		     if ("slideDireita".equalsIgnoreCase(result)) {
		    	 Intent intente = new Intent();
	                intente.setClass(CadastroAvaliadores.this,
	                         Configuracoes.class);
	                startActivity(intente);
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
			  Cursor itemRecebido=null,auxAvaliador=null;
			  excluir.setEnabled(false);
			  temp=data.getExtras().getString("valor");
			  senhaString=data.getExtras().getString("senha");
			 itemRecebido=banco.PesquisaSLike(temp, "_id","pessoa");
			 itemRecebido.moveToFirst();
			 auxAvaliador = banco.PesquisaSLike(temp, "idPessoa","avaliador");
			 auxAvaliador.moveToFirst();
			 nome.setText(itemRecebido.getString(itemRecebido.getColumnIndex("nome")));
			 cidade.setText(itemRecebido.getString(itemRecebido.getColumnIndex("cidade")));
			 rg.setText(itemRecebido.getString(itemRecebido.getColumnIndex("rg")));
			 endereco.setText(itemRecebido.getString(itemRecebido.getColumnIndex("endereco")));
			 email.setText(itemRecebido.getString(itemRecebido.getColumnIndex("email")));
			 telefone.setText(itemRecebido.getString(itemRecebido.getColumnIndex("telefone")));
			 nascimento.setText(itemRecebido.getString(itemRecebido.getColumnIndex("nascimento")));
			 observacao.setText(itemRecebido.getString(itemRecebido.getColumnIndex("observacoes")));
			 anotacao.setText(itemRecebido.getString(itemRecebido.getColumnIndex("anotacoes")));
			 instituicao.setText(auxAvaliador.getString(auxAvaliador.getColumnIndex("instituicao")));
			 
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
		senha.setText("");
		confSenha.setText("");
		instituicao.setText("");
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
		senha.setEnabled(false);
		confSenha.setEnabled(false);
		instituicao.setEnabled(false);
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
		senha.setEnabled(true);
		instituicao.setEnabled(true);
		confSenha.setEnabled(true);
    }
    
    
    @Override
    public void onBackPressed() {
		banco.close();
		finish();

	}
}
