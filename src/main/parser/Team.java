package main.parser;

import org.json.simple.JSONObject;

public class Team {
    private String name;
    private int points;
    private int games;
    private int victories;
    private int draws;
    private int losses;
    private int goalsPro;
    private int goalsAgainst;
    
    public Team(String name) {
        this.name = name;
        this.points = 0;
        this.games = 0;
        this.victories = 0;
        this.draws = 0;
        this.losses = 0;
        this.goalsPro = 0;
        this.goalsAgainst = 0;
    }

    public String getName() {
        return (this.name);
    }

    public int getPoints() {
        return (this.points);
    }

    public void updatePoints(int amnt) {
        this.points += amnt;
    }

    public int getGames() {
        return (this.games);
    }

    public void updateGames(int amnt) {
        this.games += amnt;
    }

    public int getVictories() {
        return (this.victories);
    }

    public void updateVictories(int amnt) {
        this.victories += amnt;
    }

    public void incVictories() {
        this.victories++;
        this.games++;
        this.points += 3;
    }

    public int getDraws() {
        return (this.draws);
    }

    public void incDraws() {
        this.draws++;
        this.games++;
        this.points += 1;
    }

    public void updateDraws(int amnt) {
        this.draws += amnt;
    }

    public int getLosses() {
        return (this.losses);
    }

    public void updateLosses(int amnt) {
        this.losses += amnt;
    }

    public void incLosses() {
        this.losses++;
        this.games++;
    }

    public int getGoalsPro() {
        return (this.goalsPro);
    }

    public void updateGoalsPro(int amnt) {
        this.goalsPro+= amnt;
    }

    public int getGoalsAgainst() {
        return (this.goalsAgainst);
    }

    public void updateGoalsAgainst(int amnt) {
        this.goalsAgainst += amnt;
    }

    public int getGoalDiff() {
        return (goalsPro - goalsAgainst);
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("nome", this.name);
        obj.put("PG", this.points);
        obj.put("J", this.games);
        obj.put("V", this.victories);
        obj.put("E", this.draws);
        obj.put("D", this.losses);
        obj.put("GP", this.goalsPro);
        obj.put("GC", this.goalsAgainst);
        obj.put("SG", this.getGoalDiff());

        return (obj);
    }

    @Override
    public String toString() {
        return (this.name + " - P: " + this.points + " - J: " + this.games + " - V: " +
                this.victories + " - E: " + this.draws + " - D: " + this.losses + " - GP: " +
                this.goalsPro + " GC: " + goalsAgainst + " - SG: " + this.getGoalDiff());
    }
}
