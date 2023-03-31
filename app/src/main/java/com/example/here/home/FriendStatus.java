package com.example.here.home;

import android.widget.ImageView;

import com.example.here.R;
import com.example.here.constants.ActivityType;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FriendStatus {
    private String image;

    private String nickname;
    private String name;
    private String info;

    private float time;

    public FriendStatus(String imageSource, String name, ActivityType type, int timeMinutes, String nickname){
        this.image = imageSource;
        this.name = name;
        this.time = timeMinutes;
        this.info = generateInfo(type);
        this.nickname = nickname;
    }

    private String generateInfo(ActivityType type){
        boolean isMale = true;        //0 - female  1 - male
        boolean areHours = false;
        if(this.time>60){
            this.time/=60.0;
            Math.round(this.time*10);
            this.time/=10.0;
            areHours = true;
        }
        File names = new File("com/example/here/data/names_PL.txt");
        try {
            Scanner scan = new Scanner(names);
            while(scan.hasNext()){
                String[] temp = scan.nextLine().split(" ");
                if(temp[0].equals(this.name)){
                    if(temp[1].equals("K")){
                        isMale = false;
                    }else{
                        isMale = true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            return "no data";
        }

        String ret;

        if(isMale && areHours){
            ret = this.name + " ukończył " + String.valueOf(this.time) + " godzinn";
        }else if(!isMale && areHours){
            ret = this.name + " ukończyła " + String.valueOf(this.time) + " godzinn";
        }else if(isMale && !areHours){
            ret = this.name + " ukończył " + String.valueOf((int)this.time) + " minutow";
        }else{
            ret = this.name + " ukończyła " + String.valueOf((int)this.time) + " minutow";
        }

        switch (type){
            case WALK:
                return ret + "y marsz";
            case KAYAK:
                return ret + "y spław";
            case SPRINT:
                return ret + "y bieg";
            case CYCLING:
                return ret + "ą trasę rowerową";
            default:
                return "no data";
        }
    }

    public String getInfo() {
        return info;
    }

    public String getImageSource(){
        return this.image;
    }

    public String getNickname(){
        return this.nickname;
    }
}
