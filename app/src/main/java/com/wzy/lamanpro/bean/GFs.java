package com.wzy.lamanpro.bean;

import com.bin.david.form.annotation.SmartColumn;
import com.bin.david.form.annotation.SmartTable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
@SmartTable(name="备胎列表")
public class GFs {


    /**
     * id : 1
     * name : 汤倩
     * birth : 1991-08-23
     * height : 160
     * weight : 51
     * yanzhi : 80
     * dirct : 陕西汉中
     * st : 三本
     * work : 会计
     * ins : 5000
     * rec : 2017-12-22
     * loveme : 86
     * ilove : 82
     * relate : 84
     * others : 2
     * futureinbj : 75
     * marks : 独生子女，公务员家庭
     * sort : 4
     * point : 80
     * point_now : 0
     */

    private Long id;
    @SmartColumn(id =1,name = "姓名")
    private String name;
    @SmartColumn(id =2,name = "生日")
    private String birth;
    @SmartColumn(id =3,name = "身高")
    private int height;
    @SmartColumn(id =4,name = "体重")
    private int weight;
    @SmartColumn(id =5,name = "颜值")
    private int yanzhi;
    @SmartColumn(id =6,name = "地区")
    private String dirct;
    @SmartColumn(id =7,name = "学历")
    private String st;
    @SmartColumn(id =8,name = "工作")
    private String work;
    @SmartColumn(id =9,name = "收入")
    private int ins;
    @SmartColumn(id =10,name = "相识")
    private String rec;
    @SmartColumn(id =11,name = "被爱")
    private int loveme;
    @SmartColumn(id =12,name = "我爱")
    private int ilove;
    @SmartColumn(id =13,name = "关系")
    private int relate;
    @SmartColumn(id =14,name = "其他")
    private int others;
    @SmartColumn(id =15,name = "留京")
    private int futureinbj;
    @SmartColumn(id =16,name = "备注")
    private String marks;
    @SmartColumn(id =17,name = "排序")
    private int sort;
    @SmartColumn(id =18,name = "分值")
    private int point;
    @SmartColumn(id =19,name = "得分")
    private int point_now;
    @Generated(hash = 1362220563)
    public GFs(Long id, String name, String birth, int height, int weight,
            int yanzhi, String dirct, String st, String work, int ins, String rec,
            int loveme, int ilove, int relate, int others, int futureinbj,
            String marks, int sort, int point, int point_now) {
        this.id = id;
        this.name = name;
        this.birth = birth;
        this.height = height;
        this.weight = weight;
        this.yanzhi = yanzhi;
        this.dirct = dirct;
        this.st = st;
        this.work = work;
        this.ins = ins;
        this.rec = rec;
        this.loveme = loveme;
        this.ilove = ilove;
        this.relate = relate;
        this.others = others;
        this.futureinbj = futureinbj;
        this.marks = marks;
        this.sort = sort;
        this.point = point;
        this.point_now = point_now;
    }
    @Generated(hash = 1339457483)
    public GFs() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBirth() {
        return this.birth;
    }
    public void setBirth(String birth) {
        this.birth = birth;
    }
    public int getHeight() {
        return this.height;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public int getWeight() {
        return this.weight;
    }
    public void setWeight(int weight) {
        this.weight = weight;
    }
    public int getYanzhi() {
        return this.yanzhi;
    }
    public void setYanzhi(int yanzhi) {
        this.yanzhi = yanzhi;
    }
    public String getDirct() {
        return this.dirct;
    }
    public void setDirct(String dirct) {
        this.dirct = dirct;
    }
    public String getSt() {
        return this.st;
    }
    public void setSt(String st) {
        this.st = st;
    }
    public String getWork() {
        return this.work;
    }
    public void setWork(String work) {
        this.work = work;
    }
    public int getIns() {
        return this.ins;
    }
    public void setIns(int ins) {
        this.ins = ins;
    }
    public String getRec() {
        return this.rec;
    }
    public void setRec(String rec) {
        this.rec = rec;
    }
    public int getLoveme() {
        return this.loveme;
    }
    public void setLoveme(int loveme) {
        this.loveme = loveme;
    }
    public int getIlove() {
        return this.ilove;
    }
    public void setIlove(int ilove) {
        this.ilove = ilove;
    }
    public int getRelate() {
        return this.relate;
    }
    public void setRelate(int relate) {
        this.relate = relate;
    }
    public int getOthers() {
        return this.others;
    }
    public void setOthers(int others) {
        this.others = others;
    }
    public int getFutureinbj() {
        return this.futureinbj;
    }
    public void setFutureinbj(int futureinbj) {
        this.futureinbj = futureinbj;
    }
    public String getMarks() {
        return this.marks;
    }
    public void setMarks(String marks) {
        this.marks = marks;
    }
    public int getSort() {
        return this.sort;
    }
    public void setSort(int sort) {
        this.sort = sort;
    }
    public int getPoint() {
        return this.point;
    }
    public void setPoint(int point) {
        this.point = point;
    }
    public int getPoint_now() {
        return this.point_now;
    }
    public void setPoint_now(int point_now) {
        this.point_now = point_now;
    }

}
