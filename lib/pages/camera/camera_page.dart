import 'dart:io';
import 'package:app_flutter_tcc/pages/camera/widgets/media_bottom_sheet.dart';
import 'package:flutter/material.dart';
import 'package:camera/camera.dart';

class CameraPage extends StatefulWidget {
  const CameraPage({super.key});

  @override
  State<CameraPage> createState() => _CameraPageState();
}

class _CameraPageState extends State<CameraPage> with WidgetsBindingObserver {
  CameraController? _controller;
  Future<void>? _initializeControllerFuture;
  String? _errorMessage;
  List<CameraDescription> _cameras = [];
  int _selectedCameraIdx = 0;
  bool _isRecording = false;
  bool _isTakingPicture = false;
  bool _isProcessingVideo = false;
  final List<File> _mediaFiles = [];

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _initCamera();
  }

  Future<void> _initCamera() async {
    if (!mounted) return;
    try {
      _cameras = await availableCameras();
      if (_cameras.isEmpty) {
        setState(() {
          _errorMessage = 'Nenhuma câmera disponível.';
        });
        return;
      }
      _selectedCameraIdx = 0;
      await _setupCamera(_selectedCameraIdx);
    } on CameraException catch (e) {
      setState(() {
        _errorMessage = 'Erro ao inicializar a câmera: ${e.description}';
      });
    }
  }

  Future<void> _setupCamera(int cameraIdx) async {
    if (!mounted) return;

    _controller?.dispose();
    _controller = CameraController(_cameras[cameraIdx], ResolutionPreset.max);
    _initializeControllerFuture = _controller!.initialize();
    try {
      await _initializeControllerFuture;
      if (!mounted) return;
      setState(() {
        _errorMessage = null;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro ao inicializar a câmera.';
      });
    }
  }

  Future<void> _switchCamera() async {
    if (_cameras.length < 2) return;
    _selectedCameraIdx = (_selectedCameraIdx + 1) % _cameras.length;
    await _setupCamera(_selectedCameraIdx);
  }

  Future<void> _takePicture() async {
    if (!mounted) return;

    if (_controller == null ||
        !_controller!.value.isInitialized ||
        _isTakingPicture) {
      return;
    }

    setState(() {
      _isTakingPicture = true;
    });
    try {
      await _controller!.takePicture().then((XFile file) {
        final File imgFile = File(file.path);
        setState(() {
          _mediaFiles.add(imgFile);
        });
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro ao tirar foto.';
      });
    } finally {
      setState(() {
        _isTakingPicture = false;
      });
    }
  }

  Future<void> _startVideoRecording() async {
    if (!mounted) return;

    if (_controller == null ||
        !_controller!.value.isInitialized ||
        _isProcessingVideo) {
      return;
    }

    setState(() {
      _isProcessingVideo = true;
    });
    try {
      await _controller!.startVideoRecording();
      setState(() {
        _isRecording = true;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro ao iniciar gravação.';
      });
    } finally {
      setState(() {
        _isProcessingVideo = false;
      });
    }
  }

  Future<void> _stopVideoRecording() async {
    if (!mounted) return;

    if (_controller == null ||
        !_controller!.value.isRecordingVideo ||
        _isProcessingVideo) {
      return;
    }

    setState(() {
      _isProcessingVideo = true;
    });
    try {
      final XFile file = await _controller!.stopVideoRecording();
      final String mp4Path = file.path.replaceAll('.temp', '.mp4');
      final File tempFile = File(file.path);
      final File mp4File = await tempFile.rename(mp4Path);

      setState(() {
        _isRecording = false;
        _mediaFiles.add(mp4File);
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro ao parar gravação.';
        _isRecording = false;
      });
    } finally {
      setState(() {
        _isProcessingVideo = false;
      });
    }
  }

  void _showMediaBottomSheet() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => MediaBottomSheet(mediaFiles: _mediaFiles),
    );
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    final CameraController? cameraController = _controller;
    if (cameraController == null || !cameraController.value.isInitialized) {
      return;
    }
    if (state == AppLifecycleState.inactive) {
      cameraController.dispose();
    } else if (state == AppLifecycleState.resumed) {
      _setupCamera(_selectedCameraIdx);
    }
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    _controller?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_errorMessage != null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Câmera')),
        body: Center(
          child: Text(
            _errorMessage!,
            style: const TextStyle(color: Colors.red, fontSize: 16),
            textAlign: TextAlign.center,
          ),
        ),
      );
    }

    if (_controller == null || _initializeControllerFuture == null) {
      return Scaffold(
        appBar: AppBar(title: const Text('Câmera')),
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      appBar: AppBar(title: const Text('Câmera')),
      body: Stack(
        children: [
          FutureBuilder<void>(
            future: _initializeControllerFuture,
            builder: (context, snapshot) {
              if (snapshot.connectionState == ConnectionState.done) {
                return SizedBox.expand(child: CameraPreview(_controller!));
              } else if (snapshot.hasError) {
                return Center(
                  child: Text(
                    'Erro ao inicializar a câmera',
                    style: const TextStyle(color: Colors.red),
                  ),
                );
              }
              return const Center(child: CircularProgressIndicator());
            },
          ),
          Positioned(
            top: 16,
            left: 16,
            child: FloatingActionButton(
              heroTag: 'switch',
              onPressed: _switchCamera,
              child: const Icon(Icons.cameraswitch_rounded),
            ),
          ),
          Positioned(
            bottom: 32,
            left: 0,
            right: 0,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  FloatingActionButton(
                    heroTag: 'gallery',
                    onPressed: _showMediaBottomSheet,
                    child: const Icon(Icons.photo_library_rounded),
                  ),
                  FloatingActionButton(
                    heroTag: 'capture',
                    onPressed: _isRecording || _isTakingPicture
                        ? null
                        : _takePicture,
                    child: const Icon(Icons.camera_alt_rounded),
                  ),
                  FloatingActionButton(
                    heroTag: 'video',
                    backgroundColor: _isRecording ? Colors.red : null,
                    onPressed: _isProcessingVideo
                        ? null
                        : (_isRecording
                              ? _stopVideoRecording
                              : _startVideoRecording),
                    child: Icon(
                      _isRecording
                          ? Icons.stop_rounded
                          : Icons.videocam_rounded,
                    ),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }
}
