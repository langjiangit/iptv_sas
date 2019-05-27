package com.hgys.iptv.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.params.ExcelExportEntity;
import com.hgys.iptv.controller.assemlber.SettlementDocumentControllerAssemlber;
import com.hgys.iptv.controller.vm.SettlementDocumentCPListExcelVM;
import com.hgys.iptv.controller.vm.SettlementDocumentCPListVM;
import com.hgys.iptv.controller.vm.SettlementDocumentQueryListVM;
import com.hgys.iptv.model.*;
import com.hgys.iptv.model.QAccountSettlement;
import com.hgys.iptv.model.QCpSettlementMoney;
import com.hgys.iptv.model.vo.CpSettlementInfoExcelDTO;
import com.hgys.iptv.model.vo.ResultVO;
import com.hgys.iptv.repository.AccountSettlementRepository;
import com.hgys.iptv.repository.CpSettlementMoneyRepository;
import com.hgys.iptv.repository.SettlementDocumentRepository;
import com.hgys.iptv.service.SettlementDocumentService;
import com.hgys.iptv.util.ResultVOUtil;
import com.hgys.iptv.util.excel.ExcelForWebUtil;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SettlementDocumentServiceImpl implements SettlementDocumentService {

    @Autowired
    private SettlementDocumentRepository settlementDocumentRepository;

    @Autowired
    private SettlementDocumentControllerAssemlber settlementDocumentControllerAssemlber;

    @Autowired
    private CpSettlementMoneyRepository cpSettlementMoneyRepository;

    @Autowired
    private AccountSettlementRepository accountSettlementRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    /**
     * 查询结算文档列表信息
     * @param name
     * @param code
     * @param pageable
     * @return
     */
    @Override
    public Page<SettlementDocumentQueryListVM> findByConditions(String name, String code, Pageable pageable) {
       return settlementDocumentRepository.findAll(((root, query, builder) -> {
           List<Predicate> predicates = new ArrayList<>();
           if (StringUtils.isNotBlank(name)){
               Predicate condition = builder.like(root.get("name"), "%"+name+"%");
               predicates.add(condition);
           }
           if (StringUtils.isNotBlank(code)){
               Predicate condition = builder.like(root.get("code"), "%"+code+"%");
               predicates.add(condition);
           }

           /** 1:已录入;2:待审核;3:初审通过;4:复审通过;5:终审通过;6:驳回*/
           Predicate condition = builder.notEqual(root.get("status"), 1);
           predicates.add(condition);

           if (!predicates.isEmpty()){
               return builder.and(predicates.toArray(new Predicate[0]));
           }
           return builder.conjunction();
        }),pageable).map(settlementDocumentControllerAssemlber :: getListVM);
    }

    @Override
    public Page<SettlementDocumentCPListExcelVM> documentQueryHistoryCpMySelfList(Integer masterId,String cpCode,String pageNum, String pageSize) {
        QAccountSettlement qAccountSettlement = QAccountSettlement.accountSettlement;
        QCpSettlementMoney qCpSettlementMoney = QCpSettlementMoney.cpSettlementMoney;
        //查询分账结算信息
        AccountSettlement accountSettlement = jpaQueryFactory.selectFrom(qAccountSettlement).where(qAccountSettlement.id.eq(masterId)).fetchOne();
        QueryResults<SettlementDocumentCPListExcelVM> fetch = jpaQueryFactory.select(Projections.bean(
                SettlementDocumentCPListExcelVM.class,
                qAccountSettlement.id.as("masterId"),
                qAccountSettlement.setStartTime,
                qAccountSettlement.setEndTime,
                qAccountSettlement.status,
                qCpSettlementMoney.id,
                qCpSettlementMoney.masterCode,
                qCpSettlementMoney.masterName,
                qCpSettlementMoney.cpcode,
                qCpSettlementMoney.cpname,
                qCpSettlementMoney.productCode,
                qCpSettlementMoney.productName,
                qCpSettlementMoney.businessCode,
                qCpSettlementMoney.businessName,
                qCpSettlementMoney.settlementMoney,
                qCpSettlementMoney.createTime
        )).from(qCpSettlementMoney)
                .innerJoin(qAccountSettlement).on(qCpSettlementMoney.masterCode.eq(qAccountSettlement.code))
                .where(qCpSettlementMoney.cpcode.eq(cpCode))
                .where(qAccountSettlement.setStartTime.lt(accountSettlement.getSetStartTime()))
                .orderBy(qAccountSettlement.setStartTime.desc())
                .offset(Integer.parseInt(pageNum) - 1).limit(Integer.parseInt(pageSize)).fetchResults();

        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) - 1 ,Integer.parseInt(pageSize));
        Page<SettlementDocumentCPListExcelVM> pageImpianto = new PageImpl<>(fetch.getResults(), pageable, fetch.getTotal());
        return pageImpianto;
    }

    @Override
    public Page<SettlementDocumentQueryListVM> documentHistoryQueryCpList(Integer masterId, String pageNum, String pageSize) {
        Optional<AccountSettlement> byId = accountSettlementRepository.findById(masterId);
        AccountSettlement accountSettlement = byId.get();
        //查询当前账期以前账期的数据
        QAccountSettlement qAccountSettlement = QAccountSettlement.accountSettlement;

        QueryResults<AccountSettlement> fetch = jpaQueryFactory.selectFrom(qAccountSettlement)
                .where(qAccountSettlement.setStartTime.lt(accountSettlement.getSetStartTime()))
                .orderBy(qAccountSettlement.setStartTime.desc())
                .offset(Integer.parseInt(pageNum) - 1).limit(Integer.parseInt(pageSize)).fetchResults();

        List<AccountSettlement> list =fetch.getResults();
        List<SettlementDocumentQueryListVM> vms = new ArrayList<>();
        for (AccountSettlement a : list){
            SettlementDocumentQueryListVM s = new SettlementDocumentQueryListVM();
            BeanUtils.copyProperties(accountSettlement,s);
            //查询该分账结算下所有的cp信息
            List<CpSettlementMoney> byMasterCode = cpSettlementMoneyRepository.findByMasterCode(a.getCode());
            List<SettlementDocumentCPListVM> listVMS = new ArrayList<>();
            for (CpSettlementMoney cp : byMasterCode){
                SettlementDocumentCPListVM vm = new SettlementDocumentCPListVM();
                BeanUtils.copyProperties(cp,vm);
                listVMS.add(vm);
            }
            s.setCpList(listVMS);
            vms.add(s);
        }
        Pageable pageable = PageRequest.of(Integer.parseInt(pageNum) - 1 ,Integer.parseInt(pageSize));
        Page<SettlementDocumentQueryListVM> pageImpianto = new PageImpl<>(vms, pageable, fetch.getTotal());
        return pageImpianto;
    }


    @Override
    public ResultVO<?> findByIdQueryCpList(Integer id) {
        //查询分账结算信息
        Optional<AccountSettlement> byId = accountSettlementRepository.findById(id);
        AccountSettlement accountSettlement = byId.get();
        SettlementDocumentQueryListVM s = new SettlementDocumentQueryListVM();
        BeanUtils.copyProperties(accountSettlement,s);
        //查询该分账结算下所有的cp信息
        List<CpSettlementMoney> byMasterCode = cpSettlementMoneyRepository.findByMasterCode(accountSettlement.getCode());
        List<SettlementDocumentCPListVM> vms = new ArrayList<>();
        for (CpSettlementMoney cp : byMasterCode){
            SettlementDocumentCPListVM vm = new SettlementDocumentCPListVM();
            BeanUtils.copyProperties(cp,vm);
            vms.add(vm);
        }
        s.setCpList(vms);

        return ResultVOUtil.success(s);
    }

    @Override
    public ResultVO<?> settlementDocumentQueryCpMySelfList(Integer id) {
        QAccountSettlement qAccountSettlement = QAccountSettlement.accountSettlement;
        QCpSettlementMoney qCpSettlementMoney = QCpSettlementMoney.cpSettlementMoney;
        SettlementDocumentCPListExcelVM vm = jpaQueryFactory.select(Projections.bean(
                SettlementDocumentCPListExcelVM.class,
                qAccountSettlement.id.as("masterId"),
                qAccountSettlement.setStartTime,
                qAccountSettlement.setEndTime,
                qAccountSettlement.status,
                qCpSettlementMoney.id,
                qCpSettlementMoney.masterCode,
                qCpSettlementMoney.masterName,
                qCpSettlementMoney.cpcode,
                qCpSettlementMoney.cpname,
                qCpSettlementMoney.productCode,
                qCpSettlementMoney.productName,
                qCpSettlementMoney.businessCode,
                qCpSettlementMoney.businessName,
                qCpSettlementMoney.settlementMoney,
                qCpSettlementMoney.createTime
        )).from(qCpSettlementMoney)
                .innerJoin(qAccountSettlement).on(qCpSettlementMoney.masterCode.eq(qAccountSettlement.code))
                .where(qCpSettlementMoney.id.eq(id)).fetchOne();
        return ResultVOUtil.success(vm);
    }

    @Override
    public SettlementDocumentCPListExcelVM settlementCpExcel(Integer id) {
        QAccountSettlement qAccountSettlement = QAccountSettlement.accountSettlement;
        QCpSettlementMoney qCpSettlementMoney = QCpSettlementMoney.cpSettlementMoney;

        SettlementDocumentCPListExcelVM vm = jpaQueryFactory.select(Projections.bean(
                SettlementDocumentCPListExcelVM.class,
                qAccountSettlement.id.as("masterId"),
                qAccountSettlement.setStartTime,
                qAccountSettlement.setEndTime,
                qAccountSettlement.status,
                qAccountSettlement.set_type.as("type"),
                qCpSettlementMoney.id,
                qCpSettlementMoney.masterCode,
                qCpSettlementMoney.masterName,
                qCpSettlementMoney.cpcode,
                qCpSettlementMoney.cpname,
                qCpSettlementMoney.productCode,
                qCpSettlementMoney.productName,
                qCpSettlementMoney.businessCode,
                qCpSettlementMoney.businessName,
                qCpSettlementMoney.settlementMoney,
                qCpSettlementMoney.createTime
        )).from(qCpSettlementMoney)
                .innerJoin(qAccountSettlement).on(qCpSettlementMoney.masterCode.eq(qAccountSettlement.code))
                .where(qCpSettlementMoney.id.eq(id)).fetchOne();
        return vm;
    }


    @Override
    public void excelSettlementInfo(Integer masterId, HttpServletResponse response) {
        //查询分账结算信息
        Optional<AccountSettlement> byId = accountSettlementRepository.findById(masterId);
        if (!byId.isPresent()){
            throw new IllegalArgumentException("未查询到该结算账单信息");
        }
        AccountSettlement accountSettlement = byId.get();
        /** 1:订购量结算;2:业务级结算;3:产品级结算;4:CP定比例结算;5:业务定 */
        if (3 == accountSettlement.getSet_type()){
            try{
                List<ExcelExportEntity> colList = new ArrayList<>();
                ExcelExportEntity colEntity = new ExcelExportEntity("账期", "time");
                colEntity.setNeedMerge(true);
                colList.add(colEntity);

                colEntity = new ExcelExportEntity("CP", "cp");
                colEntity.setNeedMerge(true);
                colList.add(colEntity);

                ExcelExportEntity deliColGroup = new ExcelExportEntity("增值业务包", "zz");
                List<ExcelExportEntity> deliColList = new ArrayList<>();
                //查询该分账结算下所有的产品，然后动态设置表头
                QCpSettlementMoney qCpSettlementMoney = QCpSettlementMoney.cpSettlementMoney;
                List<CpSettlementMoney> fetch = jpaQueryFactory.select(qCpSettlementMoney).from(qCpSettlementMoney)
                        .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                        .groupBy(qCpSettlementMoney.productCode).fetch();
                for (CpSettlementMoney c : fetch){
                    deliColList.add(new ExcelExportEntity(c.getProductName(), c.getProductCode()));
                    deliColGroup.setList(deliColList);
                }
                colList.add(deliColGroup);

                List<Map<String, Object>> list = new ArrayList<>();
                List<CpSettlementMoney> vms = jpaQueryFactory.selectFrom(qCpSettlementMoney)
                        .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                        .groupBy(qCpSettlementMoney.cpcode).fetch();
                //CP编码加产品编码作为键
                Map<String, CpSettlementMoney> collect = vms.stream().collect(Collectors.toMap(s -> s.getCpcode() + s.getProductCode(), settlementDocumentCPListExcelVM -> settlementDocumentCPListExcelVM));

                for(CpSettlementMoney excelVM : vms){
                    Map<String, Object> valMap = new HashMap<>();
                    //结算账期
                    String startTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetStartTime());
                    String endTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetEndTime());
                    valMap.put("time",startTime + "-" + endTime);
                    valMap.put("cp", excelVM.getCpname());

                    //通过masterCode和cp编码查询出当前Cp在该账单所有的产品
                    List<CpSettlementMoney> moneys = jpaQueryFactory.selectFrom(qCpSettlementMoney)
                            .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                            .where(qCpSettlementMoney.cpcode.eq(excelVM.getCpcode())).fetch();

                    List<Map<String, Object>> deliDetailList = new ArrayList<>();
                    Map<String, Object> deliValMap = new HashMap<>();
                    for(CpSettlementMoney cp : moneys){
                        deliValMap.put(cp.getProductCode(),cp.getSettlementMoney());
                    }
                    deliDetailList.add(deliValMap);
                    valMap.put("zz", deliDetailList);

                    list.add(valMap);
                }
                //结算合计金额
                BigDecimal decimal = cpSettlementMoneyRepository.jsAllmoney(accountSettlement.getCode());
                if (null == decimal){
                    decimal = new BigDecimal(0);
                }
                ExportParams exportParams = new ExportParams("甘肃广电IPTV业务结算报表", "产品级结算");
                exportParams.setSecondTitle("合计金额:" + decimal.setScale(2).toString());
                Workbook workbook = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(exportParams, colList,
                        list);
                ExcelForWebUtil.workBookExportExcel(response,workbook,"产品级账单结算报表");

            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (1 == accountSettlement.getSet_type() || 4 == accountSettlement.getSet_type()){
            String startTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetStartTime());
            String endTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetEndTime());
            String timeHead = "结算账期" + startTime + "至" + endTime;
            //查询数据
            List<CpSettlementMoney> byMasterCode = cpSettlementMoneyRepository.findByMasterCode(accountSettlement.getCode());
            List<CpSettlementInfoExcelDTO> dtos = new ArrayList<>();

            //分成合计金额
            BigDecimal decimal = new BigDecimal(0);
            for (CpSettlementMoney m : byMasterCode){
                CpSettlementInfoExcelDTO dto = new CpSettlementInfoExcelDTO();
                dto.setCp(m.getCpname());
                dto.setMoney(m.getSettlementMoney().toString());
                dtos.add(dto);
                //计算分成合计金额
                decimal = decimal.add(m.getSettlementMoney());
            }

            CpSettlementInfoExcelDTO l = new CpSettlementInfoExcelDTO();
            l.setCp("分成合计(元)");
            l.setMoney(decimal.setScale(2).toString());
            //最后一列插入分成合计金额
            dtos.add(l);

            Workbook sheets = ExcelExportUtil.exportExcel(new ExportParams(timeHead,"订购量结算&CP定比例结算"), CpSettlementInfoExcelDTO.class, dtos);
            ExcelForWebUtil.workBookExportExcel(response,sheets,"Cp结算账单结算信息表");
        }else if (2 == accountSettlement.getSet_type() || 5 == accountSettlement.getSet_type()){
            try{
                List<ExcelExportEntity> colList = new ArrayList<>();
                ExcelExportEntity colEntity = new ExcelExportEntity("账期", "time");
                colEntity.setNeedMerge(true);
                colList.add(colEntity);

                colEntity = new ExcelExportEntity("CP", "cp");
                colEntity.setNeedMerge(true);
                colList.add(colEntity);

                ExcelExportEntity deliColGroup = new ExcelExportEntity("业务", "yw");
                List<ExcelExportEntity> deliColList = new ArrayList<>();
                //查询该分账结算下所有的业务，然后动态设置表头
                QCpSettlementMoney qCpSettlementMoney = QCpSettlementMoney.cpSettlementMoney;
                List<CpSettlementMoney> fetch = jpaQueryFactory.select(qCpSettlementMoney).from(qCpSettlementMoney)
                        .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                        .groupBy(qCpSettlementMoney.businessCode).fetch();
                for (CpSettlementMoney c : fetch){
                    deliColList.add(new ExcelExportEntity(c.getBusinessName(), c.getBusinessCode()));
                    deliColGroup.setList(deliColList);
                }
                colList.add(deliColGroup);

                List<Map<String, Object>> list = new ArrayList<>();
                List<CpSettlementMoney> vms = jpaQueryFactory.selectFrom(qCpSettlementMoney)
                        .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                        .groupBy(qCpSettlementMoney.cpcode).fetch();
                //CP编码加产品编码作为键
                Map<String, CpSettlementMoney> collect = vms.stream().collect(Collectors.toMap(s -> s.getCpcode() + s.getProductCode(), settlementDocumentCPListExcelVM -> settlementDocumentCPListExcelVM));

                for(CpSettlementMoney excelVM : vms){
                    Map<String, Object> valMap = new HashMap<>();
                    //结算账期
                    String startTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetStartTime());
                    String endTime = new SimpleDateFormat("yyyy-MM-dd").format(accountSettlement.getSetEndTime());
                    valMap.put("time",startTime + "-" + endTime);
                    valMap.put("cp", excelVM.getCpname());

                    //通过masterCode和cp编码查询出当前Cp在该账单所有的业务
                    List<CpSettlementMoney> moneys = jpaQueryFactory.selectFrom(qCpSettlementMoney)
                            .where(qCpSettlementMoney.masterCode.eq(accountSettlement.getCode()))
                            .where(qCpSettlementMoney.cpcode.eq(excelVM.getCpcode())).fetch();

                    List<Map<String, Object>> deliDetailList = new ArrayList<>();
                    Map<String, Object> deliValMap = new HashMap<>();
                    for(CpSettlementMoney cp : moneys){
                        deliValMap.put(cp.getBusinessCode(),cp.getSettlementMoney());
                    }
                    deliDetailList.add(deliValMap);
                    valMap.put("yw", deliDetailList);

                    list.add(valMap);
                }
                //结算合计金额
                BigDecimal decimal = cpSettlementMoneyRepository.jsAllmoney(accountSettlement.getCode());
                if (null == decimal){
                    decimal = new BigDecimal(0);
                }
                ExportParams exportParams = new ExportParams("甘肃广电IPTV业务结算报表（移动侧）", "业务级结算&业务定比列结算");
                exportParams.setSecondTitle("合计金额:" + decimal.setScale(2).toString());
                Workbook workbook = cn.afterturn.easypoi.excel.ExcelExportUtil.exportExcel(exportParams, colList,
                        list);
                ExcelForWebUtil.workBookExportExcel(response,workbook,"业务级级账单结算报表");

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
}
