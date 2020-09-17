package com.center.anwani;

public class Address {

    private String county;
    private String region;
    private String road;
    private String number;
    private String latitude;
    private String longitude;
    private String fulladdress;
    private String addressname;
    private String verificationstatus;
    private String activestatus;
    private String addresstype;
    private String category;

    public Address() {}

    public Address(String county, String region, String road, String number, String latitude, String longitude, String fulladdress,
                   String addressname, String verificationstatus, String activestatus, String addresstype, String category) {
        this.county = county;
        this.region = region;
        this.road = road;
        this.number = number;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fulladdress=fulladdress;
        this.addressname=addressname;
        this.category=category;
        this.addresstype=addresstype;
        this.verificationstatus=verificationstatus;
        this.activestatus=activestatus;
    }

    public String getCounty(){
        return county;
    }
    public String getRegion(){
        return region;
    }
    public String getRoad(){
        return road;
    }
    public String getNumber(){
        return number;
    }
    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }
    public String getFulladdress(){
        return fulladdress;
    }
    public String getAddressname(){
        return addressname;
    }
    public String getAddresstype(){
        return addresstype;
    }
    public String getVerificationstatus(){
        return verificationstatus;
    }
    public String getActivestatus(){
        return activestatus;
    }
    public String getCategory(){
        return category;
    }

}

