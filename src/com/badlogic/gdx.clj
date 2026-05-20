(ns com.badlogic.gdx
  (:require [gdl.app :as app]
            [gdl.audio :as audio]
            [gdl.files :as files]
            [gdl.graphics :as graphics])
  (:import (com.badlogic.gdx Application
                             Audio
                             Files
                             Gdx
                             Graphics
                             )))

(defn app []
  Gdx/app)

(extend-type Application
  app/App
  (audio [app]
    (.getAudio app))

  (files [app]
    (.getFiles app))

  (graphics [app]
    (.getGraphics app))

  (input [app]
    (.getInput app)))

(extend-type Audio
  audio/Audio
  (new-sound [this file-handle]
    (.newSound this file-handle)))

(extend-type Files
  files/Files
  (internal [this path]
    (.internal this path)))

(extend-type Graphics
  graphics/Graphics
  (frames-per-second [graphics]
    (.getFramesPerSecond graphics))

  (delta-time [graphics]
    (.getDeltaTime graphics))

  (new-cursor [graphics pixmap hotspot-x hotspot-y]
    (.newCursor graphics pixmap hotspot-x hotspot-y))

  (set-cursor! [graphics cursor]
    (.setCursor graphics cursor))

  (gl20 [graphics]
    (.getGL20 graphics)))
