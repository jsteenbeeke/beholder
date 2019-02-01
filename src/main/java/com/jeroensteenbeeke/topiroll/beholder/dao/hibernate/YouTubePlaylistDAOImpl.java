package com.jeroensteenbeeke.topiroll.beholder.dao.hibernate;

import com.jeroensteenbeeke.hyperion.solstice.data.HibernateDAO;
import com.jeroensteenbeeke.topiroll.beholder.dao.YouTubePlaylistDAO;
import com.jeroensteenbeeke.topiroll.beholder.entities.YouTubePlaylist;
import com.jeroensteenbeeke.topiroll.beholder.entities.filter.YouTubePlaylistFilter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "request")
class YouTubePlaylistDAOImpl extends HibernateDAO<YouTubePlaylist, YouTubePlaylistFilter> implements YouTubePlaylistDAO {

}
