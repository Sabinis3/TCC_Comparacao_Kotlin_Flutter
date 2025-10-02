import 'package:app_flutter_tcc/pages/biometric/biometric_page.dart';
import 'package:app_flutter_tcc/pages/camera/camera_page.dart';
import 'package:app_flutter_tcc/pages/flash/flash_page.dart';
import 'package:app_flutter_tcc/pages/vibration/vibration_page.dart';
import 'package:app_flutter_tcc/pages/wifi/wifi_page.dart';
import 'package:app_flutter_tcc/widgets/home_filled_icon_button.dart';
import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text("Home TCC")),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: GridView.count(
          crossAxisCount: 2,
          mainAxisSpacing: 16.0,
          crossAxisSpacing: 16.0,
          childAspectRatio: 2.5,
          children: <Widget>[
            HomeFilledIconButton(
              icon: Icon(Icons.fingerprint_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => BiometricPage()),
                );
              },
              label: Text("Biometria", style: TextStyle(fontSize: 16.0)),
            ),
            HomeFilledIconButton(
              icon: Icon(Icons.camera_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => CameraPage()),
                );
              },
              label: Text("Câmera", style: TextStyle(fontSize: 16.0)),
            ),
            HomeFilledIconButton(
              icon: Icon(Icons.vibration_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => VibrationPage()),
                );
              },
              label: Text("Vibração", style: TextStyle(fontSize: 16.0)),
            ),
            HomeFilledIconButton(
              icon: Icon(Icons.flash_on_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => FlashPage()),
                );
              },
              label: Text("Flash", style: TextStyle(fontSize: 16.0)),
            ),
            HomeFilledIconButton(
              icon: Icon(Icons.wifi_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => WifiPage()),
                );
              },
              label: Text("Wi-Fi", style: TextStyle(fontSize: 16.0)),
            ),
          ],
        ),
      ),
    );
  }
}
