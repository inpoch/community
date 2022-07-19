package com.guan.community.dao.elasticsearch;


import com.guan.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
