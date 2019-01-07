package br.com.apptg.apptg.helper;

import android.util.Base64;


//Classe criada para codificar os e-mails cadastrados, pois p Firebase não aceita o e-mail (escrito normalmente) para cadastro. Tem que estar codificado
//A base 64 codifica e descodifica, ou seja, transforma o e-mail em código e depois volta o código para e-mail
public class Base64Custom {

        public static String codificarBase64(String texto){
            return Base64.encodeToString(texto.getBytes(),   //transforma o texto passado como parametro para bytes (bytes é o tipo do parametro esperado)
                    Base64.DEFAULT).replaceAll("(\\n|\\r)",""); //o metodo replaceAll localiza uma string e a substitui
                                                    //ex: batata -> replaceAll("a","o") -> bototo
                                                    // \\n = caracteres de escape (não queremos esse tipo de caracter)

            //retorna uma string convertida para a base 64
        }

        public static String decodificarBase64(String textoCodificado){
            return new String (Base64.decode(textoCodificado, Base64.DEFAULT) );

        }
}
