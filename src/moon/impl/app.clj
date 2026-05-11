(ns moon.impl.app
  (:require moon.audio
            moon.graphics)
  (:import (com.badlogic.gdx Application
                             Gdx)
           (com.badlogic.gdx.graphics GL20)))

(defn create [_ctx]
  Gdx/app)

(extend-type Application
  moon.audio/Audio
  (new-sound [app path]
    (.newSound (.getAudio app)
               (.internal (.getFiles app) path)))

  moon.graphics/Graphics
  (frames-per-second [app]
    (.getFramesPerSecond (.getGraphics app)))

  (delta-time [app]
    (.getDeltaTime (.getGraphics app)))

  (new-cursor [app pixmap hotspot-x hotspot-y]
    (.newCursor (.getGraphics app) pixmap hotspot-x hotspot-y))

  (set-cursor! [app cursor]
    (.setCursor (.getGraphics app) cursor))

  (clear! [app r g b a]
    (.glClearColor (.getGL20 (.getGraphics app)) r g b a)
    (.glClear      (.getGL20 (.getGraphics app)) GL20/GL_COLOR_BUFFER_BIT)))
