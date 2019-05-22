package com.hgys.iptv.service;

import com.hgys.iptv.controller.vm.SettlementDocumentQueryListVM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SettlementDocumentService {

    Page<SettlementDocumentQueryListVM> findByConditions(String name, String code, Pageable pageable);
}