package com.example.conexaodb;
//TO DO
/*String msg = "message";
Intent i = new Intent(Activity_A.this, Activity_B.class);
i.putExtra("keyMessage", msg);
startActivity(i);
In Activity_B

Bundle extras = getIntent().getExtras();
String msg = extras.getString("keyMessage");*/

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.LineRegion;
import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.*;

import android.widget.ImageButton;

public class Evolucao extends Activity {
	private MultitouchPlot graficoMaior,graficoMedia;
	private XYSeries serieMaior,  serieMedia, serieTempo;
	private EditText nome,id,dataInicial,dataFinal,quantidadeExames;
	private Button btnAtualizar,btnResetar;
	private Spinner maos,testes;
	Cursor c;
	LineAndPointFormatter L;
	DatabaseHandler banco;
	String nomePesquisa, idPesquisa;
	boolean primeiroGrafico=true,primeiroGraficoAtualizar=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_evolucao);
		Number[] days =   { 1  , 2   , 3   , 4   , 5   , 6   , 7 };	
		btnAtualizar = (Button) findViewById(R.id.btnevoAtualizar);
		btnResetar= (Button) findViewById(R.id.btnResetar);
		graficoMaior = (MultitouchPlot) findViewById(R.id.graficoMaior2);
		graficoMedia = (MultitouchPlot) findViewById(R.id.graficoMedia);
		banco = new DatabaseHandler(this);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		
		nome = (EditText) findViewById(R.id.edtevoPessoa);
		id = (EditText) findViewById(R.id.edtevoId);
		dataInicial = (EditText) findViewById(R.id.edtPrimeiroexame);
		dataFinal = (EditText) findViewById(R.id.edtUltimoexame);
		quantidadeExames= (EditText) findViewById(R.id.edtQuantidadeses);
		maos = (Spinner) findViewById(R.id.spnevoMaos);
		testes = (Spinner) findViewById(R.id.spnevoExames);
		ArrayAdapter<CharSequence> adapterMaos = ArrayAdapter.createFromResource(this,R.array.maosEvo, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterTestes = ArrayAdapter.createFromResource(this,R.array.examesEvo, android.R.layout.simple_spinner_item);
		maos.setAdapter(adapterMaos);
		testes.setAdapter(adapterTestes);
		
		nomePesquisa= extras.getString("nome").toString();
		idPesquisa = extras.getString("id").toString();
		nome.setText(nomePesquisa);
		id.setText(idPesquisa);
		try {
			c = banco.PesquisaSLike(idPesquisa, "idPessoa","exame");
			quantidadeExames.setText(c.getCount()+"");
			c.moveToFirst();
			dataInicial.setText(c.getString(c.getColumnIndex("data")));
			c.moveToLast();
			dataFinal.setText(c.getString(c.getColumnIndex("data")));
		}catch (Exception e){
			exibirAlerta("Informação", "ERRO: "+ e.getMessage().toString());
		}
		graficoMedia.setTitle("Valores Medios");
		graficoMedia.setDomainLabel("Exame número:");
		graficoMedia.setRangeLabel("Valor Medio");
		
		graficoMedia.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		graficoMedia.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
		graficoMedia.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
		graficoMedia.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		graficoMedia.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
		graficoMedia.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
		graficoMedia.getBorderPaint().setStrokeWidth(1);
		graficoMedia.getBorderPaint().setAntiAlias(false);
		graficoMedia.getBorderPaint().setColor(Color.WHITE);
		
		graficoMaior.setDomainLabel("Exame número:");
		graficoMaior.setRangeLabel("Valor Máximo");
		graficoMaior.setTitle("Maiores valores");
		
		graficoMaior.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
		graficoMaior.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
		graficoMaior.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
		graficoMaior.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
		graficoMaior.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
		graficoMaior.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
		graficoMaior.getBorderPaint().setStrokeWidth(1);
		graficoMaior.getBorderPaint().setAntiAlias(false);
		graficoMaior.getBorderPaint().setColor(Color.WHITE);
		

		graficoMaior.getGraphWidget().setMarginLeft(3);
		graficoMaior.getGraphWidget().setMarginTop(5);
		graficoMaior.setTicksPerRangeLabel(3);
		// Domain
	    graficoMaior.setDomainStep(XYStepMode.INCREMENT_BY_VAL, days.length);     
	    graficoMaior.setDomainValueFormat(new DecimalFormat("0"));
	    graficoMaior.setDomainStepValue(1);
	    graficoMaior.disableAllMarkup();
	    
	    graficoMaior.position(graficoMaior.getLegendWidget(),-195, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                135, YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_TOP); //mover coisas no grafico */
	    graficoMaior.getLegendWidget().setSize(new SizeMetrics(101, SizeLayoutType.ABSOLUTE, 520, SizeLayoutType.ABSOLUTE));
		 
	    
	    graficoMedia.position(graficoMedia.getLegendWidget(),-165, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                135, YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_TOP);
	    graficoMedia.getLegendWidget().setSize(new SizeMetrics(101, SizeLayoutType.ABSOLUTE, 590, SizeLayoutType.ABSOLUTE));
		
	    
        
	    //Range
	    //graficoMaior.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
	    graficoMaior.setRangeStepValue(10);
	    //mySimpleXYPlot.setRangeStep(XYStepMode.SUBDIVIDE, values.length);
	    graficoMaior.setRangeValueFormat(new DecimalFormat("0"));
	    
//--------------------------------------------------------------------------------    
	    graficoMedia.getGraphWidget().setMarginLeft(3);
		graficoMedia.getGraphWidget().setMarginTop(5);
		
		// Domain
	    graficoMedia.setDomainStep(XYStepMode.INCREMENT_BY_VAL, days.length);     
	    graficoMedia.setDomainValueFormat(new DecimalFormat("0"));
	    graficoMedia.setDomainStepValue(1);
	    graficoMedia.disableAllMarkup();
	    //Range
	    //graficoMaior.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
	    graficoMedia.setRangeStepValue(10);
	    graficoMedia.setRangeValueFormat(new DecimalFormat("0"));

//--------------------------------------------------------------------------------
	   
	
		
		btnAtualizar.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				alimentaGrafico();
				
			}
		});
		
		btnResetar.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						primeiroGrafico=true;
						primeiroGraficoAtualizar=false;
						graficoMaior.clear();
						graficoMedia.clear();
						graficoMaior.redraw();
						graficoMedia.redraw();
						
					}
				});
	};
	
	 @Override
	    public void onBackPressed() {
			finish();
	   }
	 
	 public void exibirAlerta (String titulo, String mensagem){
	    	AlertDialog.Builder alerta = new AlertDialog.Builder(this);
	    	alerta.setTitle(titulo);
	    	alerta.setMessage(mensagem);
	    	alerta.setNeutralButton("OK", null);
	    	alerta.create().show();
	 }
	 
	 public void alimentaGrafico(){
		 String sql="idPEssoa='"+idPesquisa+"' and (";
		 ArrayList<Number> numerosMaiores = new ArrayList<Number>();
		 ArrayList<Number> numerosMedios = new ArrayList<Number>();
		 ArrayList<Number> numerosTempo = new ArrayList<Number>();
		 Cursor resultado=null;
		 String textoMaos=maos.getSelectedItem().toString();
			if (textoMaos.equals("Ambas"))
				sql= sql + "mao='Direita' or mao='Esquerda') and (";
			
			if (textoMaos.equals("Direita"))
				sql=sql + "mao='Direita') and (";
			
			if (textoMaos.equals("Esquerda"))
				sql=sql + "mao='Esquerda') and (";
			String textoExame=testes.getSelectedItem().toString();
			if (textoExame.equals("Flexores profundos dos dedos"))
				sql=sql+"tipo='Flexores profundos dos dedos')";
			
			if (textoExame.equals("Flexão da metacarpofalangeana"))
				sql=sql+"tipo='Flexão da metacarpofalangeana')";
			
			if (textoExame.equals("Oponência ao polegar"))
				sql=sql+"tipo='Oponência ao polegar')";
			
			if (textoExame.equals("Pinçamento amplo"))
				sql=sql+"tipo='Pinçamento amplo')";
			
			if (textoExame.equals("Preensão palmar"))
				sql=sql+"tipo='Preensão palmar')";
			
			if (textoExame.equals("Todos"))
				sql=sql+"tipo='Flexores profundos dos dedos' or tipo='Flexão da metacarpofalangeana' or tipo='Oponência ao polegar' or tipo='Pinçamento amplo' or tipo='Preensão palmar')";
			
			int i=10;
			int tamanho=0,contador=0;
			resultado=banco.pesquisaDireta(sql, "exame");
			tamanho=resultado.getCount();
			if (tamanho >0 && resultado != null){
				resultado.moveToFirst();
				
				while(contador<tamanho){
					//numerosMaiores.add(i++);
					//numerosMedios.add(i++);
					numerosTempo.add(Integer.parseInt(resultado.getString(resultado.getColumnIndex("duracao"))));
					numerosMaiores.add(resultado.getFloat(resultado.getColumnIndex("valMaximo")));
					numerosMedios.add(resultado.getFloat(resultado.getColumnIndex("valMedio")));
					resultado.moveToNext();
					contador++;
				}
				if (primeiroGrafico==true){
					LineAndPointFormatter formatterMedia  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.GREEN);
					LineAndPointFormatter formatterTempo  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.BLUE);
					LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
					ArrayList<Number> numeros = new ArrayList<Number>();
					//BarFormatter formatter = new BarFormatter(Color.argb(200, 100, 150, 100), Color.LTGRAY);
					serieMedia =  new SimpleXYSeries(numerosMedios, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores medios");
					serieMaior = new SimpleXYSeries(numerosMaiores, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores maiores");
					serieTempo= new SimpleXYSeries(numerosTempo, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores de tempo");
					graficoMaior.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
					graficoMaior.getBorderPaint().setStrokeWidth(1);
					graficoMaior.getBorderPaint().setAntiAlias(false);
					graficoMaior.addSeries(serieMaior, formatter);
					//graficoMaior.addSeries(serieMaior, new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED));
					graficoMedia.addSeries(serieMedia, formatterMedia);
					graficoMaior.redraw();
					graficoMedia.redraw();
					primeiroGrafico=false;
					primeiroGraficoAtualizar=true;
				} else {
					if (primeiroGraficoAtualizar==true){
						graficoMaior.removeSeries(serieMaior);
						graficoMedia.removeSeries(serieMedia);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMaior.addSeries(serieMaior, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMedia.addSeries(serieMedia, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						
						tamanho=0;
						contador=0;
						resultado=banco.pesquisaDireta(sql, "exame");
						tamanho=resultado.getCount();
						if (tamanho >0 && resultado != null){
							resultado.moveToFirst();					
							while(contador<tamanho){
								numerosMaiores.add(resultado.getFloat(resultado.getColumnIndex("valMaximo")));
								numerosMedios.add(resultado.getFloat(resultado.getColumnIndex("valMedio")));
								//numerosMaiores.add(resultado.getInt(resultado.getColumnIndex("valMaximo")));
								//numerosMedios.add(resultado.getInt(resultado.getColumnIndex("valMedio")));
								resultado.moveToNext();
								contador++;
							}
						}
						
						serieMedia =  new SimpleXYSeries(numerosMedios, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores medios");
						serieMaior = new SimpleXYSeries(numerosMaiores, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores maiores");
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMaior.addSeries(serieMaior, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMedia.addSeries(serieMedia, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);

						graficoMaior.redraw();
						graficoMedia.redraw();
						primeiroGraficoAtualizar=false;
					} 
					else{
						serieMedia =  new SimpleXYSeries(numerosMedios, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores medios");
						serieMaior = new SimpleXYSeries(numerosMaiores, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Valores maiores");
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMaior.addSeries(serieMaior, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMedia.addSeries(serieMedia, L);
						L = new LineAndPointFormatter(Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()),Color.rgb(new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue(),new Double(Math.random()*255).intValue()));
						L.setFillPaint(null);
						graficoMaior.redraw();
						graficoMedia.redraw();					
					}					
				}

			       // reduce the number of range labels
				Toast.makeText(Evolucao.this, "Foram encontrados: "+resultado.getCount()+ " exames", Toast.LENGTH_LONG).show(); 
		
	        }else{
	            Toast.makeText(Evolucao.this, "Nenhum registro encontrado com esses parâmetros", Toast.LENGTH_LONG).show(); 
	        }			
	 }
}