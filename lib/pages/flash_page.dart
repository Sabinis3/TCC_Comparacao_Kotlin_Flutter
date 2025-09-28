import 'package:camera/camera.dart';
import 'package:flutter/material.dart';

class FlashPage extends StatefulWidget {
  const FlashPage({super.key});

  @override
  State<FlashPage> createState() => _FlashPageState();
}

class _FlashPageState extends State<FlashPage> {
  late List<CameraDescription> cameras;
  late CameraController _controller;

  @override
  void initState() {
    initializeCamera();
    super.initState();
  }

  Future<void> initializeCamera() async {
    cameras = await availableCameras();
    _controller = CameraController(cameras[0], ResolutionPreset.low);
    await _controller.initialize();
  }

  Future<void> toggleFlashlight() async {
    if (_controller.value.isInitialized) {
      if (_controller.value.flashMode == FlashMode.off) {
        await _controller.setFlashMode(FlashMode.torch);
      } else {
        await _controller.setFlashMode(FlashMode.off);
      }
    }
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text("Flash")),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  onPressed: toggleFlashlight,
                  style: FilledButton.styleFrom(
                    padding: const EdgeInsets.all(12.0),
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10.0),
                    ),
                  ),
                  icon: const Icon(Icons.flash_on_rounded),
                  label: const Text(
                    "Testar Flash",
                    style: TextStyle(fontSize: 16.0),
                  ),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
