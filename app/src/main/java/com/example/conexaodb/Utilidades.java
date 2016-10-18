package com.example.conexaodb;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.io.*;
import java.math.BigInteger;

public class Utilidades {
	public boolean validarData(String data) {
		String dia;
        String mes;
        String ano;
        
        StringTokenizer token = new StringTokenizer(data, "/");
        try {
        	dia = token.nextToken();
            mes = token.nextToken();
            ano = token.nextToken();

            if (dia.length() < 2 || dia.length() > 2)
                return false;
            if (mes.length() < 2 || mes.length() > 2)
                return false;
            if (ano.length() < 4 || dia.length() > 4)
                return false;

            int intDia = Integer.parseInt(dia);
            int intMes = Integer.parseInt(mes);
            int intAno = Integer.parseInt(ano);

            if (intMes < 1 || intMes > 12)
                return false;

            if (intMes == 1 || intMes == 3 || intMes == 5 || intMes == 7
                    || intMes == 8 || intMes == 12) {
                if (intDia < 1 || intDia > 31)
                    return false;
            } else if (intMes == 4 || intMes == 6 || intMes == 9
                    || intMes == 10 || intMes == 11) {
                if (intDia < 1 || intDia > 30)
                    return false;
            } else if (!new GregorianCalendar().isLeapYear(intAno)) {
                    return false;
            }
        } catch (Exception e) {
        	return false;
        }
        return true;
	}
	
	public static String calculaIdade(String dataNasc, String pattern){
        DateFormat sdf = new SimpleDateFormat(pattern);
        Date dataNascInput = null;
        try {
            dataNascInput= sdf.parse(dataNasc);
        } catch (Exception e) {}
        
        Calendar dateOfBirth = new GregorianCalendar();
        dateOfBirth.setTime(dataNascInput);
        
        // Cria um objeto calendar com a data atual
        Calendar today = Calendar.getInstance();
        
       // Obtém a idade baseado no ano
        int age = today.get(Calendar.YEAR) - dateOfBirth.get(Calendar.YEAR);
        
        dateOfBirth.add(Calendar.YEAR, age);
        
        if (today.before(dateOfBirth)) {
            age--;
        }
        return age+"";
        
    }
	public static String md5(String senha){  
        String sen = "";  
        MessageDigest md = null;  
        try {  
            md = MessageDigest.getInstance("MD5");  
        } catch (NoSuchAlgorithmException e) {  
            e.printStackTrace();  
        }  
        BigInteger hash = new BigInteger(1, md.digest(senha.getBytes()));  
        sen = hash.toString(16);              
        return sen;  
    }  
}