import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:flutter_easyloading/flutter_easyloading.dart';
import 'package:google_fonts/google_fonts.dart';
import 'package:hive_flutter/hive_flutter.dart';
import 'package:vietmap_map/constants/colors.dart';
import 'package:vietmap_map/features/pick_address_screen/pick_address_screen.dart';
import 'package:vietmap_map/features/routing_screen/routing_screen.dart';
import 'package:vietmap_map/features/routing_screen/search_address.dart';
import 'package:vietmap_map/features/search_screen/search_screen.dart';
import 'package:path_provider/path_provider.dart';
import 'constants/route.dart';
import 'features/map_screen/bloc/bloc.dart';
import 'features/map_screen/maps_screen.dart';
import 'features/routing_screen/bloc/bloc.dart';
import 'package:vietmap_map/NavigationScreen.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();
  try {
    await dotenv.load(fileName: ".env");
  } catch (e) {}
  try {
    final appDocumentDirectory = await getApplicationDocumentsDirectory();
    Hive.init(appDocumentDirectory.path);
  } catch (e) {
    print(e);
  }

  runApp(FuelBrakeApp());
}

class FuelBrakeApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Fuel & Brake Status',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: FuelBrakeScreen(),
      routes: {
        Routes.fuelBrakeScreen: (context) => FuelBrakeScreen(),
        '/fuelBrake': (context) => FuelBrakeScreen(), // Add this line back
      },
    );
  }
}

class FuelBrakeScreen extends StatefulWidget {
  @override
  _FuelBrakeScreenState createState() => _FuelBrakeScreenState();
}

class _FuelBrakeScreenState extends State<FuelBrakeScreen> {
  double fuelLevel = 50.0; // Fuel level (0-100)
  bool isHandBrakeEngaged = false; // Handbrake status
  int selectedTabIndex = 0; // 0: Home, 1: Map

  void updateFuel(double value) {
    setState(() {
      fuelLevel = value;

      if (fuelLevel < 10) {
        showWarningDialog();
      }
    });
  }

  void toggleHandBrake(bool value) {
    setState(() {
      isHandBrakeEngaged = value;

      if (isHandBrakeEngaged) {
        showHandBrakeDialog();
      }
    });
  }

  void showWarningDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Warning!'),
        content: Text('Fuel level is below 10%! Please refuel.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              showFuelStationDialog();
            },
            child: Text('Got it'),
          ),
        ],
      ),
    );
  }

  void showFuelStationDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Nearest Fuel Station'),
        content: Text('Would you like to go to the nearest fuel station to refuel?'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
              Navigator.push(
                context,
                MaterialPageRoute(
                  builder: (context) => NavigationScreen(
                  ),
                ),
              );
            },
            child: Text('OK'),
          ),
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: Text('No'),
          ),
        ],
      ),
    );
  }

  void showHandBrakeDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Handbrake Warning!'),
        content: Text('You need to release the handbrake to allow the car to move.'),
        actions: [
          TextButton(
            onPressed: () {
              Navigator.of(context).pop();
            },
            child: Text('Got it'),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        flexibleSpace: Container(
          decoration: BoxDecoration(
            gradient: LinearGradient(
              colors: [Colors.blue, Colors.lightBlueAccent],
              begin: Alignment.topLeft,
              end: Alignment.bottomRight,
            ),
          ),
        ),
        title: Text(
          'Vehicle Status Checker',
          style: TextStyle(fontWeight: FontWeight.bold, fontSize: 22),
        ),
        centerTitle: true,
      ),
      body: Container(
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [Colors.white, Colors.lightBlue[50]!],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: Padding(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Icon(
                    Icons.directions_car,
                    size: 28,
                    color: isHandBrakeEngaged ? Colors.red : Colors.green,
                  ),
                  SizedBox(width: 8),
                  Text(
                    isHandBrakeEngaged ? '🚨 Handbrake engaged!' : '✔️ Handbrake released',
                    style: TextStyle(
                      fontSize: 18,
                      color: isHandBrakeEngaged ? Colors.red : Colors.green,
                    ),
                  ),
                  Spacer(),
                  Switch(
                    value: isHandBrakeEngaged,
                    onChanged: toggleHandBrake,
                    activeColor: Colors.red,
                    inactiveThumbColor: Colors.green,
                    inactiveTrackColor: Colors.grey,
                  ),
                ],
              ),
              SizedBox(height: 20),
              Row(
                children: [
                  Icon(
                    Icons.local_gas_station,
                    size: 28,
                    color: Colors.blue,
                  ),
                  SizedBox(width: 8),
                  Text(
                    'Fuel Level',
                    style: TextStyle(fontSize: 18),
                  ),
                  SizedBox(width: 20),
                  Expanded(
                    child: Slider(
                      value: fuelLevel,
                      min: 0,
                      max: 100,
                      divisions: 100,
                      label: '${fuelLevel.toInt()}%',
                      onChanged: updateFuel,
                      activeColor: fuelLevel > 20 ? Colors.green : Colors.red,
                      inactiveColor: Colors.grey,
                    ),
                  ),
                ],
              ),
              SizedBox(height: 20),
              LinearProgressIndicator(
                value: fuelLevel / 100,
                backgroundColor: Colors.grey[300],
                valueColor: AlwaysStoppedAnimation<Color>(
                  fuelLevel > 20 ? Colors.green : Colors.red,
                ),
              ),
            ],
          ),
        ),
      ),
      bottomNavigationBar: BottomAppBar(
        child: Row(
          mainAxisAlignment: MainAxisAlignment.spaceAround,
          children: [
            AnimatedContainer(
              duration: Duration(milliseconds: 300),
              padding: const EdgeInsets.all(8.0),
              decoration: BoxDecoration(
                color: selectedTabIndex == 0 ? Colors.blue.withOpacity(0.2) : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: IconButton(
                icon: Icon(Icons.home),
                onPressed: () {
                  setState(() {
                    selectedTabIndex = 0;
                  });
                  Navigator.pushReplacement(
                    context,
                    MaterialPageRoute(builder: (context) => FuelBrakeScreen()),
                  );
                },
              ),
            ),
            AnimatedContainer(
              duration: Duration(milliseconds: 300),
              padding: const EdgeInsets.all(8.0),
              decoration: BoxDecoration(
                color: selectedTabIndex == 1 ? Colors.blue.withOpacity(0.2) : Colors.transparent,
                borderRadius: BorderRadius.circular(12),
              ),
              child: IconButton(
                icon: Icon(Icons.map),
                onPressed: () {
                  setState(() {
                    selectedTabIndex = 1;
                  });
                  Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (context) => NavigationScreen(
                      ),
                    ),
                  );
                },
              ),
            ),
          ],
        ),
      ),
    );
  }
}


