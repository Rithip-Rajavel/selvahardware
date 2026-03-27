package com.hardware.selvahardware.repository;

import com.hardware.selvahardware.entity.ShopSettings;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, UUID> {
}
