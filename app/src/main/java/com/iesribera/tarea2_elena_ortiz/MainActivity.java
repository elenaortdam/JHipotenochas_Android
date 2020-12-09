package com.iesribera.tarea2_elena_ortiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.iesribera.tarea2_elena_ortiz.nivel.Nivel;
import com.iesribera.tarea2_elena_ortiz.nivel.NivelAvanzado;
import com.iesribera.tarea2_elena_ortiz.nivel.NivelFacil;
import com.iesribera.tarea2_elena_ortiz.nivel.NivelIntermedio;
import com.iesribera.tarea2_elena_ortiz.personajes.Personaje;
import com.iesribera.tarea2_elena_ortiz.personajes.PersonajeAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final ArrayList<Personaje> personajes = new ArrayList<>();
    private Personaje personajeSeleccionado;
    private GridLayout grid;
    private Tablero tablero;
    private Nivel nivel = new NivelFacil();
    private Casilla[][] casillas;
    private final int dificultad = 0;
    private int width;
    private int height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Personaje oreo = new Personaje("Oreo", ContextCompat.getDrawable(this, R.drawable.android_oreo));
        Personaje kitkat = new Personaje("Kitkat", ContextCompat.getDrawable(this, R.drawable.android_kitkat));
        Personaje ginger = new Personaje("GingerBread", ContextCompat.getDrawable(this, R.drawable.android_gingerbread));
        personajes.add(oreo);
        personajes.add(kitkat);
        personajes.add(ginger);
        personajeSeleccionado = oreo;
        nuevaPartida();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.instrucciones:
                mostrarInstrucciones();
                return true;
            case R.id.nuevo_juego:
                nuevaPartida();
                return true;
            case R.id.configurar:
                seleccionarNivel();
                return true;
            case R.id.selec_personaje:
                elegirPersonaje();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void elegirPersonaje() {
        Dialog personaje = new Dialog(this);
        personaje.setContentView(R.layout.spinner_personaje);
        personaje.setTitle("Personajes");

        Spinner spinner = personaje.findViewById(R.id.spinner_personaje);
        PersonajeAdapter personajeAdapter = new PersonajeAdapter(this, personajes);
        spinner.setAdapter(personajeAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                personajeSeleccionado = personajes.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                personaje.dismiss();
            }
        });
        personaje.show();
    }

    public void seleccionarNivel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final CharSequence[] items = new CharSequence[3];
        items[0] = Constantes.NivelFacil.TEXTO;
        items[1] = Constantes.NivelMedio.TEXTO;
        items[2] = Constantes.NivelAvanzado.TEXTO;
        builder.setTitle(R.string.titulo_nivel)
                .setSingleChoiceItems(items, dificultad, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nivel = crearNivel(i);
                    }
                })
                .setPositiveButton(R.string.volver, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        nuevaPartida();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private Nivel crearNivel(int i) {
        switch (i) {
            case Constantes.NivelFacil.OPCION_MENU:
                nivel = new NivelFacil();
                break;
            case Constantes.NivelMedio.OPCION_MENU:
                nivel = new NivelIntermedio();
                break;
            case Constantes.NivelAvanzado.OPCION_MENU:
                nivel = new NivelAvanzado();
                break;
        }
        if (nivel == null) {
            throw new IllegalArgumentException("Ha ocurrido un error al detectar el nivel");
        }
        return nivel;
    }

    public void nuevaPartida() {
        tablero = new Tablero(nivel);
        casillas = crearPartida(nivel);
    }

    public Casilla[][] crearPartida(Nivel nivel) {
        grid = findViewById(R.id.grid);
        grid.removeAllViews();
        grid.setColumnCount(nivel.getColumnas());
        grid.setRowCount(nivel.getFilas());
        casillas = new Casilla[nivel.getColumnas()][nivel.getFilas()];

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        GridLayout.LayoutParams param = new GridLayout.LayoutParams();
        param.setMargins(0, 0, 0, 10);
        param.height = ViewGroup.LayoutParams.MATCH_PARENT;
        param.width = ViewGroup.LayoutParams.MATCH_PARENT;
        float escala = getResources().getDisplayMetrics().density;
        float dpPx = (int) (50 * escala + 0.6f);
        height -= dpPx;
        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(width / nivel.getColumnas(), height / nivel.getFilas());
        layoutParams.setMargins(0, 0, 0, 0);
        for (int i = 0; i < nivel.getColumnas(); i++) {
            for (int j = 0; j < nivel.getFilas(); j++) {
                casillas[i][j] = new Casilla(this, i, j, (byte) tablero.getCasillas()[i][j]);
//                casillas[i][j].setLayoutParams(new ViewGroup.LayoutParams(this.width / nivel.getColumnas(),
//                        ((this.height) / nivel.getFilas())));
                casillas[i][j].setLayoutParams(layoutParams);
                casillas[i][j].setText(String.valueOf(tablero.getCasillas()[i][j]));
                casillas[i][j].setPadding(0, 0, 0, 0);
                casillas[i][j].setBackgroundResource(R.drawable.my_button_bg);
                casillas[i][j].setTextColor(Color.BLACK);
                casillas[i][j].setOnClickListener(this);
//                boton.setOnLongClickListener(this);
                grid.addView(casillas[i][j]);
            }
        }
        return casillas;
    }

    public void mostrarInstrucciones() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.instrucciones_texto)
                .setTitle(R.string.instrucciones)
                .setPositiveButton(R.string.volver, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onClick(View view) {
        Casilla casilla = (Casilla) view;
        casilla.setEnabled(false);
        casilla.setTextColor(Color.BLACK);
        if (casilla.getText().equals("0")) {
            descubrirCelda(casilla);
            int hipotenochas = Nivel.contarHipotenochasAlrededor(casillas, casilla.getFila(), casilla.getColumna());
            if (hipotenochas == 0) {
                List<Casilla> despejadas = new ArrayList<>();
                do {
                    despejadas = despejar(casilla);
                } while (!despejadas.isEmpty());

            } else {
                casilla.setText(String.valueOf(hipotenochas));
            }
        } else {
//            inabilitar();
            casilla.setText("");
            casilla.setBackground(personajeSeleccionado.getImagen());
            Toast.makeText(MainActivity.this,
                    R.string.derrota, Toast.LENGTH_LONG).show();
        }
    }

    public List<Casilla> despejar(Casilla casilla) {
        List<Casilla> casillasDespejadas = new ArrayList<>();
        for (int i = casilla.getColumna() - 1; i <= casilla.getColumna() + 1; i++) {
            for (int j = casilla.getFila() - 1; j <= casilla.getFila() + 1; j++) {
                int hipotenochas = Nivel.contarHipotenochasAlrededor(casillas, i, j);
                if (hipotenochas == 0) {
                    casilla.setText("");
                    casilla.setBackgroundColor(Color.RED);
                }
                /*
                try {
                    if (casillas[i][j].getTieneHipotenocha() == 1) {
                        despejar(casillas[i][j]);
                    } else {
                        casilla.setText("");
                        casilla.setBackgroundColor(Color.RED);
                        if (casillas[i][j] != casilla) {
                            casillasDespejadas.add(casillas[i][j]);
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }

                 */
            }
        }
        return casillasDespejadas;
    }

    public void despejar(Button boton, ArrayList<String> despejadas) {
        for (String coordenada : despejadas) {
            if (coordenada.equals(boton.getTag())) return;
        }
        despejadas.add((String) boton.getTag());
        boton.setEnabled(false);
        int[] posicion = obtenerPosicion((String) boton.getTag());
        for (int i = 0; i < grid.getChildCount(); i++) {
            Button botonHijo = (Button) grid.getChildAt(i);
            int[] posicionHijo = obtenerPosicion((String) grid.getChildAt(i).getTag());
            int hipotenochas = Nivel.contarHipotenochasAlrededor(casillas, posicionHijo[0], posicionHijo[1]);
            if (hipotenochas == 0) {
                if (posicion[0] == posicionHijo[0] & (posicion[1] == (1 + posicionHijo[1])
                        || posicion[1] == (posicionHijo[1] - 1))) {
                    botonHijo.setBackgroundColor(Color.RED);
                    botonHijo.setText("");
                    despejar(botonHijo, despejadas);
                }
                if (posicion[1] == posicionHijo[1] & (posicion[0] == (1 + posicionHijo[0]) || posicion[0] == (posicionHijo[0] - 1))) {
                    botonHijo.setBackgroundColor(Color.RED);
                    botonHijo.setText("");
                    despejar(botonHijo, despejadas);
                }
            }
        }
    }

    int[] obtenerPosicion(String coordenada) {
        int[] posicion = new int[2];
        String coma = ",";
        String[] coordenadas = coordenada.split(coma);
        for (int i = 0; i < coordenadas.length; i++) {
            posicion[i] = Integer.parseInt(coordenadas[i]);
        }
        return posicion;
    }

    public void descubrirCelda(Button boton) {
        boton.setBackgroundColor(Color.WHITE);
    }
}