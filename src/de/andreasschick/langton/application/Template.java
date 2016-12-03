package de.andreasschick.langton.application;

import java.util.List;

public class Template {

    private List<Position[]> positions;

    public Template(List<Position[]> positions) {
        this.positions = positions;
    }

    public List<Position[]> getPositions(){
        return positions;
    }

}
