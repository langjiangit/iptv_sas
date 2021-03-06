package com.hgys.iptv.repository;


import com.hgys.iptv.model.OrderQuantity;
import com.hgys.iptv.model.SettlementCombinatorialDimensionMaster;
import com.hgys.iptv.model.SettlementDimension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
    public interface OrderQuantityRepository extends JpaRepository<OrderQuantity,Object>, JpaSpecificationExecutor<OrderQuantity> {
    /**
     * 通过ID逻辑删除
     */
    @Modifying
    @Query(value = "update OrderQuantity set isdelete = 1 WHERE id = ?1")
    void batchDelete(int id);

    /**
     * 通过code查询
     * @param code
     * @return
     */
    Optional<OrderQuantity> findByCode(String code);



    /**
     * 通过code逻辑删除
     * @param oqcode
     * @return
     */
    @Modifying
    @Query(value = "update OrderQuantity set isdelete = 1 WHERE oqcode = ?1")
    void batchLogicDeleteByCode(String oqcode);

    Optional<OrderQuantity> findByName(String name);
    /**
     * 根据oqrCode删除
     * @param oqcode
     */
    @Modifying
    @Query(value = "delete from OrderQuantityWithCp where oqcode = ?1")
    void deleteByMasterCode(String oqcode);



    /**
     * 通过结算组合维度编码查询
     * @param Code
     * @return
     */
    @Query(value = "select o.name from OrderQuantity o where o.code = ?1")
    String findByMastername(String Code);



    /**
     * 通过结算组合维度编码查询
     * @param Code
     * @return
     */
    @Query(value = "select o.id from OrderQuantity o where o.code = ?1")
    Integer findByMastid(String Code);

    @Query(value = "select o from OrderQuantity o where o.code = ?1")
    List<OrderQuantity> finddetail(String code);
}



