import 'dart:async';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:convert';

class UserService {
  // Login olan kullanicinin bilgilerini getirir
  Future<Map<String, dynamic>?> getCurrentUser() async {
    final completer = Completer<Map<String, dynamic>?>();
    final xhr = html.HttpRequest();
    xhr.open('GET', 'http://localhost:18080/api/v1/users/me');
    xhr.withCredentials = true;
    xhr.onLoad.listen((_) {
      if (xhr.status == 200) {
        completer.complete(json.decode(xhr.responseText!));
      } else {
        completer.complete(null);
      }
    });
    xhr.onError.listen((_) => completer.complete(null));
    xhr.send();
    return completer.future;
  }
}
