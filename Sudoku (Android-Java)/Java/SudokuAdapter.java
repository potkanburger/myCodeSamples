package com.potkanburger.sudoku_durif_genoux_hu;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Collection;
import java.util.Iterator;


public class SudokuAdapter extends ArrayAdapter {

    private SudokuGrid sudoGrid;
    private Collection<Sudoku.Coord> conflictAll;

    public SudokuAdapter(Context context, int resource, SudokuGrid sudokuGrid){
        super(context, resource, sudokuGrid.toList());
        this.sudoGrid = sudokuGrid;
        this.conflictAll = Sudoku.checkGrid(sudokuGrid);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.item_sudoku, null);
        }
        final SquareEditText squareEditText = (SquareEditText) convertView.findViewById(R.id.item_sudoku);
        squareEditText.setText(sudoGrid.toList().get(position).toString());
        final byte row = SudokuGrid.getRow(position);
        final byte cell = SudokuGrid.getCell(position);

        squareEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("Coordonnee case: ", "" + row + " - " + cell);
                sudoGrid.set(row, cell, Element.from(squareEditText.getText().toString()));
                Collection<Sudoku.Coord> conflict = Sudoku.checkValue(sudoGrid, row, cell);
                if (!conflict.isEmpty()) {
                    squareEditText.setBackgroundColor(Color.RED);
                } else {
                    squareEditText.setBackgroundColor(Color.WHITE);
                }

                if(position!=0 ){
                    updateAdapter();
                }


            }
        });
        boolean contains = false;
        Iterator<Sudoku.Coord> it = conflictAll.iterator();
        while(it.hasNext()){
            Sudoku.Coord tmp = it.next();
            if(tmp.row*9+tmp.cell==position){
                contains = true;
                break;
            }
        }
        if(contains){
            squareEditText.setBackgroundColor(Color.RED);
        }
        else {
            squareEditText.setBackgroundColor(Color.WHITE);
        }
        return squareEditText;
    }

    public void updateAdapter(){
        this.conflictAll = Sudoku.checkGrid(sudoGrid);
        this.notifyDataSetChanged();
    }
}
