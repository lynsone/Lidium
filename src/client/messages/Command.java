package client.messages;

import client.MapleClient;

public abstract class Command 
{
    
    protected int rank;
    protected String description;

    public abstract void execute(MapleClient client, String[] params);

    public String getDescription() {
        return description;
    }

    protected void setDescription(String description) {
        this.description = description;
    }
    
    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    protected String joinStringFrom(String arr[], int start) {
        StringBuilder builder = new StringBuilder();
        for (int i = start; i < arr.length; i++) {
            builder.append(arr[i]);
            if (i != arr.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }
}