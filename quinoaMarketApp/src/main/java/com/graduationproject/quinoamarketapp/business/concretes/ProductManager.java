package com.graduationproject.quinoamarketapp.business.concretes;

import com.graduationproject.quinoamarketapp.business.abstracts.ProductService;
import com.graduationproject.quinoamarketapp.entity.Farmer;
import com.graduationproject.quinoamarketapp.entity.Product;
import com.graduationproject.quinoamarketapp.model.FarmerResponseDTO;
import com.graduationproject.quinoamarketapp.model.ProductRequestDTO;
import com.graduationproject.quinoamarketapp.model.ProductResponseDTO;
import com.graduationproject.quinoamarketapp.repository.ProductRepository;
import com.graduationproject.quinoamarketapp.util.ImageUtils;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class ProductManager implements ProductService {
    private ProductRepository productRepository;
    private final ModelMapper modelMapper;
    @Override
    public ProductResponseDTO add(ProductRequestDTO productRequestDTO, MultipartFile productPhoto) throws Exception {
        // TODO:FARMER SETLEME ISLEMI YAPILACAK
        Product product = modelMapper.map(productRequestDTO,Product.class);
        product.setProductPhoto(ImageUtils.compressImage(productPhoto.getBytes()));
        productRepository.save(product);
        return modelMapper.map(product,ProductResponseDTO.class);
    }

    @Override
    public List<ProductResponseDTO> getAll(){
       List<ProductResponseDTO> productResponseDTOList= new ArrayList<>();
       List<Product> productList =  productRepository.findAll();
        for (Product product : productList) {
            ProductResponseDTO productResponseDTO = modelMapper.map(product, ProductResponseDTO.class);
            productResponseDTOList.add(productResponseDTO);
        }
       return productResponseDTOList;
    }

    @Override
    public List<ProductResponseDTO> getDistinctByPredictionName() {
        List<Product> allProducts = productRepository.findAll();
        Set<String> distinctPredictionNames = new HashSet<>();
        List<Product> distinctProducts = new ArrayList<>();
        for (Product product : allProducts) {
            String predictionName = product.getPredictionName();
            if (predictionName != null && !distinctPredictionNames.contains(predictionName)) {
                product.setProductPhoto(ImageUtils.decompressImage(product.getProductPhoto()));
                distinctProducts.add(product);
                distinctPredictionNames.add(predictionName);
            }
        }
        return modelMapper.map(distinctProducts, new TypeToken<List<ProductResponseDTO>>() {}.getType());
    }

    @Override
    public ProductResponseDTO getById(Long id) throws Exception {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null){
            throw new Exception("Product not found with id" + id);
        }
        return modelMapper.map(product,ProductResponseDTO.class);
    }

    @Override
    public ProductResponseDTO update(ProductRequestDTO productRequest) throws Exception {
        Product product = productRepository.findById(productRequest.getId()).orElse(null);
        if(product == null){
            throw new Exception("Product not found with id "+ product.getId());
        }
        product = modelMapper.map(productRequest,Product.class);
        productRepository.save(product);
        return modelMapper.map(product,ProductResponseDTO.class);
    }

    public ProductResponseDTO updateProfilePhoto(Long id, MultipartFile profilePhoto) throws Exception{
        Product product = productRepository.findById(id).orElse(null);
        if(product == null){
            throw new Exception("Product not found with id "+ id);
        }
        product.setProductPhoto(ImageUtils.compressImage(profilePhoto.getBytes()));
        productRepository.save(product);
        return modelMapper.map(product,ProductResponseDTO.class);
    }

    @Override
    public void delete(Long id) throws Exception {
        Product product = productRepository.findById(id).orElse(null);
        if(product == null){
            throw new Exception("Product not found with id "+ id);
        }
        productRepository.delete(product);
    }
}
