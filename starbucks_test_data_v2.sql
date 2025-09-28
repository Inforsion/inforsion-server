-- ===================================
-- 스타벅스 기준 테스트 데이터 (김민균)
-- OCR 영수증 처리 → 재고 변화 테스트용
-- ===================================

-- 1. 사용자 (김민균)
INSERT INTO users (username, email, password, created_at, updated_at)
VALUES ('김민균', 'dirak4545@smail.kongju.ac.kr', 'password00', NOW(), NOW());

-- 2. 매장 (스타벅스 강남점)
INSERT INTO stores (name, location, description, thumbnail_url, is_active, user_id, created_at, updated_at)
VALUES ('스타벅스 강남점', '서울특별시 강남구 테헤란로 152', '강남역 인근 스타벅스 매장',
        'https://image.starbucks.co.kr/upload/store/skuimg/2021/04/[9]_20210426091745441.jpg',
        true, 1, NOW(), NOW());

-- 3. 스타벅스 메뉴 (제품)
INSERT INTO products (name, description, price, category, image_url, is_signature, in_stock, store_id, created_at, updated_at) VALUES
-- 에스프레소 베이스
('아메리카노', '깔끔하고 깊은 에스프레소의 풍미', 4100.00, '에스프레소', 'https://image.starbucks.co.kr/upload/store/skuimg/2021/04/[9]_20210426091745441.jpg', false, true, 1, NOW(), NOW()),
('카페라떼', '부드러운 우유와 진한 에스프레소의 조화', 4600.00, '에스프레소', null, false, true, 1, NOW(), NOW()),
('카푸치노', '풍성한 우유 거품과 에스프레소', 4600.00, '에스프레소', null, false, true, 1, NOW(), NOW()),
('카라멜 마키아또', '달콤한 카라멜과 바닐라 시럽의 조화', 5900.00, '에스프레소', null, true, true, 1, NOW(), NOW()),

-- 프라푸치노
('자바칩 프라푸치노', '진한 모카소스와 자바칩의 만남', 6100.00, '프라푸치노', null, true, true, 1, NOW(), NOW()),
('카라멜 프라푸치노', '달콤한 카라멜 프라푸치노', 5600.00, '프라푸치노', null, false, true, 1, NOW(), NOW()),

-- 티/기타
('아이스티', '시원한 홍차', 3800.00, '티', null, false, true, 1, NOW(), NOW()),
('핫초콜릿', '진한 초콜릿 음료', 4900.00, '기타', null, false, true, 1, NOW(), NOW()),

-- 디저트
('초콜릿 칩 쿠키', '바삭한 초콜릿 칩 쿠키', 2500.00, '디저트', null, false, true, 1, NOW(), NOW()),
('블루베리 머핀', '신선한 블루베리 머핀', 3800.00, '디저트', null, false, true, 1, NOW(), NOW());

-- 4. 재료 (인벤토리)
INSERT INTO inventories (ingredient_name, current_stock, min_stock_level, max_stock_level, unit, unit_cost,
                        last_restocked_date, expiry_date, stock_status, store_id, created_at, updated_at) VALUES
-- 기본 재료
('에스프레소 원두', 5000.00, 500.00, 10000.00, 'g', 0.05, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('우유', 20000.00, 2000.00, 50000.00, 'ml', 0.003, '2024-01-01', '2024-02-15', 'SUFFICIENT', 1, NOW(), NOW()),
('물', 50000.00, 5000.00, 100000.00, 'ml', 0.001, '2024-01-01', '2025-12-31', 'SUFFICIENT', 1, NOW(), NOW()),

-- 시럽/소스
('바닐라 시럽', 2000.00, 200.00, 5000.00, 'ml', 0.02, '2024-01-01', '2024-06-30', 'SUFFICIENT', 1, NOW(), NOW()),
('카라멜 시럽', 1500.00, 150.00, 3000.00, 'ml', 0.025, '2024-01-01', '2024-06-30', 'SUFFICIENT', 1, NOW(), NOW()),
('모카 소스', 1200.00, 120.00, 2500.00, 'ml', 0.03, '2024-01-01', '2024-06-30', 'SUFFICIENT', 1, NOW(), NOW()),

-- 토핑/부재료
('자바칩', 800.00, 80.00, 2000.00, 'g', 0.08, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('휘핑크림', 1000.00, 100.00, 2000.00, 'ml', 0.015, '2024-01-01', '2024-01-31', 'SUFFICIENT', 1, NOW(), NOW()),
('얼음', 10000.00, 1000.00, 20000.00, 'g', 0.0005, '2024-01-01', '2024-01-31', 'SUFFICIENT', 1, NOW(), NOW()),

-- 기타 재료
('홍차', 1000.00, 100.00, 2000.00, 'g', 0.02, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('초콜릿 파우더', 500.00, 50.00, 1000.00, 'g', 0.1, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),

-- 디저트 재료
('밀가루', 2000.00, 200.00, 5000.00, 'g', 0.003, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('버터', 1000.00, 100.00, 2000.00, 'g', 0.015, '2024-01-01', '2024-03-01', 'SUFFICIENT', 1, NOW(), NOW()),
('설탕', 1500.00, 150.00, 3000.00, 'g', 0.005, '2024-01-01', '2025-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('초콜릿 칩', 800.00, 80.00, 1500.00, 'g', 0.02, '2024-01-01', '2024-12-31', 'SUFFICIENT', 1, NOW(), NOW()),
('블루베리', 600.00, 60.00, 1200.00, 'g', 0.03, '2024-01-01', '2024-02-01', 'SUFFICIENT', 1, NOW(), NOW()),
('계란', 500.00, 50.00, 1000.00, 'g', 0.008, '2024-01-01', '2024-02-15', 'SUFFICIENT', 1, NOW(), NOW());

-- 5. 레시피 (제품과 재료 연결)
INSERT INTO receipies (menu_id, inventory_id, amount_per_menu, unit, is_active, created_at, updated_at) VALUES
-- 아메리카노 (product_id: 1) - 에스프레소 더블샷, 뜨거운 물
(1, 1, 18.00, 'g', true, NOW(), NOW()),
(1, 3, 200.00, 'ml', true, NOW(), NOW()),

-- 카페라떼 (product_id: 2) - 에스프레소 더블샷, 스팀 우유
(2, 1, 18.00, 'g', true, NOW(), NOW()),
(2, 2, 240.00, 'ml', true, NOW(), NOW()),

-- 카푸치노 (product_id: 3) - 에스프레소 더블샷, 스팀 우유, 우유 거품
(3, 1, 18.00, 'g', true, NOW(), NOW()),
(3, 2, 180.00, 'ml', true, NOW(), NOW()),
(3, 8, 20.00, 'ml', true, NOW(), NOW()),

-- 카라멜 마키아또 (product_id: 4) - 에스프레소, 우유, 바닐라시럽, 카라멜시럽
(4, 1, 18.00, 'g', true, NOW(), NOW()),
(4, 2, 200.00, 'ml', true, NOW(), NOW()),
(4, 4, 20.00, 'ml', true, NOW(), NOW()),
(4, 5, 15.00, 'ml', true, NOW(), NOW()),

-- 자바칩 프라푸치노 (product_id: 5) - 에스프레소, 우유, 모카소스, 자바칩, 휘핑크림, 얼음
(5, 1, 18.00, 'g', true, NOW(), NOW()),
(5, 2, 180.00, 'ml', true, NOW(), NOW()),
(5, 6, 30.00, 'ml', true, NOW(), NOW()),
(5, 7, 15.00, 'g', true, NOW(), NOW()),
(5, 8, 30.00, 'ml', true, NOW(), NOW()),
(5, 9, 100.00, 'g', true, NOW(), NOW()),

-- 카라멜 프라푸치노 (product_id: 6) - 우유, 카라멜시럽, 휘핑크림, 얼음
(6, 2, 200.00, 'ml', true, NOW(), NOW()),
(6, 5, 25.00, 'ml', true, NOW(), NOW()),
(6, 8, 25.00, 'ml', true, NOW(), NOW()),
(6, 9, 120.00, 'g', true, NOW(), NOW()),

-- 아이스티 (product_id: 7) - 홍차, 물, 얼음
(7, 10, 3.00, 'g', true, NOW(), NOW()),
(7, 3, 300.00, 'ml', true, NOW(), NOW()),
(7, 9, 80.00, 'g', true, NOW(), NOW()),

-- 핫초콜릿 (product_id: 8) - 초콜릿 파우더, 뜨거운 우유, 휘핑크림
(8, 11, 25.00, 'g', true, NOW(), NOW()),
(8, 2, 250.00, 'ml', true, NOW(), NOW()),
(8, 8, 20.00, 'ml', true, NOW(), NOW()),

-- 초콜릿 칩 쿠키 (product_id: 9) - 밀가루, 버터, 설탕, 초콜릿칩, 계란
(9, 12, 50.00, 'g', true, NOW(), NOW()),
(9, 13, 15.00, 'g', true, NOW(), NOW()),
(9, 14, 20.00, 'g', true, NOW(), NOW()),
(9, 15, 10.00, 'g', true, NOW(), NOW()),
(9, 16, 5.00, 'g', true, NOW(), NOW()),

-- 블루베리 머핀 (product_id: 10) - 밀가루, 버터, 설탕, 계란, 블루베리
(10, 12, 80.00, 'g', true, NOW(), NOW()),
(10, 13, 25.00, 'g', true, NOW(), NOW()),
(10, 14, 30.00, 'g', true, NOW(), NOW()),
(10, 16, 15.00, 'g', true, NOW(), NOW()),
(10, 17, 20.00, 'g', true, NOW(), NOW());

-- 6. 샘플 거래 (영수증 처리 테스트용)
INSERT INTO transactions (store_id, name, date, amount, payment_method, transaction_type, transaction_memo, transaction_category)
VALUES (1, '스타벅스 매출', '2024-01-15 14:30:00', 15700.00, 'CARD', 'INCOME', 'OCR 테스트용 거래', 'BEVERAGE_SALES');

-- 7. 샘플 주문 (영수증의 아이템들)
INSERT INTO orders (menu_id, transaction_id, quantity, unit_price, total_price, order_status, created_at, updated_at) VALUES
(5, 1, 2, 6100.00, 12200.00, 'COMPLETED', NOW(), NOW()), -- 자바칩 프라푸치노 x2
(1, 1, 1, 4100.00, 4100.00, 'COMPLETED', NOW(), NOW()),  -- 아메리카노 x1
(9, 1, 1, 2500.00, 2500.00, 'COMPLETED', NOW(), NOW());  -- 초콜릿 칩 쿠키 x1

-- 8. 재고 변화 로그 예시 (수동 입력)
INSERT INTO inventory_logs (inventory_id, log_type, quantity_change, before_quantity, after_quantity, reason, created_at)
VALUES
(1, 'DEDUCTION', -36.00, 5000.00, 4964.00, '자바칩 프라푸치노 2개, 아메리카노 1개 제조', NOW()),
(2, 'DEDUCTION', -600.00, 20000.00, 19400.00, '라떼류 제조로 인한 우유 소모', NOW()),
(7, 'DEDUCTION', -30.00, 800.00, 770.00, '자바칩 프라푸치노 2개 제조', NOW());

-- ===================================
-- OCR 테스트 시나리오:
-- 1. 위 영수증을 OCR로 읽음
-- 2. 제품 매칭 수행 (자바칩 프라푸치노, 아메리카노, 쿠키)
-- 3. 레시피 기반 재고 자동 차감 확인
-- 4. inventories 테이블의 current_stock 값 변화 확인
-- ===================================

-- 확인용 쿼리들:
-- SELECT name, current_stock FROM inventories WHERE store_id = 1;
-- SELECT p.name, r.amount_per_menu, r.unit, i.ingredient_name
-- FROM recipes r
-- JOIN products p ON r.menu_id = p.id
-- JOIN inventories i ON r.inventory_id = i.id
-- WHERE p.store_id = 1 ORDER BY p.name, r.id;