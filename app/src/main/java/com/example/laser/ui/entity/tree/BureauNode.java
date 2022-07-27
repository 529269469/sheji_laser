package com.example.laser.ui.entity.tree;

import com.chad.library.adapter.base.entity.node.BaseExpandNode;
import com.chad.library.adapter.base.entity.node.BaseNode;

import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Created by  on 2021/7/10 14:44.
 */
public class BureauNode extends BaseExpandNode {
    private String title;
    private String person;

    public BureauNode(String title, String person) {
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
