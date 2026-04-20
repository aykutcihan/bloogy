import 'dart:async';
// ignore: avoid_web_libraries_in_flutter
import 'dart:html' as html;
import 'dart:convert';

class ArticlesService {
  final String baseUrl = 'http://localhost:18080/api/v1/articles';

  // Cookie ile GET istegi atar
  Future<String> _getWithCookies(String url) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('GET', url);
    xhr.withCredentials = true;
    xhr.onLoad.listen((_) {
      completer.complete(xhr.responseText);
    });
    xhr.onError.listen((_) {
      completer.completeError('Request failed');
    });
    xhr.send();
    return completer.future;
  }

  // Cookie ile POST istegi atar
  Future<String> _postWithCookies(String url, String body) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('POST', url);
    xhr.withCredentials = true;
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onLoad.listen((_) {
      completer.complete(xhr.responseText);
    });
    xhr.onError.listen((_) {
      completer.completeError('Request failed');
    });
    xhr.send(body);
    return completer.future;
  }

  // Cookie ile PUT istegi atar
  Future<String> _putWithCookies(String url, String body) async {
    final completer = Completer<String>();
    final xhr = html.HttpRequest();
    xhr.open('PUT', url);
    xhr.withCredentials = true;
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onLoad.listen((_) {
      completer.complete(xhr.responseText);
    });
    xhr.onError.listen((_) {
      completer.completeError('Request failed');
    });
    xhr.send(body);
    return completer.future;
  }

  Future<Map<String, dynamic>> fetchArticles({String? lastCursor, int pageSize = 10}) async {
    try {
      final url = lastCursor == null
          ? '$baseUrl/pagination?pageSize=$pageSize'
          : '$baseUrl/pagination?pageSize=$pageSize&lastDocumentId=$lastCursor';

      final responseText = await _getWithCookies(url);
      return json.decode(responseText)['data'];
    } catch (e) {
      throw Exception('Error fetching articles: $e');
    }
  }

  Future<Map<String, dynamic>> getArticleById(String id) async {
    try {
      final responseText = await _getWithCookies('$baseUrl/get/$id');
      return json.decode(responseText)['data'];
    } catch (e) {
      throw Exception('Error fetching article: $e');
    }
  }

  Future<void> createArticle(String title, String content) async {
    try {
      final body = json.encode({
        "title": title,
        "content": content,
      });
      await _postWithCookies('$baseUrl/save', body);
    } catch (e) {
      throw Exception('Error creating article: $e');
    }
  }

  Future<void> updateArticle(String id, String title, String content) async {
    try {
      final body = json.encode({
        "title": title,
        "content": content,
      });
      await _putWithCookies('$baseUrl/update/$id', body);
    } catch (e) {
      throw Exception('Error updating article: $e');
    }
  }
}
