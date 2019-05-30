package org.mozilla.tv.firefox.channels

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.reactivex.Observable
import org.junit.Before
import org.junit.Test
import org.mozilla.tv.firefox.channels.pinnedtile.PinnedTileRepo

class ChannelRepoTest {

    @MockK private lateinit var pinnedTileRepo: PinnedTileRepo
    private lateinit var channelRepo: ChannelRepo

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        channelRepo = ChannelRepo(pinnedTileRepo)
    }

    @Test
    fun `WHEN blacklist is empty THEN filterNotBlacklisted should not change its input`() {
        val blacklist = Observable.just(listOf<String>())

        fakeTileObservable.filterNotBlacklisted(blacklist)
            .test()
            .assertValue(fakeTiles)
    }

    @Test
    fun `WHEN blacklist includes values in the list THEN filterNotBlacklisted should filter out these values`() {
        val blacklist = Observable.just(listOf("www.yahoo.com", "www.wikipedia.org"))

        fakeTileObservable.filterNotBlacklisted(blacklist)
            .map { tiles -> tiles.map { it.url } }
            .test()
            .assertValue(listOf("www.mozilla.org", "www.google.com"))
    }

    @Test
    fun `WHEN blacklist includes values not found in the original list THEN hte original list should be unexpected`() {
        val blacklist = Observable.just(listOf("www.bing.com"))

        fakeTileObservable.filterNotBlacklisted(blacklist).test()
            .assertValue(fakeTiles)
    }
}

private val fakeTiles = listOf(
    fakeChannelTile("www.mozilla.org"),
    fakeChannelTile("www.google.com"),
    fakeChannelTile("www.wikipedia.org"),
    fakeChannelTile("www.yahoo.com")
)

private val fakeTileObservable: Observable<List<ChannelTile>> = Observable.just(fakeTiles)

private fun fakeChannelTile(url: String) = ChannelTile(
    url = url,
    title = url,
    subtitle = null,
    setImage = { },
    tileSource = TileSource.BUNDLED,
    id = url
)