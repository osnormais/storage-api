package org.osnormais.storage.api.infrastructure.api;

import java.io.IOException;

import org.osnormais.storage.api.domain.file.valueobject.Checksum;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RequestMapping("files")
public interface FileAPI {

    @Operation(summary = "Upload file chunk", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("chunks")
    ResponseEntity<Void> uploadChunk(
            @RequestHeader("X-Chunk-Token") String chunkToken,
            @RequestHeader("X-Checksum-Value") String checksumValue,
            @RequestHeader("X-Checksum-Algorithm") Checksum.Algorithm checksumAlgorithm,
            HttpServletRequest request) throws IOException;

    @Operation(summary = "Download file chunk", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("chunks")
    ResponseEntity<StreamingResponseBody> downloadChunk(@RequestHeader("X-Chunk-Token") String chunkToken);

}