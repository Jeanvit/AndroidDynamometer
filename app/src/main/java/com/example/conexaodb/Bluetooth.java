package com.example.conexaodb;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Observable;
import java.util.Observer;
import java.io.IOException;
import java.util.UUID;

import android.os.Bundle;
import android.widget.*;
import android.content.*;
import android.database.Cursor;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.*;
import android.bluetooth.*;
import android.os.Message;
import java.util.Set;
import android.widget.Toast;
import android.os.*;
import java.util.Random;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;

import com.androidplot.Plot;
import com.androidplot.series.XYSeries;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.*;

import android.graphics.*;
import android.os.Bundle;

public class Bluetooth extends Activity  {
	private BluetoothAdapter mBluetoothAdapter;  //Adaptador Bluetooth
	private BluetoothDevice mmDevice;			//Dispositivo Bluetooth
	private EditText mensagem,valorTempo,edtNome,edtResponsavel,edtMedia,edtMaximo;
    private Button iniciar,calibrar;
    private BluetoothSocket socket;
    private InputStream input;
    private Chronometer cronometro;
    private OutputStream output;
    boolean recebeu = true;
    private Handler toastTeller;
	boolean podeFazerMedia=false;
    private Handler alteraText;
    private String nome;
	private String id,idAvaliador;
	private String senha;
	private float valor;
    private BufferedReader reader;
    private Object obj1 = new Object();
	private Object obj2 = new Object();
	private boolean clicado=false,contemEspaco=false;
	public Message msg = new Message();
	private String valorRecebido, quebra, macAdress;
	float valorRecebidoAleatorio=0;
	double coefAngular,coefLinear;
	int stepGrafico,maxGrafico,minGrafico,taxAmostragem;
	float temp;
	int taxaAmostragem;
	double divisor,multiplicador;
	String macAdressBluetooth;
	private int tString;
	private float maior=0, media=0,contador=0;
    private String valoresBanco;
	private Spinner spnMao,spnTipo;
	private Exame exame;
	private int tPonto;
	int segundos;
	double valor1,valor2,total;
	DatabaseHandler banco;
	Cursor itemRecebido;
//----------------------------------------------------------------------------------------------------------------	
	private class MyPlotUpdater implements Observer {
        Plot plot;

        public MyPlotUpdater(Plot plot) {
            this.plot = plot;
        }

        @Override
        public void update(Observable o, Object arg) {
            plot.redraw();
        }
    }
	 private XYPlot dynamicPlot;
	    private XYPlot staticPlot;
	    private MyPlotUpdater plotUpdater;
	    SampleDynamicXYDatasource data;
	    private Thread myThread;
//---------------------------------------------------------------------------------------------------------------
//Função que converte byte para string
	public String converteByteString(byte[] _bytes){
		String converte = new String (_bytes);
		return converte;
	}
	
	 
//---------------------------------------------------------------------------------------------------------------
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		exame = new Exame();
		banco = new DatabaseHandler(Bluetooth.this);
		
		itemRecebido=banco.PesquisaLike("1", "_id", "parametros");
		itemRecebido.moveToFirst();
		divisor=(itemRecebido.getDouble(itemRecebido.getColumnIndex("coef_angular")));
		multiplicador=(itemRecebido.getDouble(itemRecebido.getColumnIndex("coef_angular")));
		macAdressBluetooth=itemRecebido.getString(itemRecebido.getColumnIndex("macBt"));
		taxaAmostragem=(itemRecebido.getInt(itemRecebido.getColumnIndex("tempo_amostragem")));
		
		setContentView(R.layout.activity_bluetooth1);
	    calibrar = (Button) findViewById(R.id.btnCalibrar);
		iniciar = (Button) findViewById(R.id.iniciar);
	    edtMaximo = (EditText) findViewById(R.id.edtgMax);
	    edtMedia = (EditText) findViewById(R.id.edtMedia);
	    mensagem = (EditText) findViewById(R.id.mensagem);
	    edtResponsavel = (EditText) findViewById(R.id.edtResponsavel);
	    edtNome = (EditText) findViewById(R.id.edtPessoa);
	    valorTempo = (EditText) findViewById(R.id.edtgMin);
	    cronometro = (Chronometer) findViewById(R.id.chronometer1);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.maos, android.R.layout.simple_spinner_item);
		ArrayAdapter<CharSequence> adapterExames = ArrayAdapter.createFromResource(this,R.array.exames, android.R.layout.simple_spinner_item);
		spnMao = (Spinner) findViewById(R.id.spnMao);
		spnTipo = (Spinner) findViewById(R.id.spnExame);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapterExames.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnMao.setAdapter(adapter);
		spnTipo.setAdapter(adapterExames);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		final String nome = extras.getString("nome").toString();
		final String id = extras.getString("identificacao").toString();

        data = new SampleDynamicXYDatasource();
		edtNome.setText(nome);
		carregaConfigs();
		iniciarBT();
//-----------------------------------------------------------------------------------------------------
//Handler para exibição de Toast em threads secundárias
		toastTeller = new Handler() {
		      public void handleMessage(Message msg) {
		          if (msg.what == 2)
		             Toast.makeText(Bluetooth.this, msg.obj.toString(),
		             Toast.LENGTH_LONG).show();
		         super.handleMessage(msg);
		           }
		 };
//----------------------------------------------------------------------------------------------------
//Thread que abre a conexão e fica esperando o Stream de dados
		 Thread conexao = new Thread(){
            public void run() {
                // Identificador Único do Servidorprivate 
            	final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            	try {
            		
                	BluetoothSocket s = mmDevice.createRfcommSocketToServiceRecord(SPP_UUID);;
                    // Conectar ao Servidor
                    s.connect(); // Execução Bloqueante
                    // Fluxos de Entrada e Saída de Dados
                    InputStream in = s.getInputStream();
                    OutputStream out = s.getOutputStream();
                    // Captura de Objetos
                    socket = s; // Socket de Conexão
                    input  = in; // Fluxo de Entrada def Dados
                    output = out; // Fluxo de Saída de Dado
                    
                    int posI;
                    final byte buffer[] = new byte [1024];
                    int read=-1;
                    for (;(read=input.read(buffer))>-1;) {
                    //		try
                    	valorRecebido=converteByteString(buffer);
                    	tString = valorRecebido.indexOf("f");
                    	tPonto = valorRecebido.indexOf(".");
                    	posI=valorRecebido.indexOf("i");
                    	//quebra=valorRecebido.substring(tString, posI);
                    
                    	
                    	//if (valorRecebido.length()>=7 && tString==7  && espaco==0 && tPonto==2 ){
                    	if (valorRecebido.contains("f") && valorRecebido.contains("i") && valorRecebido.contains(".")) { 
                    		valorRecebido=valorRecebido.replaceAll("i","");
                        	valorRecebido=valorRecebido.replaceAll(" ","");
                        	valorRecebido=valorRecebido.replaceAll("f","");
	                        runOnUiThread(new Runnable() {
	                                @Override
	                                public void run() {
	                                	if (valorRecebido.contains("f")==false && valorRecebido.contains("i")==false){
	                                		try {
	                                			//valor=(float) (valor*multiplicador);
	                                			//valor=(float) (valor/divisor);
	                                			valor=Float.parseFloat(valorRecebido);
	                                			mensagem.setText(valor+"");
		                                		recebeu = true;
	                                		}catch (Exception e){
	                                			
	                                		}
	                                		
	                                	}
	                                }
	                        
	                            });
	                        
	                    }
                    }
                    
                } catch (IOException e) { // Erro de Entrada e Saída de Dados
                	msg.what=2;
                	msg.obj="ERRO. Problema de IO durante a leitura";
                	toastTeller.sendMessage(msg);
                    finish();
                }
            }
        };
//------------------------------------------------------------------------------------------
        calibrar.setOnClickListener (new View.OnClickListener() {
            public void onClick(View v) {
            	byte content[] = new byte[2];
            	content[0]=99;
            	content[1]=32;
	            		try {
	            			output.write(content,0,2);
	            		} catch(Exception e) {
	            			Toast.makeText(Bluetooth.this,"Erro ao enviar caractere de calibração", Toast.LENGTH_SHORT).show();
	            		}
            		}
            });
//------------------------------------------------------------------------------------------
       conexao.start();
		iniciar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if (clicado==false) 
            		iniciar.setText("Iniciar");
            	if (iniciar.getText()=="Iniciar"){
            		if (edtResponsavel.getText().toString().equals("")){
            			Toast.makeText(Bluetooth.this,"Por favor, insira o Responsável pelo exame", Toast.LENGTH_SHORT).show();
            		}
            		else{
	            			try{
	            				media=0;
	            				maior=0;
	            				contador=0;
	            				valoresBanco="";
	                			segundos=Integer.parseInt(valorTempo.getText().toString());
	                			if(segundos>5){
	                				Toast.makeText(Bluetooth.this,"Exames com mais de 5 segundos causam fadiga muscular!", Toast.LENGTH_SHORT).show();
	                			}
	                			iniciar.setText("Parar");
	                    		clicado=true;
	                    		cronometro.setBase(SystemClock.elapsedRealtime());
	                    		cronometro.start();
	                            spnMao.setEnabled(false);
	                            valorTempo.setEnabled(false);
	                            edtResponsavel.setEnabled(false);
	                            podeFazerMedia=true;
	                        	//Thread responsável pela execução da escrita de dados
	                        	Thread sender = new Thread(){
	                                public void run() {

	                                	long tempoPassado = SystemClock.elapsedRealtime() - cronometro.getBase();
	                                	segundos=segundos*1000;

	    	            				Random aleatorio = new Random();
	                                	try {

	                                		media=0;
	                                		contador=0;
	                                		maior=0;
	                                		byte content[] = new byte[2];
    	                                	content[0]=97;
    	                                	content[1]=32;
	                                		while (tempoPassado<=segundos){
	                                			tempoPassado = SystemClock.elapsedRealtime() - cronometro.getBase();
	            	            	            valorRecebidoAleatorio= aleatorio.nextFloat();
	            	            	            //
	    	                                	//DESCOMENTAR ESSA PARTE PARA FUNCIONAR
	    	                                	//
	    	                                	
	    	                                	
	    	                                	//synchronized (obj2) {
	    	                                		try {
	    	                                			//while (true){
	    	                                				//if (recebeu==true){
	    	                                					output.write(content,0,2);
	    	                                					recebeu=false;
	    	                                				//
	    	                                				Thread.sleep(taxaAmostragem);
	    	                                			//}
	    	                                		} catch (Exception e) { // Erro Encontrado
	    	                                			msg.what=2;
	    	                                			msg.obj="Erro na leitura da entrada de dados";
	    	                                			toastTeller.sendMessage(msg);
	    	                                		}
	    	                                	//}
	                                		}runOnUiThread(new Runnable() {
	            	                    	     public void run() {
	            	                    	    	 cronometro.stop();
	            	                                 
	            	                                 if (iniciar.getText().toString().equals("Parar")){
	            	                                	 try { 
		            	                                	 spnMao.setEnabled(true);
		                	                                 valorTempo.setEnabled(true);
		                	                                 podeFazerMedia=false;
		                	                                 edtResponsavel.setEnabled(true);
		                	                                 iniciar.setText("Iniciar");
		            	                                	 Calendar cal = Calendar.getInstance(); 
		            	                                	 int mes = cal.get(Calendar.MONTH);
			            	                                 mes++;
			            	                                 int ano = cal.get(Calendar.YEAR);
			            	                                 int dia = cal.get(Calendar.DAY_OF_MONTH);
			            	                                 exame.setMao(spnMao.getSelectedItem().toString());
			            	                                 exame.setValores(valoresBanco);
			            	                                 exame.setResponsavel(Integer.parseInt(idAvaliador));
			            	                                 exame.setDuracao(valorTempo.getText().toString());
			            	                                 exame.setData((dia+"/"+mes+"/"+ano));
			            	                                 exame.setIdPessoa(id);
			            	                                 exame.setTipo(spnTipo.getSelectedItem().toString());
			            	                                 exame.setValMax(maior);
			            	                                 exame.setValMed(temp);
			            	                                 banco.adicionaExame(exame);
			            	                                 media=0;
			            	                                 maior=0;
			            	                                 edtMaximo.setText("0");
			            	                                 edtMedia.setText("0");
			            	                                 exibirAlerta("Exame concluído",spnTipo.getSelectedItem().toString()+" de "+ segundos/1000 +"s com a mão "+spnMao.getSelectedItem().toString()+" concluído com sucesso!");
	            	                                	 } catch (Exception e){
	            	                                		 exibirAlerta("ERRO"," Ocorreu um erro ao salvar o exame. ERRO = " + e.getMessage().toString());
	            	                                	 }
	            	                                }
	            	                            }
	            	                    	});
	            	                    	clicado = false;
	            	                    	
	                                	}
	                                	catch (Exception e){
	                                		runOnUiThread(new Runnable() {
	            	                    	     public void run() {
	            	                    	    	 exibirAlerta("Erro!","Ocorreu um erro durante o exame.");
	            	                    	    }
	            	                    	});
	                                	}
	                                	
	                                }
	                                
	                            };
	                            sender.start();
	                    		
	                		}
	                	catch (Exception e){
	                			Toast.makeText(Bluetooth.this,"Por favor, escolha uma valor para o Tempo!", Toast.LENGTH_SHORT).show();
	                            spnMao.setEnabled(true);
	                            valorTempo.setEnabled(true);
	                            clicado = false;
	                            edtResponsavel.setEnabled(true);
	                            media=0;
	                            contador=0;
	                            maior=0;
	
	                	}
            		}// IF DO NOME DO RESPONSÁVEL
            	}
            	else {
            		iniciar.setText("Iniciar");
            		clicado = false;
            		cronometro.stop();
            		cronometro.setText("0:00");
            		media=0;
                    maior=0;
                    edtMaximo.setText("0");
                    edtMedia.setText("0");
            		cronometro.stop();
                    spnMao.setEnabled(true);
                    valorTempo.setEnabled(true);
                    edtResponsavel.setEnabled(true);
                    valoresBanco="";
            	}
 
            }
        });
		
		edtResponsavel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Bluetooth.this, PesquisaAvaliador.class);
				startActivityForResult(i, 1);
				
			}
		});
		//===============================================================================================
		 // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.graficoMaior);
        plotUpdater = new MyPlotUpdater(dynamicPlot);
        dynamicPlot.position(dynamicPlot.getLegendWidget(),-135, XLayoutStyle.ABSOLUTE_FROM_CENTER,
                155, YLayoutStyle.ABSOLUTE_FROM_CENTER,
                AnchorPosition.LEFT_TOP);
        dynamicPlot.getLegendWidget().setSize(new SizeMetrics(101, SizeLayoutType.ABSOLUTE, 520, SizeLayoutType.ABSOLUTE));
		dynamicPlot.getGraphWidget().getDomainLabelPaint().setColor(Color.TRANSPARENT);
        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
        // getInstance and position datasets:
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Valor atual");
        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Valor Medio");
        SampleDynamicSeries sine3Series = new SampleDynamicSeries(data, 2, "Valor Maximo");
        //==============================

        dynamicPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        dynamicPlot.getGraphWidget().getGridLinePaint().setColor(Color.BLACK);
        dynamicPlot.getGraphWidget().getGridLinePaint().setPathEffect(new DashPathEffect(new float[]{1,1}, 1));
        dynamicPlot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        dynamicPlot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
 
        dynamicPlot.setBorderStyle(Plot.BorderStyle.SQUARE, null, null);
        dynamicPlot.getBorderPaint().setStrokeWidth(1);
        dynamicPlot.getBorderPaint().setAntiAlias(false);
        dynamicPlot.getBorderPaint().setColor(Color.WHITE);
 
    
 
        // setup our line fill paint to be a slightly transparent gradient:
        Paint lineFill = new Paint();
        lineFill.setAlpha(220);
        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.BLUE, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.RED, Color.RED);
        formatter.setFillPaint(lineFill);
        dynamicPlot.getGraphWidget().setPaddingRight(2);
        //=====
        
        
        Paint lineFill2 = new Paint();
        lineFill2.setAlpha(220);
        lineFill2.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter2  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        formatter2.setFillPaint(lineFill2);
        dynamicPlot.setTitle("Dinamômetro");
        dynamicPlot.setDomainLabel("Tempo");
        dynamicPlot.setRangeLabel("Valores");
        
        
        Paint lineFill3 = new Paint();
        lineFill3.setAlpha(200);
        lineFill3.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
 
        LineAndPointFormatter formatter3  = new LineAndPointFormatter(Color.rgb(0, 0,0), Color.BLUE, Color.RED);
        //formatter3.setFillPaint(lineFill3);
        // create a series using a formatter with some transparency applied:
        /*LineAndPointFormatter f1 =
                new LineAndPointFormatter(Color.rgb(0, 0, 200), null, Color.rgb(0, 0, 80), (PointLabelFormatter) null);
        
        
        
        
        
        
        
        f1.getFillPaint().setAlpha(220);*/
        dynamicPlot.addSeries(sine3Series, formatter3);
        dynamicPlot.addSeries(sine1Series, formatter);
        dynamicPlot.addSeries(sine2Series, formatter2);

        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);
        
        dynamicPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
        dynamicPlot.setDomainStepValue(sine1Series.size());

        // thin out domain/range tick labels so they dont overlap each other:
        dynamicPlot.setTicksPerDomainLabel(2); //Valores de x pra x
        dynamicPlot.setTicksPerRangeLabel(2);  //Quantidade de valores em y

        // uncomment this line to freeze the range boundaries:
        //Valores maximos pra x
        dynamicPlot.setRangeBoundaries(0, 80, BoundaryMode.FIXED);
        
}
//------------------------------------------------------------------------------------------------------
// Habilita a conexão bluetooth e procura pelo Mac Adress da placa do CI
	public void iniciarBT() {
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
	    if (mBluetoothAdapter == null) {
	        Toast.makeText(Bluetooth.this, "Nenhum dispositivo Bluetooth", Toast.LENGTH_LONG).show();
	        finish();
	    }

	    else {
	    	if (!mBluetoothAdapter.isEnabled()) {
	    		Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    		startActivityForResult(enableBluetooth, 0);
	    	}

	    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	    	mmDevice = mBluetoothAdapter.getRemoteDevice(macAdressBluetooth);

	    	if (pairedDevices.contains(mmDevice)) {
	    		String msg1 = mmDevice.getName();
	    		String msg2 = mmDevice.getAddress();
	    		String info = "Emparelhado com\nDispostivo " + msg1 + "\n" + "Endereço: " + msg2;
	    		//Label2.setText(String.valueOf(info));
	    		Toast.makeText(Bluetooth.this, info, Toast.LENGTH_LONG).show();
	    	}
	    }
	}
//-----------------------------------------------------------------------------------------------
	 @Override
	    public void onResume() {
	        // kick off the data generating thread:
	        myThread = new Thread(data);
	        myThread.start();
	        super.onResume();
	    }

	    @Override
	    public void onPause() {
	        data.stopThread();
	        super.onPause();
	    }

	    class SampleDynamicXYDatasource implements Runnable {

	        // encapsulates management of the observers watching this datasource for update events:
	        class MyObservable extends Observable {
	            @Override
	            public void notifyObservers() {
	                setChanged();
	                super.notifyObservers();
	            }
	        }

	        private static final int MAX_AMP_SEED = 100; // Maximo do grafico
	        private static final int MIN_AMP_SEED = 10;  // Minimo do grafico
	        private static final int AMP_STEP = 5;			// Adiçao dos valores em x é feito de x em x
	        public static final int Sensor1 = 0;
	        public static final int Sensor2 = 1;
	        public static final int Sensor3 = 2;
	        private static final int SAMPLE_SIZE = 50;      // Tamanho maximo de x
	        private int phase = 0;
	        private float sinAmp = 80;
	        private float sinAmp2= 10;
	        private float sinAmp3= 40;
	        private MyObservable notifier;
	        private boolean keepRunning = false;
	        private float passado=0;
	        {
	            notifier = new MyObservable();
	        }

	        public void stopThread() {
	            keepRunning = false;
	        }

	        //@Override
	        public void run() {
	            try {
	                keepRunning = true;
	                float temp=0;
	                boolean isRising = true;
	                contador=0;
	    	        media=0;
	    	        maior=0;
	    	        valoresBanco="";
	    	        runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                        	
                            iniciar.setEnabled(false);
                        }
                
                    });
	    	        
	    	        Thread.sleep(5000);
	    	        
	    	        runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                        	
                        	 iniciar.setEnabled(true);
                        }
                
                    });

	                while (keepRunning) {

	                    Thread.sleep(200); // decrease or remove to speed up the refresh rate.  
	                    phase++;
	                    
	                    /*if (sinAmp >= MAX_AMP_SEED) {
	                        isRising = false;
	                    } else if (sinAmp <= MIN_AMP_SEED) {
	                        isRising = true;
	                    }

	                    if (isRising) {
	                        sinAmp += AMP_STEP;
	                    } else {
	                        sinAmp -= AMP_STEP;
	                    }*/
	                    notifier.notifyObservers();
	                }
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	            }
	        }

	        public int getItemCount(int series) {
	            return 15; //Valor maximo em y
	        }

	        public Number getX(int series, int index) {
	            if (index >= SAMPLE_SIZE) {
	                throw new IllegalArgumentException();
	            }
	            return index;
	        }
	        public Number getY(int series, int index) {
	            if (index >= SAMPLE_SIZE) {
	                throw new IllegalArgumentException();
	            }
	            Random g = new Random();
	            sinAmp= valor;
                sinAmp2=g.nextFloat();
                sinAmp3=g.nextFloat();
	            
	            
	            switch (series) {
	                case Sensor1: //Azul meio
	                	
	                	if (podeFazerMedia==true){
	                		media=media+sinAmp;
	                		contador++;
	                		temp=media/contador;
	                	}
	                	runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                            	
                                edtMedia.setText(temp+"");
                            }
                    
                        });

	                	return media/contador;
	                case Sensor2: // Verde frente
	                	if (passado!=sinAmp)
	                		valoresBanco=valoresBanco+sinAmp+";";
	                	passado=sinAmp;
	                	runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                            	
                                mensagem.setText(valorRecebido);
                            }
                    
                        });

	                	return sinAmp; //valor do Bluetooth = return valorRecebido;
	                case Sensor3: //Vermelho 
	                	if (sinAmp>maior){
	                		maior=sinAmp;
	                	}
	                	runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                edtMaximo.setText(maior+"");
                            }
                    
                        });

	                    return maior;
	                default:
	                    throw new IllegalArgumentException();
	            }
	        }

	        public void addObserver(Observer observer) {
	            notifier.addObserver(observer);
	        }

	        public void removeObserver(Observer observer) {
	            notifier.deleteObserver(observer);
	        }

	    }

	    class SampleDynamicSeries implements XYSeries {
	        private SampleDynamicXYDatasource datasource;
	        private int seriesIndex;
	        private String title;

	        public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
	            this.datasource = datasource;
	            this.seriesIndex = seriesIndex;
	            this.title = title;
	        }

	        @Override
	        public String getTitle() {
	            return title;
	        }

	        @Override
	        public int size() {
	            return datasource.getItemCount(seriesIndex);
	        }

	        @Override
	        public Number getX(int index) {
	            return datasource.getX(seriesIndex, index);
	        }

	        @Override
	        public Number getY(int index) {
	            return datasource.getY(seriesIndex, index);
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
			setResult(Activity.RESULT_CANCELED);
			/*try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}*/
			try {
				output.close();
	            input.close();
	            socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			banco.close();
			finish();
			
	    }
	    public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			if (resultCode!=0){
				

				idAvaliador=data.getExtras().getString("valor");
				nome=data.getExtras().getString("nome");
				senha=data.getExtras().getString("senha");
				AlertDialog.Builder builder2;
		        final EditText input = new EditText(Bluetooth.this);
		 		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		 		builder2 = new AlertDialog.Builder(Bluetooth.this);
		        builder2.setTitle("Confirmação");
		        builder2.setView(input);
		        builder2.setMessage("Insira a senha de "+nome);

		        builder2.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
		        
		            public void onClick(DialogInterface dialog, int which) {
		            	String md5 = Utilidades.md5(input.getText().toString());
		            	if (md5.equals(senha)) {
		            		edtResponsavel.setText(nome);
		            	}else {
		            		edtResponsavel.setText("");
		            		Toast.makeText(Bluetooth.this, "Senha incorreta", Toast.LENGTH_LONG).show(); 
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
			}else {
				
			}
		}
	    public void carregaConfigs(){
	    	Cursor c;
	    	c = banco.carregarConfiguracoes();
	    	c.moveToFirst();
	    	macAdress = c.getString(c.getColumnIndex("macBt"));
	    	maxGrafico = c.getInt(c.getColumnIndex("maxGrafico"));
	    	minGrafico = c.getInt(c.getColumnIndex("minGrafico"));
	    	stepGrafico = c.getInt(c.getColumnIndex("stepGrafico"));
	    	taxAmostragem = c.getInt(c.getColumnIndex("tempo_amostragem"));
	    }
	}