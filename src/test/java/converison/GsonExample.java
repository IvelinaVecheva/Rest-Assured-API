package converison;

import com.google.gson.Gson;
// 2: convert pojo to json using Gson
// mobile phone POJO
class MobilePhone {
    private String Brand;
    private String Name;
    private int Ram;
    private int Rom;

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getRam() {
        return Ram;
    }

    public void setRam(int ram) {
        Ram = ram;
    }

    public int getRom() {
        return Rom;
    }

    public void setRom(int rom) {
        Rom = rom;
    }
}

class ConvertJSON {
    public static MobilePhone getMobile(MobilePhone mobile) {
        mobile.setBrand("SAMSUNG");
        mobile.setName("J2 Core");
        mobile.setRam(2);
        mobile.setRom(4);
        return mobile;
    }

    public static void main(String[] args) {
        MobilePhone mobilePhone = new MobilePhone();
        getMobile(mobilePhone);
        System.out.println("The JSON representation of Object mobilePhone is ");
        // conversion of pojo object with Gson to Json
        System.out.println(new Gson().toJson(mobilePhone));
    }
}