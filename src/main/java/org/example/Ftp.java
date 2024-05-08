package org.example;
import java.net.Socket;
import java.io.*;
public class Ftp {
    public static void main(String[] args){
        try{
            String reponse;
            String user="raouf";
            String pass="admin";
            //
            Socket soc=new Socket("127.0.0.1",21);
            BufferedReader reader=new BufferedReader(new InputStreamReader(soc.getInputStream()));
            PrintWriter out=new PrintWriter(new OutputStreamWriter(soc.getOutputStream()),true);
            //
            reponse=reader.readLine();
            reponse=reader.readLine();
            System.out.println(reponse);
            if(!reponse.startsWith("220")){
                throw new IOException("Erreur de connexion au Ftp : "+reponse);
            }
            // envoyer username , password et voir la reponse
            out.println("USER "+user);
             //hello
            reponse=reader.readLine();
            System.out.println(reponse);
            if(!reponse.startsWith("331")){
                throw new IOException("Erreur de connexion avec username : "+reponse);
            }
            out.println("Pass "+pass);
            reponse=reader.readLine();
            System.out.println(reponse);
            if(!reponse.startsWith("230")){
                throw new IOException("Erreur de connexion avec password : "+reponse);
            }
            //
            System.out.println("Nous sommes maintenant connectés");
            //
            //Enntree Passive mode
            out.println("PASV");
            reponse=reader.readLine();
            System.out.println(reponse);
            //
            //PWD
            out.println("PWD");
            reponse=reader.readLine();
            System.out.println(reponse);
            //
            //CWD
            out.println("CWD "+"/ftp-2");
            reponse=reader.readLine();
            System.out.println(reponse);
            //
            //DELE
            String file="t4.txt";
            out.println("DELE "+file);
            reponse=reader.readLine();
            System.out.println(reponse);
            //
            //Quitter le server
            out.println("QUIT");
            reponse=reader.readLine();
            System.out.println(reponse);
            //
            soc.close();
        }
        catch(Exception e){e.printStackTrace();}
    }
}