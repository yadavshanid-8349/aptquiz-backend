package com.tictactoe.aptquizbackend.dto;

public class LeaderboardPlayer {

    private Long id;
    private String playerName;
    private Integer score;

    public LeaderboardPlayer() {
    }

    public LeaderboardPlayer(
            Long id,
            String playerName,
            Integer score) {

        this.id = id;
        this.playerName = playerName;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}