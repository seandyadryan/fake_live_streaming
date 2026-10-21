"""Maintain native Android chat translations; no network translation at runtime.

Each row contains: welcome | eight general comments | music | story | daily |
self label | input hint | send accessibility label | heading.
"""
from pathlib import Path
from xml.sax.saxutils import escape

ROOT = Path(__file__).resolve().parents[1] / "app/src/main/res"
TRANSLATIONS = {
    "": [
        "Welcome to the LIVE! 👋",
        "Love the energy here ✨|Hello from my little corner of the world!|This is such a good vibe|You make this look so natural|Tell us more!|That made my day ♥|Just joined. What did I miss?|Keep going, you've got this!",
        "What inspired your creative process?", "What happened next? I'm invested!", "What's one thing you're excited about today?",
        "You", "Say something…", "Send comment", "Live chat"],
    "in": [
        "Selamat datang di LIVE! 👋",
        "Suka banget energinya di sini ✨|Halo dari sudut kecil duniaku!|Suasananya asyik banget|Kamu terlihat natural banget|Cerita lagi dong!|Ini bikin hariku lebih baik ♥|Baru gabung. Tadi ada apa?|Lanjut terus, kamu pasti bisa!",
        "Apa inspirasi di balik karyamu?", "Terus apa yang terjadi? Aku penasaran!", "Apa yang bikin kamu semangat hari ini?",
        "Anda", "Tulis sesuatu…", "Kirim komentar", "Obrolan live"],
    "ms": [
        "Selamat datang ke LIVE! 👋",
        "Suka tenaga di sini ✨|Helo dari sudut kecil dunia saya!|Suasananya memang seronok|Nampak sangat bersahaja|Ceritalah lagi!|Ini menceriakan hari saya ♥|Baru masuk. Apa yang saya terlepas?|Teruskan, awak pasti boleh!",
        "Apa yang memberi inspirasi kepada karya awak?", "Apa yang berlaku selepas itu? Saya nak tahu!", "Apa yang buat awak teruja hari ini?",
        "Anda", "Tulis sesuatu…", "Hantar komen", "Sembang langsung"],
    "es": [
        "¡Bienvenidos al LIVE! 👋",
        "Me encanta esta energía ✨|¡Hola desde mi rincón del mundo!|Qué buen ambiente|Lo haces parecer muy natural|¡Cuéntanos más!|Esto me alegró el día ♥|Acabo de llegar. ¿Qué me perdí?|¡Sigue así, tú puedes!",
        "¿Qué inspiró tu proceso creativo?", "¿Qué pasó después? ¡Quiero saber más!", "¿Qué te ilusiona hoy?",
        "Tú", "Escribe algo…", "Enviar comentario", "Chat en directo"],
    "fr": [
        "Bienvenue sur le LIVE ! 👋",
        "J'adore cette énergie ✨|Bonjour de mon petit coin du monde !|Quelle bonne ambiance|Tu as l'air tellement à l'aise|Raconte-nous la suite !|Ça a égayé ma journée ♥|Je viens d'arriver. Qu'ai-je manqué ?|Continue, tu vas y arriver !",
        "Qu'est-ce qui inspire ta créativité ?", "Et ensuite ? Je veux connaître la suite !", "Qu'est-ce qui te réjouit aujourd'hui ?",
        "Vous", "Écrivez quelque chose…", "Envoyer un commentaire", "Discussion en direct"],
    "de": [
        "Willkommen im LIVE! 👋",
        "Ich liebe die Energie hier ✨|Hallo aus meinem kleinen Teil der Welt!|Hier ist eine tolle Stimmung|Du wirkst so natürlich|Erzähl uns mehr!|Das hat meinen Tag verschönert ♥|Bin gerade dazugekommen. Was habe ich verpasst?|Weiter so, du schaffst das!",
        "Was inspiriert deinen kreativen Prozess?", "Was ist dann passiert? Ich will mehr wissen!", "Worauf freust du dich heute?",
        "Du", "Schreib etwas…", "Kommentar senden", "Livechat"],
    "it": [
        "Benvenuti nella LIVE! 👋",
        "Adoro questa energia ✨|Ciao dal mio piccolo angolo di mondo!|Che bella atmosfera|Sembri davvero a tuo agio|Raccontaci di più!|Mi hai rallegrato la giornata ♥|Sono appena arrivato. Cosa mi sono perso?|Continua così, ce la puoi fare!",
        "Cosa ispira il tuo processo creativo?", "E poi cosa è successo? Voglio saperne di più!", "Cosa ti entusiasma oggi?",
        "Tu", "Scrivi qualcosa…", "Invia commento", "Chat dal vivo"],
    "pt": [
        "Boas-vindas à LIVE! 👋",
        "Adoro essa energia ✨|Olá do meu cantinho do mundo!|Que clima bom por aqui|Você parece tão à vontade|Conta mais pra gente!|Isso alegrou meu dia ♥|Acabei de entrar. O que perdi?|Continue assim, você consegue!",
        "O que inspira seu processo criativo?", "O que aconteceu depois? Quero saber mais!", "O que te anima hoje?",
        "Você", "Escreva algo…", "Enviar comentário", "Chat ao vivo"],
    "ar": [
        "أهلاً بكم في البث المباشر! 👋",
        "أحب هذه الأجواء ✨|مرحباً من ركني الصغير في العالم!|الأجواء هنا رائعة|تبدو مرتاحاً وطبيعياً جداً|أخبرنا بالمزيد!|هذا أسعد يومي ♥|انضممت للتو. ماذا فاتني؟|واصل، أنت قادر على ذلك!",
        "ما الذي يلهم إبداعك؟", "وماذا حدث بعد ذلك؟ أريد معرفة المزيد!", "ما الشيء الذي يحمسك اليوم؟",
        "أنت", "اكتب شيئاً…", "إرسال تعليق", "الدردشة المباشرة"],
    "hi": [
        "लाइव में आपका स्वागत है! 👋",
        "यहाँ की ऊर्जा बहुत अच्छी है ✨|दुनिया के मेरे छोटे से कोने से नमस्ते!|यहाँ का माहौल बहुत अच्छा है|आप बहुत सहज लग रहे हैं|हमें और बताइए!|इससे मेरा दिन बन गया ♥|अभी जुड़ा हूँ। क्या छूट गया?|ऐसे ही जारी रखिए, आप कर सकते हैं!",
        "आपकी रचनात्मकता को प्रेरणा कहाँ से मिलती है?", "फिर क्या हुआ? मैं और जानना चाहता हूँ!", "आज आप किस बात को लेकर उत्साहित हैं?",
        "आप", "कुछ लिखें…", "टिप्पणी भेजें", "लाइव चैट"],
    "bn": [
        "লাইভে স্বাগতম! 👋",
        "এখানকার প্রাণশক্তি দারুণ ✨|পৃথিবীর আমার ছোট্ট কোণ থেকে শুভেচ্ছা!|পরিবেশটা খুব ভালো লাগছে|আপনাকে খুব স্বাভাবিক লাগছে|আরও বলুন!|এটা আমার দিনটা ভালো করে দিল ♥|এইমাত্র যোগ দিলাম। কী বাদ পড়ল?|চালিয়ে যান, আপনি পারবেন!",
        "আপনার সৃজনশীলতার অনুপ্রেরণা কী?", "তারপর কী হলো? আরও জানতে চাই!", "আজ কোন বিষয়টি আপনাকে উৎসাহ দিচ্ছে?",
        "আপনি", "কিছু লিখুন…", "মন্তব্য পাঠান", "লাইভ চ্যাট"],
    "ur": [
        "لائیو میں خوش آمدید! 👋",
        "یہاں کی توانائی بہت اچھی ہے ✨|دنیا کے میرے چھوٹے سے کونے سے سلام!|یہاں کا ماحول بہت اچھا ہے|آپ بہت پُرسکون لگ رہے ہیں|ہمیں مزید بتائیں!|اس نے میرا دن خوشگوار بنا دیا ♥|ابھی شامل ہوا ہوں۔ کیا رہ گیا؟|جاری رکھیں، آپ کر سکتے ہیں!",
        "آپ کی تخلیقی صلاحیت کو کس چیز سے تحریک ملتی ہے؟", "پھر کیا ہوا؟ میں مزید جاننا چاہتا ہوں!", "آج آپ کس چیز کے لیے پُرجوش ہیں؟",
        "آپ", "کچھ لکھیں…", "تبصرہ بھیجیں", "لائیو چیٹ"],
    "ja": [
        "LIVEへようこそ！ 👋",
        "この雰囲気が大好き ✨|世界の片隅からこんにちは！|すごくいい雰囲気ですね|とても自然で素敵です|もっと聞かせて！|おかげでいい一日になった ♥|今来ました。何の話ですか？|その調子、きっとできる！",
        "創作のインスピレーションはどこから？", "それでどうなったの？続きが気になる！", "今日は何を楽しみにしていますか？",
        "あなた", "コメントを入力…", "コメントを送信", "ライブチャット"],
    "ko": [
        "라이브에 오신 것을 환영해요! 👋",
        "여기 에너지가 정말 좋아요 ✨|세계 한편에서 인사해요!|분위기가 정말 좋네요|정말 자연스러워 보여요|더 이야기해 주세요!|덕분에 기분 좋은 하루가 됐어요 ♥|방금 왔어요. 무슨 이야기 중인가요?|계속해요, 잘하고 있어요!",
        "창작할 때 어디에서 영감을 받나요?", "그다음에는 어떻게 됐나요? 더 듣고 싶어요!", "오늘 무엇이 가장 기대되나요?",
        "나", "댓글을 입력하세요…", "댓글 보내기", "라이브 채팅"],
    "zh": [
        "欢迎来到直播间！ 👋",
        "好喜欢这里的活力 ✨|从世界的小小角落向你问好！|这里的气氛真好|你看起来好自然|再多讲一点吧！|今天的快乐有了 ♥|刚进来，错过了什么？|继续加油，你可以的！",
        "你的创作灵感来自哪里？", "后来发生了什么？好想知道后续！", "今天有什么让你期待的事？",
        "你", "说点什么…", "发送评论", "直播聊天"],
    "b+zh+Hant": [
        "歡迎來到直播間！ 👋",
        "好喜歡這裡的活力 ✨|從世界的小小角落向你問好！|這裡的氣氛真好|你看起來好自然|再多講一點吧！|今天的快樂有了 ♥|剛進來，錯過了什麼？|繼續加油，你可以的！",
        "你的創作靈感來自哪裡？", "後來發生了什麼？好想知道後續！", "今天有什麼讓你期待的事？",
        "你", "說點什麼…", "傳送留言", "直播聊天"],
    "ru": [
        "Добро пожаловать в LIVE! 👋",
        "Обожаю эту энергетику ✨|Привет из моего уголка мира!|Здесь отличная атмосфера|Ты выглядишь так естественно|Расскажи ещё!|Это подняло мне настроение ♥|Только что присоединился. Что пропустил?|Продолжай, у тебя всё получится!",
        "Что вдохновляет тебя на творчество?", "А что было дальше? Очень интересно!", "Что тебя радует сегодня?",
        "Вы", "Напишите что-нибудь…", "Отправить комментарий", "Чат трансляции"],
    "tr": [
        "Canlı yayına hoş geldiniz! 👋",
        "Buradaki enerjiye bayıldım ✨|Dünyanın küçük köşemden merhaba!|Burada harika bir hava var|Çok doğal görünüyorsun|Biraz daha anlat!|Bu, günümü güzelleştirdi ♥|Yeni katıldım. Neler kaçırdım?|Devam et, başarabilirsin!",
        "Yaratıcılığına ne ilham veriyor?", "Sonra ne oldu? Çok merak ettim!", "Bugün seni heyecanlandıran ne var?",
        "Sen", "Bir şeyler yaz…", "Yorum gönder", "Canlı sohbet"],
    "vi": [
        "Chào mừng đến với LIVE! 👋",
        "Thích năng lượng ở đây quá ✨|Xin chào từ góc nhỏ của mình trên thế giới!|Không khí ở đây thật tuyệt|Bạn trông tự nhiên thật đấy|Kể thêm đi bạn!|Điều này làm ngày của mình vui hơn ♥|Mình mới vào. Đã bỏ lỡ gì vậy?|Cứ tiếp tục nhé, bạn làm được mà!",
        "Điều gì truyền cảm hứng sáng tạo cho bạn?", "Rồi chuyện gì xảy ra nữa? Mình tò mò quá!", "Hôm nay bạn đang háo hức về điều gì?",
        "Bạn", "Viết gì đó…", "Gửi bình luận", "Trò chuyện trực tiếp"],
    "th": [
        "ยินดีต้อนรับสู่ไลฟ์! 👋",
        "ชอบพลังงานที่นี่มาก ✨|สวัสดีจากมุมเล็ก ๆ ของโลก!|บรรยากาศดีมากเลย|ดูเป็นธรรมชาติมากเลย|เล่าให้ฟังอีกหน่อย!|ทำให้วันนี้สดใสขึ้นเลย ♥|เพิ่งเข้ามา พลาดอะไรไปบ้าง?|ทำต่อไปนะ คุณทำได้!",
        "อะไรเป็นแรงบันดาลใจในการสร้างสรรค์ของคุณ?", "แล้วเกิดอะไรขึ้นต่อ อยากรู้จัง!", "วันนี้มีอะไรที่ทำให้คุณตื่นเต้นบ้าง?",
        "คุณ", "พิมพ์ข้อความ…", "ส่งความคิดเห็น", "แชตสด"],
    "b+fil": [
        "Maligayang pagdating sa LIVE! 👋",
        "Ang ganda ng energy dito ✨|Hello mula sa maliit kong sulok ng mundo!|Ang saya ng vibe dito|Parang napakanatural lang sa iyo|Kuwento pa!|Gumanda ang araw ko dahil dito ♥|Kakasali ko lang. Ano ang na-miss ko?|Tuloy lang, kaya mo iyan!",
        "Ano ang inspirasyon mo sa paglikha?", "Ano ang sumunod na nangyari? Gusto ko pang malaman!", "Ano ang inaabangan mo ngayong araw?",
        "Ikaw", "Magsulat ng komento…", "Ipadala ang komento", "Live chat"],
    "nl": [
        "Welkom bij de LIVE! 👋",
        "Ik hou van de energie hier ✨|Hallo vanuit mijn hoekje van de wereld!|Wat een fijne sfeer|Je komt heel natuurlijk over|Vertel ons meer!|Dit maakt mijn dag goed ♥|Net erbij. Wat heb ik gemist?|Ga zo door, je kunt het!",
        "Wat inspireert jouw creativiteit?", "Wat gebeurde er daarna? Ik wil meer horen!", "Waar kijk je vandaag naar uit?",
        "Jij", "Schrijf iets…", "Reactie verzenden", "Livechat"],
    "pl": [
        "Witaj na LIVE! 👋",
        "Uwielbiam tę energię ✨|Cześć z mojego zakątka świata!|Świetna atmosfera|Wyglądasz tak naturalnie|Opowiedz więcej!|To poprawiło mi humor ♥|Właśnie dołączam. Co mnie ominęło?|Tak trzymaj, dasz radę!",
        "Co inspiruje Twoją twórczość?", "Co było dalej? Chcę usłyszeć więcej!", "Co Cię dzisiaj cieszy?",
        "Ty", "Napisz coś…", "Wyślij komentarz", "Czat na żywo"],
    "uk": [
        "Ласкаво просимо на LIVE! 👋",
        "Обожнюю цю енергію ✨|Привіт із мого куточка світу!|Тут чудова атмосфера|Ти виглядаєш так природно|Розкажи ще!|Це покращило мій настрій ♥|Щойно приєднуюся. Що пропустив?|Продовжуй, у тебе все вийде!",
        "Що надихає тебе на творчість?", "А що було далі? Хочу дізнатися більше!", "Що тебе тішить сьогодні?",
        "Ви", "Напишіть щось…", "Надіслати коментар", "Чат трансляції"],
    "fa": [
        "به پخش زنده خوش آمدید! 👋",
        "انرژی اینجا را دوست دارم ✨|سلام از گوشه کوچک من در جهان!|چه حال و هوای خوبی|خیلی راحت و طبیعی به نظر می‌رسی|بیشتر برایمان بگو!|این روزم را قشنگ کرد ♥|تازه پیوستم. چه چیزی را از دست دادم؟|ادامه بده، تو می‌توانی!",
        "چه چیزی الهام‌بخش خلاقیت توست؟", "بعد چه اتفاقی افتاد؟ مشتاقم بیشتر بدانم!", "امروز چه چیزی تو را هیجان‌زده می‌کند؟",
        "شما", "چیزی بنویسید…", "ارسال نظر", "گفتگوی زنده"],
}

KEYS = ("chat_welcome", "chat_general", "chat_music", "chat_story", "chat_daily", "chat_you", "chat_input_hint", "chat_send_action", "chat_heading")


def android_text(value):
    return escape(value).replace("'", "\\'").replace('"', '\\"')


def generate():
    for qualifier, row in TRANSLATIONS.items():
        assert len(row) == len(KEYS), qualifier
        general = row[1].split("|")
        assert len(general) == 8 and all(general), qualifier
        assert all(row), qualifier
        lines = ['<?xml version="1.0" encoding="utf-8"?>', '<!-- Generated by tools/generate_chat_resources.py -->', '<resources>']
        for key, text in zip(KEYS, row):
            if key == "chat_general":
                lines.append('    <string-array name="chat_general">')
                lines += ['        <item>' + android_text(item) + '</item>' for item in general]
                lines.append('    </string-array>')
            else:
                lines.append(f'    <string name="{key}">{android_text(text)}</string>')
        lines.append('</resources>')
        folder = ROOT / ('values-' + qualifier if qualifier else 'values')
        folder.mkdir(parents=True, exist_ok=True)
        (folder / 'chat.xml').write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'Generated {len(TRANSLATIONS)} complete native Android chat languages.')


if __name__ == '__main__':
    generate()
