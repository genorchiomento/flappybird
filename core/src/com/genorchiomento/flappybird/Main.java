package com.genorchiomento.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Main extends ApplicationAdapter {

    //Texturas
    private SpriteBatch batch;
    private Texture fundo;
    private Texture[] passaros;
    private Texture canoBaixo;
    private Texture canoTopo;

    //atributo de configuração
    private float larguraDoDispositivo;
    private float alturaDoDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float posicaoInicialPassaroY = 0;
    private float posicaoCanoX = 0;
    private float posicaoCanoY = 0;

	@Override
	public void create () {
        inicializarTextura();

        inicializarObjetos();
	}

	@Override
	public void render () {
	    verificarEstadoDoJogo();
	    desenharTextura();

	}

	private void verificarEstadoDoJogo() {
        //evento de click
        boolean toqueNaTela = Gdx.input.justTouched();
        if (toqueNaTela) {
            gravidade = -25;
        }

        //aplicar gravidade no passaro
        if (posicaoInicialPassaroY > 0 || toqueNaTela) {
            posicaoInicialPassaroY = posicaoInicialPassaroY - gravidade;
        }

        gravidade++;
        variacao += (Gdx.graphics.getDeltaTime() * 8);
        //variacao para bater as asas
        if (variacao > 3){
            variacao =0;
        }
    }

	private void desenharTextura() {
        batch.begin();

        batch.draw(fundo, 0, 0, larguraDoDispositivo, alturaDoDispositivo);

        batch.draw(passaros[(int)variacao], 30, posicaoInicialPassaroY);

        posicaoCanoX--;
//        TODO: parei aqui nos canos
        batch.draw(canoBaixo, posicaoCanoX, alturaDoDispositivo/2);
//        batch.draw(canoTopo, 0, 0);

        batch.end();
    }

	private void inicializarTextura() {
        //passaro
	    passaros = new Texture[3];
        passaros[0] = new Texture("passaro1.png");
        passaros[1] = new Texture("passaro2.png");
        passaros[2] = new Texture("passaro3.png");

        //cano
        canoBaixo = new Texture("cano_baixo_maior.png");
        canoTopo = new Texture("cano_topo_maior.png");

        //bg
        fundo = new Texture("fundo.png");
    }

    private void inicializarObjetos() {
        batch = new SpriteBatch();

        larguraDoDispositivo = Gdx.graphics.getWidth();
        alturaDoDispositivo = Gdx.graphics.getHeight();
        posicaoInicialPassaroY = (alturaDoDispositivo/2);
        posicaoCanoX = larguraDoDispositivo;
    }
	
	@Override
	public void dispose () {
    }
}
