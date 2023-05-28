package com.example.safaripassclone.services.images;

import ImageUtils;//One that has compress and decompress image functions
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final DestinationImageRepository destinationImageRepository;
    private final ActivityImageRepository activityImageRepository;
    public String uploadDestinationImage(MultipartFile file)throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof AnonymousAuthenticationToken)){
            Users details = (Users) authentication.getPrincipal();
            DestinationImages image = destinationImageRepository.save(
                    DestinationImages.builder()
                            .name(file.getOriginalFilename())
                            .addedBy(details.getId())
                            .addedOn(new Date(System.currentTimeMillis()))
                            .imageBytes(ImageUtils.compressImage(file.getBytes()))
                            .build()
            );
        }
        return "success";
    }
    public String uploadActivityImage(MultipartFile file) throws IOException{
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(!(auth instanceof AnonymousAuthenticationToken)){
            Users details = (Users) auth.getPrincipal();
            ActivityImages image = activityImageRepository.save(
                    ActivityImages.builder()
                            .name(file.getOriginalFilename())
                            .addedBy(details.getId())
                            .addedOn(new Date(System.currentTimeMillis()))
                            .imageBytes(ImageUtils.compressImage(file.getBytes()))
                            .build()
            );
        }
        return "success";
    }
    public byte[] downloadDestinationImage(String name){
        Optional<DestinationImages> dbImage = destinationImageRepository.findByNameContainingIgnoreCase(name);
        if (dbImage.isEmpty()){
            throw new NotFoundException("No file found with name "+name);
        }
        return ImageUtils.decompressImage(dbImage.get().getImageBytes());
    }
    public byte[] downloadActivityImage(String name){
        Optional<ActivityImages> theImage = activityImageRepository.findByNameContainingIgnoreCase(name);
        if (theImage.isEmpty()){
            throw new NotFoundException("No file found with name "+name);
        }
        return ImageUtils.decompressImage(theImage.get().getImageBytes());
    }
}
