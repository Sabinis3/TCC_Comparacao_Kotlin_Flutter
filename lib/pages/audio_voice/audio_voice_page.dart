import 'dart:async';
import 'package:app_flutter_tcc/widgets/home_filled_icon_button.dart';
import 'package:flutter/material.dart';
import 'package:flutter_sound/flutter_sound.dart';

typedef Fn = void Function();

class AudioVoicePage extends StatefulWidget {
  const AudioVoicePage({super.key});

  @override
  State<AudioVoicePage> createState() => _AudioVoicePageState();
}

class _AudioVoicePageState extends State<AudioVoicePage> {
  final FlutterSoundRecorder _recorder = FlutterSoundRecorder();
  final FlutterSoundPlayer _player = FlutterSoundPlayer();

  String? _audioFilePath;
  bool _isRecorderInitialized = false;
  bool _isPlayerInitialized = false;
  bool _isRecording = false;
  bool _isPlaying = false;
  String? _errorMessage;

  @override
  void initState() {
    _init();
    super.initState();
  }

  @override
  void dispose() {
    _recorder.closeRecorder();
    _player.closePlayer();
    super.dispose();
  }

  Future<void> _init() async {
    try {
      await _recorder.openRecorder();
      await _player.openPlayer();
      setState(() {
        _isRecorderInitialized = true;
        _isPlayerInitialized = true;
        _errorMessage = null;
      });
    } catch (e) {
      setState(() {
        _errorMessage = "Erro ao inicializar: $e";
      });
    }
  }

  Future<void> _startRecording() async {
    setState(() {
      _errorMessage = null;
    });
    try {
      setState(() {
        _isRecording = true;
      });
      await _recorder.startRecorder(
        codec: Codec.aacMP4,
        toFile: "audio_file.aac",
      );
    } catch (e) {
      setState(() {
        _isRecording = false;
        _errorMessage = "Erro ao iniciar gravação: $e";
      });
    }
  }

  Future<void> _stopRecording() async {
    setState(() {
      _errorMessage = null;
    });
    try {
      var url = await _recorder.stopRecorder();
      setState(() {
        _isRecording = false;
        _audioFilePath = url;
      });
    } catch (e) {
      setState(() {
        _isRecording = false;
        _errorMessage = "Erro ao parar gravação: $e";
      });
    }
  }

  Future<void> _playRecordingFromFile() async {
    setState(() {
      _errorMessage = null;
    });
    try {
      setState(() {
        _isPlaying = true;
      });
      await _player.startPlayer(
        fromURI: _audioFilePath,
        codec: Codec.aacMP4,
        whenFinished: () {
          setState(() {
            _isPlaying = false;
          });
        },
      );
    } catch (e) {
      setState(() {
        _isPlaying = false;
        _errorMessage = "Erro ao reproduzir: $e";
      });
    }
  }

  Future<void> _stopPlayer() async {
    setState(() {
      _errorMessage = null;
    });
    try {
      await _player.stopPlayer();
      setState(() {
        _isPlaying = false;
      });
    } catch (e) {
      setState(() {
        _isPlaying = false;
        _errorMessage = "Erro ao parar reprodução: $e";
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Áudio")),
      body: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Center(
          child: Column(
            spacing: 16.0,
            mainAxisSize: MainAxisSize.min,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                spacing: 16.0,
                children: [
                  Expanded(
                    child: SizedBox(
                      height: 72.7,
                      child: HomeFilledIconButton(
                        icon: Icon(Icons.mic_rounded, size: 28.0),
                        onPressed:
                            _isRecorderInitialized &&
                                !_isRecording &&
                                !_isPlaying
                            ? _startRecording
                            : null,
                        label: Text("Gravar", style: TextStyle(fontSize: 16.0)),
                      ),
                    ),
                  ),
                  Expanded(
                    child: SizedBox(
                      height: 72.7,
                      child: HomeFilledIconButton(
                        icon: Icon(Icons.stop_rounded, size: 28.0),
                        onPressed: _isRecorderInitialized && _isRecording
                            ? _stopRecording
                            : null,
                        label: Text(
                          "Parar gravação",
                          style: TextStyle(fontSize: 16.0),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                spacing: 16.0,
                children: [
                  Expanded(
                    child: SizedBox(
                      height: 72.7,
                      child: HomeFilledIconButton(
                        icon: Icon(Icons.play_arrow_rounded, size: 28.0),
                        onPressed:
                            _isPlayerInitialized &&
                                !_isPlaying &&
                                !_isRecording &&
                                _audioFilePath != null
                            ? _playRecordingFromFile
                            : null,
                        label: Text(
                          "Reproduzir",
                          style: TextStyle(fontSize: 16.0),
                        ),
                      ),
                    ),
                  ),
                  Expanded(
                    child: SizedBox(
                      height: 72.7,
                      child: HomeFilledIconButton(
                        icon: Icon(Icons.stop_rounded, size: 28.0),
                        onPressed: _isPlayerInitialized && _isPlaying
                            ? _stopPlayer
                            : null,
                        label: Text(
                          "Parar reprodução",
                          style: TextStyle(fontSize: 16.0),
                        ),
                      ),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 24),
              if (_errorMessage != null)
                Padding(
                  padding: const EdgeInsets.only(top: 16.0),
                  child: Text(
                    _errorMessage!,
                    style: const TextStyle(color: Colors.red),
                    textAlign: TextAlign.center,
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}
