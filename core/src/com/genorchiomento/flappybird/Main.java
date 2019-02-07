package com.genorchiomento.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Random;

public class Main extends ApplicationAdapter {

    //Texturas
    private SpriteBatch batch;
    private Texture fundo;
    private Texture[] passaros;
    private Texture canoBaixo;
    private Texture canoTopo;
    private Texture gameOver;

    //formas para colisao
    private ShapeRenderer shapeRenderer;
    private Circle circuloPassaro;
    private Rectangle retanguloCanoTopo;
    private Rectangle retanguloCanoBaixo;

    //atributo de configuração
    private float larguraDoDispositivo;
    private float alturaDoDispositivo;
    private float variacao = 0;
    private float gravidade = 0;
    private float valorGravidade = -15;
    private float posicaoInicialPassaroY = 0;
    private float posicaoInicialPassaroX = 60;
    private float posicaoCanoX;
    private float posicaoCanoY;
    private float espacoEntreCanos;
    private Random random;
    private int pontuacao = 0;
    private int pontuacaoMaxima = 0;
    private boolean passouDoCano;
    private int statusJogo = 0;

    //Exibicao de textos
    BitmapFont textoPontuacao;
    BitmapFont textoReiniciar;
    BitmapFont textoMelhorPontuacao;

    //Config sons
    Sound somVoando;
    Sound somColisao;
    Sound somPontuacao;

    //Conf preferencias
    Preferences preferences;

    //obj para a camaera
    private OrthographicCamera camera;
    private Viewport viewport;
    private final float VIRTUAL_WIDTH = 720;
    private final float VIRTUAL_HEIGHT = 1280;

    @Override
    public void create () {
        inicializarTextura();

        inicializarObjetos();
    }

    @Override
    public void render () {
        //Limpar os frames anteriores
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BITS);

        verificarEstadoDoJogo();
        validarPontos();
        desenharObjetos();
        detectarColisoes();
    }

    private void verificarEstadoDoJogo() {

        /*
        *
        * 0 - Jogo inicial, passaro parado
        * 1 - Começa o jogo
        * 2 - Colidiu
        *
         */
        boolean toqueNaTela = Gdx.input.justTouched();

        if (statusJogo == 0) {
            //evento de click
            if (toqueNaTela) {
                gravidade = valorGravidade;
                statusJogo = 1;
                somVoando.play();
            }

        } else if (statusJogo == 1) {

            //evento de click
            if (toqueNaTela) {
                gravidade = valorGravidade;
                somVoando.play();
            }

            //movimentacao canos
            posicaoCanoX -= Gdx.graphics.getDeltaTime() * 400;
            if (posicaoCanoX < -canoTopo.getWidth()) {
                posicaoCanoX = larguraDoDispositivo;
                posicaoCanoY = random.nextInt(600) - 300;
                passouDoCano = false;
            }

            //aplicar gravidade no passaro
            if (posicaoInicialPassaroY > 0 || toqueNaTela) {
                posicaoInicialPassaroY = posicaoInicialPassaroY - gravidade;
            }

            gravidade++;

        } else if (statusJogo == 2) {

            if (pontuacao > pontuacaoMaxima) {
                pontuacaoMaxima = pontuacao;
                preferences.putInteger("pontuacaoMaxima", pontuacaoMaxima);
            }

            //aplicar gravidade no passaro
            if (posicaoInicialPassaroY > 0 || toqueNaTela) {
                posicaoInicialPassaroY = posicaoInicialPassaroY - gravidade;
            }
            gravidade++;

            if (toqueNaTela) {
                statusJogo = 0;
                pontuacao = 0;
                gravidade = 0;
                posicaoInicialPassaroY = (alturaDoDispositivo/2);
                posicaoCanoX = larguraDoDispositivo;
            }
        }



    }

    public void detectarColisoes() {

        circuloPassaro.set(
                posicaoInicialPassaroX + passaros[0].getWidth()/2,posicaoInicialPassaroY + passaros[0].getHeight()/2, passaros[0].getWidth()/2
        );

        retanguloCanoBaixo.set(
                posicaoCanoX, alturaDoDispositivo/2 + espacoEntreCanos/2 + posicaoCanoY,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        retanguloCanoTopo.set(
                posicaoCanoX, alturaDoDispositivo/2 - canoBaixo.getHeight() - (espacoEntreCanos/2) + posicaoCanoY,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        boolean colisaoCanoTopo = Intersector.overlaps(circuloPassaro, retanguloCanoTopo);
        boolean colisaoCanoBaixo = Intersector.overlaps(circuloPassaro, retanguloCanoBaixo);

        if (colisaoCanoTopo || colisaoCanoBaixo){
            Gdx.app.log("Log", "Colidiu!");
            if (statusJogo == 1){
                somColisao.play();
                statusJogo = 2;
            }
        }

        /*
        //shape
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);

        //passaro
        shapeRenderer.circle(
                posicaoInicialPassaroX + passaros[0].getWidth()/2,posicaoInicialPassaroY + passaros[0].getHeight()/2, passaros[0].getWidth()/2
        );

        //topo
        shapeRenderer.rect(
                posicaoCanoX, alturaDoDispositivo/2 - canoBaixo.getHeight() - (espacoEntreCanos/2) + posicaoCanoY,
                canoBaixo.getWidth(), canoBaixo.getHeight()
        );

        //baixo
        shapeRenderer.rect(
                posicaoCanoX, alturaDoDispositivo/2 + espacoEntreCanos/2 + posicaoCanoY,
                canoTopo.getWidth(), canoTopo.getHeight()
        );

        shapeRenderer.end();
        */
    }

    private void desenharObjetos() {

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        batch.draw(fundo, 0, 0, larguraDoDispositivo, alturaDoDispositivo);

        batch.draw(passaros[(int)variacao], posicaoInicialPassaroX, posicaoInicialPassaroY);

        batch.draw(canoBaixo, posicaoCanoX, alturaDoDispositivo/2 - canoBaixo.getHeight() - (espacoEntreCanos/2) + posicaoCanoY);
        batch.draw(canoTopo, posicaoCanoX, alturaDoDispositivo/2 + espacoEntreCanos/2 + posicaoCanoY);

        textoPontuacao.draw(batch, String.valueOf(pontuacao), larguraDoDispositivo/2, alturaDoDispositivo - 110);

        if (statusJogo == 2) {
            batch.draw(gameOver, larguraDoDispositivo/2 - gameOver.getWidth()/2, alturaDoDispositivo/2);
            textoReiniciar.draw(batch, "Toque para reiniciar", larguraDoDispositivo/2 - 140, alturaDoDispositivo/2 - gameOver.getHeight()/2);
                        textoMelhorPontuacao.draw(batch, "Seu record é: " + pontuacaoMaxima + " pontos", larguraDoDispositivo/2 - 140, alturaDoDispositivo/2 - gameOver.getHeight());
        }

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

        //gameOver
        gameOver = new Texture("game_over.png");

        //configura preferencias
        preferences = Gdx.app.getPreferences("flappyBird");
        pontuacaoMaxima = preferences.getInteger("pontuacaoMaxima", 0);
    }

    private void inicializarObjetos() {
        batch = new SpriteBatch();
        random = new Random();

        larguraDoDispositivo = VIRTUAL_WIDTH;
        alturaDoDispositivo = VIRTUAL_HEIGHT;
        posicaoInicialPassaroY = (alturaDoDispositivo/2);
        posicaoCanoX = larguraDoDispositivo;
        espacoEntreCanos = 280;

        //Config dos textos
        textoPontuacao = new BitmapFont();
        textoPontuacao.setColor(Color.WHITE);
        textoPontuacao.getData().setScale(10);

        textoReiniciar = new BitmapFont();
        textoReiniciar.setColor(Color.GREEN);
        textoReiniciar.getData().setScale(2);

        textoMelhorPontuacao = new BitmapFont();
        textoMelhorPontuacao.setColor(Color.RED);
        textoMelhorPontuacao.getData().setScale(2);

        //Som
        somVoando = Gdx.audio.newSound(Gdx.files.internal("som_asa.wav"));
        somColisao = Gdx.audio.newSound(Gdx.files.internal("som_colisao.wav"));
        somPontuacao = Gdx.audio.newSound(Gdx.files.internal("som_pontos.wav"));

        shapeRenderer = new ShapeRenderer();
        circuloPassaro = new Circle();
        retanguloCanoTopo = new Rectangle();
        retanguloCanoBaixo = new Rectangle();

        //config da camera
        camera = new OrthographicCamera();
        camera.position.set(VIRTUAL_WIDTH/2, VIRTUAL_HEIGHT/2, 0);
        viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void validarPontos() {
        if (posicaoCanoX < (posicaoInicialPassaroX - passaros[0].getWidth())) {
            if (!passouDoCano) {
                pontuacao++;
                passouDoCano = true;
                somPontuacao.play();
            }
        }

        variacao += (Gdx.graphics.getDeltaTime() * 8);
        //variacao para bater as asas
        if (variacao > 3){
            variacao =0;
        }
    }

    @Override
    public void dispose () {
    }
}