-- ===========================================
-- 인포전(Inforsion) 카페 관리 시스템 테스트 데이터
-- ===========================================
-- 이 파일은 개발/테스트 환경에서 사용할 샘플 데이터를 포함합니다.
-- 총 3개의 카페(커피원두 본점, 디저트&커피, 로스터리카페)의 
-- 실제 운영 상황을 시뮬레이션하는 데이터로 구성되어 있습니다.
-- ===========================================

-- MySQL Workbench에서 직접 실행시 데이터베이스 선택 (Spring Boot에서는 자동으로 처리됨)
-- USE inforsion_db;

-- 1. 사용자 데이터 (카페 사장님들)
-- UserEntity 테이블: users
-- 각 카페의 사장님 계정 정보
INSERT IGNORE INTO users (username, email, password, created_at, updated_at) VALUES
('김민수', 'minsoo.kim@cafe.com', '$2a$10$N9qo8uLOickgx2ZMRj6OZO0VJYr8Ql5L8f9YR6xWv.7M2nQg1i6M.', NOW(), NOW()),
('이지은', 'jieun.lee@coffee.com', '$2a$10$N9qo8uLOickgx2ZMRj6OZO0VJYr8Ql5L8f9YR6xWv.7M2nQg1i6M.', NOW(), NOW()),
('박현우', 'hyeonwoo.park@dessert.com', '$2a$10$N9qo8uLOickgx2ZMRj6OZO0VJYr8Ql5L8f9YR6xWv.7M2nQg1i6M.', NOW(), NOW());

-- 2. 매장 데이터 (카페 정보)
-- StoreEntity 테이블: stores
-- 3개 카페의 기본 정보 (위치, 설명, 소유자)
INSERT IGNORE INTO stores (name, location, description, user_id, created_at, updated_at) VALUES
('커피원두 본점', '서울시 강남구 테헤란로 123', '신선한 원두와 정성스러운 핸드드립으로 유명한 스페셜티 커피 전문점', 1, NOW(), NOW()),
('디저트&커피', '서울시 홍대 와우산로 456', '수제 디저트와 특별한 시그니처 음료를 즐길 수 있는 아늑한 카페', 2, NOW(), NOW()),
('로스터리카페', '부산시 해운대구 센텀로 789', '자체 로스팅으로 최고급 원두를 제공하는 프리미엄 로스터리 카페', 3, NOW(), NOW());

-- 3. 재고 관리 데이터 (인벤토리)
-- InventoryEntity 테이블: inventories
-- 각 카페별 원자재, 재료, 소모품 현황
-- 재고 상태: SUFFICIENT(충분), LOW(부족), OUT_OF_STOCK(품절)
INSERT IGNORE INTO inventories (store_id, ingredient_name, current_stock, min_stock_level, max_stock_level, unit, unit_cost, last_restocked_date, expiry_date, stock_status, created_at, updated_at) VALUES
-- 커피원두 본점 재료
(1, '에티오피아 원두', 50.0, 10.0, 100.0, 'kg', 25000.00, '2024-01-15', '2024-06-15', 'SUFFICIENT', NOW(), NOW()),
(1, '콜롬비아 원두', 30.0, 10.0, 100.0, 'kg', 28000.00, '2024-01-15', '2024-06-15', 'SUFFICIENT', NOW(), NOW()),
(1, '우유', 20.0, 5.0, 50.0, 'L', 2500.00, '2024-01-20', '2024-02-05', 'SUFFICIENT', NOW(), NOW()),
(1, '바닐라시럽', 10.0, 3.0, 20.0, 'L', 8000.00, '2024-01-18', '2024-12-31', 'SUFFICIENT', NOW(), NOW()),
(1, '카라멜시럽', 8.0, 3.0, 20.0, 'L', 8500.00, '2024-01-18', '2024-12-31', 'LOW', NOW(), NOW()),
(1, '휘핑크림', 15.0, 5.0, 30.0, 'L', 12000.00, '2024-01-19', '2024-02-15', 'SUFFICIENT', NOW(), NOW()),
(1, '일회용 컵 (S)', 500.0, 100.0, 1000.0, '개', 80.00, '2024-01-10', '2025-12-31', 'SUFFICIENT', NOW(), NOW()),
(1, '일회용 컵 (M)', 400.0, 100.0, 1000.0, '개', 100.00, '2024-01-10', '2025-12-31', 'SUFFICIENT', NOW(), NOW()),
(1, '일회용 컵 (L)', 300.0, 100.0, 1000.0, '개', 120.00, '2024-01-10', '2025-12-31', 'SUFFICIENT', NOW(), NOW()),

-- 디저트&커피 재료
(2, '과테말라 원두', 40.0, 10.0, 100.0, 'kg', 26000.00, '2024-01-16', '2024-06-16', 'SUFFICIENT', NOW(), NOW()),
(2, '우유', 25.0, 5.0, 50.0, 'L', 2500.00, '2024-01-20', '2024-02-05', 'SUFFICIENT', NOW(), NOW()),
(2, '초콜릿시럽', 12.0, 3.0, 20.0, 'L', 9000.00, '2024-01-18', '2024-12-31', 'SUFFICIENT', NOW(), NOW()),
(2, '딸기시럽', 6.0, 3.0, 20.0, 'L', 8500.00, '2024-01-18', '2024-12-31', 'LOW', NOW(), NOW()),
(2, '크로와상', 50.0, 10.0, 100.0, '개', 1500.00, '2024-01-21', '2024-01-23', 'SUFFICIENT', NOW(), NOW()),
(2, '머핀', 40.0, 10.0, 80.0, '개', 2000.00, '2024-01-21', '2024-01-24', 'SUFFICIENT', NOW(), NOW()),
(2, '마카롱', 30.0, 5.0, 50.0, '개', 3000.00, '2024-01-21', '2024-01-23', 'SUFFICIENT', NOW(), NOW()),

-- 로스터리카페 재료
(3, '브라질 원두', 60.0, 10.0, 120.0, 'kg', 24000.00, '2024-01-17', '2024-06-17', 'SUFFICIENT', NOW(), NOW()),
(3, '케냐 원두', 35.0, 10.0, 100.0, 'kg', 30000.00, '2024-01-17', '2024-06-17', 'SUFFICIENT', NOW(), NOW()),
(3, '오트밀크', 18.0, 5.0, 40.0, 'L', 3500.00, '2024-01-19', '2024-02-10', 'SUFFICIENT', NOW(), NOW()),
(3, '아몬드밀크', 15.0, 5.0, 40.0, 'L', 4000.00, '2024-01-19', '2024-02-10', 'SUFFICIENT', NOW(), NOW()),
(3, '헤이즐넛시럽', 9.0, 3.0, 20.0, 'L', 9500.00, '2024-01-18', '2024-12-31', 'SUFFICIENT', NOW(), NOW());

-- 4. 제품 및 메뉴 데이터
-- ProductEntity 테이블: products
-- 각 카페의 판매 메뉴 (커피, 디저트 등)
INSERT IGNORE INTO products (store_id, name, price, category, description, in_stock, created_at, updated_at) VALUES
-- 커피원두 본점 메뉴
(1, '에티오피아 드립커피', 4500.00, '커피', '산미가 살아있는 에티오피아 원두로 내린 드립커피', true, NOW(), NOW()),
(1, '콜롬비아 드립커피', 5000.00, '커피', '부드럽고 균형잡힌 맛의 콜롬비아 원두 드립커피', true, NOW(), NOW()),
(1, '아메리카노', 4000.00, '커피', '에스프레소에 뜨거운 물을 넣은 기본 아메리카노', true, NOW(), NOW()),
(1, '카페라떼', 5500.00, '커피', '에스프레소와 부드러운 우유로 만든 라떼', true, NOW(), NOW()),
(1, '바닐라라떼', 6000.00, '커피', '바닐라시럽이 들어간 달콤한 라떼', true, NOW(), NOW()),
(1, '카라멜마키아또', 6500.00, '커피', '카라멜시럽과 휘핑크림이 올라간 마키아또', true, NOW(), NOW()),

-- 디저트&커피 메뉴
(2, '과테말라 아메리카노', 4200.00, '커피', '과테말라 원두로 내린 진한 아메리카노', true, NOW(), NOW()),
(2, '초콜릿라떼', 5800.00, '커피', '진한 초콜릿시럽이 들어간 달콤한 라떼', true, NOW(), NOW()),
(2, '딸기라떼', 6200.00, '커피', '상큼한 딸기시럽이 들어간 핑크라떼', true, NOW(), NOW()),
(2, '크로와상', 3500.00, '디저트', '바삭하고 부드러운 버터 크로와상', true, NOW(), NOW()),
(2, '블루베리머핀', 4000.00, '디저트', '블루베리가 가득한 촉촉한 머핀', true, NOW(), NOW()),
(2, '마카롱 세트', 8000.00, '디저트', '5개입 마카롱 세트 (랜덤 맛)', true, NOW(), NOW()),

-- 로스터리카페 메뉴
(3, '브라질 핸드드립', 5500.00, '커피', '브라질 원두로 정성스럽게 내린 핸드드립', true, NOW(), NOW()),
(3, '케냐 스페셜티', 6500.00, '커피', '케냐 AA급 원두로 만든 스페셜티 커피', true, NOW(), NOW()),
(3, '오트밀라떼', 5800.00, '커피', '건강한 오트밀크로 만든 식물성 라떼', true, NOW(), NOW()),
(3, '아몬드라떼', 6000.00, '커피', '고소한 아몬드밀크 라떼', true, NOW(), NOW()),
(3, '헤이즐넛라떼', 6200.00, '커피', '헤이즐넛시럽이 들어간 고급 라떼', true, NOW(), NOW());

-- 5. 레시피 데이터 (메뉴별 재료 구성)
-- RecipeEntity 테이블: recipes
-- 각 메뉴를 만들기 위해 필요한 재료와 분량
-- amount_per_menu: 메뉴 1개 당 필요한 재료량
INSERT IGNORE INTO recipes (menu_id, inventory_id, amount_per_menu, unit, is_active, created_at, updated_at) VALUES
-- 에티오피아 드립커피 레시피
(1, 1, 0.025, 'kg', true, NOW(), NOW()), -- 에티오피아 원두 25g
(1, 7, 1.0, '개', true, NOW(), NOW()),   -- S컵 1개

-- 콜롬비아 드립커피 레시피
(2, 2, 0.025, 'kg', true, NOW(), NOW()), -- 콜롬비아 원두 25g
(2, 7, 1.0, '개', true, NOW(), NOW()),   -- S컵 1개

-- 아메리카노 레시피
(3, 1, 0.020, 'kg', true, NOW(), NOW()), -- 에티오피아 원두 20g
(3, 8, 1.0, '개', true, NOW(), NOW()),   -- M컵 1개

-- 카페라떼 레시피
(4, 1, 0.020, 'kg', true, NOW(), NOW()), -- 에티오피아 원두 20g
(4, 3, 0.15, 'L', true, NOW(), NOW()),  -- 우유 150ml
(4, 8, 1.0, '개', true, NOW(), NOW()),   -- M컵 1개

-- 바닐라라떼 레시피
(5, 1, 0.020, 'kg', true, NOW(), NOW()), -- 에티오피아 원두 20g
(5, 3, 0.15, 'L', true, NOW(), NOW()),  -- 우유 150ml
(5, 4, 0.020, 'L', true, NOW(), NOW()), -- 바닐라시럽 20ml
(5, 8, 1.0, '개', true, NOW(), NOW()),   -- M컵 1개

-- 카라멜마키아또 레시피
(6, 1, 0.020, 'kg', true, NOW(), NOW()), -- 에티오피아 원두 20g
(6, 3, 0.15, 'L', true, NOW(), NOW()),  -- 우유 150ml
(6, 5, 0.020, 'L', true, NOW(), NOW()), -- 카라멜시럽 20ml
(6, 6, 0.050, 'L', true, NOW(), NOW()), -- 휘핑크림 50ml
(6, 9, 1.0, '개', true, NOW(), NOW()),   -- L컵 1개

-- 초콜릿라떼 레시피 (디저트&커피)
(8, 10, 0.020, 'kg', true, NOW(), NOW()), -- 과테말라 원두 20g
(8, 11, 0.15, 'L', true, NOW(), NOW()),  -- 우유 150ml
(8, 12, 0.025, 'L', true, NOW(), NOW()); -- 초콜릿시럽 25ml

-- 6. 거래 데이터 (매출 정보)
-- TransactionEntity 테이블: transactions
-- 2024년 1월 22일 하루 동안의 매출 현황
-- 결제 방법: CARD(카드), CASH(현금), MOBILE_PAY(모바일페이)
INSERT IGNORE INTO transactions (store_id, total_amount, transaction_type, payment_method, transaction_date, created_at, updated_at) VALUES
(1, 15000.00, 'SALE', 'CARD', '2024-01-22 09:30:00', NOW(), NOW()),
(1, 22000.00, 'SALE', 'CASH', '2024-01-22 11:15:00', NOW(), NOW()),
(1, 8500.00, 'SALE', 'MOBILE_PAY', '2024-01-22 14:20:00', NOW(), NOW()),
(2, 18500.00, 'SALE', 'CARD', '2024-01-22 10:45:00', NOW(), NOW()),
(2, 12000.00, 'SALE', 'CASH', '2024-01-22 16:30:00', NOW(), NOW()),
(3, 24000.00, 'SALE', 'CARD', '2024-01-22 13:00:00', NOW(), NOW());

-- 7. 주문 상세 데이터
-- OrderEntity 테이블: orders
-- 각 거래별 주문 내역 (어떤 메뉴를 몇 개씩 주문했는지)
INSERT IGNORE INTO orders (transaction_id, product_id, quantity, unit_price, total_price, created_at, updated_at) VALUES
-- 트랜잭션 1의 주문들
(1, 1, 1, 4500.00, 4500.00, NOW(), NOW()), -- 에티오피아 드립커피 1잔
(1, 4, 2, 5500.00, 11000.00, NOW(), NOW()), -- 카페라떼 2잔

-- 트랜잭션 2의 주문들
(2, 3, 2, 4000.00, 8000.00, NOW(), NOW()), -- 아메리카노 2잔
(2, 5, 1, 6000.00, 6000.00, NOW(), NOW()), -- 바닐라라떼 1잔
(2, 6, 1, 6500.00, 6500.00, NOW(), NOW()), -- 카라멜마키아또 1잔

-- 트랜잭션 3의 주문들
(3, 2, 1, 5000.00, 5000.00, NOW(), NOW()), -- 콜롬비아 드립커피 1잔
(3, 3, 1, 4000.00, 4000.00, NOW(), NOW()), -- 아메리카노 1잔

-- 트랜잭션 4의 주문들 (디저트&커피)
(4, 8, 2, 5800.00, 11600.00, NOW(), NOW()), -- 초콜릿라떼 2잔
(4, 10, 2, 3500.00, 7000.00, NOW(), NOW()), -- 크로와상 2개

-- 트랜잭션 5의 주문들 (디저트&커피)
(5, 11, 3, 4000.00, 12000.00, NOW(), NOW()), -- 블루베리머핀 3개

-- 트랜잭션 6의 주문들 (로스터리카페)
(6, 13, 2, 5500.00, 11000.00, NOW(), NOW()), -- 브라질 핸드드립 2잔
(6, 14, 2, 6500.00, 13000.00, NOW(), NOW()); -- 케냐 스페셜티 2잔

-- 8. 재고 변화 로그
-- InventoryLogEntity 테이블: inventory_logs
-- 재고 입출고 기록 추적
-- log_type: RESTOCK(입고), DEDUCTION(차감), ADJUSTMENT(조정)
INSERT IGNORE INTO inventory_logs (inventory_id, log_type, quantity_change, before_quantity, after_quantity, reason, created_at) VALUES
-- 원두 사용 기록들
(1, 'DEDUCTION', -0.065, 50.065, 50.0, '에티오피아 드립커피 주문으로 인한 차감', '2024-01-22 09:30:00'),
(2, 'DEDUCTION', -0.025, 30.025, 30.0, '콜롬비아 드립커피 주문으로 인한 차감', '2024-01-22 14:20:00'),
(3, 'DEDUCTION', -0.45, 20.45, 20.0, '라떼류 주문으로 인한 우유 차감', '2024-01-22 11:15:00'),
(4, 'DEDUCTION', -0.020, 10.020, 10.0, '바닐라라떼 주문으로 인한 시럽 차감', '2024-01-22 11:15:00'),

-- 입고 기록들
(1, 'RESTOCK', 50.0, 0.0, 50.0, '정기 원두 입고', '2024-01-15 08:00:00'),
(2, 'RESTOCK', 30.0, 0.0, 30.0, '정기 원두 입고', '2024-01-15 08:00:00'),
(3, 'RESTOCK', 20.0, 0.0, 20.0, '유제품 정기 입고', '2024-01-20 07:00:00');

-- 9. 알림 시스템 데이터
-- AlertEntity 테이블: alerts
-- 재고 부족, 입고 필요 등의 알림
-- is_read: 알림 확인 여부 (false=미확인, true=확인함)
INSERT IGNORE INTO alerts (user_id, store_id, inventory_id, alert_type, title, message, is_read, created_at) VALUES
(1, 1, 5, 'LOW_STOCK', '재고 부족 알림', '카라멜시럽의 재고가 부족합니다. (현재: 8L, 최소: 10L)', false, '2024-01-21 15:30:00'),
(2, 2, 13, 'LOW_STOCK', '재고 부족 알림', '딸기시럽의 재고가 부족합니다. (현재: 6L, 최소: 10L)', false, '2024-01-21 16:00:00'),
(1, 1, 2, 'RESTOCK_NEEDED', '입고 필요 알림', '콜롬비아 원두의 입고가 필요합니다.', false, '2024-01-22 08:00:00'),
(2, 2, 14, 'LOW_STOCK', '재고 부족 알림', '마카롱의 재고가 부족합니다.', true, '2024-01-20 10:00:00');

-- 10. 분석 및 통계 데이터
-- AnalyticsEntity 테이블: analytics
-- 일별 매출, 주문 수, 고객 수, 평균 주문금액 등
-- 매장별 성과 분석을 위한 데이터
INSERT IGNORE INTO analytics (store_id, date, total_sales, total_orders, top_selling_product_id, total_customers, average_order_value, created_at, updated_at) VALUES
(1, '2024-01-22', 45500.00, 6, 3, 3, 15166.67, NOW(), NOW()), -- 커피원두 본점
(2, '2024-01-22', 30500.00, 5, 11, 2, 15250.00, NOW(), NOW()), -- 디저트&커피
(3, '2024-01-22', 24000.00, 4, 13, 1, 24000.00, NOW(), NOW()), -- 로스터리카페
(1, '2024-01-21', 38000.00, 5, 4, 3, 12666.67, NOW(), NOW()),
(2, '2024-01-21', 25000.00, 4, 8, 2, 12500.00, NOW(), NOW()),
(3, '2024-01-21', 19500.00, 3, 15, 2, 9750.00, NOW(), NOW());

-- 11. OCR (광학 문자 인식) 시스템 데이터
-- ===========================================
-- OCR 원본 데이터는 MongoDB에 저장됨 (컬렉션명: ocr_raw_data)
-- 영수증이나 주문서 이미지의 원본 데이터
-- ===========================================

-- OCR 결과 데이터 (텍스트 추출 및 매칭 결과)
-- OcrResultEntity 테이블: ocr_results
-- OCR로 인식된 아이템을 실제 메뉴와 매칭
-- match_method: AUTO(자동매칭), MANUAL(수동매칭)
-- match_type: MENU(메뉴), INGREDIENT(재료), UNKNOWN(미매칭)
INSERT IGNORE INTO ocr_results (store_id, raw_data_id, ocr_item_name, quantity, price, match_type, target_id, total_amount, match_method, created_at, updated_at) VALUES
(1, 1001, '아메리카노', 3, 4000, 'MENU', 3, 12000.00, 'AUTO', '2024-01-22 08:30:00', NOW()),
(1, 1001, '카페라떼', 2, 5500, 'MENU', 4, 11000.00, 'AUTO', '2024-01-22 08:30:00', NOW()),
(2, 1002, '초콜릿 라떼', 1, 5800, 'MENU', 8, 5800.00, 'MANUAL', '2024-01-22 09:15:00', NOW()),
(2, 1002, '크로와상', 4, 3500, 'MENU', 10, 14000.00, 'AUTO', '2024-01-22 09:15:00', NOW());