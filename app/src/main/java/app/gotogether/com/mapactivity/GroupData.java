package app.gotogether.com.mapactivity;

public class GroupData {

    private String _id;
    private String groupName;

    public GroupData(String _id, String groupName) {
        this._id = _id;
        this.groupName = groupName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


}