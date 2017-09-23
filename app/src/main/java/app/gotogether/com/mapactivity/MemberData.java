package app.gotogether.com.mapactivity;

/**
 * Created by so yeon on 2017-09-18.
 */

public class MemberData {

    // 이렇게 값을 담는 클래스를 VO 또는 DTO 라고 부른다.
    // 원본 데이터를 담음
    // 여기까지가 레이아웃을 만들고 원본 데이터를 담는 곳을 만드는 과정 그 다음 어댑터 만들어야함

    private String _id;
    private String name;
    private String phone;
    //alt + insert : 게터 세터 생성, 생성자 생성


    public MemberData(String _id, String name, String phone) {
        this._id = _id;
        this.name = name;
        this.phone = phone;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}