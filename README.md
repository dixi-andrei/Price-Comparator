#  Price Comparator - Market

> Backend application for comparing grocery prices across Romanian supermarket chains (Lidl, Kaufland, Profi)

[![Java](https://img.shields.io/badge/Java-11-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.9-brightgreen.svg)](https://spring.io/)

*Developed for the **Accesa Java Internship Programme 2025***


## Project Overview

A comprehensive price comparison system that helps users save money on groceries by:

- **Comparing prices** across multiple stores in real-time
- **Finding best discounts** and tracking price changes over time  
- **Optimizing shopping baskets** to minimize total cost
- **Calculating value per unit** for fair product comparison
- **Setting price alerts** for target prices

### Tech Stack
- **Backend**: Java 11, Spring Boot 2.7.9
- **Data**: CSV processing with OpenCSV
- **API**: RESTful endpoints with comprehensive documentation
- **Build**: Maven with wrapper included

## Core Features

### 1. Shopping Basket Optimization
**Find the cheapest store for your complete shopping list**
```bash
POST /api/products/optimize-basket
Body: ["P001", "P008", "P017", "P020"]
```
**Result**: Compare total prices across all stores

### 2. Best Discounts Discovery  
**Identify highest percentage discounts across stores**
```bash
GET /api/products/best-discounts?limit=5
GET /api/discounts/best?limit=10
```

### 3. Price History & Trends
**Track price changes over time for smart buying decisions**
```bash
GET /api/products/price-history/P001?store=lidl
GET /api/products/compare?productName=lapte%20zuzu
```

### 4. Value Per Unit Comparison
**Compare products fairly regardless of package size**
```bash
GET /api/products/best-value?category=lactate&limit=5
```

### 5. New Discounts Tracking
**Stay updated on latest deals**
```bash
GET /api/products/new-discounts?since=2025-05-08
```

### 6.  **Custom Price Alerts**  *Featured*

**Set target prices and get notified when products reach your desired price point**

#### Creating Price Alerts
```bash
# Create an alert for milk under 9.0 RON at any store
POST /api/alerts
{
  "productId": "P001",
  "targetPrice": 9.0,
  "userId": "user123"
}

# Create store-specific alert
POST /api/alerts  
{
  "productId": "P001",
  "store": "lidl",
  "targetPrice": 8.5,
  "userId": "user123"
}
```

#### Managing Alerts
```bash
# View all your alerts
GET /api/alerts?userId=user123&activeOnly=true

# Check triggered alerts
POST /api/alerts/check

# Delete an alert
DELETE /api/alerts/1
```

#### How It Works
1. **Set Target Price**: Define your maximum price for any product
2. **Choose Store**: Optional - monitor specific store or all stores
3. **Automatic Monitoring**: System checks prices against your alerts
4. **Get Notified**: Alerts trigger when price drops to/below target
5. **Smart Buying**: Purchase at your preferred price point


## Quick Start

```bash
# 1. Clone and setup
git clone https://github.com/yourusername/price-comparator.git
cd priceComparator

# 2. Add sample data to 'data' directory
mkdir data
# Copy CSV files following: storename_date.csv format

# 3. Run application  
./mvnw spring-boot:run

# 4. Test API
curl http://localhost:7777/api/products/best-discounts?limit=3
```

# Postman
You can import the Postman code from the file 'postman-import-code' and try all the calls.
## API Examples

<details>
<summary><strong>Compare Prices</strong></summary>

```bash
curl "http://localhost:7777/api/products/compare?productName=lapte%20zuzu"
```
```json
{
  "lidl": { "price": 9.90, "discountedPrice": 8.91, "discountPercentage": 10 },
  "kaufland": { "price": 10.10 },
  "profi": { "price": 12.90 }
}
```

</details>

<details>
<summary><strong>Optimize Basket</strong></summary>

```bash
curl -X POST http://localhost:7777/api/products/optimize-basket \
  -H "Content-Type: application/json" \
  -d '["P001", "P008", "P017"]'
```
```json
{
  "lidl": 51.31,
  "kaufland": 53.20,
  "profi": 52.10
}
```

</details>

## Project Structure

```
src/main/java/com/accesa/pricecomparator/
├── controller/     # REST API endpoints
├── service/        # Business logic  
├── model/          # Data models (Product, Discount, PriceAlert)
└── util/           # CSV parsing utilities
data/               # CSV data files
```

## Testing

```bash
# Run tests
./mvnw test

# Test coverage  
./mvnw test jacoco:report
```

