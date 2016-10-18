package com.example.conexaodb;

import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
 
    // Nome do banco
    private static final String DATABASE_NAME = "Sistema";
 
    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
 
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("PRAGMA foreign_keys = ON");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE pessoa( _id INTEGER PRIMARY KEY, nome Text, nascimento Text, telefone Text, endereco Text," +
        		"sexo Text, email Text, rg Text, cidade Text, anotacoes Text, observacoes Text)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        //CREATE_CONTACTS_TABLE = "CREATE TABLE exame(_id INTEGER PRIMARY KEY, valores Text, idPessoa INTEGER, responsavel Text , data Text, duracao Text, mao Text, valMaximo float, valMedio float, tipo Text ,FOREIGN KEY(idPessoa) REFERENCES pessoa(_id) ON DELETE CASCADE)";
        CREATE_CONTACTS_TABLE = "CREATE TABLE exame(_id INTEGER PRIMARY KEY, valores Text, idPessoa INTEGER, idAvaliador INTEGER  , data Text, duracao Text, mao Text, valMaximo float, valMedio float, tipo Text ,FOREIGN KEY(idPessoa) REFERENCES pessoa(_id) ON DELETE CASCADE, FOREIGN KEY(idAvaliador) REFERENCES avaliador(_id))";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        CREATE_CONTACTS_TABLE = "CREATE TABLE parametros(_id INTEGER PRIMARY KEY, coef_angular double, coef_linear, macBt Text, tempo_amostragem int, maxGrafico int, minGrafico int, stepGrafico int)";
        db.execSQL(CREATE_CONTACTS_TABLE);
        CREATE_CONTACTS_TABLE = "CREATE TABLE avaliador(_id INTEGER PRIMARY KEY,senha Text, instituicao Text, idPessoa INTEGER, FOREIGN KEY(idPessoa) REFERENCES pessoa(_id) ON DELETE CASCADE )";
        db.execSQL(CREATE_CONTACTS_TABLE);
        
        String sql="INSERT INTO parametros(coef_angular,coef_linear,macBt,tempo_amostragem,maxGrafico,minGrafico,stepGrafico) values('1','1','00:12:11:28:20:79','100','100','10','5')";
        db.execSQL(sql);
    }
 
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS pessoa");
        db.execSQL("DROP TABLE IF EXISTS parametros");
        db.execSQL("DROP TABLE IF EXISTS exame");
 
        // Create tables again
        onCreate(db);
    }
    
    public void adicionarPessoa(Pessoa p){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql="INSERT INTO pessoa(nome,telefone,endereco,sexo,email,rg,cidade,nascimento, observacoes, anotacoes) values('"+ p.getNome() + "','" + p.getTelefone() + "','"
    	+ p.getEndereco() + "','" + p.getSexo() + "','" + p.getEmail() + "','" + p.getRg() + "','" + p.getCidade() + "','"+p.getNascimento()+ "','" +p.getObservacao() +"','"+p.getAnotacao()+"')";
    	db.execSQL(sql);
    }
    
    public void adicionarAvaliador(Avaliador p){
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor cursor = null;
    	String sql="INSERT INTO pessoa(nome,telefone,endereco,sexo,email,rg,cidade,nascimento, observacoes, anotacoes) values('"+ p.getNome() + "','" + p.getTelefone() + "','"
    	    	+ p.getEndereco() + "','" + p.getSexo() + "','" + p.getEmail() + "','" + p.getRg() + "','" + p.getCidade() + "','"+p.getNascimento()+ "','" +p.getObservacao() +"','"+p.getAnotacao()+"')";
    	db.execSQL(sql);
    	cursor = PesquisaSLike(p.getNome(), "nome", "pessoa");
    	cursor.moveToFirst();
    	int id = cursor.getInt((cursor.getColumnIndex("_id")));
    	sql="INSERT INTO avaliador(senha, instituicao,idPessoa) values('"+p.getSenha()+"','"+p.getInstituicao()+"','"+id+"')";
    	db.execSQL(sql);
    }
    
    public void adicionaExame(Exame exame){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql="INSERT INTO exame(valores,idPessoa,idAvaliador,data,duracao,mao,tipo,valMaximo,valMedio) values('"+ exame.getValores() + "','" + exame.getIdPessoa() + "','"
    	+ exame.getResponsavel() + "','"+ exame.getData() +"','" + exame.getDuracao() + "','" + exame.getMao()+"','"+exame.getTipo()+"','"+ exame.getValMax()+"','"+exame.getValMed()+"')";
    	db.execSQL(sql);
    }
    
    
    public void alteraRegistro(Pessoa p,String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql=("UPDATE pessoa SET nome='"+p.getNome()+"',cidade='"+p.getCidade()+"', email='"+p.getEmail()+"',endereco='"+p.getEndereco()+"', sexo='"+p.getSexo()+"',rg='"+p.getRg()+"',telefone='"+p.getTelefone()+"',nascimento='"+p.getNascimento()+ "',anotacoes='"+p.getAnotacao() +"',observacoes='"+p.getObservacao()+"' WHERE _id='"+id+"'");    	
    	db.execSQL(sql);
    }
    
    public void alteraRegistroAvaliador(Avaliador p,String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql=("UPDATE pessoa SET nome='"+p.getNome()+"',cidade='"+p.getCidade()+"', email='"+p.getEmail()+"',endereco='"+p.getEndereco()+"', sexo='"+p.getSexo()+"',rg='"+p.getRg()+"',telefone='"+p.getTelefone()+"',nascimento='"+p.getNascimento()+ "',anotacoes='"+p.getAnotacao() +"',observacoes='"+p.getObservacao()+"' WHERE _id='"+id+"'");    	
    	db.execSQL(sql);
    	sql=("UPDATE avaliador SET senha='"+p.getSenha()+"',instituicao='"+p.getInstituicao()+"' WHERE idPessoa='"+id+"'");    	
    	db.execSQL(sql);
    }
    
    public void alteraConfig(Configuracao c,String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql=("UPDATE parametros SET coef_angular='"+c.getCoefAngular() +"', coef_linear='"+c.getCoefLinear()+"', macBt='"+c.getMacAdress()+"', tempo_amostragem='"+c.getTaxAmostragem()+"', maxGrafico='"+c.getMaxGrafico()+"', minGrafico='"+c.getMinGrafico()+"', stepGrafico='"+c.getStepGrafico()+"' WHERE _id='"+id+"'");
    	db.execSQL(sql);
    }
    
    public void deletaRegistro(String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("PRAGMA foreign_keys = ON");
    	String sql=("DELETE from pessoa WHERE _id='"+id+"'");    	
    	db.execSQL(sql);
    }
    
    public void deletaRegistroAvaliador(String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	db.execSQL("PRAGMA foreign_keys = ON");
    	String sql=("DELETE from pessoa WHERE _id='"+id+"'");    	
    	db.execSQL(sql);
    }
    
    public void deletaRegistroExame(String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql=("DELETE from exame WHERE _id='"+id+"'");    	
    	db.execSQL(sql);
    }
    
    public Cursor selecionarTodos(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql= "select * from pessoa";
    	Cursor cursor = db.rawQuery(sql, null);
    	return cursor;
    }
    
    public Cursor carregarConfiguracoes(){
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql= "select * from parametros";
    	Cursor cursor = db.rawQuery(sql, null);
    	return cursor;
    }
    
    public boolean  verificaSeEAvaliador(String id){
    	SQLiteDatabase db = this.getWritableDatabase();
    	Cursor aux = PesquisaSLike(id, "idPessoa", "avaliador");
    	aux.moveToFirst();
    	if (aux.getCount()>0){
    		return true;
    	} else return false;
    	
    }
    
    public  Cursor PesquisaLike(String item, String campo, String tabela) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql= "select * from "+tabela+" where "+campo+" LIKE '%"+item+"%'";
    	//selecct * from dinamomentro where mao=id and 
    	Cursor cursor = db.rawQuery(sql, null);
    	return cursor;
    }
    
    public  Cursor PesquisaSLike(String item, String campo, String tabela) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql= "select * from "+tabela+" where "+campo+"='"+item+"'";
    	//selecct * from dinamomentro where mao=id and 
    	Cursor cursor = db.rawQuery(sql, null);
    	return cursor;
    }
    public  Cursor pesquisaDireta(String campo, String tabela) {
    	SQLiteDatabase db = this.getWritableDatabase();
    	String sql= "select * from "+tabela+" where "+campo;
    	//selecct * from dinamomentro where mao=id and 
    	Cursor cursor = db.rawQuery(sql, null);
    	return cursor;
    }
    
    
    
}