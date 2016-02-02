package com.meizi.wrh.mymeizi.constans;

/**
 * Created by wrh on 16/2/2.
 */
public enum BaseEnum {
    app("App"), all("all"), iOS("iOS"), Android("Android"), fuli("福利"), video("休息视频"), expand("拓展资源"), qianduan("前端");

    String value;

    private BaseEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}
