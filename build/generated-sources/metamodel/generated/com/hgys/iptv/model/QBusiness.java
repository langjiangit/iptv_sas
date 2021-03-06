package com.hgys.iptv.model;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.Generated;
import com.querydsl.core.types.Path;


/**
 * QBusiness is a Querydsl query type for Business
 */
@Generated("com.querydsl.codegen.EntitySerializer")
public class QBusiness extends EntityPathBase<Business> {

    private static final long serialVersionUID = 1808048738L;

    public static final QBusiness business = new QBusiness("business");

    public final NumberPath<Integer> bizType = createNumber("bizType", Integer.class);

    public final StringPath code = createString("code");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.sql.Timestamp> inputTime = createDateTime("inputTime", java.sql.Timestamp.class);

    public final NumberPath<Integer> isdelete = createNumber("isdelete", Integer.class);

    public final DateTimePath<java.sql.Timestamp> modifyTime = createDateTime("modifyTime", java.sql.Timestamp.class);

    public final StringPath name = createString("name");

    public final NumberPath<Integer> settleType = createNumber("settleType", Integer.class);

    public final NumberPath<Integer> status = createNumber("status", Integer.class);

    public QBusiness(String variable) {
        super(Business.class, forVariable(variable));
    }

    public QBusiness(Path<? extends Business> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBusiness(PathMetadata metadata) {
        super(Business.class, metadata);
    }

}

