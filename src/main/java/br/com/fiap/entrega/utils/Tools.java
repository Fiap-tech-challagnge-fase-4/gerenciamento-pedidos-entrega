package br.com.fiap.entrega.utils;

import java.util.Random;

import org.springframework.data.util.Pair;

public class Tools {
    
    public Pair<String,String> getCordenadasPeloEndereco(String endereco)
    {
        //TODO: Se der tempo consultar uma API que retorne a coordenada com base no endereço
        //Por enquanto o método esta apenas "Mockado" retornando coordenadas aleatorias
        
        Random random = new Random();
        double latitude = -90 + (90 - -90) * random.nextDouble();
        double longitude = -180 + (180 - -180) * random.nextDouble();

        return Pair.of(Double.toString(latitude), Double.toString(longitude));
    }
}
