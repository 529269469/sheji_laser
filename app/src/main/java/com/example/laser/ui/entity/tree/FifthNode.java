package com.example.laser.ui.entity.tree;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FifthNode extends BaseExpandNode {
    private String title;
    private String person;

    public FifthNode(String title, String person) {
        this.title = title;
        this.person = person;
    }

    public String getTitle() {
        return title;
    }

    public String getPerson() {
        return person;
    }
    @Nullable
    @Override
    public List<BaseNode> getChildNode() {
        return null;
    }


}
