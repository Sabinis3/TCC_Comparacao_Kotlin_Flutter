import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class BluetoothPage extends StatefulWidget {
  const BluetoothPage({super.key});

  @override
  State<BluetoothPage> createState() => _BluetoothPageState();
}

class _BluetoothPageState extends State<BluetoothPage> {
  static const platform = MethodChannel(
    'com.example.app_flutter_tcc/bluetooth',
  );

  List<Map<String, String>> bondedDevices = [];
  List<Map<String, String>> discoveredDevices = [];
  bool isDiscovering = false;

  @override
  void initState() {
    super.initState();
    _getBondedDevices();
  }

  Future<void> _getBondedDevices() async {
    try {
      final List devices = await platform.invokeMethod('getBondedDevices');
      if (!mounted) return;
      setState(() {
        bondedDevices = devices
            .cast<Map>()
            .map((e) => Map<String, String>.from(e))
            .toList();
      });
    } on PlatformException catch (e) {
      debugPrint("Erro ao buscar dispositivos pareados: $e");
    }
  }

  Future<void> _startDiscovery() async {
    setState(() {
      discoveredDevices.clear();
      isDiscovering = true;
    });
    try {
      final List devices = await platform.invokeMethod('startDiscovery');
      if (!mounted) return;
      setState(() {
        discoveredDevices = devices
            .cast<Map>()
            .map((e) => Map<String, String>.from(e))
            .toList();
      });
    } on PlatformException catch (e) {
      debugPrint("Erro ao buscar dispositivos: $e");
    }
    
    if (!mounted) return;
    setState(() {
      isDiscovering = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    var theme = Theme.of(context);
    return Scaffold(
      appBar: AppBar(title: const Text('Bluetooth')),
      body: SingleChildScrollView(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Padding(
              padding: EdgeInsets.all(16.0),
              child: Text(
                'Pareados',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.w300,
                ),
              ),
            ),
            ...bondedDevices.map(
              (d) => ListTile(
                leading: const Icon(Icons.bluetooth_connected_rounded),
                title: Text(d['name'] ?? d['address'] ?? ''),
                subtitle: Text(d['address'] ?? ''),
              ),
            ),
            Padding(
              padding: EdgeInsets.all(16.0),
              child: Text(
                'Encontrados',
                style: theme.textTheme.titleLarge?.copyWith(
                  fontWeight: FontWeight.w300,
                ),
              ),
            ),
            ...discoveredDevices.map(
              (d) => ListTile(
                leading: const Icon(Icons.bluetooth_searching_rounded),
                title: Text(d['name'] ?? d['address'] ?? ''),
                subtitle: Text(d['address'] ?? ''),
              ),
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: isDiscovering ? null : _startDiscovery,
        child: Icon(isDiscovering ? Icons.stop_rounded : Icons.search_rounded),
      ),
    );
  }
}
