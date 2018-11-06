package com.liyafeng.video.practice.audiorecord;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AudioPlayPCM {

    /**
     * https://www.cnblogs.com/renhui/p/7463287.html（使用 AudioTrack 播放PCM音频）
     * Pulse-code modulation
     * 脉冲编码调制
     * <p>
     * ============AudioTrack 两种模式================
     * MODE_STREAM 数据多次写入AudioTrack缓冲区
     * MODE_STATIC 一次写入，适合时长较短的音频。
     * 如果采用STATIC模式，须先调用write写数据，然后再调用play
     * =======================AudioTrack底层=====================
     * 每一个音频流对应着一个AudioTrack类的一个实例，每个AudioTrack会在创建时注册到 AudioFlinger中，
     * 由AudioFlinger把所有的AudioTrack进行混合（Mixer），然后输送到AudioHardware中进行播放，
     * 目前Android同时最多可以创建32个音频流，也就是说，Mixer最多会同时处理32个AudioTrack的数据流。
     * ======================AudioTrack和mediaPlayer对比=========================
     * 其中最大的区别是MediaPlayer可以播放多种格式的声音文件，例如MP3，AAC，WAV，OGG，MIDI等。
     * MediaPlayer会在framework层创建对应的音频解码器。而AudioTrack只能播放已经解码的PCM流，
     * 如果对比支持的文件格式的话则是AudioTrack只支持wav格式的音频文件，
     * 因为wav格式的音频文件大部分都是PCM流。AudioTrack不创建解码器，所以只能播放不需要解码的wav文件
     * <p>
     * MediaPlayer在framework层还是会创建AudioTrack，把解码后的PCM数流传递给AudioTrack，
     * AudioTrack再传递给AudioFlinger进行混音，
     * 然后才传递给硬件播放,所以是MediaPlayer包含了AudioTrack。
     * <p>
     * ====================应用场景===============================
     * 提供了非常强大的控制能力，支持低延迟播放，适合流媒体和VoIP语音电话等场景
     * <p>
     * =====================================
     * ByteArrayOutputStream 是讲字节流写入内存中（ByteArrayOutputStream对象内部的byte数组）
     * ByteArrayInputStream(new byte[1024]) 是将new byte[1024]当做数据源，读取到我们指定的容器中
     */
    public AudioPlayPCM() {
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void play(File file, int recorderSamplerate) throws IOException {

        if (!file.exists()) {
            Log.i("test", "play: 不存在");
            return;
        }
        FileInputStream inputStream = new FileInputStream(file);

//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        new AudioTrack.Builder().
        int bufferSize = AudioTrack.getMinBufferSize(recorderSamplerate,
                AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.i("test", "play: size " + bufferSize);//1296

        AudioTrack audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                recorderSamplerate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
        );

        //java.lang.IllegalStateException: play() called on uninitialized AudioTrack.

        audioTrack.play();

        byte[] bytes = new byte[2048];
        int length = 0;
        while ((length = inputStream.read(bytes)) != -1) {
            Log.i("test", "play: 写入" + length);
            audioTrack.write(bytes, 0, length);
        }
        audioTrack.stop();
        audioTrack.release();
        Log.i("test", "play: 播放结束");
        audioTrack = null;

        inputStream.close();
    }
}
