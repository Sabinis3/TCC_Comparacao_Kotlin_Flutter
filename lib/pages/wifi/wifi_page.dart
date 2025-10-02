import 'package:flutter/material.dart';
import 'package:wifi_scan/wifi_scan.dart';
import 'package:network_info_plus/network_info_plus.dart';

class WifiPage extends StatefulWidget {
  const WifiPage({super.key});

  @override
  State<WifiPage> createState() => _WifiPageState();
}

class _WifiPageState extends State<WifiPage> {
  String? _connectedSSID;
  List<WiFiAccessPoint> _wifiList = [];

  @override
  void initState() {
    super.initState();
    _getConnectedWifi();
    _scanWifi();
  }

  Future<void> _getConnectedWifi() async {
    final info = NetworkInfo();
    final ssid = await info.getWifiName();
    setState(() {
      _connectedSSID = ssid;
    });
  }

  Future<void> _scanWifi() async {
    final can = await WiFiScan.instance.canStartScan();
    if (can == CanStartScan.yes) {
      await WiFiScan.instance.startScan();
      final aps = await WiFiScan.instance.getScannedResults();
      setState(() {
        _wifiList = aps;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Wi-Fi')),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          ListTile(
            title: const Text('Rede conectada:'),
            subtitle: Text(_connectedSSID ?? 'Carregando...'),
          ),
          const Divider(),
          const Padding(
            padding: EdgeInsets.all(8.0),
            child: Text('Redes disponíveis:'),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: _wifiList.length,
              itemBuilder: (context, index) {
                final ap = _wifiList[index];
                return ListTile(
                  title: Text(ap.ssid),
                  subtitle: Text('BSSID: ${ap.bssid} | Sinal: ${ap.level}'),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
