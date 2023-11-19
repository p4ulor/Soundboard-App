package xfam0usx.soundboard;

public class MyButton {
    int numOfSets;
    int id;
    public MyButton(int id, int numOfSets){
        this.numOfSets=numOfSets;
        this.id=id;
    }

    public int getId() {
        return id;
    }

    public int getNumOfSets() {
        return numOfSets;
    }
}
