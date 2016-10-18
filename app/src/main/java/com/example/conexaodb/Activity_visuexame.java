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
import java.util.StringTokenizer;

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

public class Activity_visuexame extends Activity {
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
		setContentView(R.layout.activity_visuexame);

		Number[] days =   { 1  , 2   , 3   , 4   , 5   , 6   , 7 };	
		 ArrayList<Number> numeros = new ArrayList<Number>();
		 
		graficoMaior = (MultitouchPlot) findViewById(R.id.graficoExameind);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		nomePesquisa= extras.getString("valor").toString();
		String x = null;
		int cont,i;
		StringTokenizer st = new StringTokenizer(nomePesquisa, ";"); 
		nomePesquisa.replace(".", ",");
	    //exibirAlerta("teste",nomePesquisa);
	    while(st.hasMoreTokens()) {
	    	//try(){
	    	x=st.nextToken();
	    	//} catch (Exception e){
	    		
	    	//}
		
	      numeros.add((int)Float.parseFloat(x));
	    }
	       serieMaior = new SimpleXYSeries(numeros, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Amostras");
	    //huuih
	     //teste
		//tokenizer		
		graficoMaior.setDomainLabel("Amostra");
		graficoMaior.setRangeLabel("Valor");
		graficoMaior.setTitle("Exame");
		
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
	    LineAndPointFormatter formatterMedia  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.GREEN);
		LineAndPointFormatter formatterTempo  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.BLUE);
		LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
		
	    graficoMaior.position(graficoMaior.getLegendWidget(),-195, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                135, YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_TOP); //mover coisas no grafico 
	    graficoMaior.getLegendWidget().setSize(new SizeMetrics(101, SizeLayoutType.ABSOLUTE, 520, SizeLayoutType.ABSOLUTE));
	    graficoMaior.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
		graficoMaior.getBorderPaint().setStrokeWidth(1);
		graficoMaior.getBorderPaint().setAntiAlias(false);
		graficoMaior.addSeries(serieMaior, formatter);
		//graficoMaior.addSeries(serieMaior, new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED));
	    
	    
        
	    //Range
	    //graficoMaior.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
	    graficoMaior.setRangeStepValue(10);
	    //mySimpleXYPlot.setRangeStep(XYStepMode.SUBDIVIDE, values.length);
	    graficoMaior.setRangeValueFormat(new DecimalFormat("0"));
	    
//--------------------------------------------------------------------------------    
	   

//--------------------------------------------------------------------------------
	   
	
		
		
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
	 
	
}