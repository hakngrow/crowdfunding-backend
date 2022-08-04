package com.aestus.api.attachment.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.web.multipart.MultipartFile;

/**
 * The DTO marshalled from the HTTP request to the {@code upload} function of the {@code
 * AttachmentController}**.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UploadRequestBody {

    /** The files to be uploaded as attachments. */
    private MultipartFile[] files;

    /** The corresponding file descriptions. */
    private String[] descriptions;

    /** The type of document these files are attached to. */
    private String documentType;

    /** The id of document these files are attached to. */
    private int documentId;
}
