package com.seungro.client.components;

public class FolderPopup extends SidebarPopup {
    public FolderPopup() {
        addItem("새 파일");
        addItem("새 폴더");
        addSeparator();
        addItem("이름 수정");
        addItem("삭제");
    }
}
