package com.potkanburger.sudoku_durif_genoux_hu;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RelativeLayout;


public class Main_Sudoku extends Activity {

    GridView grille = null;
    EditText item_sudoku = null;
    ArrayAdapter<String> arrayAdapter = null;
    SudokuAdapter sudokuAdapter = null;
    Button generateButton = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        grille = (GridView) findViewById(R.id.grille);
        item_sudoku = (EditText) findViewById(R.id.item_sudoku);
        generateButton = (Button) findViewById(R.id.generer);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_sudoku);
        sudokuAdapter = new SudokuAdapter(this, R.layout.item_sudoku, Sudoku.getExampleGrid());


        for(int i=1;i<=81;i++){
            arrayAdapter.add(String.valueOf(i));
        }
        grille.setAdapter(arrayAdapter);


        grille.setAdapter(sudokuAdapter);

        if(getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(generateButton.getLayoutParams());
            lp.addRule(RelativeLayout.RIGHT_OF, R.id.grille);
            generateButton.setLayoutParams(lp);
        }

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateSudokuGrid().execute(grille);
            }
        });
    }

    private class UpdateSudokuGrid extends AsyncTask<GridView, Void, GridView>{

        private SudokuAdapter sdk;
        @Override
        protected GridView doInBackground(GridView... params) {
            GridView grille = params[0];
            this.sdk = new SudokuAdapter(getApplicationContext(), R.layout.item_sudoku, Sudoku.generateValidGrid());
            return grille;
        }

        @Override
        protected void onPostExecute(GridView gridView) {
            gridView.setAdapter(this.sdk);
            gridView.invalidateViews();
        }
    }
}
