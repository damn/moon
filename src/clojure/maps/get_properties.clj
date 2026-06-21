(ns clojure.maps.get-properties)

(defprotocol GetProperties
  (get-properties [_]))

(extend-type com.badlogic.gdx.maps.tiled.TiledMap
  GetProperties
  (get-properties [tiled-map]
    (.getProperties tiled-map)))

(extend-type com.badlogic.gdx.maps.tiled.TiledMapTileLayer
  GetProperties
  (get-properties [layer]
    (.getProperties layer)))

(extend-type com.badlogic.gdx.maps.tiled.TiledMapTile
  GetProperties
  (get-properties [tile]
    (.getProperties tile)))
