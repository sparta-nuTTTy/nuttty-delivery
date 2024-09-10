CREATE UNIQUE INDEX unique_name_idx ON p_hubs (name) WHERE is_delete = false;
CREATE UNIQUE INDEX unique_address_idx ON p_hubs (address) WHERE is_delete = false;

INSERT INTO p_hubs
(hub_id, name, address, latitude, longitude, user_id, created_at, updated_at, deleted_at, is_delete)
VALUES
    ('c2b9f964-07f7-4b73-bb02-f0cfb96ae6d0', '서울특별시 센터', '서울특별시 송파구 송파대로 55', '37.5111', '127.1048', 1, NOW(), NOW(), NULL, FALSE),
    ('dbe1f1a4-18b5-4f67-8e30-58e871e5043d', '경기 북부 센터', '경기도 고양시 덕양구 권율대로 570', '37.6741', '126.8491', 2, NOW(), NOW(), NULL, FALSE),
    ('e0b0a33c-5a05-4f7a-85b2-3a9c9f6b87b5', '경기 남부 센터', '경기도 이천시 덕평로 257-21', '37.2746', '127.4254', 3, NOW(), NOW(), NULL, FALSE),
    ('f0e0d0e8-7a05-4e89-96db-5508455f5b2d', '부산광역시 센터', '부산 동구 중앙대로 206', '35.1032', '129.0402', 4, NOW(), NOW(), NULL, FALSE),
    ('0f7b9a43-bba0-4e9e-9617-790f824b6f4d', '대구광역시 센터', '대구 북구 태평로 161', '35.8964', '128.6236', 5, NOW(), NOW(), NULL, FALSE),
    ('1d8e7c32-5e74-48b8-9e7a-08c7d2e47f2b', '인천광역시 센터', '인천 남동구 정각로 29', '37.4528', '126.7030', 6, NOW(), NOW(), NULL, FALSE),
    ('2d0e6c72-9e0a-4c6a-b32d-1b62a1f7a1e7', '광주광역시 센터', '광주 서구 내방로 111', '35.1595', '126.8526', 7, NOW(), NOW(), NULL, FALSE),
    ('3e8a3f24-fd95-4b3a-b6de-8c3c97345d14', '대전광역시 센터', '대전 서구 둔산로 100', '36.3504', '127.3845', 8, NOW(), NOW(), NULL, FALSE),
    ('4b5d7c7e-8d5b-45cf-a8b7-3f8e8fd1d452', '울산광역시 센터', '울산 남구 중앙로 201', '35.5397', '129.3110', 9, NOW(), NOW(), NULL, FALSE),
    ('5c6b84ee-1e6d-4c6a-97b5-16cb3e1e5e9b', '세종특별자치시 센터', '세종특별자치시 한누리대로 2130', '36.4793', '127.2896', 10, NOW(), NOW(), NULL, FALSE),
    ('6d7e95b3-f14e-4f09-aeb7-d92a8b2f9d88', '강원특별자치도 센터', '강원특별자치도 춘천시 중앙로 1', '37.8821', '127.7294', 11, NOW(), NOW(), NULL, FALSE),
    ('7e8f16b4-29f4-4d3a-84a0-2d4c19d1f98d', '충청북도 센터', '충북 청주시 상당구 상당로 82', '36.6336', '127.4898', 12, NOW(), NOW(), NULL, FALSE),
    ('8f9b27c5-4b36-4f2b-9d42-2b7b0d28e1c7', '충청남도 센터', '충남 홍성군 홍북읍 충남대로 21', '36.6425', '126.6794', 13, NOW(), NOW(), NULL, FALSE),
    ('9a0c3f6e-d054-4a04-bb6e-3c3b9b3e7889', '전북특별자치도 센터', '전북특별자치도 전주시 완산구 효자로 225', '35.8213', '127.1532', 14, NOW(), NOW(), NULL, FALSE),
    ('ab1d4e54-8e7e-4b7b-8b73-fc0346d1b63d', '전라남도 센터', '전남 무안군 삼향읍 오룡길 1', '34.9610', '126.7270', 15, NOW(), NOW(), NULL, FALSE),
    ('bc2e5f64-9259-4e37-bc0c-26d69d4c2f90', '경상북도 센터', '경북 안동시 풍천면 도청대로 455', '36.5667', '128.7208', 16, NOW(), NOW(), NULL, FALSE),
    ('cd3f6d85-a08c-4a4e-87a3-1a65e4d1f84f', '경상남도 센터', '경남 창원시 의창구 중앙대로 300', '35.2416', '128.6808', 17, NOW(), NOW(), NULL, FALSE);