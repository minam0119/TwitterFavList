package com.lifeistech.android.twittertest.model;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by MINAMI on 2015/06/13.
 */
@Table(name = "Categories")
public class Category extends Model {
    @Column(name = "Name")
    public String name;
    @Column(name = "Color")
    public int color;
    @Column(name = "Ids")
    public List<Long> ids;
}
