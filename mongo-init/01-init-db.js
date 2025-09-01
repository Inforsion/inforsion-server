// MongoDB 초기화 스크립트
print('MongoDB 초기화 시작...');

// OCR 데이터베이스로 전환
db = db.getSiblingDB('inforsion_ocr_db');

// OCR 사용자 생성 (애플리케이션용)
db.createUser({
  user: 'ocr_user',
  pwd: 'ocr_password',
  roles: [
    {
      role: 'readWrite',
      db: 'inforsion_ocr_db'
    }
  ]
});

// 영수증 OCR 데이터 컬렉션 생성 및 인덱스 설정
db.createCollection('receiptOcrData');

// 기본 인덱스 생성
db.receiptOcrData.createIndex({ "receiptId": 1 }, { unique: true });
db.receiptOcrData.createIndex({ "userId": 1 });
db.receiptOcrData.createIndex({ "storeId": 1 });
db.receiptOcrData.createIndex({ "createdAt": 1 });
db.receiptOcrData.createIndex({ "processingStatus": 1 });

// 영수증 핵심 데이터용 텍스트 인덱스 (제품명 검색 최적화)
db.receiptOcrData.createIndex({
  "receiptData.items.productName": "text"
});

// 매출 분석용 인덱스
db.receiptOcrData.createIndex({
  "storeId": 1,
  "receiptData.totalAmount": 1,
  "createdAt": 1
});

// 재료별 수량/금액 분석용 인덱스 (배열 필드용)
db.receiptOcrData.createIndex({
  "storeId": 1,
  "receiptData.items.quantity": 1
});

db.receiptOcrData.createIndex({
  "storeId": 1,
  "receiptData.items.amount": 1
});

// 샘플 영수증 데이터 삽입 (테스트용)
db.receiptOcrData.insertOne({
  receiptId: "receipt-001",
  userId: "user123",
  storeId: "store456",
  fileName: "receipt_20250822_001.jpg",
  imageMetadata: {
    size: 102400,
    format: "jpg",
    dimensions: "800x600"
  },
  receiptData: {
    storeName: "마트24",
    storeAddress: "서울시 강남구",
    transactionDate: "2025-08-22",
    transactionTime: "14:30:00",
    items: [
      {
        productName: "삼겹살 1kg",
        quantity: 2,
        unitPrice: 15000,
        amount: 30000
      },
      {
        productName: "상추 1봉",
        quantity: 1,
        unitPrice: 3000,
        amount: 3000
      },
      {
        productName: "쌈장",
        quantity: 1,
        unitPrice: 2500,
        amount: 2500
      }
    ],
    subtotal: 35500,
    tax: 3550,
    totalAmount: 39050,
    paymentMethod: "카드"
  },
  ocrMetadata: {
    engine: "Naver OCR",
    confidence: 0.92,
    rawText: "원본 OCR 텍스트...",
    processingTime: "2.3초"
  },
  processingStatus: "COMPLETED",
  createdAt: new Date(),
  parsedAt: new Date()
});

print('MongoDB 초기화 완료!');
print('컬렉션: receiptOcrData');
print('사용자: ocr_user 생성됨');
print('영수증 매출/재료 관리용 인덱스 설정 완료');
print('생성된 인덱스:');
print('- 기본 인덱스: receiptId, userId, storeId, createdAt, processingStatus');
print('- 제품 검색용: receiptData.items.productName (텍스트 인덱스)');
print('- 매출 분석용: storeId + totalAmount + createdAt');
print('- 재료 관리용: storeId + productName (텍스트 인덱스)');