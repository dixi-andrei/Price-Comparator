package com.accesa.pricecomparator.util;

import com.accesa.pricecomparator.model.Discount;
import com.accesa.pricecomparator.model.Product;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CSVHelper {

    private static final String PRODUCT_FILENAME_PATTERN = "([a-zA-Z]+)_(\\d{4}-\\d{2}-\\d{2})\\.csv";
    private static final String DISCOUNT_FILENAME_PATTERN = "([a-zA-Z]+)_discounts_(\\d{4}-\\d{2}-\\d{2})\\.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public List<Product> parseProductCSV(String filePath) {
        try {
            // Extract store name and date from filename
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString();

            Pattern pattern = Pattern.compile(PRODUCT_FILENAME_PATTERN);
            Matcher matcher = pattern.matcher(fileName);

            if (!matcher.matches()) {
                log.error("Invalid product file name format: {}", fileName);
                return Collections.emptyList();
            }

            String storeName = matcher.group(1);
            LocalDate fileDate = LocalDate.parse(matcher.group(2), DATE_FORMATTER);

            // Parse CSV
            Reader reader = new FileReader(filePath);
            List<Product> products = new CsvToBeanBuilder<Product>(reader)
                    .withType(Product.class)
                    .withSeparator(';')
                    .build()
                    .parse();

            // Set store and date for each product
            products.forEach(product -> {
                product.setStore(storeName);
                product.setDate(fileDate);
            });

            reader.close();
            return products;

        } catch (Exception e) {
            log.error("Failed to parse CSV file: {}", filePath, e);
            return Collections.emptyList();
        }
    }

    public List<Discount> parseDiscountCSV(String filePath) {
        try {
            // Extract store name and date from filename
            Path path = Paths.get(filePath);
            String fileName = path.getFileName().toString();

            Pattern pattern = Pattern.compile(DISCOUNT_FILENAME_PATTERN);
            Matcher matcher = pattern.matcher(fileName);

            if (!matcher.matches()) {
                log.error("Invalid discount file name format: {}", fileName);
                return Collections.emptyList();
            }

            String storeName = matcher.group(1);
            LocalDate fileDate = LocalDate.parse(matcher.group(2), DATE_FORMATTER);

            // Parse CSV
            Reader reader = new FileReader(filePath);
            List<Discount> discounts = new CsvToBeanBuilder<Discount>(reader)
                    .withType(Discount.class)
                    .withSeparator(';')
                    .build()
                    .parse();

            // Set store and date for each discount
            discounts.forEach(discount -> {
                discount.setStore(storeName);
                discount.setDiscountDate(fileDate);
            });

            reader.close();
            return discounts;

        } catch (Exception e) {
            log.error("Failed to parse CSV file: {}", filePath, e);
            return Collections.emptyList();
        }
    }

    public List<String> findProductFiles(String dataDirectory) {
        try (Stream<Path> stream = Files.walk(Paths.get(dataDirectory))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(file -> file.matches(".*" + PRODUCT_FILENAME_PATTERN))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list product files in directory: {}", dataDirectory, e);
            return Collections.emptyList();
        }
    }

    public List<String> findDiscountFiles(String dataDirectory) {
        try (Stream<Path> stream = Files.walk(Paths.get(dataDirectory))) {
            return stream
                    .filter(Files::isRegularFile)
                    .map(Path::toString)
                    .filter(file -> file.matches(".*" + DISCOUNT_FILENAME_PATTERN))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("Failed to list discount files in directory: {}", dataDirectory, e);
            return Collections.emptyList();
        }
    }
}