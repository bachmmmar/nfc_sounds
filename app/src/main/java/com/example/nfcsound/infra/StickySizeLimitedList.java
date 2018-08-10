package com.example.nfcsound.infra;

import java.util.ArrayList;

public class StickySizeLimitedList extends ArrayList<String> {

    private int max_size;
    private String [] sticky_part = new String[0];

    public StickySizeLimitedList(int max_size) {
        this.max_size = max_size;
    }

    public void setStickyPart(String [] part) {
        sticky_part = part.clone();
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

        for (String e : sticky_part) {
            if (e.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public String [] getAllAsArray() {
        int total_size = sticky_part.length + this.size();
        String output[] = new String[total_size];
        System.arraycopy(sticky_part,0,output,0,sticky_part.length);

        for (int i=0; i < this.size(); i++) {
            output[i+sticky_part.length]= this.get(i);
        }
        return output;
    }

}
