import 'package:app_flutter_tcc/pages/biometric_page.dart';
import 'package:app_flutter_tcc/pages/camera_page.dart';
import 'package:flutter/material.dart';

class HomePage extends StatelessWidget {
  const HomePage({super.key});

  @override
  Widget build(BuildContext context) {
    var theme = Theme.of(context);

    return Scaffold(
      appBar: AppBar(
        backgroundColor: theme.colorScheme.inversePrimary,
        title: Text("Home TCC"),
      ),
      body: Padding(
        padding: const EdgeInsets.all(8.0),
        child: Center(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            spacing: 8.0,
            children: <Widget>[
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  icon: Icon(Icons.camera_alt),
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
              ),
              SizedBox(
                width: double.infinity,
                child: FilledButton.icon(
                  icon: Icon(Icons.fingerprint),
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
              ),
            ],
          ),
        ),
      ),
    );
  }
}
