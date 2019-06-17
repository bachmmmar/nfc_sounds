package com.bachmann.nfcsound.infra;

public class DataEntryIterator {

    private DataManager data_manager;
    private int current_idx = 0;

    public DataEntryIterator(DataManager dm) {
        data_manager = dm;

    }

    public int size() {
        return data_manager.size();
    }

    public DataEntry getNext() {
        if (data_manager.size()==0) {
            throw new IndexOutOfBoundsException();
        }
        DataEntry de = data_manager.get(current_idx);
        incrementIdx();
        return de;
    }

    private void incrementIdx() {
        current_idx = current_idx + 1;
        if (current_idx >= data_manager.size()) {
            current_idx = 0;
        }
    }

}
