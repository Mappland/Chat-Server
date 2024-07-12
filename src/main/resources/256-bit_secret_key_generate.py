import secrets

def generate_256_bit_secret_key():
    # 生成32字节（256位）的密钥
    secret_key = secrets.token_hex(32)
    return secret_key

# 调用函数并打印生成的密钥
secret_key = generate_256_bit_secret_key()
print(f"Your 256-bit secret key: {secret_key}")
