package bldg5.jj.pgnhelper.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Games implements Serializable {
    @SerializedName("data")
    public List<Game> listGames;
}
