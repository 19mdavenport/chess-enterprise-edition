package model;

import chess.TeamColor;

public record JoinGameRequest(TeamColor playerColor, int gameID) {}
