package com.bachmann.nfcsound.infra;

import java.util.ArrayList;

public class SizeLimitedList extends ArrayList<String> {

    private int max_size;

    public SizeLimitedList(int max_size) {
        this.max_size = max_size;
    }

    @Override
    public boolean add(String e) {
        if (isAlreadyInList(e)) {
            return false;
        }

        if (this.size() >= max_size) {
            remove(0);
        }
        return super.add(e);
    }

    private boolean isAlreadyInList(String value) {
        for (int i=0; i<this.size();i++) {
            if (this.get(i).equals(value)) {
                return true;
            }
        }

        return false;
    }

    public String [] getAllAsArray() {
        String output[] = new String[this.size()];

        for (int i=0; i < this.size(); i++) {
            output[i]= this.get(i);
        }
        return output;
    }

}
