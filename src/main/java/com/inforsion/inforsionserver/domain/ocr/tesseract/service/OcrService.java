package com.inforsion.inforsionserver.domain.ocr.tesseract.service;

/*
네이버 OCR 사용
 */
//import com.inforsion.inforsionserver.domain.ocr.tesseract.dto.response.OcrResponse;
//import lombok.extern.slf4j.Slf4j;
//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.awt.image.ConvolveOp;
//import java.awt.image.Kernel;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//// Tesseract OCR 서비스 비활성화 - Naver OCR 사용
///*
//@Slf4j
//@Service
//public class OcrService {
//
//    // Tesseract 사용 중단 - 네이버 OCR로 변경
//    // private final ITesseract tesseract;
//
//    public OcrService() {
//        // this.tesseract = new Tesseract();
//
//        /*
//        try {
//            // 환경별 tessdata 경로 설정
//            String tessdataPath = findTessdataPath();
//            if (tessdataPath != null) {
//                tesseract.setDatapath(tessdataPath);
//                log.info("Tesseract 데이터 경로 설정: {}", tessdataPath);
//            }
//
//            // 한국어 우선, 영어 보조로 설정
//            tesseract.setLanguage("kor");
//
//            // OCR 엔진 모드 설정 (1: Neural nets LSTM engine only)
//            tesseract.setOcrEngineMode(1);
//
//            // 페이지 세그멘테이션 모드 (6: Uniform block of text)
//            tesseract.setPageSegMode(6);
//
//            // 한국어 중심 문자 화이트리스트 설정
//            tesseract.setTessVariable("tessedit_char_whitelist",
//                "가나다라마바사아자차카타파하거너더러머버서어저처커터퍼허고노도로모보소오조초코토포호구누두루무부수우주추쿠투푸후그느드르므브스으즈츠크트프흐기니디리미비시이지치키티피히0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,:-()/ ");
//
//            // DPI 설정
//            tesseract.setTessVariable("user_defined_dpi", "300");
//
//            // 한국어 인식 향상을 위한 추가 설정
//            tesseract.setTessVariable("tessedit_pageseg_mode", "6");
//            tesseract.setTessVariable("preserve_interword_spaces", "1");
//            tesseract.setTessVariable("textord_really_old_xheight", "1");
//            tesseract.setTessVariable("textord_min_linesize", "1.25");
//
//            log.info("Tesseract OCR 초기화 완료 - 언어: kor, 엔진: LSTM, PSM: 6");
//        } catch (Exception e) {
//            log.warn("Tesseract 한국어 설정 실패, 영어로 대체. 오류: {}", e.getMessage());
//            try {
//                // 영어로 fallback
//                String tessdataPath = findTessdataPath();
//                if (tessdataPath != null) {
//                    tesseract.setDatapath(tessdataPath);
//                }
//                tesseract.setLanguage("eng");
//                tesseract.setOcrEngineMode(1);
//                tesseract.setPageSegMode(3);
//                log.info("Tesseract OCR 영어 모드로 초기화 완료");
//            } catch (Exception ex) {
//                log.error("Tesseract 초기화 완전 실패: {}", ex.getMessage());
//                throw new RuntimeException("Tesseract OCR 초기화 실패", ex);
//            }
//        }
//
//
//        // 네이버 OCR 사용으로 변경
//        log.info("네이버 OCR 서비스로 변경됨");
//    }
//
//    /**
//     * 환경별 tessdata 경로 찾기
//     */
//    private String findTessdataPath() {
//        // 가능한 tessdata 경로들 (우선순위 순)
//        String[] possiblePaths = {
//            "/usr/share/tessdata",                          // Docker 환경 (일반적)
//            "/usr/share/tesseract-ocr/4.00/tessdata",      // Ubuntu/Debian
//            "/usr/share/tesseract-ocr/5.00/tessdata",      // 최신 버전
//            "/opt/homebrew/share/tessdata",                 // macOS Homebrew
//            "/usr/local/share/tessdata",                    // 일반적인 Unix
//            "C:/Program Files/Tesseract-OCR/tessdata"      // Windows
//        };
//
//        for (String path : possiblePaths) {
//            java.io.File dir = new java.io.File(path);
//            if (dir.exists() && dir.isDirectory()) {
//                log.info("tessdata 경로 발견: {}", path);
//                return path;
//            }
//        }
//
//        log.warn("tessdata 경로를 찾을 수 없습니다. 시스템 기본값 사용");
//        return null;
//    }
//
//    public OcrResponse processImage(MultipartFile file) throws IOException, TesseractException {
//        long startTime = System.currentTimeMillis();
//
//        log.info("OCR 처리 시작 - 파일명: {}, 크기: {} bytes", file.getOriginalFilename(), file.getSize());
//
//        // MultipartFile을 BufferedImage로 변환
//        BufferedImage originalImage = ImageIO.read(file.getInputStream());
//
//        if (originalImage == null) {
//            throw new IllegalArgumentException("이미지 파일을 읽을 수 없습니다: " + file.getOriginalFilename());
//        }
//
//        // 이미지 전처리 수행
//        BufferedImage preprocessedImage = preprocessImageForOCR(originalImage);
//
//        log.info("이미지 전처리 완료 - 원본: {}x{}, 처리후: {}x{}",
//                originalImage.getWidth(), originalImage.getHeight(),
//                preprocessedImage.getWidth(), preprocessedImage.getHeight());
//
//        // OCR 실행 (Tesseract 사용 중단)
//        // String extractedText = tesseract.doOCR(preprocessedImage);
//        String extractedText = "네이버 OCR로 변경 예정"; // 임시
//
//        // 텍스트를 줄별로 분리 (빈 줄 제거)
//        List<String> extractedLines = Arrays.stream(extractedText.split("\n"))
//                .map(String::trim)
//                .filter(line -> !line.isEmpty())
//                .collect(Collectors.toList());
//
//        long processingTime = System.currentTimeMillis() - startTime;
//
//        log.info("OCR 처리 완료 - 소요시간: {}ms, 추출된 줄 수: {}", processingTime, extractedLines.size());
//
//        // 콘솔에 OCR 결과 출력 (테스트용)
//        System.out.println("=== OCR 처리 결과 ===");
//        System.out.println("파일명: " + file.getOriginalFilename());
//        System.out.println("파일 크기: " + file.getSize() + " bytes");
//        System.out.println("이미지 크기: " + preprocessedImage.getWidth() + "x" + preprocessedImage.getHeight() + " pixels");
//        System.out.println("처리 시간: " + processingTime + "ms");
//        System.out.println("--- 추출된 텍스트 ---");
//        System.out.println(extractedText);
//        System.out.println("--- 줄별 텍스트 ---");
//        for (int i = 0; i < extractedLines.size(); i++) {
//            System.out.println((i + 1) + ": " + extractedLines.get(i));
//        }
//        System.out.println("==================");
//
//        return OcrResponse.builder()
//                .originalFileName(file.getOriginalFilename())
//                .extractedText(extractedText)
//                .extractedLines(extractedLines)
//                .processingTimeMs(processingTime)
//                .fileSizeBytes(file.getSize())
//                .imageWidth(preprocessedImage.getWidth())
//                .imageHeight(preprocessedImage.getHeight())
//                .build();
//    }
//
//    /**
//     * OCR을 위한 이미지 전처리
//     * - 그레이스케일 변환
//     * - 해상도 향상 (스케일업)
//     * - 노이즈 제거
//     * - 대비 향상
//     * - 이진화 (Optional)
//     */
//    private BufferedImage preprocessImageForOCR(BufferedImage originalImage) {
//        log.info("이미지 전처리 시작");
//
//        // 1. 그레이스케일 변환
//        BufferedImage grayImage = convertToGrayscale(originalImage);
//        log.debug("그레이스케일 변환 완료");
//
//        // 2. 해상도 향상 (작은 이미지의 경우)
//        BufferedImage scaledImage = scaleImageIfNeeded(grayImage);
//        log.debug("해상도 스케일링 완료");
//
//        // 3. 노이즈 제거 (가우시안 블러)
//        BufferedImage denoisedImage = removeNoise(scaledImage);
//        log.debug("노이즈 제거 완료");
//
//        // 4. 대비 향상
//        BufferedImage enhancedImage = enhanceContrast(denoisedImage);
//        log.debug("대비 향상 완료");
//
//        // 5. 샤프닝 필터 적용
//        BufferedImage sharpenedImage = applySharpenFilter(enhancedImage);
//        log.debug("샤프닝 필터 적용 완료");
//
//        // 6. 선택적 이진화 (텍스트가 명확하지 않은 경우)
//        BufferedImage finalImage = adaptiveThreshold(sharpenedImage);
//        log.debug("적응형 이진화 완료");
//
//        log.info("이미지 전처리 완료");
//        return finalImage;
//    }
//
//    /**
//     * 그레이스케일 변환
//     */
//    private BufferedImage convertToGrayscale(BufferedImage original) {
//        BufferedImage grayImage = new BufferedImage(
//                original.getWidth(), original.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        Graphics2D g2d = grayImage.createGraphics();
//        g2d.drawImage(original, 0, 0, null);
//        g2d.dispose();
//        return grayImage;
//    }
//
//    /**
//     * 이미지 크기가 작은 경우 스케일 업
//     */
//    private BufferedImage scaleImageIfNeeded(BufferedImage image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        // 최소 크기 기준 (300 DPI 기준으로 텍스트 인식에 적합)
//        int minWidth = 1000;
//        int minHeight = 800;
//
//        if (width < minWidth || height < minHeight) {
//            double scaleX = (double) minWidth / width;
//            double scaleY = (double) minHeight / height;
//            double scale = Math.max(scaleX, scaleY);
//
//            // 최대 3배까지만 확대
//            scale = Math.min(scale, 3.0);
//
//            int newWidth = (int) (width * scale);
//            int newHeight = (int) (height * scale);
//
//            BufferedImage scaledImage = new BufferedImage(newWidth, newHeight, image.getType());
//            Graphics2D g2d = scaledImage.createGraphics();
//            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
//            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//            g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
//            g2d.dispose();
//
//            log.info("이미지 스케일 업: {}x{} -> {}x{} (scale: {:.2f})", width, height, newWidth, newHeight, scale);
//            return scaledImage;
//        }
//
//        return image;
//    }
//
//    /**
//     * 가우시안 블러를 이용한 노이즈 제거
//     */
//    private BufferedImage removeNoise(BufferedImage image) {
//        float[] kernel = {
//            1f/16f, 2f/16f, 1f/16f,
//            2f/16f, 4f/16f, 2f/16f,
//            1f/16f, 2f/16f, 1f/16f
//        };
//
//        ConvolveOp op = new ConvolveOp(new Kernel(3, 3, kernel), ConvolveOp.EDGE_NO_OP, null);
//        return op.filter(image, null);
//    }
//
//    /**
//     * 대비 향상
//     */
//    private BufferedImage enhanceContrast(BufferedImage image) {
//        BufferedImage enhancedImage = new BufferedImage(
//                image.getWidth(), image.getHeight(), image.getType());
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int rgb = image.getRGB(x, y);
//                int gray = rgb & 0xFF; // 그레이스케일 값 추출
//
//                // 대비 향상 (S-curve 적용)
//                double normalized = gray / 255.0;
//                double enhanced = Math.pow(normalized, 0.7); // 감마 보정
//                int newGray = Math.max(0, Math.min(255, (int) (enhanced * 255)));
//
//                int newRgb = (newGray << 16) | (newGray << 8) | newGray;
//                enhancedImage.setRGB(x, y, newRgb);
//            }
//        }
//
//        return enhancedImage;
//    }
//
//    /**
//     * 샤프닝 필터 적용 (텍스트 경계 강화)
//     */
//    private BufferedImage applySharpenFilter(BufferedImage image) {
//        float[] sharpenKernel = {
//            0.0f, -1.0f, 0.0f,
//            -1.0f, 5.0f, -1.0f,
//            0.0f, -1.0f, 0.0f
//        };
//
//        ConvolveOp sharpenOp = new ConvolveOp(new Kernel(3, 3, sharpenKernel), ConvolveOp.EDGE_NO_OP, null);
//        return sharpenOp.filter(image, null);
//    }
//
//    /**
//     * 적응형 이진화 (Otsu's method 근사)
//     */
//    private BufferedImage adaptiveThreshold(BufferedImage image) {
//        // 히스토그램 계산
//        int[] histogram = new int[256];
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int gray = image.getRGB(x, y) & 0xFF;
//                histogram[gray]++;
//            }
//        }
//
//        // Otsu's method로 최적 임계값 찾기
//        int total = image.getWidth() * image.getHeight();
//        float sum = 0;
//        for (int i = 0; i < 256; i++) {
//            sum += i * histogram[i];
//        }
//
//        float sumB = 0;
//        int wB = 0;
//        int wF = 0;
//        float varMax = 0;
//        int threshold = 0;
//
//        for (int i = 0; i < 256; i++) {
//            wB += histogram[i];
//            if (wB == 0) continue;
//
//            wF = total - wB;
//            if (wF == 0) break;
//
//            sumB += i * histogram[i];
//
//            float mB = sumB / wB;
//            float mF = (sum - sumB) / wF;
//
//            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);
//
//            if (varBetween > varMax) {
//                varMax = varBetween;
//                threshold = i;
//            }
//        }
//
//        // 이진화 적용 (너무 극단적이지 않게 조정)
//        threshold = Math.max(100, Math.min(180, threshold)); // 임계값 범위 제한
//
//        BufferedImage binaryImage = new BufferedImage(
//                image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//
//        for (int y = 0; y < image.getHeight(); y++) {
//            for (int x = 0; x < image.getWidth(); x++) {
//                int gray = image.getRGB(x, y) & 0xFF;
//                int binary = gray > threshold ? 255 : 0;
//                int rgb = (binary << 16) | (binary << 8) | binary;
//                binaryImage.setRGB(x, y, rgb);
//            }
//        }
//
//        log.info("적응형 이진화 임계값: {}", threshold);
//        return binaryImage;
//    }
//
//    /**
//     * Tesseract 설정 정보 확인
//     */
//    public void printTesseractInfo() {
//        System.out.println("=== Tesseract 설정 정보 ===");
//        System.out.println("Tesseract OCR이 초기화되었습니다.");
//        System.out.println("언어: kor (한국어 우선)");
//        System.out.println("이미지 전처리: 활성화됨");
//        System.out.println("- 그레이스케일 변환");
//        System.out.println("- 해상도 향상 (필요시 최대 3배)");
//        System.out.println("- 노이즈 제거 (가우시안 블러)");
//        System.out.println("- 대비 향상 (감마 보정)");
//        System.out.println("- 샤프닝 필터 (텍스트 경계 강화)");
//        System.out.println("- 적응형 이진화 (Otsu's method)");
//        System.out.println("Tesseract 설정:");
//        System.out.println("- PSM: 6 (Uniform block of text)");
//        System.out.println("- DPI: 300");
//        System.out.println("- 문자 화이트리스트: 한글 우선+영문+숫자+기본기호");
//        System.out.println("========================");
//    }
//}
