package com.example.pushmahjong;

import java.util.ArrayList;

import android.content.Context;

public class GameData {

	private static GameData gameData;
	private ArrayList<MahJong> mahjongList = new ArrayList<MahJong>();
	private GameBoard gameBoard;
	
	private Context context;
	
	private int imageWidth;
	private int imageHeight;
	
	public GameData(Context context) {
		this.context = context;
		createMahJong();
		createGameBoard();
	}

	public ArrayList<MahJong> getMahjongList() {
		return mahjongList;
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	private void createGameBoard() {
		imageWidth = context.getResources().getDrawable(R.drawable.b1).getIntrinsicWidth();
		imageHeight = context.getResources().getDrawable(R.drawable.b1).getIntrinsicHeight();
		gameBoard = new GameBoard(context, mahjongList, imageWidth, imageHeight);
	}

	private void createMahJong() {
		for (int i = 0; i < MahJong.TOTAL; i++) {
			mahjongList.add(new MahJong(context, i));
		}
	}
}
