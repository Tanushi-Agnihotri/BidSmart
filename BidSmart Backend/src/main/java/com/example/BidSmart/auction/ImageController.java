package com.example.BidSmart.auction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.example.BidSmart.user.User;

@RestController
@RequestMapping("/api")
public class ImageController {

    private final AuctionRepository auctionRepository;
    private final AuctionImageRepository imageRepository;
    private final ImageStorageService storageService;

    public ImageController(AuctionRepository auctionRepository,
                           AuctionImageRepository imageRepository,
                           ImageStorageService storageService) {
        this.auctionRepository = auctionRepository;
        this.imageRepository = imageRepository;
        this.storageService = storageService;
    }

    // Upload image for an auction
    @PostMapping("/auctions/{auctionId}/images")
    @Transactional
    public ResponseEntity<ImageResponse> uploadImage(
            @PathVariable UUID auctionId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));

        if (!auction.getSeller().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the seller can upload images");
        }

        int currentCount = imageRepository.countByAuctionId(auctionId);
        if (currentCount >= 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum 5 images per auction");
        }

        String storedName = storageService.store(file);

        AuctionImage image = new AuctionImage();
        image.setAuction(auction);
        image.setFileName(file.getOriginalFilename());
        image.setFilePath(storedName);
        image.setFileSize(file.getSize());
        image.setContentType(file.getContentType());
        image.setSortOrder(currentCount);

        imageRepository.save(image);

        return ResponseEntity.status(HttpStatus.CREATED).body(ImageResponse.from(image));
    }

    // Get all images for an auction
    @GetMapping("/auctions/{auctionId}/images")
    public ResponseEntity<List<ImageResponse>> getAuctionImages(@PathVariable UUID auctionId) {
        List<AuctionImage> images = imageRepository.findByAuctionIdOrderBySortOrder(auctionId);
        return ResponseEntity.ok(images.stream().map(ImageResponse::from).toList());
    }

    // Serve image file
    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {
        try {
            Path filePath = storageService.load(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                    .body(resource);

        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
        }
    }

    // Delete image
    @DeleteMapping("/auctions/{auctionId}/images/{imageId}")
    @Transactional
    public ResponseEntity<Void> deleteImage(
            @PathVariable UUID auctionId,
            @PathVariable UUID imageId,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Auction not found"));

        if (!auction.getSeller().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the seller can delete images");
        }

        AuctionImage image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found"));

        storageService.delete(image.getFilePath());
        imageRepository.delete(image);

        return ResponseEntity.noContent().build();
    }

    // Response DTO
    public record ImageResponse(UUID id, String url, String fileName, long fileSize, int sortOrder) {
        static ImageResponse from(AuctionImage img) {
            return new ImageResponse(
                img.getId(),
                "/api/images/" + img.getFilePath(),
                img.getFileName(),
                img.getFileSize(),
                img.getSortOrder()
            );
        }
    }
}
