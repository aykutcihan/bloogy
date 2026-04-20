import 'package:flutter/material.dart';
import 'article_details_page.dart';
import '../services/articles_service.dart';

class ArticlesPage extends StatefulWidget {
  @override
  _ArticlesPageState createState() => _ArticlesPageState();
}

class _ArticlesPageState extends State<ArticlesPage> {
  final ArticlesService _articlesService = ArticlesService();
  List<dynamic> _articles = [];
  String? _lastCursor;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadArticles();
  }

  Future<void> _loadArticles() async {
    if (_isLoading) return;
    setState(() => _isLoading = true);

    try {
      final data = await _articlesService.fetchArticles(lastCursor: _lastCursor);
      setState(() {
        _articles.addAll(data['data']);
        _lastCursor = data['nextCursor'];
      });
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('Failed to load articles: $e')),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Articles'),
      ),
      body: NotificationListener<ScrollNotification>(
        onNotification: (scroll) {
          if (scroll is ScrollEndNotification &&
              scroll.metrics.pixels == scroll.metrics.maxScrollExtent) {
            _loadArticles();
          }
          return true;
        },
        child: _articles.isEmpty && !_isLoading
            ? Center(child: Text('No articles found.'))
            : ListView.builder(
                itemCount: _articles.length + 1,
                itemBuilder: (context, index) {
                  if (index < _articles.length) {
                    final article = _articles[index];
                    return ListTile(
                      title: Text(article['title'] ?? 'Untitled'),
                      subtitle: Text(article['author'] ?? 'Unknown author'),
                      trailing: Icon(Icons.arrow_forward_ios, size: 16),
                      onTap: () async {
                        final result = await Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (context) => ArticleDetailsPage(
                              articleId: article['id'],
                            ),
                          ),
                        );
                        if (result == true) {
                          setState(() {
                            _articles = [];
                            _lastCursor = null;
                          });
                          _loadArticles();
                        }
                      },
                    );
                  } else {
                    return _isLoading
                        ? Center(child: Padding(
                            padding: EdgeInsets.all(16),
                            child: CircularProgressIndicator(),
                          ))
                        : SizedBox();
                  }
                },
              ),
      ),
    );
  }
}
