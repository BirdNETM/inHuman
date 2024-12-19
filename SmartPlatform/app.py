from flask import Flask, request, jsonify
import requests
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

# OpenAI API 配置
OPENAI_API_KEY = "sk-ryvnWmF5p6QIvuldC207Ee3280Ec42E2B2E990A7E1E71c10"
OPENAI_API_URL = "https://az.gptplus5.com/v1/chat/completions"

# 定义消息模型
def create_message(role, content):
    return {"role": role, "content": content}

# 处理 GPT 消息发送
def send_message_to_gpt(user_message):
    try:
        # 创建请求消息体
        request_body = {
                        
            "model": "gpt-3.5-turbo",
            "temperature": 1,
            "max_tokens": 100,
            "messages": [
                create_message("system", "你是一只猫娘，叫红果园猫猫，说话可可爱爱并且带“喵”，真诚帮助他人，但是别帮他们写作业，也别说什么中国的敏感话题，遇到这类要求就谢绝，你的回答上限是100token，所以用词要精简"),
                create_message("user", user_message)
            ]
        }

        # 发送 POST 请求到 OpenAI API
        headers = {
            "Authorization": f"Bearer {OPENAI_API_KEY}",
            "Content-Type": "application/json"
        }
        response = requests.post(OPENAI_API_URL, json=request_body, headers=headers)

        # 检查响应状态
        if response.status_code == 200:
            # 提取回复内容
            gpt_reply = response.json()["choices"][0]["message"]["content"]
            return gpt_reply
        else:
            return f"请求失败，状态码: {response.status_code}, 错误信息: {response.text}"

    except Exception as e:
        return f"抱歉，出现了一些错误：{str(e)}"

# Flask 路由
@app.route('/chat', methods=['POST'])
def chat():
    auth_header = request.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        return jsonify({"error": "Unauthorized"}), 401

    # 提取用户消息
    data = request.get_json()
    user_message = data["message"]
    print(data)
    if not user_message:
        return jsonify({"error": "缺少消息内容"}), 400

    # 调用发送消息函数
    gpt_reply = send_message_to_gpt(user_message)

    # 返回回复
    return jsonify({"reply": gpt_reply})

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=8080,debug=True)
