package com.example.conexaodb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
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


public class Configuracoes extends Activity implements OnGesturePerformedListener {
	private ImageButton salvar,editar,bkp;
	GestureLibrary mLibrary;
	private EditText macAdress,gMax,gMin,taxAmostragem,gStep,coefAngular,coefLinear;
	DatabaseHandler banco;
	Cursor itemRecebido;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		//Setando objetos de tela
		setContentView(com.example.conexaodb.R.layout.activity_configuracoes);
		super.onCreate(savedInstanceState);
		macAdress= (EditText) findViewById(R.id.edtMac);
		gMax =(EditText) findViewById(R.id.edtgMax);
		bkp = (ImageButton) findViewById(R.id.btnBKP3);
		gMin = (EditText) findViewById(R.id.edtevoPessoa);
		taxAmostragem = (EditText) findViewById(R.id.edtTaxa);
		gStep = (EditText) findViewById(R.id.edtgStep);
		coefAngular = (EditText) findViewById(R.id.edtCoefAng);
		coefLinear = (EditText) findViewById(R.id.edtCoefLin);
		salvar= (ImageButton) findViewById(R.id.btnconfigSalvar3);
		editar = (ImageButton) findViewById(R.id.btnconfigEditar);
		salvar.setEnabled(false);
		mLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
		   if (!mLibrary.load()) {
		     finish();
		   }
		 
		   GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gesturesConfig);
		   gestures.addOnGesturePerformedListener(this);
		   gestures.setGestureVisible(false);
		//Adiquirindo os valores do banco
		banco = new DatabaseHandler(Configuracoes.this);
		itemRecebido=banco.PesquisaLike("1", "_id", "parametros");
		itemRecebido.moveToFirst();
		
		//comandos para abertura de tela correta
		macAdress.setText(itemRecebido.getString(itemRecebido.getColumnIndex("macBt")));
		gMax.setText(itemRecebido.getInt(itemRecebido.getColumnIndex("maxGrafico"))+"");
		gMin.setText(itemRecebido.getInt(itemRecebido.getColumnIndex("minGrafico"))+"");
		taxAmostragem.setText(itemRecebido.getInt(itemRecebido.getColumnIndex("tempo_amostragem"))+"");
		gStep.setText(itemRecebido.getInt(itemRecebido.getColumnIndex("stepGrafico"))+"");
		coefAngular.setText(itemRecebido.getDouble(itemRecebido.getColumnIndex("coef_angular"))+"");
		coefLinear.setText(itemRecebido.getDouble(itemRecebido.getColumnIndex("coef_angular"))+"");
		//Action botão editar
		editar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				salvar.setEnabled(true);
				editar.setEnabled(false);
				macAdress.setEnabled(true);
				gMax.setEnabled(true);
				gMin.setEnabled(true);
				taxAmostragem.setEnabled(true);
				gStep.setEnabled(true);
				coefAngular.setEnabled(true);
				coefLinear.setEnabled(true);
				
			}
		});
		//-----------------------------------------------------------------
		salvar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Configuracao config = new Configuracao();
				
				config.setCoefAngular(Double.parseDouble(coefAngular.getText().toString()));
				config.setCoefLinear(Double.parseDouble(coefLinear.getText().toString()));
				config.setMacAdress(macAdress.getText().toString());
				config.setMaxGrafico(Integer.parseInt(gMax.getText().toString()));
				config.setMinGrafico(Integer.parseInt(gMin.getText().toString()));
				config.setStepGrafico(Integer.parseInt(gStep.getText().toString()));
				config.setTaxAmostragem(Integer.parseInt(taxAmostragem.getText().toString()));
				
				try{
					banco.alteraConfig(config, "1");
					Toast.makeText(Configuracoes.this, "Configurações alteradas com sucesso!", Toast.LENGTH_SHORT).show(); 
					salvar.setEnabled(false);
					editar.setEnabled(true);
					macAdress.setEnabled(false);
					gMax.setEnabled(false);
					gMin.setEnabled(false);
					taxAmostragem.setEnabled(false);
					gStep.setEnabled(false);
					coefAngular.setEnabled(false);
					coefLinear.setEnabled(false);
				}catch (Exception e){
					Toast.makeText(Configuracoes.this, "Erro ao alterar configurações! " + e.getMessage().toString(), Toast.LENGTH_LONG).show(); 
				}
				
			}
			
			
		});
	
		
		bkp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				File direct = new File(Environment.getExternalStorageDirectory() + "/BackupSistema");

		        if(!direct.exists())
		         {
		             if(direct.mkdir()) 
		               {
		                //directory is created;
		               }

		         }
		     exportDB();
		     importDB();
			}
		});	
		
	}
	
	public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
		   ArrayList<Prediction> predictions = mLibrary.recognize(gesture);
		 
		   if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
		     String result = predictions.get(0).name;
		 
		     if ("slideEsquerda".equalsIgnoreCase(result)) {
		    	 Intent intente = new Intent();
	                intente.setClass(Configuracoes.this,
	                         CadastroAvaliadores.class);
	                startActivity(intente);
	                finish();
		     }
		     
		   }
	}
	 private void importDB() {
	        // TODO Auto-generated method stub

	        try {
	            File sd = Environment.getExternalStorageDirectory();
	            File data  = Environment.getDataDirectory();

	            if (sd.canWrite()) {
	                String  currentDBPath= "//data//" + "com.example.conexaodb"
	                        + "//databases//" + "Sistema";
	                String backupDBPath  = "/Sistema";
	                File  backupDB= new File(data, currentDBPath);
	                File currentDB  = new File(sd, backupDBPath);

	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                Toast.makeText(getBaseContext(), backupDB.toString(),
	                        Toast.LENGTH_LONG).show();

	            }
	        } catch (Exception e) {

	            Toast.makeText(getBaseContext(), "Erro na importação "+ e.toString(), Toast.LENGTH_LONG)
	                    .show();

	        }
	    }
	//exporting database 
	    private void exportDB() {
	        // TODO Auto-generated method stub

	        try {
	            File sd = Environment.getExternalStorageDirectory();
	            File data = Environment.getDataDirectory();

	            if (sd.canWrite()) {
	                String  currentDBPath= "//data//" + "com.example.conexaodb"
	                        + "//databases//" + "Sistema";
	                String backupDBPath  = "/Sistema";
	                File currentDB = new File(data, currentDBPath);
	                File backupDB = new File(sd, backupDBPath);

	                FileChannel src = new FileInputStream(currentDB).getChannel();
	                FileChannel dst = new FileOutputStream(backupDB).getChannel();
	                dst.transferFrom(src, 0, src.size());
	                src.close();
	                dst.close();
	                Toast.makeText(getBaseContext(), "Backup dos dados feitos em: " + backupDB.toString(),
	                        Toast.LENGTH_LONG).show();

	            }
	        } catch (Exception e) {

	            Toast.makeText(getBaseContext(), "Erro na exportação " +e.toString(), Toast.LENGTH_LONG)
	                    .show();

	        }
	    }

	
	@Override
	public void onBackPressed() {
		banco.close();
		finish();
    }
}
