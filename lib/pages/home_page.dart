import 'package:app_flutter_tcc/pages/biometric_page.dart';
import 'package:app_flutter_tcc/pages/camera_page.dart';
import 'package:app_flutter_tcc/pages/flash_page.dart';
import 'package:app_flutter_tcc/pages/vibration_page.dart';
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
            FilledButton.icon(
              icon: Icon(Icons.fingerprint_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => BiometricPage()),
                );
              },
              style: FilledButton.styleFrom(
                padding: EdgeInsets.all(12.0),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10.0),
                ),
              ),
              label: Text("Biometria", style: TextStyle(fontSize: 16.0)),
            ),
            FilledButton.icon(
              icon: Icon(Icons.camera_alt_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => CameraPage()),
                );
              },
              style: FilledButton.styleFrom(
                padding: EdgeInsets.all(12.0),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10.0),
                ),
              ),
              label: Text("Câmera", style: TextStyle(fontSize: 16.0)),
            ),

            FilledButton.icon(
              icon: Icon(Icons.vibration_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => VibrationPage()),
                );
              },
              style: FilledButton.styleFrom(
                padding: EdgeInsets.all(12.0),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10.0),
                ),
              ),
              label: Text("Vibração", style: TextStyle(fontSize: 16.0)),
            ),
            FilledButton.icon(
              icon: Icon(Icons.flash_on_rounded, size: 28.0),
              onPressed: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(builder: (context) => FlashPage()),
                );
              },
              style: FilledButton.styleFrom(
                padding: EdgeInsets.all(12.0),
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10.0),
                ),
              ),
              label: Text("Flash", style: TextStyle(fontSize: 16.0)),
            ),
          ],
        ),
      ),
    );
  }
}
