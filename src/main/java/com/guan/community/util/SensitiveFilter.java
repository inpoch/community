package com.guan.community.util;


import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


@Component
public class SensitiveFilter {

    private static final String REPLACEMENT = "***";


    //前缀树结构
    private class TreeNode {

        private boolean isKeywordEnd = false;

        private Map<Character, TreeNode> subNodes = new HashMap<>();

        public boolean isKeywordEnd() {
            return isKeywordEnd;
        }

        public void  setKeywordEnd(boolean keywordEnd) {
            isKeywordEnd = keywordEnd;
        }

        public void addSubNode(Character c, TreeNode node) {
            subNodes.put(c, node);
        }

        public TreeNode getSubNode(Character c) {
            return subNodes.get(c);
        }
    }

    private TreeNode rootNode = new TreeNode();

    @PostConstruct
    public void init() {

        try(
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("sensitive-words.txt");
                BufferedReader reader = new BufferedReader( new InputStreamReader(is));
        ) {
            String keyWord;
            while (!((keyWord = reader.readLine()) != null)) {
                this.addKeyword(keyWord);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    //add敏感词到前缀树中
    private void addKeyword(String keyWord) {

        TreeNode tempNode = rootNode;
        for (int i = 0; i < keyWord.length(); i++) {
            char c = keyWord.charAt(i);
            TreeNode subNode = tempNode.getSubNode(c);

            if (subNode == null) {
                subNode = new TreeNode();
                tempNode.addSubNode(c, subNode);
            }

            tempNode = subNode;

            if (i == keyWord.length() - 1) {
                tempNode.setKeywordEnd(true);
            }
        }
    }

    //判断是否为符号
    private boolean isSymbol(Character c) {
        return !CharUtils.isAsciiAlphanumeric(c) && (c < 0x2E80 || c > 0x9FFF);
    }

    //过滤敏感词
    public String filter(String text) {

        if (StringUtils.isBlank(text)) {
            return null;
        }

        //指针1
        TreeNode tempNode = rootNode;
        //指针2
        int begin = 0;
        //指针3
        int end = 0;
        //结果
        StringBuffer sb = new StringBuffer();

        while (begin < text.length()) {

            char c = text.charAt(end);

            if (isSymbol(c)) {
                if (tempNode == rootNode) {
                    begin ++;
                }
                sb.append(c);
                end++;
                continue;
            }

            tempNode = tempNode.getSubNode(c);
            if (tempNode == null) {

                sb.append(text.charAt(begin));
                end = ++begin;
                tempNode = rootNode;
            } else if (tempNode.isKeywordEnd()) {
                sb.append(REPLACEMENT);
                begin = ++end;
                tempNode = rootNode;
            } else {
                end++;
            }
        }
        return sb.toString();
    }

}
