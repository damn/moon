(ns clojure.get-name)

(defprotocol GetName
  (get-name [_]))

(extend-type com.badlogic.gdx.maps.tiled.TiledMapTileLayer
  GetName
  (get-name [layer]
    (.getName layer)))

(extend-type com.badlogic.gdx.scenes.scene2d.Actor
  GetName
  (get-name [actor]
    (.getName actor)))
