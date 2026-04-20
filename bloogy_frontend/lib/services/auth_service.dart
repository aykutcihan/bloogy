// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:async';

class AuthService {
  Future<bool> isAuthenticated() async {
    final completer = Completer<bool>();
    final xhr = html.HttpRequest();
    xhr.open('GET', 'http://localhost:18080/api/v1/users/me');
    xhr.withCredentials = true; // cookie gonder
    xhr.onLoad.listen((_) {
      completer.complete(xhr.status == 200);
    });
    xhr.onError.listen((_) {
      completer.complete(false);
    });
    xhr.send();
    return completer.future;
  }
}
