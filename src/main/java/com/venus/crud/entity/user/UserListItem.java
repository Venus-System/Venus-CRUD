package com.venus.crud.entity.user;

import com.venus.crud.entity.product.Product;
import com.venus.crud.entity.shared.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@Entity
@Table(name = "user_list_items")
@AttributeOverride(name = "id", column = @Column(name = "user_list_item_id"))
public class UserListItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_user_list_id", nullable = false)
    private UserList userList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_id", nullable = false)
    private Product product;

    @Column(name = "position_order", nullable = false)
    private Integer positionOrder;
}
