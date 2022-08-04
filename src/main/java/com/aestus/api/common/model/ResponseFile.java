package com.aestus.api.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Entity;

/** The type Response file. */
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
public class ResponseFile {

    private String name;
    private String url;
    private String type;
    private long size;
}
