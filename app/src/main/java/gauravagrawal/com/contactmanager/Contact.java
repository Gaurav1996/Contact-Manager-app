package gauravagrawal.com.contactmanager;

import android.net.Uri;

/**
 * Created by PC on 21-Jan-16.
 */

public class Contact {
    private String _name,_phone,_email,_address;
    private Uri _imageuri;
    private int _id;
    public Contact(int id,String name,String phone,String email,String address,Uri imageuri){
        _id=id;
        _name=name;
        _phone=phone;
        _email=email;
        _address=address;
        _imageuri=imageuri;
    }
    public int getId(){
        return _id;
    }
    public String getname(){
        return _name;
    }
    public String getphone(){
        return _phone;
    }
    public String getemail(){
        return _email;
    }
    public String getaddress(){
        return _address;
    }
    public Uri getimageuri(){
        return _imageuri;
    }
}
