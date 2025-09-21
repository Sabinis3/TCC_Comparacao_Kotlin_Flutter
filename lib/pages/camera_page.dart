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
  CameraDescription? _cameraDescription;

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _initCamera();
  }

  Future<void> _initCamera() async {
    try {
      final cameras = await availableCameras();
      if (cameras.isEmpty) {
        setState(() {
          _errorMessage = 'Nenhuma câmera disponível.';
        });
        return;
      }
      // Use a câmera traseira se disponível, senão a primeira
      _cameraDescription = cameras.length > 1 ? cameras[0] : cameras[1];
      _controller = CameraController(_cameraDescription!, ResolutionPreset.max);
      _initializeControllerFuture = _controller!.initialize();
      await _initializeControllerFuture;
      if (!mounted) return;
      setState(() {
        _errorMessage = null;
      });
    } on CameraException catch (e) {
      String errorText;
      switch (e.code) {
        case 'CameraAccessDenied':
          errorText = 'Permissão da câmera negada.';
          break;
        case 'CameraAccessDeniedWithoutPrompt':
          errorText =
              'Permissão da câmera negada permanentemente. Vá em Ajustes para liberar.';
          break;
        case 'CameraAccessRestricted':
          errorText = 'Acesso à câmera restrito (controle parental).';
          break;
        case 'AudioAccessDenied':
          errorText = 'Permissão do microfone negada.';
          break;
        case 'AudioAccessDeniedWithoutPrompt':
          errorText =
              'Permissão do microfone negada permanentemente. Vá em Ajustes para liberar.';
          break;
        case 'AudioAccessRestricted':
          errorText = 'Acesso ao microfone restrito (controle parental).';
          break;
        default:
          errorText = 'Erro ao inicializar a câmera: ${e.description}';
      }
      setState(() {
        _errorMessage = errorText;
      });
    } catch (e) {
      setState(() {
        _errorMessage = 'Erro inesperado ao inicializar a câmera.';
      });
    }
  }

  Future<void> _reinitializeCamera() async {
    if (_cameraDescription != null) {
      _controller = CameraController(_cameraDescription!, ResolutionPreset.max);
      _initializeControllerFuture = _controller!.initialize();
      try {
        await _initializeControllerFuture;
        if (!mounted) return;
        setState(() {
          _errorMessage = null;
        });
      } on CameraException catch (e) {
        setState(() {
          _errorMessage = 'Erro ao reabrir a câmera: ${e.description}';
        });
      }
    }
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
      _reinitializeCamera();
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
      return const Scaffold(body: Center(child: CircularProgressIndicator()));
    }

    return Scaffold(
      body: SafeArea(
        child: FutureBuilder<void>(
          future: _initializeControllerFuture,
          builder: (context, snapshot) {
            if (snapshot.connectionState == ConnectionState.done) {
              return CameraPreview(_controller!);
            } else if (snapshot.hasError) {
              return Center(
                child: Text(
                  'Erro ao inicializar a câmera',
                  style: const TextStyle(color: Colors.red),
                ),
              );
            } else {
              return const Center(child: CircularProgressIndicator());
            }
          },
        ),
      ),
    );
  }
}
