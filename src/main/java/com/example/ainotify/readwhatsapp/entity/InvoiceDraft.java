package com.example.ainotify.readwhatsapp.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "invoice_draft")
public class InvoiceDraft {

    @Id
    @GeneratedValue
    private UUID id;

    private String phone;
    private String customerName;

    @Column(columnDefinition = "TEXT")
    private String itemsJson;

    private Double gstPercent;
    private Double totalAmount;
    private String status;
    private Boolean previewSent;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getItemsJson() { return itemsJson; }
    public void setItemsJson(String itemsJson) { this.itemsJson = itemsJson; }
    public Double getGstPercent() { return gstPercent; }
    public void setGstPercent(Double gstPercent) { this.gstPercent = gstPercent; }
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public void setPreviewSent(Boolean previewSent) { this.previewSent = previewSent; }

    public Boolean getPreviewSent() {
        return previewSent;
    }
}