"""
文件名: main.py
说明: 移动端文档布局解析与翻译APP (Kivy)
依赖: kivy, requests, pillow (用于图像处理)
"""

import os
import base64
import time
import json
import random
import hashlib
import requests
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.label import Label
from kivy.uix.textinput import TextInput
from kivy.uix.image import Image
from kivy.uix.progressbar import ProgressBar
from kivy.uix.scrollview import ScrollView
from kivy.uix.popup import Popup
from kivy.uix.filechooser import FileChooserListView
from kivy.clock import Clock
from kivy.core.window import Window
from kivy.graphics.texture import Texture
from kivy.utils import platform
from PIL import Image as PILImage
import io

# ========== 配置 ==========
API_URL = "https://sdza0dg6f8x0mad4.aistudio-app.com/layout-parsing"
TOKEN = "9a2c796ee50ca0accd88e8314230f72ed5167639"
BAIDU_APPID = "20260310002569969"          # 你的百度翻译APP ID
BAIDU_SECRET_KEY = "qlZ9vjpy0Ib69jjd0IIX"     # 你的百度翻译密钥
# ==========================

class MainLayout(BoxLayout):
    def __init__(self, **kwargs):
        super().__init__(orientation='vertical', **kwargs)
        self.selected_file = None
        self.current_result = None
        self.current_markdown = ""

        # 顶部按钮栏
        btn_layout = BoxLayout(size_hint_y=0.1, spacing=5)
        self.camera_btn = Button(text='拍照')
        self.camera_btn.bind(on_press=self.take_picture)
        self.gallery_btn = Button(text='从相册选择')
        self.gallery_btn.bind(on_press=self.choose_from_gallery)
        self.process_btn = Button(text='开始处理')
        self.process_btn.bind(on_press=self.process_file)
        self.process_btn.disabled = True
        self.translate_btn = Button(text='翻译到中文')
        self.translate_btn.bind(on_press=self.translate_markdown)
        self.translate_btn.disabled = True
        btn_layout.add_widget(self.camera_btn)
        btn_layout.add_widget(self.gallery_btn)
        btn_layout.add_widget(self.process_btn)
        btn_layout.add_widget(self.translate_btn)
        self.add_widget(btn_layout)

        # 图像预览区
        self.image_preview = Image(size_hint_y=0.3, allow_stretch=True)
        self.add_widget(self.image_preview)

        # 选项区域（简单复选框）
        opt_layout = BoxLayout(size_hint_y=0.1, spacing=10)
        self.opt_orient = Button(text='检测方向', background_color=(0.5,0.5,0.5,1))
        self.opt_orient.bind(on_press=self.toggle_orient)
        self.opt_unwarp = Button(text='文档展平', background_color=(0.5,0.5,0.5,1))
        self.opt_unwarp.bind(on_press=self.toggle_unwarp)
        self.opt_chart = Button(text='图表识别', background_color=(0.5,0.5,0.5,1))
        self.opt_chart.bind(on_press=self.toggle_chart)
        opt_layout.add_widget(self.opt_orient)
        opt_layout.add_widget(self.opt_unwarp)
        opt_layout.add_widget(self.opt_chart)
        self.add_widget(opt_layout)

        # 进度条
        self.progress = ProgressBar(max=100, value=0, size_hint_y=0.05)
        self.add_widget(self.progress)

        # 识别结果显示区（可滚动）
        scroll = ScrollView(size_hint_y=0.25)
        self.result_text = TextInput(text='识别结果将显示在这里', readonly=True, font_size=14)
        scroll.add_widget(self.result_text)
        self.add_widget(scroll)

        # 翻译结果显示区
        scroll2 = ScrollView(size_hint_y=0.2)
        self.trans_text = TextInput(text='翻译结果将显示在这里', readonly=True, font_size=14)
        scroll2.add_widget(self.trans_text)
        self.add_widget(scroll2)

        # 状态标签
        self.status_label = Label(text='就绪', size_hint_y=0.05)
        self.add_widget(self.status_label)

        # 选项状态
        self.orient_state = False
        self.unwarp_state = False
        self.chart_state = False

    def toggle_orient(self, instance):
        self.orient_state = not self.orient_state
        instance.background_color = (0,1,0,1) if self.orient_state else (0.5,0.5,0.5,1)

    def toggle_unwarp(self, instance):
        self.unwarp_state = not self.unwarp_state
        instance.background_color = (0,1,0,1) if self.unwarp_state else (0.5,0.5,0.5,1)

    def toggle_chart(self, instance):
        self.chart_state = not self.chart_state
        instance.background_color = (0,1,0,1) if self.chart_state else (0.5,0.5,0.5,1)

    def take_picture(self, instance):
        """调用相机拍照（仅Android支持）"""
        if platform == 'android':
            from android.permissions import request_permissions, Permission
            request_permissions([Permission.CAMERA, Permission.WRITE_EXTERNAL_STORAGE])
            from plyer import camera
            filepath = os.path.join(self.get_app_dir(), 'temp.jpg')
            camera.take_picture(filename=filepath, on_complete=self.camera_callback)
        else:
            self.status_label.text = "拍照仅支持Android设备"

    def camera_callback(self, filepath):
        if filepath:
            self.load_image(filepath)

    def choose_from_gallery(self, instance):
        """从相册选择图片"""
        if platform == 'android':
            from android.permissions import request_permissions, Permission
            request_permissions([Permission.READ_EXTERNAL_STORAGE])
            from plyer import filechooser
            filechooser.open_file(on_selection=self.gallery_callback, filters=['*.png','*.jpg','*.jpeg'])
        else:
            # 桌面调试时使用FileChooser
            content = FileChooserListView(path=os.path.expanduser('~'), filters=['*.png','*.jpg','*.jpeg'])
            popup = Popup(title='选择图片', content=content, size_hint=(0.9,0.9))
            content.bind(on_submit=lambda x, y, z: self.load_image(y[0]) or popup.dismiss())
            popup.open()

    def gallery_callback(self, selection):
        if selection:
            self.load_image(selection[0])

    def load_image(self, filepath):
        self.selected_file = filepath
        self.process_btn.disabled = False
        self.translate_btn.disabled = True
        # 显示图片预览
        try:
            pil_img = PILImage.open(filepath)
            # 缩放以适合预览
            pil_img.thumbnail((Window.width, 300))
            texture = self.pil_to_texture(pil_img)
            self.image_preview.texture = texture
            self.status_label.text = f"已加载: {os.path.basename(filepath)}"
        except Exception as e:
            self.status_label.text = f"加载图片失败: {str(e)}"

    def pil_to_texture(self, pil_img):
        """将PIL图像转换为Kivy纹理"""
        if pil_img.mode != 'RGB':
            pil_img = pil_img.convert('RGB')
        data = pil_img.tobytes()
        texture = Texture.create(size=pil_img.size, colorfmt='rgb')
        texture.blit_buffer(data, colorfmt='rgb', bufferfmt='ubyte')
        texture.flip_vertical()
        return texture

    def process_file(self, instance):
        if not self.selected_file:
            self.status_label.text = "请先选择图片"
            return
        self.status_label.text = "正在处理..."
        self.progress.value = 20
        # 启动后台线程调用API（避免阻塞UI）
        from threading import Thread
        Thread(target=self.api_call).start()

    def api_call(self):
        try:
            with open(self.selected_file, "rb") as f:
                file_bytes = f.read()
                file_data = base64.b64encode(file_bytes).decode("ascii")

            ext = os.path.splitext(self.selected_file)[1].lower()
            file_type = 0 if ext == '.pdf' else 1

            headers = {
                "Authorization": f"token {TOKEN}",
                "Content-Type": "application/json"
            }

            payload = {
                "file": file_data,
                "fileType": file_type,
                "useDocOrientationClassify": self.orient_state,
                "useDocUnwarping": self.unwarp_state,
                "useChartRecognition": self.chart_state,
            }

            response = requests.post(API_URL, json=payload, headers=headers, timeout=60)
            if response.status_code != 200:
                Clock.schedule_once(lambda dt: self.show_error(f"API请求失败: {response.status_code}"))
                return

            result = response.json()
            if "result" not in result:
                Clock.schedule_once(lambda dt: self.show_error("响应中缺少result字段"))
                return

            self.current_result = result["result"]
            Clock.schedule_once(lambda dt: self.display_results(), 0)
            self.progress.value = 80

        except Exception as e:
            Clock.schedule_once(lambda dt: self.show_error(str(e)))
        finally:
            self.progress.value = 100
            Clock.schedule_once(lambda dt: self.progress_sleep(), 0.5)

    def progress_sleep(self):
        self.progress.value = 0
        self.status_label.text = "处理完成"

    def show_error(self, msg):
        self.status_label.text = f"错误: {msg}"
        self.progress.value = 0

    def display_results(self):
        if not self.current_result:
            return
        parsing_results = self.current_result.get("layoutParsingResults", [])
        if not parsing_results:
            self.result_text.text = "无解析结果"
            return
        first = parsing_results[0]
        self.current_markdown = first.get("markdown", {}).get("text", "")
        self.result_text.text = self.current_markdown
        if BAIDU_APPID and BAIDU_SECRET_KEY:
            self.translate_btn.disabled = False

        # 尝试显示outputImages中的第一张图
        output_images = first.get("outputImages", {})
        if output_images:
            first_img_url = next(iter(output_images.values()))
            self.download_image(first_img_url)

    def download_image(self, url):
        try:
            resp = requests.get(url, timeout=10)
            if resp.status_code == 200:
                img_data = resp.content
                pil_img = PILImage.open(io.BytesIO(img_data))
                texture = self.pil_to_texture(pil_img)
                Clock.schedule_once(lambda dt: self.update_preview(texture), 0)
        except Exception as e:
            print(f"下载图像失败: {e}")

    def update_preview(self, texture):
        self.image_preview.texture = texture

    def translate_markdown(self, instance):
        if not self.current_markdown:
            self.status_label.text = "无文本可翻译"
            return
        self.translate_btn.disabled = True
        self.status_label.text = "翻译中..."
        from threading import Thread
        Thread(target=self.translate_call).start()

    def translate_call(self):
        if not BAIDU_APPID or not BAIDU_SECRET_KEY:
            Clock.schedule_once(lambda dt: self.translate_error("请配置百度翻译密钥"))
            return
        try:
            # 简单分割（1500字符）
            chunks = self.split_text(self.current_markdown, 1500)
            translated_parts = []
            for chunk in chunks:
                salt = random.randint(32768, 65536)
                sign_str = BAIDU_APPID + chunk + str(salt) + BAIDU_SECRET_KEY
                sign = hashlib.md5(sign_str.encode()).hexdigest()

                url = "https://fanyi-api.baidu.com/api/trans/vip/translate"
                params = {
                    'q': chunk,
                    'from': 'auto',
                    'to': 'zh',
                    'appid': BAIDU_APPID,
                    'salt': salt,
                    'sign': sign
                }
                resp = requests.get(url, params=params, timeout=10)
                result = resp.json()
                if 'trans_result' in result:
                    trans = '\n'.join([item['dst'] for item in result['trans_result']])
                    translated_parts.append(trans)
                else:
                    Clock.schedule_once(lambda dt, msg=result.get('error_msg','未知错误'): self.translate_error(msg))
                    return
            final_trans = '\n'.join(translated_parts)
            Clock.schedule_once(lambda dt: self.translate_success(final_trans), 0)
        except Exception as e:
            Clock.schedule_once(lambda dt: self.translate_error(str(e)), 0)

    def split_text(self, text, max_len=1500):
        chunks = []
        current = ""
        sentences = text.replace('\n', ' ').split('. ')
        for sent in sentences:
            if len(current) + len(sent) < max_len:
                current += sent + '. '
            else:
                if current:
                    chunks.append(current.strip())
                current = sent + '. '
        if current:
            chunks.append(current.strip())
        return chunks

    def translate_success(self, trans_text):
        self.trans_text.text = trans_text
        self.translate_btn.disabled = False
        self.status_label.text = "翻译完成"

    def translate_error(self, msg):
        self.trans_text.text = f"翻译失败: {msg}"
        self.translate_btn.disabled = False
        self.status_label.text = "翻译失败"

class MainApp(App):
    def build(self):
        return MainLayout()

if __name__ == '__main__':
    MainApp().run()