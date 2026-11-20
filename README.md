# Thiandz Client (Fabric, Minecraft 1.21.11)

## Cách 1: Build tự động trên GitHub (không cần cài gì)
1. Tạo repo mới trên github.com, upload toàn bộ thư mục này (nhớ cả thư mục .github).
2. Vào tab Actions -> chạy workflow "build" (hoặc tự chạy sau khi upload).
3. Khi xong, mở lần chạy đó -> mục Artifacts -> tải "thiandz-client-jar".
4. Giải nén, bỏ file .jar vào .minecraft/mods cùng Fabric Loader + Fabric API 0.141.1+1.21.11.

## Cách 2: Build trên máy
JDK 21 + Gradle 9.2.1 (hoặc thêm gradlew từ fabric-example-mod), rồi chạy `gradle build`.
File .jar nằm trong build/libs/.

## Phím & lệnh
- Right Shift: menu | C (giữ): zoom
- /tc add <tên>, /tc remove <tên>, /tc list  (alias /thiandz)
- Cấu hình: .minecraft/config/thiandzclient.json
