package com.finalshell.editor;

/**
 * 语法常量实现
 * 定义编辑器支持的语法高亮类型
 */
public interface SyntaxConstantsImpl {
    
    String SYNTAX_STYLE_NONE = "text/plain";
    String SYNTAX_STYLE_SHELL = "text/x-sh";
    String SYNTAX_STYLE_BASH = "text/x-bash";
    String SYNTAX_STYLE_PYTHON = "text/x-python";
    String SYNTAX_STYLE_JAVA = "text/x-java";
    String SYNTAX_STYLE_JAVASCRIPT = "text/javascript";
    String SYNTAX_STYLE_JSON = "application/json";
    String SYNTAX_STYLE_XML = "text/xml";
    String SYNTAX_STYLE_HTML = "text/html";
    String SYNTAX_STYLE_CSS = "text/css";
    String SYNTAX_STYLE_SQL = "text/x-sql";
    String SYNTAX_STYLE_PROPERTIES = "text/x-properties";
    String SYNTAX_STYLE_INI = "text/x-ini";
    String SYNTAX_STYLE_YAML = "text/x-yaml";
    String SYNTAX_STYLE_MARKDOWN = "text/x-markdown";
    String SYNTAX_STYLE_C = "text/x-c";
    String SYNTAX_STYLE_CPP = "text/x-c++";
    String SYNTAX_STYLE_CSHARP = "text/x-csharp";
    String SYNTAX_STYLE_GO = "text/x-go";
    String SYNTAX_STYLE_RUBY = "text/x-ruby";
    String SYNTAX_STYLE_PERL = "text/x-perl";
    String SYNTAX_STYLE_PHP = "text/x-php";
    String SYNTAX_STYLE_LUA = "text/x-lua";
    String SYNTAX_STYLE_GROOVY = "text/x-groovy";
    String SYNTAX_STYLE_SCALA = "text/x-scala";
    String SYNTAX_STYLE_KOTLIN = "text/x-kotlin";
    String SYNTAX_STYLE_SWIFT = "text/x-swift";
    String SYNTAX_STYLE_RUST = "text/x-rust";
    String SYNTAX_STYLE_DOCKERFILE = "text/x-dockerfile";
    String SYNTAX_STYLE_MAKEFILE = "text/x-makefile";
    
    static String getSyntaxByExtension(String ext) {
        if (ext == null) return SYNTAX_STYLE_NONE;
        
        switch (ext.toLowerCase()) {
            case "sh":
            case "bash":
                return SYNTAX_STYLE_BASH;
            case "py":
                return SYNTAX_STYLE_PYTHON;
            case "java":
                return SYNTAX_STYLE_JAVA;
            case "js":
                return SYNTAX_STYLE_JAVASCRIPT;
            case "json":
                return SYNTAX_STYLE_JSON;
            case "xml":
                return SYNTAX_STYLE_XML;
            case "html":
            case "htm":
                return SYNTAX_STYLE_HTML;
            case "css":
                return SYNTAX_STYLE_CSS;
            case "sql":
                return SYNTAX_STYLE_SQL;
            case "properties":
                return SYNTAX_STYLE_PROPERTIES;
            case "ini":
            case "conf":
            case "cfg":
                return SYNTAX_STYLE_INI;
            case "yml":
            case "yaml":
                return SYNTAX_STYLE_YAML;
            case "md":
            case "markdown":
                return SYNTAX_STYLE_MARKDOWN;
            case "c":
            case "h":
                return SYNTAX_STYLE_C;
            case "cpp":
            case "cc":
            case "hpp":
                return SYNTAX_STYLE_CPP;
            case "cs":
                return SYNTAX_STYLE_CSHARP;
            case "go":
                return SYNTAX_STYLE_GO;
            case "rb":
                return SYNTAX_STYLE_RUBY;
            case "pl":
            case "pm":
                return SYNTAX_STYLE_PERL;
            case "php":
                return SYNTAX_STYLE_PHP;
            case "lua":
                return SYNTAX_STYLE_LUA;
            case "groovy":
                return SYNTAX_STYLE_GROOVY;
            case "scala":
                return SYNTAX_STYLE_SCALA;
            case "kt":
                return SYNTAX_STYLE_KOTLIN;
            case "swift":
                return SYNTAX_STYLE_SWIFT;
            case "rs":
                return SYNTAX_STYLE_RUST;
            case "dockerfile":
                return SYNTAX_STYLE_DOCKERFILE;
            case "makefile":
                return SYNTAX_STYLE_MAKEFILE;
            default:
                return SYNTAX_STYLE_NONE;
        }
    }
}
