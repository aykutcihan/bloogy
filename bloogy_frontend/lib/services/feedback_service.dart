import 'dart:async';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:convert';

class FeedbackService {
  final String baseUrl = 'http://localhost:18080/api/v1/feedbacks';

  Future<String> _postWithCookies(String url, String body) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('POST', url);
    xhr.withCredentials = true;
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onLoad.listen((_) => completer.complete(xhr.responseText));
    xhr.onError.listen((_) => completer.completeError('Request failed'));
    xhr.send(body);
    return completer.future;
  }

  Future<String> _getWithCookies(String url) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('GET', url);
    xhr.withCredentials = true;
    xhr.onLoad.listen((_) => completer.complete(xhr.responseText));
    xhr.onError.listen((_) => completer.completeError('Request failed'));
    xhr.send();
    return completer.future;
  }

  Future<void> submitFeedback(String articleId, String commenterName, String comment) async {
    final body = json.encode({
      "articleId": articleId,
      "commenterName": commenterName,
      "comment": comment,
    });
    await _postWithCookies('$baseUrl/save', body);
  }

  Future<List<dynamic>> getFeedbacksByArticle(String articleId) async {
    final responseText = await _getWithCookies('$baseUrl/article/$articleId');
    final decoded = json.decode(responseText);
    return decoded['data'] ?? [];
  }
}
