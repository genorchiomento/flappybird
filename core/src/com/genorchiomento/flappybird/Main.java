package com.genorchiomento.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private Texture fundo;
    private int movimentoX = 0;
    private int movimentoY = 0;
    private Texture[] passaros;

    //atributo de configuração
    private float larguraDoDispositivo;
    private float alturaDoDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float posicaoPassaroY = 0;

	@Override
	public void create () {
        batch = new SpriteBatch();
        passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");
        fundo = new Texture("fundo.png");

        larguraDoDispositivo = Gdx.graphics.getWidth();
        alturaDoDispositivo = Gdx.graphics.getHeight();
        posicaoPassaroY = (alturaDoDispositivo/2);
	}

	@Override
	public void render () {
	    //start
	    batch.begin();

	    if (variacao > 3){
            variacao =0;
        }

        //evento de click
        boolean toqueNaTela = Gdx.input.justTouched();
        if (toqueNaTela) {
            gravidade = -15;
        }

        //aplicar gravidade no passaro
        if (posicaoPassaroY > 0 || toqueNaTela) {
            posicaoPassaroY = posicaoPassaroY - gravidade;
        }

	    batch.draw(fundo, 0, 0, larguraDoDispositivo, alturaDoDispositivo);
	    batch.draw(passaros[(int)variacao], 30, posicaoPassaroY);

	    gravidade++;
        variacao += (Gdx.graphics.getDeltaTime() * 8);
	    movimentoX++;

	    //finish
	    batch.end();
	}
	
	@Override
	public void dispose () {
    }
}
